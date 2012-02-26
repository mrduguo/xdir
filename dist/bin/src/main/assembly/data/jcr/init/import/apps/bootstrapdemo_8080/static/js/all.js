${response.setContentType("text/javascript; charset=utf-8")}<#assign rawoutput>
<#include "jquery.js">
<#include "bootstrap.js">
</#assign>
<#if isCacheableResponse()>${app.service.jsCodec.apply(rawoutput)}<#else>${rawoutput}</#if>