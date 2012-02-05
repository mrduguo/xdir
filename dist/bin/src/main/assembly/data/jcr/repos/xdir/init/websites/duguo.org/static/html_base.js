<#macro script_files>
	<#include "js/jquery_button.js">
</#macro>
<#macro script_init>
		$('table.sortable').tablesorter();
		$('.button').makebutton();
</#macro>
<#include "js/xdir_base.js">