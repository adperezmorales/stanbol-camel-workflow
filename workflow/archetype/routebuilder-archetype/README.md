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

# Building archetype and install it locally

    $ cd routebuilder-archetype ; mvn clean install;

# Create a project using the archetype

    $ mvn archetype:generate -DarchetypeCatalog=local
	or
    $ mvn archetype:generate -DarchetypeArtifactId=org.apache.stanbol.flow.routebuilder-archetype -DarchetypeGroupId=org.apache.stanbol -DgroupId={project-packaging} -DartifactId={project-name} -DinteractiveMode=true
**Note:** Leave the default value for the property classPrefix as is. This property value converts the value of *routeName* property putting the first letter in uppercase and it is used in class name and class file name. 
	
# Test the new created route
Once you have modified and filled the **createRoute** method, you must build the bundle and putting it in Stanbol. After that, a new route (using direct component of Camel)
will be available in Stanbol in order to be used through the endpoint *http://localhost:8080/flow/{routeName}*

