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

<h3>Stateless REST workflow analysis</h3>

<p>This stateless interface allows the caller to submit content to the Stanbol workflow and
get the resulting enhancements formatted as RDF at once without storing anything on the
server-side.</p>
<p><strong>Take into account that this is only useful if the route to be executed is a synchronous (triggered by http requests) route and the result is a content item</strong></p>
<p><strong>There can be asynchronous routes triggered by events occurred in a queue (for example) and ending with a content item sent to another remote component (e.g Solr).</strong></p>
<p><strong>Another kind of routes can start synchronously (using http requests) and finishing in a remote component. In this case the response containing the content item is still sent to the client, so the client will receive the response</strong></p>

<p>The content to analyze should be sent in a POST request with the mimetype specified in
the <code>Content-type</code> header. The response will hold the RDF enhancement serialized
in the format specified in the <code>Accept</code> header:</p>
   
<pre>
curl -X POST -H "Accept: text/turtle" -H "Content-type: text/plain" \
     --data "The Stanbol enhancer can detect famous cities such as \
             Paris and people such as Bob Marley." ${it.serviceUrl}
</pre> 

<p>The list of mimetypes accepted as inputs depends on the deployed engines. By default only
 <code>text/plain</code> content will be analyzed</p>
 
<p>Stanbol Workflow is able to serialize the response in the following RDF formats:</p>
<ul>
<li><code>application/json</code> (JSON-LD)</li>
<li><code>application/rdf+xml</code> (RDF/XML)</li>
<li><code>application/rdf+json</code> (RDF/JSON)</li>
<li><code>text/turtle</code> (Turtle)</li>
<li><code>text/rdf+nt</code> (N-TRIPLES)</li>
</ul> 

<h3> Additional supported QueryParameters:</h3>
<ul>
<li><code>uri={content-item-uri}</code>: By default the URI of the content 
    item being enhanced is a local, non de-referencable URI automatically built 
    out of a hash digest of the binary content. Sometimes it might be helpful 
    to provide the URI of the content-item to be used in the enhancements RDF 
    graph.
<code>uri</code> request parameter
</ul>

<h4>Example</h4>

<p>The following example shows how to send an workflow enhancement request with a
custom content item URI that will include the execution metadata in the
response.</p>

<pre>
curl -X POST -H "Accept: text/turtle" -H "Content-type: text/plain" \
     --data "The Stanbol enhancer can detect famous cities such as \
             Paris and people such as Bob Marley." \
     "${it.serviceUrl}?uri=urn:fise-example-content-item"
</pre> 