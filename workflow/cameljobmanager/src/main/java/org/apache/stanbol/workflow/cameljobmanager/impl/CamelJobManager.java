package org.apache.stanbol.workflow.cameljobmanager.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.stanbol.workflow.component.core.StanbolCamelComponent;
import org.apache.stanbol.workflow.servicesapi.FlowJobManager;
import org.apache.stanbol.workflow.servicesapi.RouteManager;
import org.apache.stanbol.workflow.servicesapi.StanbolRoute;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Naive EnhancementJobManager implementation that keeps its request queue in
 * memory.
 * 
 */
@org.apache.felix.scr.annotations.Component(immediate = true, metatype = false)
@Service(FlowJobManager.class)
@References({
		@Reference(referenceInterface = StanbolCamelComponent.class, name = "StanbolCamelComponent", policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE),
		@Reference(referenceInterface = RoutesBuilder.class, name = "Route", cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC) })
public class CamelJobManager implements FlowJobManager {

	List<StanbolCamelComponent> camelComponents = new ArrayList();

	@Reference(policy = ReferencePolicy.STATIC)
	private OsgiDefaultCamelContext stanbolCamelContext;

	private static final Logger logger = LoggerFactory
			.getLogger(CamelJobManager.class);

	@Reference
	protected ChainManager chainManager;

	@Reference
	protected RouteManager routeManager;
	
	protected void bindRoute(RoutesBuilder e) throws Exception {
		RouteBuilder srb = (RouteBuilder) e;
		addRouteToContext(srb);
	}

	public Boolean addRoutes(InputStream is) {
		return addRoutes(is, null);
	}

	public Boolean addRoutes(InputStream is, String group) {
		try {
			RoutesDefinition rds = stanbolCamelContext

			.loadRoutesDefinition(is);
			stanbolCamelContext.addRouteDefinitions(rds.getRoutes());
			for (RouteDefinition rd : rds.getRoutes()) {
				rd.setGroup(group);
				stanbolCamelContext.startRoute(rd.getId());
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * <p>
	 * Removes route by group
	 * </p>
	 * 
	 * @param group
	 *            a {@code String} containing the group
	 * @return true if any route with that group is removed or false is no route
	 *         is removed
	 */
	public Boolean removeRoutes(String group) {
		Boolean removed = false;
		List<RouteDefinition> routeDefinitions = stanbolCamelContext
				.getRouteDefinitions();
		for (final RouteDefinition rd : routeDefinitions) {
			if (rd.getGroup() != null && rd.getGroup().equals(group)) {
				try {
					stanbolCamelContext.removeRouteDefinition(rd);
					removed = true;
				} catch (Exception e) {
					// Doing nothing. Silent the exception
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
			stanbolCamelContext.stopRoute(routeDefs);
			stanbolCamelContext.removeRouteDefinition(routeDefs);
		}
	}

	protected void bindStanbolCamelComponent(StanbolCamelComponent e) {
		camelComponents.add(e);
		registerComponent(e);
	}

	protected void unbindStanbolCamelComponent(StanbolCamelComponent e) {
		camelComponents.remove(e);
		unregisterComponent(e);
	}

	@Activate
	public void activate(ComponentContext ce) throws IOException {
		try {
			for (StanbolCamelComponent sc : camelComponents) {
				registerComponent(sc);
			}
			stanbolCamelContext.start();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private void registerComponent(StanbolCamelComponent component) {
		if (stanbolCamelContext == null)
			return;
		
		if(stanbolCamelContext.getComponent(component.getURIScheme()) == null)
		stanbolCamelContext.addComponent(component.getURIScheme(), component);
		

	}

	private void unregisterComponent(StanbolCamelComponent component) {
		if (stanbolCamelContext == null)
			return;
		if(stanbolCamelContext.getComponent(component.getURIScheme()) != null)
			stanbolCamelContext.removeComponent(component.getURIScheme());
			
	}

	@Deactivate
	public void deactivate(ComponentContext ce) throws Exception {
		stanbolCamelContext.stop();
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
		/*ProducerTemplate tpl = stanbolCamelContext.createProducerTemplate();
		ContentItem result = tpl.requestBody("direct://" + chain.getName(), ci,
				ContentItem.class);*/
		
		StanbolRoute route = routeManager.getRoute(chain.getName());
		if(route == null)
			throw new ChainException(
					"Unable to enhance ContentItem '"
							+ ci.getUri()
							+ "' because currently no route with name "
							+ chain.getName()
							+ " is registered. Please"
							+ "configure a route with that name or execute the default route");
		
		route.executeRoute(ci);
	}

	public List<EnhancementEngine> getActiveEngines() {
		return null;
	}

	private void addRouteToContext(RouteBuilder rb) throws Exception {
		stanbolCamelContext.addRoutes(rb);
		for (RouteDefinition rd : rb.getRouteCollection().getRoutes()) {
			stanbolCamelContext.startRoute(rd.getId());
		}
	}

}
