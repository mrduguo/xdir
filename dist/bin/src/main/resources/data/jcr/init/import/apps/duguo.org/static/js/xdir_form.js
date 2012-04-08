<#include "jquery_autoresize.js">

function addKeyValuePairField(_source){
	var _buttonTr=$(_source).parentsUntil("tr").parent();
	var _uniqueKey=new Date().getTime();
	var _field_content="<tr><th>Key/Value</th><td><input class=\"fullwidth\" type=\"text\" name=\"propertiesnew"+_uniqueKey+"_name\" value=\"\"/></td></tr>"+
		"<tr><td colspan=\"2\"><textarea  class=\"multiautoresize\" name=\"propertiesnew"+_uniqueKey+"_value\"></textarea></td></tr>"
	var _field_trElement=$(_field_content);
    _field_trElement.insertBefore(_buttonTr);
    $("textarea.multiautoresize",_field_trElement).autoResize({limit:400});
}