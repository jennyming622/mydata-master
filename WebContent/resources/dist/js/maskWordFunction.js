/**
 *  utils below develop by john
 */

// 宣告enum
const MaskType = Object.freeze({"name":0, "phone" : 1, "uid":2, "email" : 3, "birthdate" : 4, "NHI" : 5, "domicile": 6});
// 宣告資料
var infoData = {uid:'', phone: '', name: '', domicile: '', NHI: '', email: '', birthdate: ''};

/**
 * input欄位focusin事件
 * @param obj
 */
function inputFocusinEvent(obj) {
    // input欄位的sibling element -> i tag
    var i = $(obj).parent().find('.eye-switch');
    var count = $(obj).attr('data-id') == undefined?0:parseInt($(obj).attr('data-id'));
    count = count + 1;
    $(obj).attr('data-id', count);
    if(count == 1) {
        // 小眼睛切換
        if($(i).hasClass('fa-eye')){
            $(i).removeClass('fa-eye').addClass('fa-eye-slash');
        }else {
            $(i).removeClass('fa-eye-slash').addClass('fa-eye');
        }

        // 若input欄位為空字串則不遮罩
        if(typeof $(obj).attr('data-value') !='undefined' && $(obj).attr('data-value').length > 0){
            var maskValue = $(obj).val();
            $(obj).val($(obj).attr('data-value'));
            $(obj).attr('data-value', maskValue);
        }else {
            $(obj).attr('data-value','');
        }
    }
}

function inputFocusinEvent_1(obj) {
    // input欄位的sibling element -> i tag
    var i = $(obj).parent().find('.eye-switch');
	// 小眼睛切換
	if($(i).hasClass('fa-eye')){
		$(i).removeClass('fa-eye').addClass('fa-eye-slash');
	}
	
	// 若input欄位為空字串則不遮罩
	if(typeof $(obj).attr('data-value') !='undefined' && $(obj).attr('data-value').length > 0){
		var maskValue = $(obj).val();
		$(obj).val($(obj).attr('data-value'));
	}else {
		$(obj).attr('data-value','');
	}
}

/**
 * 判斷字串是否為中文
 * */
function isChn(str) {
    let reg = /^[\u4E00-\u9FA5]+$/;
    // console.log('str------>', str, reg.test(str));
    if (reg.test(str)) {
        // alert("全是中文");
        return true;
    } else {
        // alert("不全是中文");
        return false;
    }
}
function inputFocusoutEvent(obj, type) {
    // 若生日只有6碼則前面補0
    if(type === MaskType.birthdate && $(obj).val().length === 6){
        var tmpValue = $(obj).val();
        var tmpDataVal = $(obj).attr('data-value');
        $(obj).val('0' + tmpValue);
        $(obj).attr('data-value', '0'+ tmpDataVal);
    }
    
    if($(obj).val().length === 0){
        $(obj).attr('data-value','');
    }else {
	    if(type === MaskType.email||type === MaskType.name){
	        // 更新value
	        var newVal = $(obj).val();
	        var dataVal = $(obj).attr('data-value');
	        $(obj).attr('data-value', newVal);
	        // 根據input的id判斷用什麼方法mask
	        var maskVal = maskSenstiveInformation_v2_1(type, newVal, dataVal).mask;
	        $(obj).val(maskVal);
	    }
    }
    
    // focusout切換小眼睛
    var i = $(obj).parent().find('.eye-switch');
    if($(i).hasClass('fa-eye-slash')){
        $(i).removeClass('fa-eye-slash').addClass('fa-eye');
    }else {
        $(i).removeClass('fa-eye').addClass('fa-eye-slash');
    }

    $(obj).attr('data-id', 0)
}

function inputFocusoutEvent_1(obj, type) {
    // 若生日只有6碼則前面補0
    if(type === MaskType.birthdate && $(obj).val().length === 6){
        var tmpValue = $(obj).val();
        $(obj).val('0' + tmpValue);
    }

    if($(obj).val().length === 0){
        $(obj).attr('data-value','');
    }else {
        // 更新value
        var newVal = $(obj).val();
        var dataVal = $(obj).attr('data-value');
        if(newVal.indexOf('*')==-1){
	        $(obj).attr('data-value', newVal);
	        // 根據input的id判斷用什麼方法mask
	        var maskVal = maskSenstiveInformation_v2_1(type, newVal, dataVal).mask;
	        $(obj).val(maskVal);
        }
    }

    // focusout切換小眼睛
    var i = $(obj).parent().find('.eye-switch');
    if($(i).hasClass('fa-eye-slash')){
        $(i).removeClass('fa-eye-slash').addClass('fa-eye');
    }
}
/**
 * input 輸入中
 * @param obj
 * @param type
 */
