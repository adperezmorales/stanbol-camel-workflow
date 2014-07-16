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

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;

/**
 * <p>EngineEndpoint class</p>
 * <p>Camel endpoint for engine scheme</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class EngineEndpoint extends DefaultEndpoint {
	
	final EnhancementEngine engine;
	final Map<String,Object> parameters;
	public EngineEndpoint(String uri, EngineComponent component, EnhancementEngine e, Map<String,Object> parameters) {
        super(uri, component);
        this.engine = e;
        this.parameters = new HashMap<String,Object>(parameters);
    }

	@Override
	public Producer createProducer() throws Exception {
        return new EngineProducer(this);
    }
    
    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
    	throw new UnsupportedOperationException("You cannot get messages from this endpoint: " + getEndpointUri());
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
