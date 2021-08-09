/* Rilock */
(function($){
	
	/**
	 * 鎖定，可以帶入不同的字串來識別不同的鎖定 function.
	 */
	$.Rilock = {
		// lock function.
		lock : function(key) {
			key = this.akey(key);
			$.data(document.body, key, 'lock');
		},
		// unlock function.
		unlock : function(key) {
			key = this.akey(key);
			$.data(document.body, key, '');
		},
		// detect key is on list or not.
		locked : function(key) {
			key = this.akey(key);
			var locked = $.data(document.body, key);
			return 'lock' === locked;
		},
		pass : function(key) {
			key = this.akey(key);
			var locked = $.data(document.body, key);
			return !(locked) || '' === locked;
		},
		akey : function(key) {
			return 'rilock_' + key;
		}
	};
	
})(jQuery);

/* jQuery addition method */
jQuery.fn.serializeJSONObject = function()
{
    var o = {};
    var a = this.serializeArray();
    var $this = this;
    $.each(a, function() {
    	if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
        	if($this.find('[type="checkbox"][name="' + this.name + '"]').length > 1) {
        		o[this.name] = [this.value];
        	}else {        		
        		o[this.name] = this.value || '';
        	}
        }
    });
    return o;
};

/**
 * 回傳的物件中，只保留最上層的 key name，下面的 array or object 皆會被轉為 JSON string.
 * */
jQuery.fn.form2jsonChildWithString = function()
{
	var p = this.serializeJSONObject();
    return $.each(p,function(key,obj){
		if($.type(obj) == 'array' || $.type(obj) == 'object') {
			p[key] = JSON.stringify(obj);
		}
	});
};

/**
 * simple use
 * */
jQuery.fn.form2json = function()
{
	return this.serializeJSONObject();
};

/**
 * 只針對底下一層的子 element 作組合
 * */
jQuery.fn.elt2json = function() {
	var o = {};
	$(this).children('input').each(function(){
		
		var t = $(this),key=t.attr('name'),val=t.val();
		
		 if (o[key] !== undefined) {
			 if (!o[key].push) {
				 o[key] = [o[key]];
			 }
			 o[key].push(val || '');
		 }else {
			 o[key] = val || '';
		 }
		 
	});
    return o;
};


/*
* File:        jquery.loadJSON.js
* Version:     1.0.0.
* Author:      Jovan Popovic 
* 
* Copyright 2011 Jovan Popovic, all rights reserved.
*
* This source file is free software, under either the GPL v2 license or a
* BSD style license, as supplied with this software.
* 
* This source file is distributed in the hope that it will be useful, but 
* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
* or FITNESS FOR A PARTICULAR PURPOSE. 
*
* This file contains implementation of the JQuery templating engine that load JSON
* objects into the HTML code. It is based on Alexandre Caprais notemplate plugin 
* with several enchancements that are added to this plugin.
*/

(function ($) {
    $.fn.loadJSON = function (obj, options) {

        function setElementValue(element, value, name) {
        	
            var type = element.type || element.tagName;
            if (type == null)
                return;
            type = type.toLowerCase();
            switch (type) {
            
                case 'radio':
                    if (value.toString().toLowerCase() == element.value.toLowerCase())
                        $(element).prop("checked", true);
                    else {
                    	$(element).prop("checked", false);
                    }
                    break;

                case 'checkbox':
                	if(value.constructor == String) {
                		if (value) {
                        	$(element).prop("checked", true);
                        }else {                    	
                        	$(element).prop("checked", false);
                        }
                	}else if(value.constructor == Array) {
                		$(element).prop("checked", $.inArray($(element).val(), value) != -1);
                	}
                    break;

                case 'select-multiple':
                    var values = value.constructor == Array ? value : [value];
                    for (var i = 0; i < element.options.length; i++) {
                        for (var j = 0; j < values.length; j++) {
                            element.options[i].selected |= element.options[i].value == values[j];
                        }
                    }
                    break;

                case 'select':
                case 'select-one':
                	$(element).val(value);
                	break;
                case 'text':
                case 'hidden':
                    $(element).attr("value", value);
                    break;
                case 'a':
                    var href = $(element).attr("href");
                    var iPosition = href.indexOf('?');
                    if (iPosition > 0) // if parameters in the URL exists add new pair using &
                        href = href.substring(0, iPosition) + '?' + name + '=' + value;
                    else//otherwise attach pair to URL
                        href = href + '?' + name + '=' + value;
                    $(element).attr("href", href);
                    break;
                case 'img': //Assumption is that value is in the HREF$ALT format
                    var iPosition = value.indexOf('$');
                    var src = "";
                    var alt = "";
                    if (iPosition > 0) {
                        src = value.substring(0, iPosition);
                        alt = value.substring(iPosition + 1);
                    }
                    else {
                        src = value;
                        var iPositionStart = value.lastIndexOf('/')+1;
                        var iPositionEnd = value.indexOf('.');
                        alt = value.substring(iPositionStart, iPositionEnd);
                    }
                    $(element).attr("src", src);
                    $(element).attr("alt", alt);
                    break;

                case 'textarea':
                case 'submit':
                case 'button':
                default:
                    try {
                        $(element).html(value);
                    } catch (exc) { }
            }

        }

        function browseJSON(obj, element, name) {

            // no object
            if (obj == undefined) {
            }
            // branch
            else if (obj.constructor == Object) {
                for (var prop in obj) {
                    if (prop == null)
                        continue;
                    //Find an element with class, id, name, or rel attribute that matches the propertu name
                    var child = jQuery.makeArray(
                    		jQuery("." + prop, element)).length > 0 ? jQuery("." + prop, element) :
                    			jQuery("#" + prop, element).length > 0 ? jQuery("#" + prop, element) :
                    				jQuery('[name="' + prop + '"]', element).length > 0 ? jQuery('[name="' + prop + '"]', element) : 
                    					jQuery('[rel="' + prop + '"]');
                    if (child.length != 0) {
                    	browseJSON(obj[prop], jQuery(child, element), prop);
                    }
                }
            }
            // array
            else if (obj.constructor == Array) {
                if (element.length > 0 && element[0].tagName == "SELECT") {
                    setElementValue(element[0], obj, name);
                }else if (element.length > 0 && element[0].tagName == "INPUT" && element[0].type.toLowerCase() == 'checkbox') {
                	for(var i=0;i<element.length;i++) {
                		setElementValue(element[i], obj, name);
                	}
                } else {
                    var arr = jQuery.makeArray(element);
                    //how many duplicate
                    var nbToCreate = obj.length - arr.length;
                    var i = 0;
                    for (iExist = 0; iExist < arr.length; iExist++) {
                        if (i < obj.length) {
                            $(element).eq(iExist).loadJSON(obj[i]);
                        }
                        i++;
                    }
                    //fill started by last
                    i = obj.length - 1;
                    for (iCreate = 0; iCreate < nbToCreate; iCreate++) {
                        //duplicate the last
                        $(arr[arr.length - 1]).clone(true).insertAfter(arr[arr.length - 1]).loadJSON(obj[i]);
                        i--;
                    }
                }
            }
            // data only
            else {
//                var value = obj;
//                var type;
                if (element.length > 0) {
                    for (i = 0; i < element.length; i++) {
                    	setElementValue(element[i], obj, name);
                    }
                }
                else {
                    setElementValue(element, obj, name);
                }
            }
        } //function browseJSON end

        var defaults = {
        	resetBefore : false
        };

        properties = $.extend(defaults, options);
        if(properties.resetBefore) {
        	if(this.prop("tagName").toLowerCase() == 'form') {
        		this.trigger('reset');
        	}
        }

        return this.each(function () {
            if (obj.constructor == String) {
                var element = $(this);
                $.get(obj, function (data) {
                    element.loadJSON(data);
                });
            }
            else {
                browseJSON(obj, this);
            }
        });
    };
})(jQuery);

