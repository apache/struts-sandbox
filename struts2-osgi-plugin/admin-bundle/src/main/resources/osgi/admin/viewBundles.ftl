<html>
<head>
    <title>OSGi Bundles</title>
    <@s.url var="mainCss" value="/static/main.css" includeParams="none" />
    <link rel="stylesheet" type="text/css" href="${mainCss}" />
</head>
<body>

<div class="right">
    <@s.url var="bundlesUrl" nampespace="/osgi/admin" action="bundles" includeParams="none" />
    <@s.url var="osgiShellUrl" namespace="/osgi/admin" action="shell" includeParams="none" />
    <a href="${bundlesUrl}"><img src='<@s.url value="/static/search.gif"/>'</a>
    <a href="${bundlesUrl}">Installed Bundles</a>
    <a href="${bundlesUrl}"><img src='<@s.url value="/static/terminal.gif"/>'</a>
    <a href="${osgiShellUrl}">OSGi Shell</a>
</div>
<table class="properties" style="clear:both">
    <thead>
        <tr>
            <th>Name</th>
            <th>State</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <#list bundles as bundle>
        <tr>
            <td>
                <a href="bundle_${bundle.symbolicName}!view.action">${bundle.symbolicName}</a>
            </td>
            <td>${action.getBundleState(bundle)}</td>
            <td>
                <#if action.isAllowedAction(bundle, "start")>
                <a href="bundle_${bundle.symbolicName}!start.action">Start</a>
                </#if>

                <#if action.isAllowedAction(bundle, "stop")>
                <a href="bundle_${bundle.symbolicName}!stop.action">Stop</a>
                </#if>

                <#if action.isAllowedAction(bundle, "update")>
                <a href="bundle_${bundle.symbolicName}!update.action">Update</a>
                </#if>
            </td>
        </tr>
        </#list>
    </tbody>
</table>
</body>
</html>