function inputTypingEvent(obj, type){
	//console.log('inputTypingEvent-->',obj);
    if($(obj).val().length === 0){
        infoDataEmpty(type)
        $(obj).attr('data-value','');
    }else {
        // 更新value
        if(typing == false){
		    var newVal = maskSenstiveInformation_v2_1(type, $(obj).val(), $(obj).attr('data-value')); // 根據input的id判斷用什麼方法mask
		    //console.log('newVal.real----> ',newVal.real);
		    //console.log('newVal.mask----> ',newVal.mask);
		    if(newVal !== undefined){
		        $(obj).attr('data-value',newVal.real);
		        $(obj).val(newVal.mask);
		    }        
        }
    }
}

/**
 * 透過類型辨別，清空對應的infoData項目
 * @param type
 */
function infoDataEmpty(type){
    switch (type) {
        case MaskType.name:
            return infoData.name = '';
        case MaskType.phone:
            return infoData.phone = '';
        case MaskType.uid:
            return infoData.uid = '';
        case MaskType.email:
            return infoData.email = '';
        case MaskType.birthdate:
            return infoData.birthdate = '';
        case MaskType.NHI://健保卡
            return infoData.NHI = '';
        case MaskType.domicile://戶號
            return infoData.domicile = '';
    }
}

/**
 * svg click事件
 * @param obj
 */
function maskorUnMask_v2(obj) {
    var eyeid = $(obj).attr('id');
    var idx = eyeid.indexOf('eye_');
    var typeId = eyeid.substring(idx+4, eyeid.length);
    var isDisabled = $('#' + typeId).is(":disabled");
    if(isDisabled == true) {
        if($(obj).hasClass('fa-eye')){
            $(obj).removeClass('fa-eye').addClass('fa-eye-slash');
        }else {
            $(obj).removeClass('fa-eye-slash').addClass('fa-eye');
        }
        var maskValue = $('#' + typeId).val();
        $('#' + typeId).val($('#' + typeId).attr('data-value'));
        $('#' + typeId).attr('data-value', maskValue);
    } else {
        var isfocus = $('#' + typeId).is(":focus");
        if (isfocus == true) {
            $('#' + typeId).blur();
        } else {
            $('#' + typeId).focus();
        }
    }
}

/**
 * 隱碼類型判斷
 * @param type
 * @param value
 */
function maskSenstiveInformation_v2_1(type,value,value2) {
    switch (type) {
        case MaskType.name:
            return maskName_1(value);
        case MaskType.phone:
            return maskPhoneNumber_1(value,value2);
        case MaskType.uid:
            return maskPhoneNumberOrUid_1(value,value2);
        case MaskType.email:
            return maskEmailAddress_1(value);
        case MaskType.birthdate:
            return maskBirthdate_1(value,value2);
        case MaskType.NHI://健保卡
            return maskNHINumber_1(value,value2);
        case MaskType.domicile://戶號
            return maskDomicile_1(value,value2);
    }
}
/**
 * 在第{index}位置以{replace}取代原有char
 * @param string
 * @param index
 * @param replace
 * @returns {*}
 */
function replaceAt_1(string, index, replace) {
    return string.substring(0, index) + replace + string.substring(index + 1);
}


