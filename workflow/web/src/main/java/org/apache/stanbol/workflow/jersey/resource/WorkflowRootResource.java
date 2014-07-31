/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.workflow.jersey.resource;

import java.io.File;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.core.sparql.QueryEngine;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.web.base.resource.BaseStanbolResource;
import org.apache.stanbol.enhancer.servicesapi.ContentItemFactory;
import org.apache.stanbol.workflow.jersey.service.WorkflowRouteUploaderService;
import org.apache.stanbol.workflow.servicesapi.RouteManager;
import org.apache.stanbol.workflow.servicesapi.WorkflowJobManager;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * RESTful interface to browse the list of available routes and allow to call
 * them in a stateless, synchronous way.
 * </p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 */
@Component
@Service(Object.class)
@Property(name = "javax.ws.rs", boolValue = true)
@Path("/workflow")
public final class WorkflowRootResource extends BaseStanbolResource {

	@Reference
	private WorkflowJobManager jobManager;
	@Reference
	private ContentItemFactory ciFactory;
	@Reference
	private Serializer serializer;
	@Reference
	private QueryEngine queryEngine;

	@Reference
	private RouteManager routeManager;

	@Reference
	private WorkflowRouteUploaderService routeUploaderService;
	
	@Path("")
	public GenericWorkflowEnhancerUiResource get() {
		return new GenericWorkflowEnhancerUiResource(null, routeUploaderService, jobManager,
				routeManager, ciFactory, serializer, getLayoutConfiguration(),
				getUriInfo());
	}

	/**
	 * <p>
	 * Call a specific workflow route using the route id
	 * </p>
	 * <p>
	 * So it is mandatory to deploy a route with such id
	 * </p>
	 * 
	 * @param route
	 *            The route id
	 * @return a {@code GenericWorkflowEnhancerUiResource} instance
	 */
	@Path("{workflowRoute}")
	public GenericWorkflowEnhancerUiResource get(
			@PathParam(value = "workflowRoute") String route) {
		return new GenericWorkflowEnhancerUiResource(route, routeUploaderService, jobManager,
				routeManager, ciFactory, serializer, getLayoutConfiguration(),
				getUriInfo());
	}

}
