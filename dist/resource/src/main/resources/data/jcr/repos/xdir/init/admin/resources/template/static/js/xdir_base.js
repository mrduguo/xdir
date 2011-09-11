${model.response.setContentType("text/javascript; charset=utf-8")}<#assign rawoutput>
	var baseUrl="${app.baseUri}";
	<#include "jquery.js">
	<#include "jquery_cookie.js">
	<#include "jquery_inlineedit.js">
	<#include "jquery_tablesorter.js">
	<#include "xdir_account.js">
	<#if (script_files??)>
		<@script_files/>
	</#if>
	$(function() {
		<#if (script_init??)>
			<@script_init/>
		</#if>
	});
</#assign>
${model.app.template.compress("javascript",rawoutput)}