/**
 * 規則：只顯示第一個字與最後一個字，中間隱碼處理。若姓名只有兩個字，則將名字的最後一個字隱碼
 * @param name
 * @returns {*}
 */
 function maskName(name) {
    if(typeof name == 'undefined' || name.length == 0) return '';
    // 若不足3個字則補齊
    if(name.length === 2){
        name += '#';
    }
    // 取代中間的char
    name = replaceAt(name, (name.length)/2,"*");
    // slice補充的字元
    return (name.endsWith('#')) ? name.slice(0,-1) : name;
}
function maskName_1(name,name2) {
    //console.log('maske name ----->',name,name2);
    var isChin = isChn(name);
    
    if(typeof name == 'undefined' || name.length == 0) return '';

    if(infoData.name.length > name.length ){//刪除字元時
        infoData.name = infoData.name.substring(0,name.length);
    }else{
        infoData.name += name.substring( name.length-1 , name.length);
    }
    var ret ='';
    if(name.length === 1){
        ret = name.substring(0,1);
        //console.log('1個字',name, ret);
    }else if(name.length === 2){
        ret = name.substring(0,1) + '*';
        //console.log('2個字',name, ret);
    }else if(name.length >= 3){
        var star ='';
        for(var i=2; i < name.length; i++){
            star += '*';
        }
        ret = name.substring(0,1) + star + name.substring(name.length-1,name.length);
        //console.log('大於3個字',name,ret,star);
    }
    return {
        real: infoData.name,
        mask: ret
    }
}

/**
 * 規則：遮中間4碼(4-7)
 * @param value
 * @returns {string}
 */
function maskPhoneNumber_1(value, value2) {
    if(typeof value == 'undefined' || value.length == 0) return '';
    //console.log('phone value--->', value);
    if(infoData.phone.length > value.length ){//刪除字元時
        infoData.phone = infoData.phone.substring(0,value.length);
    }else if(value.length <= 10){
        infoData.phone = value2 + value.substring(value2.length , value.length);
    }

    var ret = '', star = '';

    if(value.length < 4){
        ret = value;
    }else if(value.length >= 4 && value.length <= 7) {
        for(var i = 4 ; i < value.length + 1 ; i++){
            star += '*';
        }
        ret = value.substring(0,3) + star;
    }else if(value.length > 7 && value.length <= 10){
        ret = value.substring(0,3) + '****' + value.substring(7,value.length);
    }else{
        ret = value.substring(0,3) + '****' + value.substring(7,10);
    }
    //console.log('info data ----phone>', infoData.phone, ret);
    return {
        real: infoData.phone,
        mask: ret
    }
}

/**
 * 規則：遮後4碼(7-10)
 * @param value
 * @returns {string}
 */
function maskPhoneNumberOrUid_1(value,value2) {
    //console.log('uid  onchange--->', value, value2);
    if(typeof value == 'undefined' || value.length == 0) return '';

    if(value.length < 7 && value.length > infoData.uid.length) {
        infoData.uid = value2 + value.substring( value2.length , value.length);
        return {
            real: value,
            mask: value
        };
    }else{
        var star = '';
        for( var i=7 ; i < value.length+1 ; i++){
            star += '*';
        }
        if(infoData.uid.length > value.length ){//刪除字元時
            infoData.uid = infoData.uid.substring(0,value.length);
        }else {
            infoData.uid = value2 + value.substring(value2.length , value.length);
        }
        //console.log('infoData----------uid------------->', infoData.uid);
        return {
            real: infoData.uid,
            mask: value.substring(0,6) + star
        };
    }
}

/**
 * 規則:遮 @ 前面的4碼。若 @ 前只有 2~4個字，則只顯示第一個字
 * @param email
 * @returns {string}
 */
function maskEmailAddress(email) {
	if(typeof email == 'undefined' || email == null) return '';

	if(email.indexOf('@') < 0) {
	    return email;
    }
	
    var cs = email.split('@');
    var preStr = cs[0];
    if(preStr.length < 5){
        for(var i = 1; i < preStr.length ;i++){
            preStr = replaceAt(preStr,i,'*');
        }
    }else {
        preStr = preStr.substring(0, preStr.length-4) + '****';
    }
    return preStr + '@' + cs[1];
}

/**
 * 規則:遮 @ 前面的4碼。若 @ 前只有 2~4個字，則只顯示第一個字
 * @param email
 * @returns {string}
 */