/*
Watermark v3.1.4 (August 13, 2012) plugin for jQuery
http://jquery-watermark.googlecode.com/
Copyright (c) 2009-2012 Todd Northrop
http://www.speednet.biz/
Dual licensed under the MIT or GPL Version 2 licenses.
*/
(function(n,t,i){var g="TEXTAREA",d="function",nt="password",c="maxLength",v="type",r="",u=!0,rt="placeholder",h=!1,tt="watermark",s=tt,o="watermarkClass",w="watermarkFocus",a="watermarkSubmit",b="watermarkMaxLength",e="watermarkPassword",f="watermarkText",l=/\r/g,ft=/^(button|checkbox|hidden|image|radio|range|reset|submit)$/i,it="input:data("+s+"),textarea:data("+s+")",p=":watermarkable",k=["Page_ClientValidate"],y=h,ut=rt in document.createElement("input");n.watermark=n.watermark||{version:"3.1.4",runOnce:u,options:{className:tt,useNative:u,hideBeforeUnload:u},hide:function(t){n(t).filter(it).each(function(){n.watermark._hide(n(this))})},_hide:function(n,i){var a=n[0],w=(a.value||r).replace(l,r),h=n.data(f)||r,p=n.data(b)||0,y=n.data(o),s,u;h.length&&w==h&&(a.value=r,n.data(e)&&(n.attr(v)||r)==="text"&&(s=n.data(e)||[],u=n.parent()||[],s.length&&u.length&&(u[0].removeChild(n[0]),u[0].appendChild(s[0]),n=s)),p&&(n.attr(c,p),n.removeData(b)),i&&(n.attr("autocomplete","off"),t.setTimeout(function(){n.select()},1))),y&&n.removeClass(y)},show:function(t){n(t).filter(it).each(function(){n.watermark._show(n(this))})},_show:function(t){var p=t[0],g=(p.value||r).replace(l,r),i=t.data(f)||r,k=t.attr(v)||r,d=t.data(o),h,s,a;g.length!=0&&g!=i||t.data(w)?n.watermark._hide(t):(y=u,t.data(e)&&k===nt&&(h=t.data(e)||[],s=t.parent()||[],h.length&&s.length&&(s[0].removeChild(t[0]),s[0].appendChild(h[0]),t=h,t.attr(c,i.length),p=t[0])),(k==="text"||k==="search")&&(a=t.attr(c)||0,a>0&&i.length>a&&(t.data(b,a),t.attr(c,i.length))),d&&t.addClass(d),p.value=i)},hideAll:function(){y&&(n.watermark.hide(p),y=h)},showAll:function(){n.watermark.show(p)}},n.fn.watermark=n.fn.watermark||function(i,y){var tt="string";if(!this.length)return this;var k=h,b=typeof i==tt;return b&&(i=i.replace(l,r)),typeof y=="object"?(k=typeof y.className==tt,y=n.extend({},n.watermark.options,y)):typeof y==tt?(k=u,y=n.extend({},n.watermark.options,{className:y})):y=n.watermark.options,typeof y.useNative!=d&&(y.useNative=y.useNative?function(){return u}:function(){return h}),this.each(function(){var et="dragleave",ot="dragenter",ft=this,h=n(ft),st,d,tt,it;if(h.is(p)){if(h.data(s))(b||k)&&(n.watermark._hide(h),b&&h.data(f,i),k&&h.data(o,y.className));else{if(ut&&y.useNative.call(ft,h)&&(h.attr("tagName")||r)!==g){b&&h.attr(rt,i);return}h.data(f,b?i:r),h.data(o,y.className),h.data(s,1),(h.attr(v)||r)===nt?(st=h.wrap("<span>").parent(),d=n(st.html().replace(/type=["']?password["']?/i,'type="text"')),d.data(f,h.data(f)),d.data(o,h.data(o)),d.data(s,1),d.attr(c,i.length),d.focus(function(){n.watermark._hide(d,u)}).bind(ot,function(){n.watermark._hide(d)}).bind("dragend",function(){t.setTimeout(function(){d.blur()},1)}),h.blur(function(){n.watermark._show(h)}).bind(et,function(){n.watermark._show(h)}),d.data(e,h),h.data(e,d)):h.focus(function(){h.data(w,1),n.watermark._hide(h,u)}).blur(function(){h.data(w,0),n.watermark._show(h)}).bind(ot,function(){n.watermark._hide(h)}).bind(et,function(){n.watermark._show(h)}).bind("dragend",function(){t.setTimeout(function(){n.watermark._show(h)},1)}).bind("drop",function(n){var i=h[0],t=n.originalEvent.dataTransfer.getData("Text");(i.value||r).replace(l,r).replace(t,r)===h.data(f)&&(i.value=t),h.focus()}),ft.form&&(tt=ft.form,it=n(tt),it.data(a)||(it.submit(n.watermark.hideAll),tt.submit?(it.data(a,tt.submit),tt.submit=function(t,i){return function(){var r=i.data(a);n.watermark.hideAll(),r.apply?r.apply(t,Array.prototype.slice.call(arguments)):r()}}(tt,it)):(it.data(a,1),tt.submit=function(t){return function(){n.watermark.hideAll(),delete t.submit,t.submit()}}(tt))))}n.watermark._show(h)}})},n.watermark.runOnce&&(n.watermark.runOnce=h,n.extend(n.expr[":"],{data:n.expr.createPseudo?n.expr.createPseudo(function(t){return function(i){return!!n.data(i,t)}}):function(t,i,r){return!!n.data(t,r[3])},watermarkable:function(n){var t,i=n.nodeName;return i===g?u:i!=="INPUT"?h:(t=n.getAttribute(v),!t||!ft.test(t))}}),function(t){n.fn.val=function(){var u=this,e=Array.prototype.slice.call(arguments),o;return u.length?e.length?(t.apply(u,e),n.watermark.show(u),u):u.data(s)?(o=(u[0].value||r).replace(l,r),o===(u.data(f)||r)?r:o):t.apply(u):e.length?u:i}}(n.fn.val),k.length&&n(function(){for(var u,r,i=k.length-1;i>=0;i--)u=k[i],r=t[u],typeof r==d&&(t[u]=function(t){return function(){return n.watermark.hideAll(),t.apply(null,Array.prototype.slice.call(arguments))}}(r))}),n(t).bind("beforeunload",function(){n.watermark.options.hideBeforeUnload&&n.watermark.hideAll()}))})(jQuery,window);

/*
 * Lazy Load - jQuery plugin for lazy loading images
 *
 * Copyright (c) 2007-2013 Mika Tuupola
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Project home:
 *   http://www.appelsiini.net/projects/lazyload
 *
 * Version:  1.9.3
 *
 */

(function($, window, document, undefined) {
    var $window = $(window);

    $.fn.lazyload = function(options) {
        var elements = this;
        var $container;
        var settings = {
            threshold       : 0,
            failure_limit   : 0,
            event           : "scroll",
            effect          : "show",
            container       : window,
            data_attribute  : "original",
            skip_invisible  : true,
            appear          : null,
            load            : null,
            placeholder     : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAANSURBVBhXYzh8+PB/AAffA0nNPuCLAAAAAElFTkSuQmCC"
        };

        function update() {
            var counter = 0;

            elements.each(function() {
                var $this = $(this);
                if (settings.skip_invisible && !$this.is(":visible")) {
                    return;
                }
                if ($.abovethetop(this, settings) ||
                    $.leftofbegin(this, settings)) {
                        /* Nothing. */
                } else if (!$.belowthefold(this, settings) &&
                    !$.rightoffold(this, settings)) {
                        $this.trigger("appear");
                        /* if we found an image we'll load, reset the counter */
                        counter = 0;
                } else {
                    if (++counter > settings.failure_limit) {
                        return false;
                    }
                }
            });

        }

        if(options) {
            /* Maintain BC for a couple of versions. */
            if (undefined !== options.failurelimit) {
                options.failure_limit = options.failurelimit;
                delete options.failurelimit;
            }
            if (undefined !== options.effectspeed) {
                options.effect_speed = options.effectspeed;
                delete options.effectspeed;
            }

            $.extend(settings, options);
        }

        /* Cache container as jQuery as object. */
        $container = (settings.container === undefined ||
                      settings.container === window) ? $window : $(settings.container);

        /* Fire one scroll event per scroll. Not one scroll event per image. */
        if (0 === settings.event.indexOf("scroll")) {
            $container.bind(settings.event, function() {
                return update();
            });
        }

        this.each(function() {
            var self = this;
            var $self = $(self);

            self.loaded = false;

            /* If no src attribute given use data:uri. */
            if ($self.attr("src") === undefined || $self.attr("src") === false) {
                if ($self.is("img")) {
                    $self.attr("src", settings.placeholder);
                }
            }

            /* When appear is triggered load original image. */
            $self.one("appear", function() {
                if (!this.loaded) {
                    if (settings.appear) {
                        var elements_left = elements.length;
                        settings.appear.call(self, elements_left, settings);
                    }
                    $("<img />")
                        .bind("load", function() {

                            var original = $self.attr("data-" + settings.data_attribute);
                            $self.hide();
                            if ($self.is("img")) {
                                $self.attr("src", original);
                            } else {
                                $self.css("background-image", "url('" + original + "')");
                            }
                            $self[settings.effect](settings.effect_speed);

                            self.loaded = true;

                            /* Remove image from array so it is not looped next time. */
                            var temp = $.grep(elements, function(element) {
                                return !element.loaded;
                            });
                            elements = $(temp);

                            if (settings.load) {
                                var elements_left = elements.length;
                                settings.load.call(self, elements_left, settings);
                            }
                        })
                        .attr("src", $self.attr("data-" + settings.data_attribute));
                }
            });

            /* When wanted event is triggered load original image */
            /* by triggering appear.                              */
            if (0 !== settings.event.indexOf("scroll")) {
                $self.bind(settings.event, function() {
                    if (!self.loaded) {
                        $self.trigger("appear");
                    }
                });
            }
        });

        /* Check if something appears when window is resized. */
        $window.bind("resize", function() {
            update();
        });

        /* With IOS5 force loading images when navigating with back button. */
        /* Non optimal workaround. */
        if ((/(?:iphone|ipod|ipad).*os 5/gi).test(navigator.appVersion)) {
            $window.bind("pageshow", function(event) {
                if (event.originalEvent && event.originalEvent.persisted) {
                    elements.each(function() {
                        $(this).trigger("appear");
                    });
                }
            });
        }

        /* Force initial check if images should appear. */
        $(document).ready(function() {
            update();
        });

        return this;
    };

    /* Convenience methods in jQuery namespace.           */
    /* Use as  $.belowthefold(element, {threshold : 100, container : window}) */

    $.belowthefold = function(element, settings) {
        var fold;

        if (settings.container === undefined || settings.container === window) {
            fold = (window.innerHeight ? window.innerHeight : $window.height()) + $window.scrollTop();
        } else {
            fold = $(settings.container).offset().top + $(settings.container).height();
        }

        return fold <= $(element).offset().top - settings.threshold;
    };

    $.rightoffold = function(element, settings) {
        var fold;

        if (settings.container === undefined || settings.container === window) {
            fold = $window.width() + $window.scrollLeft();
        } else {
            fold = $(settings.container).offset().left + $(settings.container).width();
        }

        return fold <= $(element).offset().left - settings.threshold;
    };

    $.abovethetop = function(element, settings) {
        var fold;

        if (settings.container === undefined || settings.container === window) {
            fold = $window.scrollTop();
        } else {
            fold = $(settings.container).offset().top;
        }

        return fold >= $(element).offset().top + settings.threshold  + $(element).height();
    };

    $.leftofbegin = function(element, settings) {
        var fold;

        if (settings.container === undefined || settings.container === window) {
            fold = $window.scrollLeft();
        } else {
            fold = $(settings.container).offset().left;
        }

        return fold >= $(element).offset().left + settings.threshold + $(element).width();
    };

    $.inviewport = function(element, settings) {
         return !$.rightoffold(element, settings) && !$.leftofbegin(element, settings) &&
                !$.belowthefold(element, settings) && !$.abovethetop(element, settings);
     };

    /* Custom selectors for your convenience.   */
    /* Use as $("img:below-the-fold").something() or */
    /* $("img").filter(":below-the-fold").something() which is faster */

    $.extend($.expr[":"], {
        "below-the-fold" : function(a) { return $.belowthefold(a, {threshold : 0}); },
        "above-the-top"  : function(a) { return !$.belowthefold(a, {threshold : 0}); },
        "right-of-screen": function(a) { return $.rightoffold(a, {threshold : 0}); },
        "left-of-screen" : function(a) { return !$.rightoffold(a, {threshold : 0}); },
        "in-viewport"    : function(a) { return $.inviewport(a, {threshold : 0}); },
        /* Maintain BC for couple of versions. */
        "above-the-fold" : function(a) { return !$.belowthefold(a, {threshold : 0}); },
        "right-of-fold"  : function(a) { return $.rightoffold(a, {threshold : 0}); },
        "left-of-fold"   : function(a) { return !$.rightoffold(a, {threshold : 0}); }
    });

})(jQuery, window, document);

/**
 * 用 json 合併到 form 表單中，若是表單內沒有的話，將會新增一個 input[type=hidden] 到表單內.
 * */
function _margeFormField(form, json) {
	var $form = $(form),
		fdata = $form.form2json2();
	for(var d in json) {
		if(fdata[d] == undefined) {
			$('<input>').attr({
				type : 'hidden',
				name : d,
				value : json[d]
			}).appendTo($form);
		}
	}
	return $form;
}

/**
 * String protocol endsWith
 * */
String.prototype.endsWith = function(pattern) {
    var d = this.length - pattern.length;
    return d >= 0 && this.lastIndexOf(pattern) === d;
};
/**
 * String protocol startsWith
 * */
String.prototype.startsWith = function(pattern) {
	return this.indexOf(pattern) == 0;
};

/**
 * date input field, allow keys only is 'backspace', 'enter'
 * */
function _dateInput(target) {
	jQuery(target).keypress(function(e){
		var keycode = e.charCode || e.keyCode;
		if(keycode == 8) {
			this.value = '';
		}
		if(keycode == 8 || keycode == 13)
			return true;
		else
			return false;
	});
}
/**
 * get object from another object by array keys
 * */
function _findObjectByKeys(obj, keys) {
	var a = {};
	for(var i=0;i<keys.length;i++) {
		var key = keys[i];
		a[key] = obj[key];
	}
	return a;
}

/**
 * faster log writer
 * */
function _clog(msg, a, b) {

	try {
		if(a)
			console.log(a);
		console.log(msg);
		if(b)
			console.log(b);
	}catch(ex){}
}

/**
 * pagination common options.
 * */
function _getPagerOptions(arg) {
	return {
		num_display_entries : 8,
		num_edge_entries : 2,
		prev_text : '&laquo; 上一頁',
		prev_show_always : false,
		next_text : '&raquo; 下一頁',
		next_show_always : false,
		link_to : '#',
		hidden_zero_entry : true
	};
}

/**
 * make pagination.
 * */
function _makePagination(total, arg) {

	arg = $.extend({
		contentContainer : $('body'),
		page : {
			pageIndex : 0,
			pageSize : 10
		},
		onClick : function(page,contentContainer){}
	}, arg);

	jQuery(arg.contentContainer).find('#pagination, div.pagination').pagination(total, $.extend(_getPagerOptions(), {
		items_per_page : arg.page.pageSize,
		current_page : arg.page.pageIndex,
		contentContainer : arg.contentContainer,
		callback : function(cpIndex, elt) {

			if(arg.onClick) {

				arg.onClick({
					pageIndex : cpIndex,
					pageSize : this.items_per_page
				}, this.contentContainer);
			}
		}
	}));
}

/**
 * make pagination with form.
 * */
function _makePaginationForm(total, arg) {

	var f = $(arg.form),
		cc = arg.contentContainer || $('body');

	jQuery(cc).find('#pagination, div.pagination').pagination(total, $.extend(_getPagerOptions(), {
		items_per_page : f.find('input[name="psize"]').val(),
		current_page : f.find('input[name="pindex"]').val(),
		contentContainer : cc,
		usedform : arg.form,
		callback : function(cpIndex, elt) {

			$(this.usedform).loadJSON({
				pindex : cpIndex,
				psize : this.items_per_page
			}).submit();

		}
	}));
}

/**
 * remove table rows.
 * #target : table element object.
 * #rowNumber	: remove from row number.
 */
function _removeTable(target, rowNumber) {
	var l = target.rows.length - rowNumber;
	for(var i=0;i<l;i++) {
		target.deleteRow(rowNumber);
	}
}
/**
 * 驗證圖重新刷新
 * id : 該 img object id 值.
 */
function _captchaRefresh(id) {
	var img = document.getElementById(id);
	if(img==null) return;
	var seed = Math.floor(getRandomValues() * 100);
	var s = img.src;
	if(s.indexOf("?") != -1) {
		s = s.substring(0,s.indexOf('?'));
	}
	s += "?" + seed;
	img.src = s;
}

/**
 * @param id
 * @param width
 * @param height
 * @param css
 * @parma align
 * @returns {String} 驗證碼 HTML
 */
function _captchaWriter(id, width, height, css, align) {
	var seed = Math.floor(getRandomValues() * 100);
	var path = _getWebContextPath();
	if(RiAPI != null) {
		path = RiAPI.ctxUrl();
	}
	var s = '<img src="' + path + '/kaptcha.jpg?' + seed + '" ';
	if(id)
		s += ' id="' + id + '"';
	if(width)
		s += ' width="' + width + '"';
	if(height)
		s += ' height="' + height + '"';
	if(css)
		s += ' class="' + css + '"';
	if(align)
		s += ' align="'+align+'"/>';
	else
		s += ' />';
	return s;
}

//update CKEditor element
function _ckEditorUpdate() {
	for (var instance in CKEDITOR.instances )
	    CKEDITOR.instances[instance].updateElement();
}

/**
 * return web APP domain path
 * */
function _getWebContextPath() {
	var p = window.location.pathname;
	if(p.indexOf('/') != -1) {
		var a = p.split("/");
		//修改當Domain Name不存在時
		if(a[1].indexOf('.')!=-1){
			return "";
		}else{
			return "/" + a[1];
		}
	}else {
		return null;
	}
}
/**
 * open a new window
 * */
function _openWinClearBar(theURL,winName,features, myWidth, myHeight) {
	features = 'toolbar=no,scrollbars=no,location=no,resizable=no,menubar=no,status=no,directories=no';
	if(window.screen){
		var myLeft = (screen.width-myWidth)/2;
		var myTop = (screen.height-myHeight)/2;
		features+=(features!='')?',':'';
		features+='left='+myLeft+',top='+myTop;
	}
	var t = window.open(theURL,winName,features+((features!='')?',':'')+'width='+myWidth+',height='+myHeight);
	if(t != null) {
		t.focus();
	}
	return t;
}

/* ============== MM ===================*/
function MM_preloadImages() { // v3.0
	var d = document;
	if (d.images) {
		if (!d.MM_p)
			d.MM_p = new Array();
		var i, j = d.MM_p.length, a = MM_preloadImages.arguments;
		for (i = 0; i < a.length; i++)
			if (a[i].indexOf("#") != 0) {
				d.MM_p[j] = new Image;
				d.MM_p[j++].src = a[i];
			}
	}
}
function MM_swapImgRestore() { // v3.0
	var i, x, a = document.MM_sr;
	for (i = 0; a && i < a.length && (x = a[i]) && x.oSrc; i++)
		x.src = x.oSrc;
}
function MM_findObj(n, d) { // v4.01
	var p, i, x;
	if (!d)
		d = document;
	if ((p = n.indexOf("?")) > 0 && parent.frames.length) {
		d = parent.frames[n.substring(p + 1)].document;
		n = n.substring(0, p);
	}
	if (!(x = d[n]) && d.all)
		x = d.all[n];
	for (i = 0; !x && i < d.forms.length; i++)
		x = d.forms[i][n];
	for (i = 0; !x && d.layers && i < d.layers.length; i++)
		x = MM_findObj(n, d.layers[i].document);
	if (!x && d.getElementById)
		x = d.getElementById(n);
	return x;
}
function MM_swapImage() { // v3.0
	var i, j = 0, x, a = MM_swapImage.arguments;
	document.MM_sr = new Array;
	for (i = 0; i < (a.length - 2); i += 3)
		if ((x = MM_findObj(a[i])) != null) {
			document.MM_sr[j++] = x;
			if (!x.oSrc)
				x.oSrc = x.src;
			x.src = a[i + 2];
		}
}

var spinner,spinnerContainer,spinOverlay = null;
function showLoadSpinner(){
	spinner = new Spinner({
		lines: 13, 	// The number of lines to draw
		length: 20, // The length of each line
		width: 10, 	// The line thickness
		radius: 30, // The radius of the inner circle
		corners: 1, // Corner roundness (0..1)
		rotate: 0, 	// The rotation offset
		direction: 1, // 1: clockwise, -1: counterclockwise
		color: '#FFF', // #rgb or #rrggbb or array of colors
		speed: 1, // Rounds per second
		trail: 60, // Afterglow percentage
		shadow: false, // Whether to render a shadow
		hwaccel: false, // Whether to use hardware acceleration
		className: 'spinner', // The CSS class to assign to the spinner
		zIndex: 2e9, // The z-index (defaults to 2000000000)
		top: '50%', // Top position relative to parent
		left: '50%' // Left position relative to parent
	});

	spinnerContainer = $('<div/>').css({
		'width' : '100%',
		'height' : '100%',
		'position' : 'fixed',
		'top' : 0,
		'left' : 0,
		'z-index' : 9999999
	}).appendTo($('body'));

	spinnerContainer.show();
	spinner.spin(this.spinnerContainer[0]);

	if(spinOverlay == null) {
		spinOverlay = $('<div/>').css({
			'width' : '100%',
			'height' : '100%',
			'background-color' : 'black',
			'position' : 'fixed',
			'top' : 0,
			'left' : 0,
			'z-index' : 999999,
			'opacity' : 0.5,
			'filter' : 'alpha(opacity=50)'
		}).appendTo($('body')).show();
	}else {
		spinOverlay.show();
	}
}

function hideLoadSpinner(){
	setTimeout(function(){
		if(spinner) spinner.stop();
		if(spinOverlay) spinOverlay.hide();
		if(spinnerContainer) spinnerContainer.remove();
		spinner = null;
		spinnerContainer = null;
	}, 200);
}
//fgnass.github.com/spin.js#v2.0.1
!function(a,b){"object"==typeof exports?module.exports=b():"function"==typeof define&&define.amd?define(b):a.Spinner=b()}(this,function(){"use strict";function a(a,b){var c,d=document.createElement(a||"div");for(c in b)d[c]=b[c];return d}function b(a){for(var b=1,c=arguments.length;c>b;b++)a.appendChild(arguments[b]);return a}function c(a,b,c,d){var e=["opacity",b,~~(100*a),c,d].join("-"),f=.01+c/d*100,g=Math.max(1-(1-a)/b*(100-f),a),h=j.substring(0,j.indexOf("Animation")).toLowerCase(),i=h&&"-"+h+"-"||"";return l[e]||(m.insertRule("@"+i+"keyframes "+e+"{0%{opacity:"+g+"}"+f+"%{opacity:"+a+"}"+(f+.01)+"%{opacity:1}"+(f+b)%100+"%{opacity:"+a+"}100%{opacity:"+g+"}}",m.cssRules.length),l[e]=1),e}function d(a,b){var c,d,e=a.style;for(b=b.charAt(0).toUpperCase()+b.slice(1),d=0;d<k.length;d++)if(c=k[d]+b,void 0!==e[c])return c;return void 0!==e[b]?b:void 0}function e(a,b){for(var c in b)a.style[d(a,c)||c]=b[c];return a}function f(a){for(var b=1;b<arguments.length;b++){var c=arguments[b];for(var d in c)void 0===a[d]&&(a[d]=c[d])}return a}function g(a,b){return"string"==typeof a?a:a[b%a.length]}function h(a){this.opts=f(a||{},h.defaults,n)}function i(){function c(b,c){return a("<"+b+' xmlns="urn:schemas-microsoft.com:vml" class="spin-vml">',c)}m.addRule(".spin-vml","behavior:url(#default#VML)"),h.prototype.lines=function(a,d){function f(){return e(c("group",{coordsize:k+" "+k,coordorigin:-j+" "+-j}),{width:k,height:k})}function h(a,h,i){b(m,b(e(f(),{rotation:360/d.lines*a+"deg",left:~~h}),b(e(c("roundrect",{arcsize:d.corners}),{width:j,height:d.width,left:d.radius,top:-d.width>>1,filter:i}),c("fill",{color:g(d.color,a),opacity:d.opacity}),c("stroke",{opacity:0}))))}var i,j=d.length+d.width,k=2*j,l=2*-(d.width+d.length)+"px",m=e(f(),{position:"absolute",top:l,left:l});if(d.shadow)for(i=1;i<=d.lines;i++)h(i,-2,"progid:DXImageTransform.Microsoft.Blur(pixelradius=2,makeshadow=1,shadowopacity=.3)");for(i=1;i<=d.lines;i++)h(i);return b(a,m)},h.prototype.opacity=function(a,b,c,d){var e=a.firstChild;d=d.shadow&&d.lines||0,e&&b+d<e.childNodes.length&&(e=e.childNodes[b+d],e=e&&e.firstChild,e=e&&e.firstChild,e&&(e.opacity=c))}}var j,k=["webkit","Moz","ms","O"],l={},m=function(){var c=a("style",{type:"text/css"});return b(document.getElementsByTagName("head")[0],c),c.sheet||c.styleSheet}(),n={lines:12,length:7,width:5,radius:10,rotate:0,corners:1,color:"#000",direction:1,speed:1,trail:100,opacity:.25,fps:20,zIndex:2e9,className:"spinner",top:"50%",left:"50%",position:"absolute"};h.defaults={},f(h.prototype,{spin:function(b){this.stop();{var c=this,d=c.opts,f=c.el=e(a(0,{className:d.className}),{position:d.position,width:0,zIndex:d.zIndex});d.radius+d.length+d.width}if(e(f,{left:d.left,top:d.top}),b&&b.insertBefore(f,b.firstChild||null),f.setAttribute("role","progressbar"),c.lines(f,c.opts),!j){var g,h=0,i=(d.lines-1)*(1-d.direction)/2,k=d.fps,l=k/d.speed,m=(1-d.opacity)/(l*d.trail/100),n=l/d.lines;!function o(){h++;for(var a=0;a<d.lines;a++)g=Math.max(1-(h+(d.lines-a)*n)%l*m,d.opacity),c.opacity(f,a*d.direction+i,g,d);c.timeout=c.el&&setTimeout(o,~~(1e3/k))}()}return c},stop:function(){var a=this.el;return a&&(clearTimeout(this.timeout),a.parentNode&&a.parentNode.removeChild(a),this.el=void 0),this},lines:function(d,f){function h(b,c){return e(a(),{position:"absolute",width:f.length+f.width+"px",height:f.width+"px",background:b,boxShadow:c,transformOrigin:"left",transform:"rotate("+~~(360/f.lines*k+f.rotate)+"deg) translate("+f.radius+"px,0)",borderRadius:(f.corners*f.width>>1)+"px"})}for(var i,k=0,l=(f.lines-1)*(1-f.direction)/2;k<f.lines;k++)i=e(a(),{position:"absolute",top:1+~(f.width/2)+"px",transform:f.hwaccel?"translate3d(0,0,0)":"",opacity:f.opacity,animation:j&&c(f.opacity,f.trail,l+k*f.direction,f.lines)+" "+1/f.speed+"s linear infinite"}),f.shadow&&b(i,e(h("#000000","0 0 4px #000"),{top:"2px"})),b(d,b(i,h(g(f.color,k),"0 0 1px rgba(0,0,0,.1)")));return d},opacity:function(a,b,c){b<a.childNodes.length&&(a.childNodes[b].style.opacity=c)}});var o=e(a("group"),{behavior:"url(#default#VML)"});return!d(o,"transform")&&o.adj?i():j=d(o,"animation"),h});


/**
 *  utils below develop by john
 */

// 宣告enum
//const MaskType = Object.freeze({"name":0, "phone" : 1, "uid":2, "email" : 3, "birthdate" : 4});

/*
* 遮罩機敏資料，姓名、手機號碼、身分證字號、電子信箱、生日
* type 姓名(0) , 手機號碼(1),身分證字號(1), 電子信箱(2), 生日(3)
* ps. 暫時不遮罩生日
* */
function maskSenstiveInformation(type,value) {
    switch (type) {
        case MaskType.name:
            return maskName(value);
        case MaskType.phone:
        case MaskType.uid:
            return maskPhoneNumberOrUid(value);
        case MaskType.email:
            return maskEmailAddress(value);
        // case 3:
        //     return maskBirthdate(value);
    }
}

function maskSenstiveInformation_v2(type,value) {
    switch (type) {
        case MaskType.name:
            return maskName(value);
        case MaskType.phone:
            return maskPhoneNumber(value);
        case MaskType.uid:
            return maskPhoneNumberOrUid(value);
        case MaskType.email:
            return maskEmailAddress(value);
        case MaskType.birthdate:
            return maskBirthdate(value);
    }
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

/**
 * 規則：遮後4碼
 * @param value
 * @returns {string}
 */
function maskPhoneNumberOrUid(value) {
    if(typeof value == 'undefined' || value.length == 0) return '';

    if(value.length < 5) {
        return value.substring(0,value.length-1) + '*';
    }

    return value.substring(0,value.length-4) + '****';
}

/**
 * 規則：遮中間4碼
 * @param value
 * @returns {string}
 */
function maskPhoneNumber(value) {
    if(typeof value == 'undefined' || value.length == 0) return '';

    if(value.length != 10) {
        return replaceAt(value, (value.length)/2,"*");
    }
	var ret = '';
	ret = value.substring(0,3) + '****' + value.substring(7,10);
    return ret;
}

/**
 * 規則：遮後4碼
 * @param value
 * @returns {string}
 */
function maskUid(value) {
    if(typeof value == 'undefined' || value.length == 0) return '';
    if(value.length < 5) {
        return value.substring(0,value.length-1) + '*';
    }

	return value.substring(0,value.length-4) + '****';
}

/**
 * 目前不遮罩生日
 * @param bd
 */
function maskBirthdate(bd) {
    if(typeof bd == 'undefined' || bd == null) return '';
    if(bd.length < 7) {
        return bd.substring(0,bd.length-1) + '*';
    }

    var tmp1 = replaceAt(bd, 1, '*');
    var tmp2 = replaceAt(tmp1, 3, '*');
    return replaceAt(tmp2, 5, '*');
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
 * 在第{index}位置以{replace}取代原有char
 * @param string
 * @param index
 * @param replace
 * @returns {*}
 */
function replaceAt(string, index, replace) {
    return string.substring(0, index) + replace + string.substring(index + 1);
}


/**
 * 檢核身分證字號
 * @param id
 * @returns {boolean}
 */
function verifyUidExpression(uid){
    return /^[A-Z][1|2]\d{8}$/.test(uid);
}

function verifyResidentUidExpression(uid){
    return /^[A-Z]{2}\d{8}$/.test(uid);
}

function verifyNewResidentUidExpression(uid){
    return /^[A-Z][8|9]\d{8}$/.test(uid);
}

function verifyExpression(uid) {
    return verifyUidExpression(uid) || verifyResidentUidExpression(uid) || verifyNewResidentUidExpression(uid);
}



/**
 * 檢核生日
 * @param bd
 * @returns {boolean}
 */
function verifyBirthDateExpression(bd) {
	var check = false;
	if(bd.length==8){
		check = /^((19|20)[0-9]{2})(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$/.test(bd);
	}else if(bd.length==7){
		check = /^([0-9]{3})(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$/.test(bd);
	}else if(bd.length==6){
		check = /^([0-9]{2})(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$/.test(bd);
	}
    return check;
}
/**
 * 西元為西元，民國轉西元
 * @param bd
 * @returns
 */
function transferBirthDateToAD(bd) {
	let ret = '';
	if(typeof bd == 'undefined') {
		return ret;
	}
	if(bd.length==8){
		ret = bd;
	}else if(bd.length==7){
		let yearStr = bd.substring(0,3);
		let monthStr = bd.substring(3,5);
		let dayStr = bd.substring(5,7);
		yearStr = String(parseInt(yearStr,10)+1911);
		ret = yearStr+monthStr+dayStr;
	}else if(bd.length==6){
		let yearStr = bd.substring(0,2);
		let monthStr = bd.substring(2,4);
		let dayStr = bd.substring(4,6);
		yearStr = String(parseInt(yearStr,10)+1911);
		ret = yearStr+monthStr+dayStr;
	}
	return ret;
}
/**
 * 檢核email
 * @param email
 * @returns {boolean}
 */
function verfiyEmailExpression(email) {
	if(typeof email == 'undefined' || email.length == 0) {
		return false;
	}
	
    return /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z]+$/.test(email);
}

/**
 * 檢核手機號碼
 * @param mobile
 * @returns {boolean}
 */
function verifyMobileExpression(mobile) {
	if(typeof mobile == 'undefined' || mobile.length == 0) {
		return false;
	}
	
    return /^09\d{8}$/.test(mobile);
}


// --------------- download,multiple-download 等頁面遮罩功能 ---------------
/**
 * @param isSingle 是否為單筆下載 true(download), false(multiple-download)
 * @param prId 若 isSingle=true，則需要prId判斷遮罩哪個prId之input欄位
 */

function maskUidAfterVerify(isSingle,prId) {
    let id = isSingle ? '#uid' + prId : '#uid';
    let tmpUid = $(id).val();
    $(id).val(maskUid(tmpUid));
    $(id).data('value',tmpUid);
}

function maskNameAfterVerify(isSingle,prId) {
    let name = isSingle ? '#name' + prId : '#name';
    if(name === undefined || name.length === 0 ){
        return
    }
    let tmpName = $(name).val();
    $(name).val(maskName(tmpName));
    $(name).data('value',tmpName);
}
function maskEmailAfterVerify(isSingle,prId) {
    let email = isSingle ? '#email' + prId : '#email';
    let tmpEmail = $(email).val();
    if(typeof tmpEmail != 'undefined' && tmpEmail.length > 0) {
    	$(email).val(maskEmailAddress(tmpEmail));
        $(email).data('value',tmpEmail);
    }
}

/**
 * 取得input欄位未遮罩的值
 */
function getUnmaskValue($data) {
    if(typeof $($data).val() === 'undefined') return '';
    return ($($data).val().indexOf('*') > -1||$($data).val().length == 0) ? $($data).attr('data-value') : $($data).val();
}

/**
 * 遮罩/解除遮罩
 * 切換小眼睛
 * @param obj
 */
function maskOrUnmask(obj) {
	// fa-eye-slash
	if($(obj).hasClass('fa-eye')){
		$(obj).removeClass('fa-eye').addClass('fa-eye-slash');
		unMaskInfoByClick(obj);
	}else {
		$(obj).removeClass('fa-eye-slash').addClass('fa-eye');
		maskInfoByClick(obj);
	}
}


/**
 * input欄位遮罩事件
 * @param obj
 */
function maskInfoByClick(obj) {
	let input = $(obj).parent().find('input');
	let realData = $(input).data('value');
	let maskData = $(input).val();
	
	// 遮罩後可將新的mobile做遮罩
	let type = $(input).attr('type');
	if(type == 'mobile') {
		$(input).attr('disabled', true);
		realData = maskPhoneNumber(maskData);
	} else if (type == 'email') {
        $(input).attr('disabled', true);
        realData = maskEmailAddress(maskData);
    }
	
	$(input).val(realData);
	$(input).data('value',maskData);
}
/**
 * input欄位解除遮罩事件
 * @param obj
 */
function unMaskInfoByClick(obj) {
	let input = $(obj).parent().find('input');
	let maskData= $(input).data('value');
	let realData = $(input).val();
	
	// 解除遮罩後可編輯
	let type = $(input).data('type');
	if(type == 'mobile') {
		$(input).attr('disabled', false);
	}
	
	$(input).val(maskData);
	$(input).data('value',realData)
}


/**
 * 發送驗證碼後秒數倒數
 */

function checkOTPWaitTime() {
    var waitsec = parseInt($('#resendOTPText').attr('data-time'));
    waitsec = parseInt(waitsec);
    if (waitsec == 0) {
        $('#resendOTPText').empty();
        $('#resendOTPBtn').attr('href', 'javascript:resendOTP();');
        $('#resendOTPText').empty().append('點此重新發送驗證碼');
        $('#resendOTPText').css('text-decoration', '');
        $('#resendOTPText').css('color', '');
        $('#resendOTPText').css('cursor', '');
        $('#resendOTPText').addClass('link');
        // $('#resendOTPBtn').css('text-decoration', '');
        // $('#resendOTPBtn').css('color', '');
        // $('#resendOTPBtn').css('cursor', '');
        clearInterval(waitOTPsecInterval);
    } else {
    		if( $('#resendOTPText').attr('data-value')==''){
    			$('#resendOTPText').empty().append('已發送驗證碼，1分鐘後才能再次重新發送。(' + waitsec + '秒)');
    		}else{
    			$('#resendOTPText').empty().append($('#resendOTPText').attr('data-value')+'(' + waitsec + '秒)');
    		}
    		
        waitsec = waitsec - 1;
        $('#resendOTPText').attr('data-time', waitsec)
    }
}

/**
 * 再次發送otp簡訊
 */
function resendOTP() {

    var p = {
        // "uid": $('#uid').val(),
        "uid": getUnmaskValue('#uid'),
        "birthdate": transferBirthDateToAD(getUnmaskValue('#birthdate')),
        "scope": "basic"
    };

    // member頁面的informMethod才會有值
    if(informMethod !== undefined && informMethod.length > 0){
        p.inform_method = informMethod;
    }
    RiAPI.run({
        type: 'POST',
        url: '/rest/user/emailOrMobileLoginStep1',
        loadSpin: true,
        data: p,
        success: function (resp) {
            if (resp.code < 0) {
                //登入失敗
                alert('登入失敗，請重新操作！');
                window.close();
            } else {
                if (typeof resp.data.uuidcheck != 'undefined') {
                    uuidcheck = resp.data.uuidcheck;
                    //初次發送，一樣等一分鐘後，才可重送
	            		if( $('#resendOTPText').attr('data-value')==''){
	            			$('#resendOTPText').empty().append('已發送驗證碼，1分鐘後才能再次重新發送。(120秒)');
	            		}else{
	            			$('#resendOTPText').empty().append($('#resendOTPText').attr('data-value')+'(120秒)');
	            		}                    
                    $('#resendOTPText').css('cssText', 'cursor: default;text-decoration:none !important;color:#414141 !important;');
                    $('#resendOTPBtn').attr('href', 'javascript:void(0);');
                    $('#resendOTPText').attr('data-time', 120);
                    waitOTPsecInterval = setInterval(function () {
                        //$resendOTPBtnWait,$resendOTPBtn,$resendOTPText
                        checkOTPWaitTime();
                    }, 1000);
                }
            }
        }
    });
}
/**
 * 
 * @param time
 * @returns
 */
function timeStamp2String (time){
	var datetime = new Date();
	datetime.setTime(time);
	var year = datetime.getFullYear();
	var month = datetime.getMonth() + 1;
	var date = datetime.getDate();
	var hour = datetime.getHours();
	var minute = datetime.getMinutes();
	var second = datetime.getSeconds();
	var mseconds = datetime.getMilliseconds();
	return year + "-" + month + "-" + date+" "+hour+":"+minute+":"+second+"."+mseconds;
};

function timeStamp2DateString(time){
	var datetime = new Date();
	datetime.setTime(time);
	var year = datetime.getFullYear();
	var month = datetime.getMonth() + 1;
	var date = datetime.getDate();
	//var hour = datetime.getHours();
	//var minute = datetime.getMinutes();
	//var second = datetime.getSeconds();
	//var mseconds = datetime.getMilliseconds();
	if(month.toString().length==1){
		month = '0' + month.toString();
	}
	if(date.toString().length==1){
		date = '0' + date.toString();
	}
	return year.toString() + month.toString() + date.toString();
};

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
/**
 * input欄位focusout事件
 * @param obj
 */
function inputFocusoutEvent(obj, type) {

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
        $(obj).attr('data-value', newVal);
        // 根據input的id判斷用什麼方法mask
        $(obj).val(maskSenstiveInformation_v2(type, newVal));
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
 * 生日6碼補7碼
 */
function addZeroPrefixForBirthdate(birthdate) {
    if(typeof birthdate === 'undefined') return '';
    return birthdate.length === 6 ? '0' + birthdate : birthdate;
}


/**
 * 雙證件領換補日期6碼補7碼
 */
function addZeroPrefixForIdMarkDate(obj) {
    var tmpValue = $(obj).val();
    if(tmpValue.length === 6){
        $(obj).val('0' + tmpValue);
    }

}

function piiPrompt() {
    $('.learning-wrap').click(function(event) {
        var $this = $(this);

        if($this.hasClass('active')) {
            $this.removeClass('active');
        } else {
            $('.learning-wrap').removeClass('active');
            $this.addClass('active');
        }
    });
}

function checkMobileOs() {
    var text = navigator.userAgent;

    var check = false;
    var match = /(Android (\d{1,2}(\.\d{1,2})?)|iPhone OS (\d{1,2}_\d{1,2}))/g.exec(text);
    if(match != null) {
        var info = match[0].replace("_", ".");
        var symbolIdx = info.lastIndexOf(" ");
        var os = info.substring(0, symbolIdx);
        var version = parseFloat(info.substring(symbolIdx + 1, info.length));

        if(os == 'Android') {
            check = version >= 8;
        } else if(os == 'iPhone OS') {
            check = version >= 12.4;
        }
    }

    return check;
}

function showServerName($brand, host, path) {
    if(host.indexOf('mydatadev') != -1){
        if(path.indexOf('mydata-stage') != -1){
            $brand.attr('class','bg-alert-with-text');
            if(path.indexOf('organ') != -1) {
                $brand.text('試辦區－機關操作區')
            } else {
                $brand.text('試辦區')
            }
        }else if(path.indexOf('mydata') != -1){
            $brand.attr('class','bg-blue-with-text');
            if(path.indexOf('organ') != -1) {
                $brand.text('測試區－機關操作區')
            } else {
                $brand.text('測試區')
            }
        }
    }else if(path.indexOf('mydata-dev-beta-testing') != -1){
        $brand.attr('class','bg-alert-with-text');
        if(path.indexOf('organ') != -1) {
            $brand.text('開發區－機關操作區')
        } else {
            $brand.text('開發區')
        }
    }else if(host.indexOf('localhost') != -1){
        $brand.attr('class','bg-alert-with-text');
        if(path.indexOf('organ') != -1) {
            $brand.text('測試區－機關操作區')
        } else {
            $brand.text('測試區')
        }
    }else if(host.indexOf('develop') != -1){
        $brand.attr('class','bg-alert-with-text');
        if(path.indexOf('organ') != -1) {
            $brand.text('開發區－機關操作區')
        } else {
            $brand.text('開發區')
        }
    } else {
        $brand.attr('class','bg-green-with-text');
        if(path.indexOf('organ') != -1) {
            $brand.text('機關操作區')
        } else {
            $brand.text('')
            $brand.hide()
        }
    }
}

function checkTaxId(sid){
    var tbNum = new Array(1,2,1,2,1,2,4,1);
    var temp = 0;
    var total = 0;
    if(sid==""){
        return false;
    }else if(!sid.match(/^\d{8}$/)) {
        return false;
    }else{
        for(var i = 0; i < tbNum.length ;i ++){
            temp = sid.charAt(i) * tbNum[i];
            total += Math.floor(temp/10)+temp%10;
        }
        if(total%10==0 || (total%10==9 && sid.charAt(6)==7)){
            return true;
        }else{
            return false;
        }
    }
}

function getRandomValues() {
    return (crypto.getRandomValues(new Uint32Array(1))[0] / 4294967295);
}
