var postTarget;
var timeoutId;
var pinCode;
var callbackFun;

function postData(target,data) {
	if(!http.sendRequest) {
		return null;
	}
	http.url=target;
	http.actionMethod="POST";
	var code=http.sendRequest(data);
	if(code!=0){
		return null;
	}
	return http.responseText;

}
function checkFinish() {
	if(postTarget){
		postTarget.close();
		$('#message-text').text('尚未安裝元件');
		$('#pop-message').modal('show');
	}
}
function makeSignature(pin, callback) {
	if(pin==null || pin==''){
		$('#message-text').text('請輸入PIN碼');
		$('#pop-message').modal('show');
		return;
	}
	pinCode = pin;
	callbackFun = callback;
    var ua = window.navigator.userAgent;
	if(ua.indexOf("MSIE")!=-1 || ua.indexOf("Trident")!=-1) {
		postTarget=window.open("waiting.htm?簽章中", "Signing","height=200, width=200, left=100, top=20");
		var tbsPackage=getTbsPackage();
		document.body.innerHTML += '<span id="httpObject"><OBJECT id="http" width=1 height=1 style="LEFT: 1px; TOP: 1px" type="application/x-httpcomponent" VIEWASTEXT></OBJECT></span>';
		var data=postData("http://localhost:61161/sign","tbsPackage="+tbsPackage);
		postTarget.close();
		postTarget=null;
		if(!data){
			$('#message-text').text('尚未安裝元件');
			$('#pop-message').modal('show');
		}else{
			setSignature(data, callback);
		}
	}else{
		postTarget=window.open("http://localhost:61161/popupForm", "簽章中","height=200, width=200, left=100, top=20");
		timeoutId=setTimeout(checkFinish,3500);
	}
}

function getTbsPackage() {
	var tbsData = {};
	tbsData["tbs"]=encodeURIComponent(generateUUID());
	tbsData["hashAlgorithm"]='SHA256';
	tbsData["pin"]=pinCode;
	tbsData["func"]="MakeSignature";
	tbsData["signatureType"]="PKCS7";
	var json = JSON.stringify(tbsData);
	return json;
}

function setSignature(signature, callback) {
	var ret=JSON.parse(signature);
	if(ret.ret_code!=0){
		if(ret.last_error){
			ret.errorMsg = MajorErrorReason(ret.ret_code)+'，'+MinorErrorReason(ret.last_error);
		}else{
			ret.errorMsg = MajorErrorReason(ret.ret_code);
		}
	}
	pinCode = null;
	callback(ret);
}

function receiveMessage(event) {
	//安全起見，這邊應填入網站位址檢查
	if(event.origin!="http://localhost:61161")
		return;
	try{
		var ret = JSON.parse(event.data);
		if(ret.func){
			if(ret.func=="getTbs"){
				clearTimeout(timeoutId);
				var json=getTbsPackage()
				postTarget.postMessage(json,"*");
			}else if(ret.func=="sign"){
				setSignature(event.data, callbackFun);
			}
		}else{
			if(console) console.error("no func");
		}
	}catch(e){
		//errorhandle
		if(console) console.error(e);
	}
}
//產生自然人憑證登入用的壓密值，系統並不允許兩次驗證用同一值
function generateUUID() {
    return ("10000000-1000-4000-8000-100000000000").replace(/[018]/g, s => {
        const c = Number.parseInt(s, 10)
        return (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    })
}
if (window.addEventListener) {
	window.addEventListener("message", receiveMessage, false);
}else {
	//for IE8
	window.attachEvent("onmessage", receiveMessage);
}
//for IE8
var console=console||{"log":function(){}, "debug":function(){}, "error":function(){}};