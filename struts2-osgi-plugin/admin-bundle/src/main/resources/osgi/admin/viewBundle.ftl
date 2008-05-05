<html>
    <head>
        <@s.url var="mainCss" value="/static/main.css" includeParams="none" />
        <link rel="stylesheet" type="text/css" href="${mainCss}" />
    </head>
<body>

<div class="right">
    <@s.url var="bundlesUrl" nampespace="/osgi/admin" action="bundles" includeParams="none" />
    <a href="${bundlesUrl}">Installed Bundles</a>
</div>

<@s.actionerror />

<table class="bundleDetails" style="clear:both">
    <tr class="detailRow">
        <td class="rowTitle">Id</td>
        <td class="rowValue">${bundle.bundleId!}</td>
    </tr>
    <tr class="detailRow">
        <td class="rowTitle">Name</td>
        <td class="rowValue">${bundle.symbolicName!}</td>
    </tr>
    <tr class="detailRow">
        <td class="rowTitle">Location</td>
        <td class="rowValue">${bundle.location!}</td>
    </tr>
    <tr class="detailRow">
        <td class="rowTitle">State</td>
        <td class="rowValue">${action.getBundleState(bundle)}</td>
    </tr>
    <tr class="detailRow">
        <td class="rowTitle">Registered Services</td>
        <td class="rowValue">
            <#list (bundle.registeredServices)! as service>
                <table class="properties">
                    <#list (service.propertyKeys)! as key >
                        <tr>
                            <td class="name">${key}</td>
                            <td>${action.displayProperty(service.getProperty(key))}</td>
                        </tr>
                    </#list>
                </table>
                <br/>
            </#list>
        </td>
    </tr>
    <tr class="detailRow">
        <td class="rowTitle">Services in Use</td>
        <td class="rowValue">
            <#list (bundle.servicesInUse)! as service>
                <table class="properties">
                    <#list (service.propertyKeys)! as key >
                        <tr>
                            <td class="name">${key}</td>
                            <td>${action.displayProperty(service.getProperty(key))!}</td>
                        </tr>
                    </#list>
                </table>
                <br/>
           </#list>
        </td>
    </tr>
    <tr class="detailRow">
        <td class="rowTitle">Packages</td>
        <td class="rowValue">
            <#list packages! as pkg>
                <table class="properties">
                    <tr>
                        <td class="name">Name</td>
                        <td>${pkg.name}</td>
                    </tr>
                    <tr>
                        <td class="name">Actions</td>
                        <td>
                            <ul>
                                <#list (pkg.actionConfigs.keySet())! as name >
                                    <li>${name}</li>
                                </#list>
                            </ul>
                        </td>
                    </tr>
                </table>
                <br/>
            </#list>
        </td>
    </tr>
    <tr class="detailRow">
        <td class="rowTitle">Actions</td>
        <td class="rowValue">
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
</table>
</body>
</html>