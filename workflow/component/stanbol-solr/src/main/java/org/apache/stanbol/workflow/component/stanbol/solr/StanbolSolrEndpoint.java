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
package org.apache.stanbol.workflow.component.stanbol.solr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.solr.SolrEndpoint;

/**
 * <p>
 * StanbolSolrEndpoint class
 * </p>
 * <p>
 * Camel endpoint for stanbol-solr scheme
 * </p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 * 
 */
public class StanbolSolrEndpoint extends SolrEndpoint {

	/**
	 * <p>
	 * Constant for the field separator
	 * </p>
	 */
	private static final String FIELD_SEPARATOR = ",|, ";

	protected final Map<String, Object> parameters;

	/**
	 * <p>
	 * The LDPath program
	 * </p>
	 */
	protected String ldpath = null;

	/**
	 * <p>
	 * The list of fields to be dereferenced
	 * </p>
	 */
	protected List<String> fields = new ArrayList<String>();

	/**
	 * <p>
	 * Flag indicating if the dereference engine ldpath program (if exists)
	 * should be used if no ldpath program is supplied
	 * </p>
	 */
	protected Boolean useDereferenceLdpath = true;

	protected final StanbolSolrComponent stanbolSolrComponent;

	public StanbolSolrEndpoint(String uri, StanbolSolrComponent component,
			String address, Map<String, Object> parameters) throws Exception {
		super(uri, component, address);
		this.parameters = new HashMap<String, Object>(parameters);
		this.stanbolSolrComponent = component;
	}

	@Override
	public StanbolSolrComponent getComponent() {
		return stanbolSolrComponent;
	}

	@Override
	public Producer createProducer() throws Exception {
		super.createProducer(); // Creating custom Solr Servers
		SolrProducerWrapper producer = new StanbolSolrProducer(this,
				getComponent().getSolrServer(this), getComponent()
						.getUpdateSolrServer(this));
		return producer;
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		throw new UnsupportedOperationException(
				"You cannot get messages from this endpoint: "
						+ getEndpointUri());
	}

	/**
	 * <p>
	 * Gets the configured ldpath program parameter
	 * </p>
	 * 
	 * @return the configured ldpath program or null if no program is configured
	 */
	public String getLdpath() {
		return this.ldpath;
	}

	/**
	 * <p>
	 * Sets the ldpath program to be used to extract enhancement metadata
	 * </p>
	 * 
	 * @param ldpath
	 *            The ldpath program
	 */
	public void setLdpath(String ldpath) {
		this.ldpath = ldpath;
	}

	/**
	 * <p>
	 * Sets the field names to be extracted as enhancement metadata
	 * </p>
	 * <p>
	 * Must be a comma-separated list of field names
	 * </p>
	 * 
	 * @param fields
	 *            a comma-separated list of field names (absolute or prefixed
	 *            uri's)
	 */
	public void setFields(String fields) {
		String[] theFields = fields.split(FIELD_SEPARATOR);
		this.fields = Arrays.asList(theFields);
	}

	/**
	 * <p>
	 * Gets the field names to be extracted as enhancement metadata
	 * </p>
	 * 
	 * @return the list of fields to be extracted
	 */
	public List<String> getFields() {
		return this.fields;
	}

	/**
	 * <p>
	 * Flag indicating if the configured dereference engine ldpath program (if
	 * exists) must be used
	 * </p>
	 * <p>
	 * If this flag is enabled and an ldpath is configured, this ldpath will be
	 * skipped
	 * <p>
	 * <p>
	 * If the flag is not enabled, and no ldpath is configured, the dereference
	 * engine ldpath will be used
	 * </p>
	 * 
	 * @return a {@code Boolean} indicating whether the dereference ldpath must
	 *         be used or not
	 */
	public Boolean getUseDereferenceLdpath() {
		return this.useDereferenceLdpath;
	}

	/**
	 * <p>
	 * Sets the flag indicating if the dereference engine ldpath must be used
	 * </p>
	 * 
	 * @param useDereferenceLdpath
	 *            The {@code Boolean} value of the flag
	 */
	public void setUseDereferenceLdpath(Boolean useDereferenceLdpath) {
		this.useDereferenceLdpath = useDereferenceLdpath;
	}
}
