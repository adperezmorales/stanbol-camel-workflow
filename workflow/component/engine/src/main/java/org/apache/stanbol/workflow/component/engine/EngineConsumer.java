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

import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

/**
 * <p>EngineConsumer class</p>
 * <p>Camel Consumer for Engine component</p>
 *  
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class EngineConsumer extends ScheduledPollConsumer {
    
	final EngineEndpoint endpoint;

    public EngineConsumer(EngineEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected int poll() throws Exception {
    	throw new UnsupportedOperationException("You cannot get messages from this endpoint: ");
    }

}
