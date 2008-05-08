<!--
/*
 * $Id: webconsole.html 590812 2007-10-31 20:32:54Z apetrelli $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<html>
<head>
<link rel="stylesheet" type="text/css" href="<@s.url value="/static/shell.css" />" />
<script src=" <@s.url value="/static/shell.js" />"></script>
<script src="<@s.url value="/static/dojo.js" />"></script>
<link rel="stylesheet" type="text/css" href="<@s.url value="/static/main.css" />" />
<title>OSGi Console</title>
</head>
<body>
<div class="right">
    <@s.url var="bundlesUrl" nampespace="/osgi/admin" action="bundles" includeParams="none" />
    <a href="${bundlesUrl}">Installed Bundles</a>
</div>
<div id="shell" >
   <form onsubmit="return false" id="wc-form">
        <div class="wc-results" id="wc-result">
             Welcome to the OSGi console! Type 'help' to see the list of available commands.
             <br />
             :-&gt;
        </div>
        <@s.url var="execUrl" namespace="/osgi/admin" action="execCommand" />
        <label for"command">Command:</label>
        <input name="command" onkeyup="keyEvent(event, '${execUrl}')" class="wc-command" id="wc-command" type="text" />
    </form>
</div>
</body>
</html>
