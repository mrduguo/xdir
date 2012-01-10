<#macro script_init>
		$('table.sortable').tablesorter();

		$(".pagemenubutton").click(function(){
            $('.pagearticle').toggle();
            $('.pagemenu').toggle();
			if($(this).hasClass("opened")){
                $(this).html("menu");
			}else{
                $(this).html("close");
			}
            $(this).toggleClass("opened");
		});

		var upButton=$(".breadcrumb a:last");
		if(upButton.length>0){
			upButton=upButton.clone(true);
			upButton.addClass("backbutton button floatleft");
			upButton.html("up");
            $('.pagemenubar').prepend(upButton);

			var pagePaths="<h3 class=\"breadcrumb\">Where you are</h3><ul>";
            $(".breadcrumb a").each(function(){
				pagePaths+="<li><a href=\""+$(this).attr("href")+"\">"+$(this).html()+"</a></li>";
				$(this).replaceWith("<span>"+$(this).html()+"</span>");
			});
			pagePaths+="</ul>";
			$('.pagenav').prepend(pagePaths);
		}else{
            $('.pagemenubar').prepend("<a href='/index.html' style='float:left;' class=\"button\">Desktop View</div>");
        }
        $('.pagemenu').hide();
</#macro>
<#include "js/xdir_base.js">