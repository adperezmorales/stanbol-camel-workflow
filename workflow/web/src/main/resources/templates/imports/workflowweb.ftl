<#--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<#if !it.routes?has_content>
  <p><em>There seems to be a problem with the Workflow component, because no routes are registered 
   To fix this an Administrator needs to install some routes by using the <a href="/system/console">OSGi console</a>.</em></p>
<#else>
  <#assign routes = it.routes>
  <div class="workflow-listing">
  	<ul>
  <#list routes as route>
  	
  	<li>
  		<div class="route-name"><strong><a href="${it.publicBaseUri}workflow/${route.name}">${route.name}</a></strong></div>
  		<div class="route-path">${route.routePath!}</div>
  	</li>
  </#list>
   </ul>
  </div>
 
<script type="text/javascript">
/*$(".enginelisting p").click(function () {
  $(this).parents("div").toggleClass("collapsed");
})
.find("a").click(function(e){
    e.stopPropagation();
    //link to all active Enhancement Chains
    window.location = "${it.publicBaseUri}enhancer/chain";
    return false;
});*/     
</script>
</#if>
<#if it.routes?has_content>
  <p>Paste some text below and submit the form to let the Workflow route <strong><em>${it.routeId}</em></strong> enhance it:</p>
  <form id="workflowInput" method="POST" accept-charset="utf-8">
    <p><textarea rows="15" name="content"></textarea></p>
    <p class="submitButtons">Output format:
      <select name="format">
        <option value="application/json">JSON-LD</option>
        <option value="application/rdf+xml">RDF/XML</option>
        <option value="application/rdf+json">RDF/JSON</option>
        <option value="text/turtle">Turtle</option>
        <option value="text/rdf+nt">N-TRIPLES</option>
      </select> <input class="submit" type="submit" value="Run workflow">
    </p>
  </form>
<script type="text/javascript">
function registerFormHandler() {
   $("#workflowInput input.submit", this).click(function(e) {
     // disable regular form click
     e.preventDefault();
     
     var data = {
       content: $("#workflowInput textarea[name=content]").val(),
       ajax: true,
       format:  $("#workflowInput select[name=format]").val()
     };
     var base = window.location.href.replace(/\/$/, "");
     
     $("#workflowOutputWaiter").show();
     
     // submit the form query using Ajax
     $.ajax({
       type: "POST",
       url: base,
       data: data,
       dataType: "html",
       cache: false,
       success: function(result) {
         $("#workflowOutputWaiter").hide();
         $("#workflowOutput").html(result);
       },
       error: function(result) {
         $("#workflowOutputWaiter").hide();
         $("#workflowOutput").text('Invalid query.');
       }
     });
   });
 }
 $(document).ready(registerFormHandler);
</script>
  <div id="workflowOutputWaiter" style="display: none">
    <p>Stanbol is analysing your content...</p>
    <p><img alt="Waiting..." src="${it.staticRootUrl}/home/images/ajax-loader.gif" /></p>
  </div>
  <p id="workflowOutput"></p>
</#if>