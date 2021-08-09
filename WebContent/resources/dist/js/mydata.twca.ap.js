var mydataTWCALib = new MydataTWCALib();

if ( window.addEventListener ) {
	window.addEventListener("message", TWCAreceiveMessage, false);
} else {
	window.attachEvent("onmessage", TWCAreceiveMessage);	
}

function MydataTWCALib() {
    var mima = "";
    var businessNo = "";
    var verifyNo = "";
    var apiVersion = "";
    var hashKeyNo = "";
    var token = "";
    var identifyNo = "";
    var proxyURL = "";
    var callback = null;
    
    var thisWindow = window;
    
    this.IsAlive = function (cbf) {
        
        var code = "0";
        var msg = "成功";

        if(twca.seccpnt === null) twca.seccpnt = new tcw();
        if(twca.seccpnt.isAlive() != 0) {
                code = "2000";
                msg = "偵測不到用戶端服務啟動中";
        }    
        
        cbf(code, msg, null, null);
    }
    
    this.ATMVerifyInvoke = function (pin, businessNo, apiVersion, hashKeyNo, verifyNo, token, identifyNo, proxyURL, callback) {
        
        this.mima = pin;
        
        this.businessNo = businessNo;
        this.verifyNo = verifyNo;
        this.apiVersion = apiVersion;
        this.hashKeyNo = hashKeyNo;
        this.token = token;
        this.identifyNo = identifyNo;
        this.proxyURL = proxyURL;
        this.callback = callback;
        
        if (!pin || !businessNo || !apiVersion || !hashKeyNo || !verifyNo || !token || !identifyNo || !proxyURL) {
            callback(5005, "參數錯誤", "", "");
            return;
        }

        var w = 480;
        var h = 520;
        var dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : window.screenX;
        var dualScreenTop = window.screenTop !== undefined ? window.screenTop : window.screenY;
        var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
        var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
        var left = ((width / 2) - (w / 2)) + dualScreenLeft;
        var top = ((height / 2) - (h / 2)) + dualScreenTop;

        var tempForm = postObject(proxyURL, 'twcaNewWin', 'twcaForm', businessNo, verifyNo, apiVersion, hashKeyNo, token, identifyNo);
        var nw = window.open('', 'twcaNewWin', 'width=' + w + ',height=' + h + ',left=' + left + ',top=' + top + ',alwaysOnTop=yes');
        if (nw) {
            tempForm.submit();
            preventUserClose(nw);
            nw.focus();
        } else {
            doneProcess(7500, "瀏覽器封鎖彈出視窗", "", "");
        }
        
    }
    
    this.ATMSignPKCS7 = function (strContent, callback) {
	twcaLib.SignPkcs7(strContent, 0x2000 | 0x0001, callback);
    }

    this.HWSignPKCS7 = function (strContent, pin, proxyURL, callback) {
        
        var data;
        var ret = twcaSign("4", strContent, pin, "", proxyURL);
        if(ret.code == 0) {
            data = JSON.stringify({
                "signature":ret.signature
            });
        }
        
        callback(ret.code, ret.msg, null, data);
        
    }

    this.SWSignPKCS7 = function (strContent, pin, userID, callback) {
        
        var data = null;
        var ret = twcaSign("5", strContent, pin, userID, "");

        if(ret.code == 0) {
            data = JSON.stringify({
                "signature":ret.signature
            });
        }
        
        callback(ret.code, ret.msg, null, data);
        
    }
    
    
    //==========================================================================
    // Private Functions
    //==========================================================================
    this.getMima = function () {
        return this.mima;
    }
    
    function postObject(action, targetID, formID, businessNo, verifyNo, apiVersion, hashKeyNo, token, identifyNo) {
        var form = document.createElement("form");
        form.setAttribute("method", "POST");
        form.setAttribute("action", action);
        form.setAttribute("target", targetID);
        form.setAttribute("id", formID);
        form.appendChild(getHiddenField("BusinessNo", businessNo));
        form.appendChild(getHiddenField("VerifyNo", verifyNo));
        form.appendChild(getHiddenField("ApiVersion", apiVersion));
        form.appendChild(getHiddenField("HashKeyNo", hashKeyNo));
        form.appendChild(getHiddenField("Token", token));
        form.appendChild(getHiddenField("IdentifyNo", identifyNo));
        document.body.appendChild(form);
        return form;
    }

    function getHiddenField(name, value) {
        var hiddenField;
        hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", name);
        hiddenField.setAttribute("value", value);
        return hiddenField;
    }
    
    function preventUserClose(newWindow) {
        newWindow.onbeforeunload = function() {
            window.console.log("ONBEFOREONLOAD");
            try {
                twcaH5Flow = {
                    "action":"Exception",
                    "raFunc":raFunc,
                    "origin":window.location.hostname,
                    "errCode":"5079",
                    "errMsg":"使用者關閉視窗"
                };
                thisWindow.postMessage({"twcaH5Flow":twcaH5Flow}, "*");

            } catch (e) {
            }
        };        
    }
    
    function doneProcess(errCode, errMsg, token, data) {
        this.callback(errCode, errMsg, token, data);
    }
    
    return this;
}

function TWCAreceiveMessage(event) {
    
    var retObj = event.data.TWCAMyDataRet;
    var data = null;

    if (!retObj || !retObj.returnCode) {
        return ;
    }
    
    if (retObj.returnCode === 0 || retObj.returnCode === "0" || retObj.returnCode === "0000") {

        if (!sessionStorage.getItem("twcaSelectedKey")) {
            sessionStorage.setItem("twcaSelectedKey", retObj.seMima);
        }
        if (!sessionStorage.getItem("selectedCertInfo")) {
            sessionStorage.setItem("selectedCertInfo", retObj.seCert);
        }
        data = JSON.stringify({
            atm_sn: retObj.atm_sn
        });
    }
    
    mydataTWCALib.callback(retObj.returnCode, retObj.returnCodeDesc, null, data);
    
}