function maskEmailAddress_1(email) {
    //console.log('email---------->',email)
    if(infoData.email.length > email.length ){//刪除字元時
        infoData.email = infoData.email.substring(0,email.length);
    }else{
        infoData.email += email.substring( email.length-1 , email.length);
    }
    // infoData.email += email.substring(email.length-1, email.length);
    if(typeof email == 'undefined' || email == null) return '';

    var pre = email;
    if(email.indexOf('@') < 0) {
        if(email.length < 5){
            for(var i = 1; i < email.length ;i++){
                pre = replaceAt_1(pre,i,'*');
            }
        }else{
            pre = infoData.email.substring(0, infoData.email.length-4) + '****';
        }
        //console.log('pre------>', pre, pre.length);
        return {
            real: infoData.email,
            mask: pre
        };
    }

    var cs = email.split('@');
    var preStr = cs[0];
    //console.log('Email string --->', cs, preStr);
    if(preStr.length < 5){
        for(var i = 1; i < preStr.length ;i++){
            preStr = replaceAt_1(preStr,i,'*');
        }
    }else {
        preStr = preStr.substring(0, preStr.length-4) + '****';
    }
    // return preStr + '@' + cs[1];
    return {
        real: infoData.email,
        mask: preStr + '@' + cs[1]
    }
}

/**
 *
 * @param bd
 */
function maskBirthdate_1(bd,bd2) {
    if(typeof bd == 'undefined' || bd == null) return '';
    if(infoData.birthdate.length > bd.length ){//刪除字元時
        infoData.birthdate = infoData.birthdate.substring(0,bd.length);
    }else if(bd.length <= 7){
        infoData.birthdate = bd2 + bd.substring( bd2.length , bd.length);
    }
    var ret = bd;
    for(var i = 0 ; i < bd.length ; i++){
        var tmp;
        if(i%2 === 0){
            tmp = replaceAt_1(ret, i,'*');
        }
        ret = tmp;
    }
    return {
        real: infoData.birthdate,
        mask: ret
    }
}

/**
 * 健保卡12碼
 * 規則：遮5-10碼(留前4後2)
 * @param value
 * @returns {string}
 */
function maskNHINumber_1(value,value2) {
    // console.log('NHI value--->', value);
    if(typeof value == 'undefined' || value.length == 0) return '';

    if(infoData.NHI.length > value.length ){//刪除字元時
        infoData.NHI = infoData.NHI.substring(0,value.length);
    }else if(value.length <= 12){
        infoData.NHI = value2 + value.substring( value2.length , value.length);
    }

    var ret = '', star = '';
    if(value.length < 5){
        ret = value;
    }else if(value.length >= 5 && value.length <= 10) {
        for(var i = 5 ; i < value.length + 1 ; i++){
            star += '*';
        }
        ret = value.substring(0,4) + star;
    }else if(value.length > 10 && value.length <= 12){
        ret = value.substring(0,4) + '******' + value.substring(10,value.length);
    }else{
        ret = value.substring(0,4) + '******' + value.substring(10,12);
    }
    // console.log('info data ----NHI>', infoData.NHI, ret);
    return {
        real: infoData.NHI,
        mask: ret
    }
}

/**
 * 戶號8碼
 * 規則：遮中間4碼----> 遮2-7碼(留前1後1)
 * @param value
 * @returns {string}
 */
function maskDomicile_1(value, value2) {
    // console.log('domicile value--->', value,value2);
    if (typeof value == 'undefined' || value.length == 0) return '';

    if (infoData.domicile.length > value.length) {//刪除字元時
        infoData.domicile = infoData.domicile.substring(0, value.length);
    } else if (value.length <= 8) {
        infoData.domicile = value2 + value.substring(value2.length, value.length);
    }

    var ret = '', star = '';
    if (value.length < 2) {
        ret = value;
    } else if (value.length >= 2 && value.length <= 7) {
        for (var i = 2; i < value.length + 1; i++) {
            star += '*';
        }
        ret = value.substring(0, 1) + star;
    } else if (value.length > 7 && value.length <= 8) {
        ret = value.substring(0, 1) + '******' + value.substring(7, value.length);
    } else {
        ret = value.substring(0, 1) + '******' + value.substring(7, 8);
    }
    // console.log('info data ----domicile>', infoData.domicile, ret);
    return {
        real: infoData.domicile,
        mask: ret
    }
}
/**
 * 在第{index}位置以{replace}取代原有char
 * @param string
 * @param index
 * @param replace
 * @returns {*}
 */
function replaceAt(string, index, replace) {
    return string.substring(0, index) + replace + string.substring(index + 1);
}