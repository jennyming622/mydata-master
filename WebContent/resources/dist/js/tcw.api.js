var twca = {};
twca.seccpnt = null;
twca.cardcpnt = null;

function twcaSign(type, tbs, pin, cn, authUrl) {
	var ret = {};
	ret.signature = "";
	
	if(type != "4" && type != "5") {
		ret.code = "1001";
		ret.msg = "不支援的載具類型";
		return ret;
	}
	
	if(type == "5") {
		if(twca.seccpnt == null) twca.seccpnt = new tcw();
		
		if(twca.seccpnt.isAlive() != 0) {
			ret.code = "2000";
			ret.msg = "偵測不到用戶端服務啟動中";
			return ret;
		}
		
		var certFilter = "//S_CL=3,I_O=TAIWAN-CA.COM Inc.,I_C=TW,S_CN=[@cn]//";
		certFilter = certFilter.replace(/\[@cn]/g, cn);
		
		var oResult = twca.seccpnt.selectSignerEx(certFilter, "", "", "", "", "C", "0");
		
		if (oResult === undefined || oResult !== "0") {
			ret.code = "1002";
			ret.msg = "選擇憑證失敗";
			return ret;
		}

		var sub = twca.seccpnt.GetCertSubject();
		
	    var regex = new RegExp("Yuanta", 'i');

		if (regex.test(sub)) {
			ret.code = "1005";
			ret.msg = "元大證券憑證目前暫不支援此項應用";
			return ret;
		}
		
		oResult = twca.seccpnt.twcaSignPkcs7(tbs, "1");
		
		if (oResult === undefined || oResult == "") {
			ret.code = "1004";
			ret.msg = "簽章失敗";
			return ret;
		}
		ret.signature = oResult;
		ret.code = 0;
		ret.msg = "";
		return ret;
		
	}else {
		if(twca.cardcpnt == null) twca.cardcpnt = new icard();
		
		if(twca.cardcpnt.isAlive() != 0) {
			ret.code = "2000";
			ret.msg = "偵測不到用戶端服務啟動中";
			return ret;
		}
		
		//雙向認證
		if(cpntAuth(authUrl).code != "0") {
			ret.code = "1006";
			ret.msg = "元件雙向認證失敗";
			return ret;
		}
		
		var oResult = twca.cardcpnt.icCardSignPkcs7(pin, tbs, '1');
    
    
		if (oResult === undefined) {
			ret.code = "1003";
			ret.msg = "簽章失敗";
			return ret;
		}
		if (oResult.errCode !== "0") {
			ret.code = "1004";
			ret.msg = oResult.errMsg;
			return ret;
		}
		ret.signature = oResult.b64SignValue;
		ret.code = 0;
		ret.msg = "";
		return ret;
	}
}

//透過Portal?
function cpntAuth(authUrl) {
    var ret = {};
    var authCode = "";
    try {
        var isOk = twca.cardcpnt.GetServerAuth();
        if (!isOk) {
            ret.code = "a001";
            ret.msg = "元件產生認證碼失敗";
            return ret;
        }
        var oResult = twca.cardcpnt.GetServerCode();
        if (oResult.errCode !== "0") {
            ret.code = "a002";
            ret.msg = "元件產生認證碼失敗:" + oResult.errMsg;
            return ret;
        }
        authCode = oResult.ServerCode;
    } catch (e) {
        ret.code = "a001";
        ret.msg = "元件產生認證碼失敗:" + e;
        return ret;
    }
    var retMessage;
    try {
        retMessage = $.ajax({async: false, type: "POST", dataType: "text", url: authUrl, data: {"authCode": authCode}}).responseText;
    } catch (e) {
        ret.code = "a002";
        ret.msg = "雙向認證通訊失敗:" + e;
        return ret;
    }
    var retArray;
    try {
        retArray = retMessage.split("||");
        if (retArray.length !== 2) {
            ret.code = "a003";
            ret.msg = "認證訊息讀取失敗:格式錯誤";
            return ret;
        }
    } catch (e) {
        ret.code = "a003";
        ret.msg = "認證訊息讀取失敗:" + e;
        return ret;
    }
    if (retArray[0] !== "0") {
        ret.code = "a004";
        ret.msg = "伺服器認證失敗:" + retArray[1];
        return ret;
    }
    var oResult = twca.cardcpnt.VerifyServerResponse(retArray[1]);
    if (!oResult) {
        ret.code = "a005";
        ret.msg = "元件認證失敗";
        return ret;
    }
    ret.code = "0";
    ret.msg = "";
    return ret;
}

