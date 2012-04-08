(function($){
$.xdir = $.xdir || {};
$.xdir.site = $.xdir.site || {};
$.xdir.site = {
			displaySection: function(sectionText,sectionTitle,titlelevel){
				var tempHtml="<div class=\"site_section\">";
				if(sectionTitle){
					if(titlelevel){
						tempHtml+="<div class=\"site_section_title_"+titlelevel+"\">"+sectionTitle+"</div>";
					}else{
						tempHtml+="<div class=\"site_section_title_1\">"+sectionTitle+"</div>";
					}					
				}
				tempHtml+=$.xdir.utils.unescapeHTML(sectionText);
				tempHtml+="</div>";
				return tempHtml;
			}
	}
})(jQuery);
