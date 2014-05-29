/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.flow.cameljobmanager.engineprotocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;


/**
 * Naive EnhancementJobManager implementation that keeps its request queue in
 * memory.
 *
 */
@Component(immediate=true)
@Service
@Reference(name="EnhancementEngine", referenceInterface=EnhancementEngine.class, policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE)
public class EngineComponent extends DefaultComponent {
	
	private Map<String, EnhancementEngine> engineMap;
	
	protected void bindEnhancementEngine(EnhancementEngine e) {
		if (engineMap == null){
			engineMap = new HashMap<String, EnhancementEngine>();
		}
		engineMap.put(e.getClass().getName(), e);
    }

    protected void unbindEnhancementEngine(EnhancementEngine e) {
        engineMap.remove(e.getClass().getName());
    }
	
    public List<EnhancementEngine> getEnhancementEngines() {
    	return Collections.unmodifiableList(new ArrayList<EnhancementEngine>(engineMap.values()));
    }
    
    public EnhancementEngine getEnhancementEngine(String engineClassName){
    	return engineMap.get(engineClassName);
    }
    
    public void setEnhancementEngine(EnhancementEngine e){
    	bindEnhancementEngine(e);
    }
    
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    	Endpoint endpoint ;
    	EnhancementEngine e = getEnhancementEngine(remaining);
    	
    	if (e != null){
    		endpoint = new EngineEndpoint(remaining, this, e);
    	}
    	else{ 
    		throw new UnsupportedOperationException("No registered engine referenced by this name : " + remaining);
    	}
    	
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
