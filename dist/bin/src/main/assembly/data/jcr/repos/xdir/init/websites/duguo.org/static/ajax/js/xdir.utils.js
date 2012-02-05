(function($){
$.xdir = $.xdir || {};
$.xdir.utils = {
			escapeHTML: function(txt) {
				if(txt && txt !=null)
					return txt.replace(/&/g,'&amp;').replace(/>/g,'&gt;').replace(/</g,'&lt;').replace(/"/g,'&quot;').replace(/'/g,"&#39;");
			},
			unescapeHTML: function(txt) {
				return txt.replace(/&amp;/g,'&').replace(/&gt;/g,'>').replace(/&lt;/g,'<').replace(/&quot;/g,'"').replace(/&#39;/g,"'");
			},
			breakLongLine: function(txt,len) {
				if(txt && txt.length <= len){
					return txt;
				}
				var newText=txt.substr(0,len);
				newText=newText.substr(0,newText.lastIndexOf("/"));
				txt=txt.substr(newText.length);				
				return newText+"<br/>"+this.breakLongLine(txt,len);
			},
			isTextFile: function(fileName) {
				fileName=fileName.substr(fileName.length-3);
				if(fileName=="htm" || fileName=="tml" || fileName=="txt" || fileName=="js" || fileName=="css"){
					return true;
				}else{
					return false;
				}
			},
			isImageFile: function(fileName) {
				fileName=fileName.substr(fileName.length-3);
				if(fileName=="jpg" || fileName=="gif" || fileName=="bmp" || fileName=="png" || fileName=="ico" || fileName=="ico"){
					return true;
				}else{
					return false;
				}
			},
			timestampString: function() {
				var date=new Date();
				var dateStr=date.getFullYear();
				dateStr+=""+this.pad(date.getMonth()+1);
				dateStr+=""+this.pad(date.getDate());
				dateStr+="-"+this.pad(date.getHours());
				dateStr+=""+this.pad(date.getMinutes());
				dateStr+=""+this.pad(date.getSeconds());
				return dateStr;
			},
			dateString: function() {
				var date=new Date();
				var dateStr=date.getFullYear();
				dateStr+="-"+this.pad(date.getMonth()+1);
				dateStr+="-"+this.pad(date.getDate());
				return dateStr;
			},
			formatTime: function(timeStr) {
				if(timeStr && (timeStr.length==24 || timeStr.length==29) && timeStr.indexOf("T")==10 && timeStr.indexOf(":")==13){
					timeStr=timeStr.substr(0,10)+" "+timeStr.substr(11,8);
				}
				return timeStr;
			},
			hours: function(timeStr) {
				return ((new Date().getTime()-parseDate(timeStr).getTime())/3600000).toFixed( 2 );
			},
			tablesorterByText: function(tableElement,sortList) {
				if(sortList){
					tableElement.tablesorter({sortList:sortList,widgets: ['zebra'],textExtraction: function(node){
						  return $(node).text();
					}});
				}else{
					tableElement.tablesorter({widgets: ['zebra'],textExtraction: function(node){
						  return $(node).text();
					}});				
				}
			},
			createRawExpandableLink: function(path,name,childcount) {
				return "<ul class=\"nodeul\"><a href=\""+path+"\" class=\"expandableLink\" title=\""+childcount+"\">"+name+"</a></ul>";
			},
			pad: function(value, length) {
				value = String(value);
				length = length || 2;
				while (value.length < length)
					value = "0" + value;
				return value;
			},
			endswith: function(value, str) {
				if(value.indexOf(str)<0) 
					return false;
				else
					return value.indexOf(str)==(value.length-str.length);
			},
			startswith: function(value, str) {
				return value.indexOf(str)==0;
			},
			isodd: true,
			cycleevenodd: function() {
				if(this.isodd){
					this.isodd=false;
					return "odd";
				}else{
					this.isodd=true;
					return "even";				
				}
			}
		 };
})(jQuery);