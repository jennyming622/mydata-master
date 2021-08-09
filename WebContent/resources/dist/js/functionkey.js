//Alt + U移至網頁上方主要導覽連結區，此區塊列有本網站的導覽列連結。 #navhead
//Alt + C移至網頁中段主要導覽連結區，此區塊列有本網站的內容。 #demo
//Alt + B移至網頁下方快速連結區，此區塊列有本網站的重點連結。  #footer

window.onload = function(){
	var map = {18: false, 66: false, 67: false, 85: false};
	$(document).keydown(function(e) {
	    if (e.keyCode in map) {
	        map[e.keyCode] = true;
	        if (map[18] && map[85]) {
	            // FIRE EVENT
	            //console.log("alt + u");
	            //window.location.hash = "head_area";
	            $('#head_area').focus();
	        }
	        if (map[18] && map[67]) {
	            // FIRE EVENT
	            //console.log("alt + c");
	            //window.location.hash = "main_area";
	            $('#main_area').focus();
	        }
	        if (map[18] && map[66]) {
	            // FIRE EVENT
	            //console.log("alt + b");
	            //window.location.hash = "footer_area";
	            $('#footer_area').focus();
	        }
	    }
	}).keyup(function(e) {
	    if (e.keyCode in map) {
	        map[e.keyCode] = false;
	    }
	});

}

