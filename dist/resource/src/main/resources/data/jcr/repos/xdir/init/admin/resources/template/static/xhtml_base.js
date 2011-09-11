<#macro script_init>
		$('table.sortable').tablesorter();
				
		var pageArticle=$('.pagearticle');
		var pageMenu=$('.pagemenu');
		var pageHeader=$('.pageheader h2');
		var pageTitle=$('h1:first').addClass("pagetitle").prependTo($("body"));
		var pageFooter=$(".pagefooter");
		var menuButton=$("<div class=\"button\">menu</div>");
		menuButton.click(function(){
			pageArticle.toggle();
			pageMenu.toggle();
			if(menuButton.hasClass("opened")){
				menuButton.html("menu");
			}else{
				menuButton.html("close");
			}
			menuButton.toggleClass("opened");
				
		});
		pageTitle.prepend(menuButton);
		pageFooter.prepend(menuButton.clone(true));
		
		var upButton=$(".breadcrumb a:last");
		if(upButton.length>0){
			upButton=upButton.clone(true);
			upButton.addClass("backbutton button");
			upButton.html("up");
			pageTitle.prepend(upButton);
			pageFooter.prepend(upButton.clone(true));
			
			var pagePaths="<h3 class=\"breadcrumb\">Where you are</h3><ul>";
			$(".breadcrumb a").each(function(){
				pagePaths+="<li><a href=\""+$(this).attr("href")+"\">"+$(this).html()+"</a></li>";
				$(this).replaceWith("<span>"+$(this).html()+"</span>");
			});
			pagePaths+="</ul>";
			$('.pagenav').prepend(pagePaths);
		}
		pageMenu.hide();
</#macro>
<#include "js/xdir_base.js">