function tcw() {
	var tcwClientUrl = "https://localhost:4450/TCW";

	var tcwDllInitKey = "bDkMh7dr7nzcfsEaDhXWsfng/Gt9Fb8zs+fBBne4ALlpg4K4ZwbCstOx58BAoVo+cw0yFTKG9WNL/yEzA+grEOkJDOtBNqYMnaeAo6Ftx+gM3+gJRWWBLHAw8zbOocIXq1Hn3drP4mV+AL6/boj9PtIWcUonGp3ksuPGGVGczV1yOfZFlXZCznqSOzSdW2/eCI0k/Ra2+tAb6y88s19GJuf86pFvFxaZqruSq4y8WatkoKlE6LMv34RNMkW9Jqx2xjX0wx2kOJcFNEP83fwNN+yGbptIYmmoda9JL3AATLJn8FL8CypEIHnH+IXrlerQlILZA1Bv1vilsN6Lon+l5w==";
        var tcwMacFwInitKey = "oU/1BptRSuvx/NjfyNq503FAlzyWtxTlzcOO2DosE/xiBi29bc9o7RxBq6JEly+X3O9zMZSThsHHTvd0EkRt0RFYCRLHd+wv0RlvTnqF0CZI9Ee+aaIPPXQeWKzanfH8i0axn6c/YgQWsDCd9e3sVQyNv0MY6V2QCTGCP/gI0sRkZ4p2X+CKLEuGrYHtPCqfGktxeMLP1RlaOCVRSXtInLUJ0lG1xc+ilw5opule3TwW6mQ8O0qQuzPcqtidG6Se+XzXKlXONJH+oTc2UcSN9bTFB6HUJgBJ2sLNLX2+p7flfn/boBrwEMjblgpozg2IOpTlYDLan3KkgFIPxrrN3g==";
	
	var oTcw = this;

	this.tcwSessionID = "";
	
	this.twcaSelectSignerCertInfo = null;	// 相容舊版 selectSigner 暫存憑證資訊
    
    // 取得 TCW 版號
	this.getVersion = function() {
		var oResult = jsonPost(getServiceUrl("getVersion"), null, 2000);
		var rtnResult = "";
		
		if(oResult.errCode == 0) rtnResult = oResult.version;
		
		return rtnResult;
	};
	
	// 測試 TCW 是否已啟動
	this.isAlive = function() {

		var oResult = jsonPost(getServiceUrl("isAlive"), null, 2000);
		var rtnResult = "";
		try {
			rtnResult = oResult.errCode;
		} catch (err) {
			rtnResult = "100";
		}
		return rtnResult;
	};
	
	// 安控元件相容舊函式
	this.selectSigner = function(certFilter, dwFlags, dwKeyUsage) {
		var oResult = null;
		var oParam = null;
		
		if ( certFilter == "" ) {
			return -1;
		}
		
		if ( dwFlags == "" ) {
			return -1;
		}
		
		if ( dwKeyUsage == "" ) {
			return -1;
		}
		
		oTcw.twcaSelectSignerCertInfo = null;	// 清除暫存憑證資訊
		
		oParam = {};
		oParam.certFilter = certFilter;
		oParam.dwFlags = dwFlags;
		oParam.dwKeyUsage = dwKeyUsage;
		oResult = tcwPost(getServiceUrl("twcaCpntSelectSigner", ""), oParam);
		if ( oResult.errCode == "0" ) {
			oTcw.twcaSelectSignerCertInfo = oResult;
		}
		
		return oResult.errCode;
	};

	this.selectSignerEx = function(certFilter, pfxPath, pfxName, userPIN, cmpDate, dwFlags, dwKeyUsage) {
		var oResult = null;
		var oParam = null;
		
		oTcw.twcaSelectSignerCertInfo = null;	// 清除暫存憑證資訊
		
		oParam = {};
		oParam.certFilter = certFilter;
		oParam.pfxPath = pfxPath;
		oParam.pfxName = pfxName; 
		oParam.userPIN = userPIN;
		oParam.cmpDate = cmpDate;
		oParam.dwFlags = dwFlags;
		oParam.dwKeyUsage = dwKeyUsage;
		oResult = tcwPost(getServiceUrl("twcaCpntSelectSignerEx", ""), oParam);
		if ( oResult.errCode == "0" ) {
			oTcw.twcaSelectSignerCertInfo = oResult;
		}
		
		return oResult.errCode;
	};
	
	// 取得簽章憑證的路徑
	this.GetSignerPfxFilePath = function() {  return getTWCASelectSignerCertInfo("pfxFilePath"); };
	// 取得簽章憑證的檔名
	this.GetSignerPfxFileName = function() {  return getTWCASelectSignerCertInfo("pfxFileName"); };
	// 取得執行簽章的憑證內容
	this.GetCertB64 = function() {  return getTWCASelectSignerCertInfo("b64Cert"); };
	// 取得執行簽章憑證的主旨
	this.GetCertSubject = function() {  return getTWCASelectSignerCertInfo("subject"); };
	// 取得執行簽章憑證的主旨用戶名稱(CommonName)
	this.GetCertSubjectCN = function() {  return getTWCASelectSignerCertInfo("cn"); };
	// 取得執行簽章憑證的發行者
	this.GetCertIssuer = function() {  return getTWCASelectSignerCertInfo("issuer"); };
	// 取得執行簽章憑證的序號
	this.GetCertSerial = function() {  return getTWCASelectSignerCertInfo("serial"); };
	//取得執行簽章憑證的摘要值
	//this.GetCertFinger = function() { return getTWCASelectSignerCertInfo("certfinger"); };
	this.GetCertFinger = function() {  return getTWCASelectSignerCertInfo("certHash"); };
	// 取得執行簽章憑證的效期起始日
	this.GetCertNotBefore = function() {  return getTWCASelectSignerCertInfo("notBefore"); };
	// 取得執行簽章憑證的效期終止日
	this.GetCertNotAfter = function() {  return getTWCASelectSignerCertInfo("notAfter"); };
	// 取得執行簽章憑證的憑證等級
	this.GetCertClassLevel = function() {  return getTWCASelectSignerCertInfo("classLevel"); };	
	
	this.twcaSignPkcs7 = function(plainText, dwFlag) {

		var oResult = null;
		var oParam = null;
		var b64SignValue = "";
		
		if ( plainText == "" ) {
			return -1;
		}
		
		if ( dwFlag == "" ) {
			return -1;
		}
		
		oParam = {};
		oParam.plainText = plainText;
		oParam.dwFlag = dwFlag;
		oResult = tcwPost(getServiceUrl("twcaCpntSignPkcs7", ""), oParam);
		if ( oResult.errCode == "0" ) {
			b64SignValue = oResult.b64SignValue;
		}
		
		return b64SignValue;
		
	};
	
	this.twcaSignPkcs7EX = function(plainText, dwFlag) {

		var oResult = null;
		var oParam = null;
		var b64SignValue = "";
		var hashvalue = "";
		var signvalueArr = "";
		var content;
		var b64cert = "";
		
		if( Array.isArray(plainText)) content = plainText;
        else {
            content = [];
            content.push(plainText);
        }
		
		if ( plainText == "" ) {
			return -1;
		}
		
		if ( dwFlag == "" ) {
			return -1;
		}
		
		for (var i = 0; i < content.length; i++) {
            var tmp = toJson(twcaLib.Hash(content[i], 0x2000|0x0000));
            hashvalue += tmp.hashValue;  
            if(i+1 < content.length ) hashvalue += "||";
        }
		
		oParam = {};
		oParam.plainText = hashvalue;
		oParam.dwFlag = dwFlag;
		oResult = tcwPost(getServiceUrl("twcaCpntSignPkcs7EX", ""), oParam);
		
		if ( oResult.errCode == "0" ) {
			signvalueArr = oResult.b64SignValue.split("||");
			b64cert = this.GetCertB64();
			for (var i = 0; i < signvalueArr.length; i++) {
				b64SignValue += toJson(twcaLib.CreatePKCS7FromSignedData(content[i], b64cert, signvalueArr[i], 0x2000|0x0000)).p7Value;
                if(i+1 < content.length ) b64SignValue += "||";
            } 
		}
		
		return b64SignValue;
		
	};
	
	function getTWCASelectSignerCertInfo(infoKey) {
		
		var infoValue = "";
		if ( oTcw.twcaSelectSignerCertInfo != null ) {
			if ( typeof(oTcw.twcaSelectSignerCertInfo[infoKey]) != "undefined" ) {
				infoValue = oTcw.twcaSelectSignerCertInfo[infoKey];
			}
		}
		return infoValue;
	};
	
	function getCurrentBrowserName() {
		
		var browserName = "";
		
		var userAgent = navigator.userAgent;
		
		do {
		
			if ( userAgent.match(/.*MSIE.*/) || userAgent.match(/.*Trident.*/) ) {
				browserName = "Internet Explorer";
				break;
			}
			
			if ( userAgent.match(/Chrome\/\d+\.\d+\.\d+/) ) {
				if ( userAgent.match(/x64/) ) {
					browserName = "Chrome 64";
				} else {
					browserName = "Chrome";
				}
				break;
			}
			
			if ( userAgent.match(/Firefox\/\d+\.\d+\.\d+/) ) {
				browserName = "FireFox";
				break;
			}
			
			if ( userAgent.match(/.*Edge.*/) ) {
				browserName = "Microsoft Edge";
				break;
			}			
			
		} while ( false );
		
		
		return browserName;
		
	};
	
	function postDataToStr(postData) {
		
		var postStr = "";
		if ( postData != null ) {
			for ( var paramKey in postData ) {
				if ( postStr != "" ) {
					postStr += "&";
				}
				postStr += paramKey + "=" + encodeURIComponent(postData[paramKey]);
			}
		}
		if ( oTcw.tcwSessionID == "" ) { 
			if ( postStr != "" ) {
				postStr += "&";
			}
			if ( isWindows() ) {
				postStr += "TCW-INIT-KEY=" + encodeURIComponent(tcwDllInitKey);
			} else {
				postStr += "TCW-INIT-KEY=" + encodeURIComponent(tcwMacFwInitKey);
			}
		}
		
		return postStr;
	};
	
	function tcwPost(targetUrl, postData, iTimeOut) {

		var postStr = postDataToStr(postData);	

		return jsonPost(targetUrl, postData, iTimeOut);
	};
	
	function jsonPost(targetUrl, postData, iTimeOut) {

		return toJson(post(targetUrl, postData, iTimeOut));
	};
	
	function post(targetUrl, postData, iTimeOut) {

		var xhr = null;		
		var postResult = "";
		var postStr = postDataToStr(postData);
		var beginTime = null;
		var endTime = null;
		var sessionSeparator = "@@";
		var sessionSeparatorPos = 0;
		var blCrossDomain = false;
		
		if (targetUrl.substring(0, 4) == "http" ) {
            blCrossDomain = true;
        }

		xhr = getXhr(iTimeOut);
		if ( xhr == null ) {			

			return "";
		}
		
		if ( blCrossDomain ) {
            targetUrl += "?" + postStr;			
		}
		
		try {
		
			xhr.open("POST", targetUrl, false);
			xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
			
			xhr.send(postStr);
			
			postResult = xhr.responseText;
			
			sessionSeparatorPos = postResult.lastIndexOf(sessionSeparator);
			if ( sessionSeparatorPos >= 0 ) {
				oTcw.tcwSessionID = postResult.substring(sessionSeparatorPos + sessionSeparator.length, postResult.length);
				postResult = postResult.substring(0, sessionSeparatorPos);
			}

			
		} catch (err) {
			postResult = "";
		}
		
		return postResult;
		
	};


	function getXhr(iTimeout) {

		var xhr = null;
		
		if ( typeof(iTimeOut) != "undefined" ) {
		}	

		do {
			// 20170421 若未使用 native http object 則 cross domain 支援性可能會有問題
			if ( typeof(XMLHttpRequest) != "undefined" ) {
				xhr = new XMLHttpRequest();
				break;
			}
			
		} while ( false );
		
		
		return xhr;
		
	};
	
	function getServiceUrl(serviceName, sessionID) {

		var serviceUrl = tcwClientUrl;

		if ( oTcw.tcwSessionID != "" ) {
			serviceUrl += "/" + oTcw.tcwSessionID;
		} 
		serviceUrl += "/" + serviceName;
		
		return serviceUrl;
	}
	
	function isWindows() {
		
		return /.*win.*/.test(window.navigator.platform.toLowerCase());
	};
	
	function isMacOS() {
		
		return /.*mac.*/.test(navigator.platform.toLowerCase());
	};
	
	function isIE() {
		
		return /.*MSIE.*/.test(navigator.userAgent) || /.*Trident.*/.test(navigator.userAgent);  
	};
	
	function isChrome() {
		
		return /.*Chrome.*/.test(navigator.userAgent);
	};
	
	function strRight(strInput, specLen) {
		
		return strInput.substring(strInput.length - specLen, strInput.length);
	};
	
	function toJson(strJson) {		
		
		var oRtn = null;
		try { oRtn = JSON.parse(strJson); } catch (e) { oRtn = null; }

		return oRtn;
	};	
	
	function $f(tagId) {
		return document.getElementById(tagId);
	};
	
	function getDebugTimeStr() {
		
		var dtNow = new Date();
		var timeStr = "";
		
		timeStr += dtNow.getFullYear();
		timeStr += "-" + strRight("00" + (dtNow.getMonth() + 1), 2);
		timeStr += "-" + strRight("00" + dtNow.getDate(), 2);
		timeStr += " " + strRight("00" + dtNow.getHours(), 2);
		timeStr += ":" + strRight("00" + dtNow.getMinutes(), 2);
		timeStr += ":" + strRight("00" + dtNow.getSeconds(), 2);
		timeStr += " " + strRight("000" + dtNow.getMilliseconds(), 3);
		
		return timeStr;
		
	}
	
}

