package org.apache.stanbol.flow.cameljobmanager.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.servicesapi.Chain;
import org.apache.stanbol.enhancer.servicesapi.ChainException;
import org.apache.stanbol.enhancer.servicesapi.ChainManager;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.EnhancementException;
import org.apache.stanbol.enhancer.servicesapi.FlowJobManager;
import org.apache.stanbol.flow.cameljobmanager.engineprotocol.EngineComponent;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Naive EnhancementJobManager implementation that keeps its request queue in
 * memory.
 * 
 */
@org.apache.felix.scr.annotations.Component(immediate = true)
@Service(FlowJobManager.class)
@References({
		@Reference(referenceInterface = Component.class, name = "Ec", policy = ReferencePolicy.DYNAMIC),
		@Reference(referenceInterface = RoutesBuilder.class, name = "Route", cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC) })
public class CamelJobManager implements FlowJobManager {

	EngineComponent ec;

	private static final Logger LOG = LoggerFactory
			.getLogger(CamelJobManager.class);
	private CamelContext cContext = null;

	@Reference
	protected ChainManager chainManager;

	protected void bindRoute(RoutesBuilder e) throws Exception {
		RouteBuilder srb = (RouteBuilder) e;
		addRouteToContext(srb);
	}

	public Boolean addRoutes(InputStream is) {
		return addRoutes(is,null);
	}
	
	public Boolean addRoutes(InputStream is, String group) {
		try {
			RoutesDefinition rds = cContext

			.loadRoutesDefinition(is);
			
			cContext.addRouteDefinitions(rds.getRoutes());
			for (RouteDefinition rd : rds.getRoutes()) {
				rd.setGroup(group);
				cContext.startRoute(rd.getId());
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * <p>Removes route by group</p>
	 * @param group a {@code String} containing the group
	 * @return true if any route with that group is removed or false is no route is removed
	 */
	public Boolean removeRoutes(String group) {
		Boolean removed = false;
		List<RouteDefinition> routeDefinitions = cContext.getRouteDefinitions();
		for(final RouteDefinition rd : routeDefinitions) {
				if(rd.getGroup() != null && rd.getGroup().equals(group)) {
					try {
						cContext.removeRouteDefinition(rd);
						removed = true;
					}
					catch(Exception e) {
						//Doing nothing. Silent the exception
					}
				}
				
			}

		return removed;
	}

	/**
	 * Remove route for the Camel context
	 * 
	 * @param e
	 *            : The route builder
	 * @throws Exception
	 */
	protected void unbindRoute(RoutesBuilder e) throws Exception {
		RouteBuilder srb = (RouteBuilder) e;
		for (RouteDefinition routeDefs : srb.getRouteCollection().getRoutes()) {
			cContext.stopRoute(routeDefs);
			cContext.removeRouteDefinition(routeDefs);
		}
	}

	protected void bindEc(Component e) {
		ec = (EngineComponent) e;
	}

	protected void unbindEc(Component e) {
		ec = null;
	}

	@Activate
	public void activate(ComponentContext ce) throws IOException {
		cContext = new OsgiDefaultCamelContext(ce.getBundleContext());

		try {
			cContext.addComponent("engine", ec);
			cContext.start();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Deactivate
	public void deactivate(ComponentContext ce) throws Exception {
		cContext.stop();
	}

	@Override
	public void enhanceContent(ContentItem ci) throws EnhancementException {
		Chain defaultChain = chainManager.getDefault();
		if (defaultChain == null) {
			throw new ChainException(
					"Unable to enhance ContentItem '"
							+ ci.getUri()
							+ "' because currently no enhancement chain is active. Please"
							+ "configure a Chain or enable the default chain");
		}
		enhanceContent(ci, defaultChain);
	}

	@Override
	public void enhanceContent(ContentItem ci, Chain chain)
			throws EnhancementException {
		// TODO : better integration with REST :
		// http://camel.apache.org/cxfrs.html
		ProducerTemplate tpl = cContext.createProducerTemplate();
		ContentItem result = tpl.requestBody("direct://" + chain.getName(), ci,
				ContentItem.class);
	}

	public List<EnhancementEngine> getActiveEngines() {
		return ec.getEnhancementEngines();
	}

	private void addRouteToContext(RouteBuilder rb) throws Exception {
		cContext.addRoutes(rb);
		for (RouteDefinition rd : rb.getRouteCollection().getRoutes()) {
			cContext.startRoute(rd.getId());
		}
	}

}
