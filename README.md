<!--
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

# Building Flow endpoint

    $ cd flow/ ; mvn clean install;
    $ cd felix-fileinstall-route-artifact/ ; mvn clean install; # Used to deploy routes placing a file with ***route*** extension in some directory
    $ cd launchers/fullflow ; mvn clean install;

# Starting Stanbol

    $ cd flow/fullflow/target ; java -jar org.apache.stanbol.launchers.fullflow-1.0.0-SNAPSHOT.jar;

    If you want to deploy Camel routes defined in XML, you have to specify the system property *felix.fileinstall.dir* pointing to the directory to be monitored. For other supported Fileinstall component properties, visit [this link](http://http://felix.apache.org/site/apache-felix-file-install.html "Apache Felix fileinstall")

    $ cd flow/fullflow/target : java -Dfelix.fileinstall.dir=./stanbol/fileinstall/ -jar org.apache.stanbol.launchers.fullflow-1.0.0-SNAPSHOT.jar; # In order to use the same directory used by Stanbol to deploy bundles

# Testing Flow :

## Pool flow feature

By default a file endpoint will fetch all files in /tmp/chaininput folder, process it with the langId engine and then write the xml-rdf result in /tmp/chainoutput folder.

## On demand flow feature

The flow endpoint offer sub-ressource that allow you to directly call some predefined particular flow.

Template for this request is :

    $ curl -X POST -H "Accept: text/turtle" -H "Content-type: text/plain" --data "content=Here comes a little test with Paris as content and also Berlin but why not detect city as Boston and some well know people like Bob Marley." http://localhost:8080/flow

Flow/Route definition for default can be find here : https://github.com/adperezmorales/stanbol-camel-workflow/blob/master/flow/weightedgraphflow/src/main/java/org/apache/stanbol/flow/weightedgraphflow/WeightedChain.java

http://localhost:8080/flow/{directRouteName} endpoint is used to start a direct route defined using a XML file or Java Object (in a bundle)

