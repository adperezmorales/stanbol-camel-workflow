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
package org.apache.stanbol.workflow.component.core.impl;

import java.util.Dictionary;

import org.apache.camel.Component;
import org.apache.camel.impl.DefaultComponent;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.workflow.component.core.StanbolCamelComponent;
import org.osgi.service.component.ComponentContext;

/**
 * <p>BaseStanbolCamelComponent class</p>
 * <p>Base class for all Stanbol Camel components</p>
 * <p>It provides a configurable Osgi property to specify the name of the protocol managed by this component</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
@org.apache.felix.scr.annotations.Component(immediate=true)
@Service(StanbolCamelComponent.class)
public abstract class BaseStanbolCamelComponent extends DefaultComponent implements StanbolCamelComponent{
	
	/**
	 * <p>URI Scheme managed by this component</p>
	 */
	@Property(name=URI_SCHEME_PROPERTY)
	protected String uriScheme;
	
	/**
	 * <p>Gets the URI Scheme managed by the component</p>
	 * @return String the URI scheme
	 */
	public String getURIScheme() {
		return uriScheme;
	}

	/**
	 * <p>Sets the URI Scheme managed by the component</p>
	 * @param uriScheme The URI scheme to be used
	 */
	public void setURIScheme(String uriScheme) {
		this.uriScheme = uriScheme;
	}

	/**
	 * <p>Activate method</p>
	 * @param ce The {@code ComponentContext} object
	 */
	@Activate
	public void activate(ComponentContext ce) {
		@SuppressWarnings("rawtypes")
		Dictionary props = ce.getProperties();
		uriScheme = (String) props.get(StanbolCamelComponent.URI_SCHEME_PROPERTY);
	}
	
	/**
	 * <p>Deactivate method</p>
	 * @param ce The {@code ComponentContext} object
	 */
	@Deactivate
	public void deactivate(ComponentContext ce) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.component.core.StanbolCamelComponent#getAsCamelComponent()
	 */
	@Override
	public Component getAsCamelComponent() {
		return (Component) this;
	}
	
}
