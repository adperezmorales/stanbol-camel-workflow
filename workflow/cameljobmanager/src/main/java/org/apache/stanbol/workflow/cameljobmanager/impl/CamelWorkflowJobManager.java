package org.apache.stanbol.workflow.cameljobmanager.impl;

import java.io.IOException;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.EnhancementException;
import org.apache.stanbol.workflow.servicesapi.RouteManager;
import org.apache.stanbol.workflow.servicesapi.StanbolRoute;
import org.apache.stanbol.workflow.servicesapi.WorkflowJobManager;
import org.apache.stanbol.workflow.servicesapi.exception.WorkflowException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Naive EnhancementJobManager implementation that keeps its request queue in
 * memory.
 * 
 */
@org.apache.felix.scr.annotations.Component(immediate = true, metatype = false)
@Service(WorkflowJobManager.class)
public class CamelWorkflowJobManager implements WorkflowJobManager {

	private static final Logger logger = LoggerFactory
			.getLogger(CamelWorkflowJobManager.class);

	@Reference
	protected RouteManager routeManager;

	@Activate
	public void activate(ComponentContext ce) throws IOException {

	}

	@Deactivate
	public void deactivate(ComponentContext ce) throws Exception {

	}

	@Override
	public void enhanceContent(ContentItem ci) throws EnhancementException {
		logger.info("Using default route to enhance");
		enhanceContent(ci, WorkflowJobManager.DEFAULT_ROUTE_NAME);
	}

	@Override
	public void enhanceContent(ContentItem ci, String routeId)
			throws EnhancementException {
		
		StanbolRoute route = routeManager.getRoute(routeId);
		logger.info("Enhancing content using route "+routeId);
		if(route == null) {
			logger.info("No route encountered with id "+routeId);
			throw new WorkflowException(
					"Unable to enhance ContentItem '"
							+ ci.getUri()
							+ "' because currently no route with name "
							+ routeId
							+ " is registered. Please"
							+ "configure a route with that name or execute the default route");
		}
		route.executeRoute(ci);
	}

	public List<EnhancementEngine> getActiveEngines() {
		return null;
	}


}
