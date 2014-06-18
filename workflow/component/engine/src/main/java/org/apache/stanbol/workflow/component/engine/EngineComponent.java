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
package org.apache.stanbol.workflow.component.engine;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngineManager;
import org.apache.stanbol.workflow.component.core.StanbolCamelComponent;
import org.osgi.service.component.ComponentContext;

/**
 * <p>EngineComponent class</p>
 * <p>Camel component to deal with enhancement engines in routes</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
@Component(immediate=true)
@Service(StanbolCamelComponent.class)
public class EngineComponent extends StanbolCamelComponent {
	
	@Reference
	EnhancementEngineManager manager;
	   
	@Activate
	public void activate(ComponentContext ce) {
		super.activate(ce);
	}
	
	@Deactivate
	public void deactivate(ComponentContext ce) {
		super.deactivate(ce);
	}
	
	public EnhancementEngineManager getEnhancementEngineManager() {
		return manager;
	}

	public void setEnhancementEngineManager(EnhancementEngineManager manager) {
		this.manager = manager;
	}

	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

    	Endpoint endpoint = null;
    	EnhancementEngine e = manager.getEngine(remaining);
    	
    	if (e != null){
    		endpoint = new EngineEndpoint(remaining, this, e);
    	}
    	else{ 
    		throw new IllegalArgumentException("No registered engine referenced by this name : " + remaining);
    	}
    	
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