function icard() {
	var tcwClientUrl = "https://localhost:4450/TCW";
	var tcwDllInitKey = "NJ7cm8+Bv7eMQ5o5wyosBEaY8FzBFReCc8HZffQofgSfkZZh9EIBQkojIEUUD/uWqmMrBG2g58tnu0SyJZBssstebwtc++MeGfqApuOktgL7MLj7NPwsJL7DmzIo46+G4zWEaUfwdbSDMhguN7TEcm3SbcefLbAkTqAvMAtitiAQy8ruvWgzTSS0m2V8UhnCvDVYi5Gfkg2ttzpFdSdaC7b5kZGt/+erQEn0NlmAxOogWoyFlpkqfxR6spqB+sN2c0x5+9UtqByF+wzrIiR/64r2KBmucSM9BvWaAci3Tf0Wf0Q0edt4v+MB9OnSejA91rG0JAWz4ti2+rYB9yygmg==";
        var tcwMacFwInitKey = "tQSUhbQfiUOLx/DDnWU6AZln/6vsyM5avGlTAfsnxTU8ntvl+q57R6M1ts2qlSP0oAG57mgc4pBAJVRHAoxBducYApF7iCoPYFfiRE1sJUDtjs0Wp+LbDWVHjJppsmPjK6uRWkP30Yasyh30svZaGw5o0SFi5nDOVgKoslfzshz7Rnd1e+PdwdHOoksKU5Mb672Sbs5d9tscO6ynmub3pr2pM5htwSf76WkiV+eDns1iYlKA0if/jQQi7oIMh/Cy1vLKf+r57FvVvEFfx01a5PZ+L8RdrSOfFpnPSAS1N/KDY6xvD6xBbam36BQ4gG53nplVdE4jBO3stJUtjTOSZw==";

	var oICard = this;
	
	this.tcwSessionID = "";
	
	// 取得 TCW 版號
	this.getVersion = function() {
		var oResult = jsonPost(getServiceUrl("getVersion"), null, 2000);
		var rtnResult = "";
		
		if(oResult.errCode == 0) rtnResult = oResult.version;
		
		return rtnResult;
	};
	
	// 測試 TCW 是否已啟動
	this.isAlive = function() {
		var oResult = jsonPost(getServiceUrl("isAlive"), null, 2000);
		var rtnResult = "";
		try {
			rtnResult = oResult.errCode;
		} catch (err) {
			rtnResult = "100";
		}
		return rtnResult;
	};
	
	this.icCardSignPkcs7 = function(pwd, plainText) {
		var oResult = null;
		
		do {
			oParam = {};
			oParam.pwd = pwd;
			oParam.plainText = plainText;
			
			oResult = tcwPost(getServiceUrl("icCardSignPkcs7"), oParam);
			if ( oResult.errCode != "0" ) {
				break;
			}
		} while ( false );
		return oResult;
	};
	
	this.icCardSignPkcs1 = function(pwd, plainText) {
		var oResult = null;
		
		do {
			oParam = {};
			oParam.pwd = pwd;
			oParam.plainText = plainText;
			
			oResult = tcwPost(getServiceUrl("icCardSignPkcs1"), oParam);
			if ( oResult.errCode != "0" ) {
				break;
			}
		} while ( false );
		return oResult;
	};
	
	this.GetServerAuth = function() {
		var oResult = null;
		var blResult = false;
		
		do {
			oResult = tcwPost(getServiceUrl("GetServerAuth"), "");
			if ( oResult.errCode != "0" ) {
				break;
			}
			blResult = true;
		} while ( false );
		return blResult;
	};
	
	this.GetServerCode = function() {
		var oResult = null;
		
		do {
			oResult = tcwPost(getServiceUrl("GetServerCode"),"");
			if ( oResult.errCode != "0" ) {
				break;
			}
		} while ( false );
		return oResult;
	};
	
	this.VerifyServerResponse = function(strServerResponse) {
		var oResult = null;
		var blResult = false;
		do {
			oResult = tcwPost(getServiceUrl("VerifyServerResponse"), {'strServerResponse':strServerResponse});
			if ( oResult.errCode != "0" ) {
				break;
			}
			blResult = true;
		} while ( false );
		return blResult;
	};

	function postDataToStr(postData) {
		var postStr = "";
		if ( postData != null ) {
			for ( var paramKey in postData ) {
				if ( postStr != "" ) {
					postStr += "&";
				}
				postStr += paramKey + "=" + encodeURIComponent(postData[paramKey]);
			}
		}
		if ( oICard.tcwSessionID == "" ) { 
			if ( postStr != "" ) {
				postStr += "&";
			}
			if ( isWindows() ) {
				postStr += "TCW-INIT-KEY=" + encodeURIComponent(tcwDllInitKey);
			} else {
				postStr += "TCW-INIT-KEY=" + encodeURIComponent(tcwMacFwInitKey);
			}
		}
		
		return postStr;
	};
	
	function tcwPost(targetUrl, postData, iTimeOut) {
		var postStr = postDataToStr(postData);	
		
		return jsonPost(targetUrl, postData, iTimeOut);
	};
	
	function jsonPost(targetUrl, postData, iTimeOut) {
		return toJson(post(targetUrl, postData, iTimeOut));
	};
	
	function post(targetUrl, postData, iTimeOut) {
		var xhr = null;		
		var postResult = "";
		var postStr = postDataToStr(postData);
		var beginTime = null;
		var endTime = null;
		var sessionSeparator = "@@";
		var sessionSeparatorPos = 0;
		
		var blCrossDomain = false;
		
		if (targetUrl.substring(0, 4) == "http" ) {
            blCrossDomain = true;
        }
		
		xhr = getXhr(iTimeOut);

		if ( xhr == null ) {			
			return "";
		}
		
		if ( blCrossDomain ) {
            targetUrl += "?" + postStr;			
		}
		
		try {
			xhr.open("POST", targetUrl, false);
			xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");

			xhr.send(postStr);
			postResult = xhr.responseText;
			sessionSeparatorPos = postResult.lastIndexOf(sessionSeparator);
			if ( sessionSeparatorPos >= 0 ) {
				oICard.tcwSessionID = postResult.substring(sessionSeparatorPos + sessionSeparator.length, postResult.length);
				postResult = postResult.substring(0, sessionSeparatorPos);
			}	
		} catch (err) {
			postResult = "";
		}
			
		return postResult;
	};

	function getXhr(iTimeout) {
		var xhr = null;
		
		do {
			// 20170421 若未使用 native http object 則 cross domain 支援性可能會有問題
			if ( typeof(XMLHttpRequest) != "undefined" ) {
				xhr = new XMLHttpRequest();
				break;
			}
			
		} while ( false );
		
		return xhr;
	};
	
	function getServiceUrl(serviceName, sessionID) {
		var serviceUrl = tcwClientUrl;

		if ( oICard.tcwSessionID != "" ) {
			serviceUrl += "/" + oICard.tcwSessionID;
		} 
		serviceUrl += "/" + serviceName;
		
		return serviceUrl;
	}
	
	function isIE() {	
		return /.*MSIE.*/.test(navigator.userAgent) || /.*Trident.*/.test(navigator.userAgent);  
	};
	
	function isWindows() {
		return /.*win.*/.test(navigator.platform.toLowerCase());
	};
	
	function isMacOS() {
		return /.*mac.*/.test(navigator.platform.toLowerCase());
	};
	
	function isChrome() {
		return /.*Chrome.*/.test(navigator.userAgent);
	};
	
	function strRight(strInput, specLen) {
		return strInput.substring(strInput.length - specLen, strInput.length);
	};
	
	function toJson(strJson) {		
		var oRtn = null;
		try { oRtn = JSON.parse(strJson); } catch (e) { oRtn = null; }
		return oRtn;
	};	
	
	function $f(tagId) {
		return document.getElementById(tagId);
	};
}


