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

# Building Workflow endpoint

    $ cd workflow/ ; mvn clean install;
    $ cd launchers/workflow ; mvn clean install;

# Starting Stanbol

    $ cd launchers/workflow/target ; java -jar org.apache.stanbol.launchers.workflow-1.0.0-SNAPSHOT.jar;

If you want to deploy Camel routes defined in XML, you have to put a Camel XML route with *route* extension in *stanbol/fileinstall* directory. If you want to change the directory where to put the Camel XML routes, you have to specify the system property *felix.fileinstall.dir* pointing to the directory to be monitored. For other supported Apache Felix Fileinstall component properties, visit [this link](http://http://felix.apache.org/site/apache-felix-file-install.html "Apache Felix fileinstall")

    $ cd launchers/workflow/target : java -Dfelix.fileinstall.dir=./stanbol/fileinstall/ -jar org.apache.stanbol.launchers.workflow-1.0.0-SNAPSHOT.jar; # In order to use the same directory used by Stanbol to deploy bundles

# Testing Workflow component:

## On demand flow feature

The flow endpoint offer sub-resource that allow you to directly call some predefined particular route

Template for this request is :

    $ curl -X POST -H "Accept: text/turtle" -H "Content-type: text/plain" --data "content=Here comes a little test with Paris as content and also Berlin but why not detect city as Boston and some well know people like Bob Marley." http://localhost:8080/flow

http://localhost:8080/flow/{directRouteName} endpoint is used to start a route by its id defined using a XML file or Java Object (in a bundle). 

To try the default enhancement chain (using chain Camel component), please copy the *examples/defaultchain.route* to *stanbol/fileinstall* directory and use the ***http://localhost:8080/flow/defaultchain*** endpoint to enhance content

If you want to try the same default enhancement chain but using a route based on engine components, please copy the *examples/defaultchainengines.route* to *stanbol/fileinstall* directory and use the ***http://localhost:8080/flow/defaultchainengines*** endpoint to enhance content
