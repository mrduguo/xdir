${model.response.setContentType("text/xml; charset=utf-8")}<#assign rawoutput>
	<?xml version="1.0" encoding="UTF-8"?>
	<#assign mmLinkPrefix=getResourceUrl("freemind/freemind.html")+"?mm=">
	<#assign mmLinkSurfix="?format=mm">
	<#if isRequestTrue("displaylink")>
		<#assign displaylink=true>
		<#assign mmLinkSurfix=mmLinkSurfix+"&displaylink=true">
	</#if>
	<#if isRequestTrue("nopaths")>
		<#assign nopaths=true>
		<#assign mmLinkSurfix=mmLinkSurfix+"&nopaths=true">
	</#if>
	<#if isRequestTrue("noproperties")>
		<#assign noproperties=true>
		<#assign mmLinkSurfix=mmLinkSurfix+"&noproperties=true">
	</#if>
	<#assign mmLinkSurfix=mmLinkSurfix?html+"#">
	<map version="0.9.0_Beta_8">
		<node POSITION="right" TEXT="${pageTitle?html}"<#if displaylink??> LINK="${pagePath}.html"</#if>>
			<@render_paths/>
			<@render_children_node node listChildren(node) getIntParameter("maxlevel",10)/>
		</node>
	</map>
</#assign>
${model.app.template.compress("xml",rawoutput)}
<#macro render_paths>
	<#if (!(nopaths??) && pagePaths?size>1)>
		<node POSITION="left" TEXT="paths" FOLDED="true">
			<#list pagePaths as p>
			  <#if p!=pagePaths?last>
				<node POSITION="left" TEXT="${p.title?html}"<#if displaylink??>  LINK="${mmLinkPrefix}${p.url}${mmLinkSurfix}"</#if>>
					<#if p.node??><@render_properties p.node 0 "left"/></#if>
				</node>
			  </#if>
			</#list>
		</node>
	</#if>
</#macro>
<#macro render_children_node currentNode currentChildren maxlevel>
	<@render_properties currentNode currentChildren?size "right"/>
	<#list currentChildren?keys?sort as child>
		<node POSITION="right" TEXT="${currentChildren[child].title?html}"
		<#if displaylink??>  LINK="${mmLinkPrefix}${currentChildren[child].url}${mmLinkSurfix}"</#if>>
			<#if (maxlevel>1 && !currentChildren[child].node.hasProperty("_mm_no_children"))>
				<@render_children_node currentChildren[child].node listChildren(currentChildren[child].node) maxlevel-1/>
			</#if>
		</node>
	</#list>
</#macro>
<#macro render_properties currentNode childrenSize,pisition>
	<#if !(noproperties??)>
		<#assign currentProperties=listDisplayableProperties(currentNode)>
		<#if (currentProperties?size>0)>
			<#if (childrenSize>0)>
				<node POSITION="${pisition}" TEXT="properties"<#if (currentProperties?size>3)> FOLDED="true"</#if>>
			</#if>
			<#if (currentProperties["overview"]??)>
				<@render_property "Overview" currentProperties["overview"].displayableValue pisition/>
			</#if>
			<#list currentProperties?keys?sort as p>
				<#if p!="overview">
					<@render_property currentProperties[p].name currentProperties[p].displayableValue pisition/>
				</#if>
			</#list>
			<#if (childrenSize>0)>
				</node>
			</#if>
		</#if>
	</#if>
</#macro>
<#macro render_property propertyName propertyValue,pisition>
	<#if (propertyValue?index_of("<")<0)>
		<node POSITION="${pisition}" TEXT="${propertyName?html}">
			<#if propertyValue?matches("^https?://.*")>
				<node POSITION="${pisition}" TEXT="${propertyValue?html}" LINK="${propertyValue?html}#"/>
			<#else>
				<node POSITION="${pisition}" TEXT="${propertyValue?replace("<br/>","\n")?html}"/>
			</#if>
		</node>
	</#if>
</#macro>