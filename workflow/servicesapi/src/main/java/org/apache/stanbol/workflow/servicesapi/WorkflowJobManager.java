/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.workflow.servicesapi;

import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EnhancementException;

/**
 * <p>
 * Accept requests for enhancing ContentItems, and processes them
 * </p>
 * <p>
 * The progress of the enhancement process should be made accessible in the
 * ContentItem's metadata.
 * </p>
 */
public interface WorkflowJobManager {

	/**
	 * Default route name to use in workflowjobmanager implementations and
	 * wanted default route
	 */
	String DEFAULT_ROUTE_NAME = "default";

	/**
	 * <p>
	 * Enhances the parsed contentItem by using the default route
	 * </p>
	 * <p>
	 * TODO: define the expected semantics if asynchronous enhancements were to
	 * get implemented.
	 * 
	 * @throws EnhancementException
	 *             if the enhancement process failed
	 */
	void enhanceContent(ContentItem ci) throws EnhancementException;

	/**
	 * <p>
	 * Processes the parsed {@link ContentItem} by using the
	 * {@link StanbolRoute stanbol route} provided
	 * </p>
	 * 
	 * @param ci
	 *            : ContentItem to be enhanced
	 * @param routeId
	 *            : The identifier of the route used to process the content item
	 * @throws EnhancementException
	 *             : if an error occurred during the enhancement process
	 */
	void enhanceContent(ContentItem ci, String routeId)
			throws EnhancementException;

}