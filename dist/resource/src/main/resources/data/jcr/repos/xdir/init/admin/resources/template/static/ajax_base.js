${model.response.setContentType("text/javascript;charset=UTF-8")}<#include "ajax/js/jquery">
var pageSessionId=new Date().getTime();
$.xdir = $.xdir || {};
$.xdir.plugins = $.xdir.plugins || {};

var pageContext="${pageContext}";
var pagePath="${pagePath}";
var pageTitle="${pageTitle}";
var cacheVersion="${plugins.cache.version}";
var protectedProperties={
		"jcr:primaryType":true,
		"jcr:pathName":true,
		"jcr:created":true,
		"jcr:createdBy":true,
		"jcr:modified":true,
		"jcr:modifiedBy":true,
		"jcr:baseVersion":true,
		"jcr:isCheckedOut":true,
		"jcr:mixinTypes":true,
		"jcr:predecessors":true,
		"jcr:uuid":true,
		"jcr:versionHistory":true
		};


<#include "ajax/js/jquery.ondemand">
<#include "ajax/js/jquery.dimensions">
<#include "ajax/js/jquery.jtemplates">
<#include "ajax/js/jquery.validate">
<#include "ajax/js/jquery.autocomplete">
<#--include "ajax/js/jquery.ajaxfileupload"-->
<#include "ajax/js/jquery.tablesorter-src">
<#include "ajax/js/jquery.date">
<#include "ajax/js/jquery.inlineedit">
	
<#include "ajax/js/ui.dialog">
<#include "ajax/js/ui.resizable">
<#include "ajax/js/ui.mouse">
<#include "ajax/js/ui.draggable">
<#include "ajax/js/ui.tabs">
<#include "ajax/js/ui.datepicker">


<#include "ajax/js/xdir.base">
<#include "ajax/js/xdir.jcr">
<#include "ajax/js/xdir.query">
<#include "ajax/js/xdir.page">
<#include "ajax/js/xdir.actions">

<#include "ajax/js/xdir.site">
<#include "ajax/js/xdir.utils">
<#include "ajax/js/xdir.plugins">
<#include "ajax/js/xdir.browser">
<#include "ajax/js/xdir-actions">
<#include "ajax/js/xdir.init">