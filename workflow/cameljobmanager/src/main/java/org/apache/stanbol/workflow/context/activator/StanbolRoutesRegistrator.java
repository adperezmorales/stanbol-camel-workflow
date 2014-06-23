package org.apache.stanbol.workflow.context.activator;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
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
import org.apache.stanbol.workflow.servicesapi.RouteManager;
import org.apache.stanbol.workflow.servicesapi.StanbolRoute;
import org.apache.stanbol.workflow.servicesapi.impl.CamelStanbolRoute;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

@org.apache.felix.scr.annotations.Component(immediate = true, metatype = false)
@Service(StanbolRoutesRegistrator.class)
@References({
		@Reference(referenceInterface = RoutesBuilder.class, name = "Route", cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC) })
public class StanbolRoutesRegistrator {
	
	@Reference(policy = ReferencePolicy.STATIC)
	private OsgiDefaultCamelContext stanbolCamelContext;
	
	@Reference(policy = ReferencePolicy.STATIC)
	private RouteManager routeManager;

	private static final Logger logger = LoggerFactory
			.getLogger(StanbolRoutesRegistrator.class);
	
	private BundleContext context;
	
	private Map<StanbolRoute, ServiceRegistration> routeServices = Maps.newHashMap();
	
	@Activate
	public void activate(ComponentContext ce) throws Exception{
		context = ce.getBundleContext();
		stanbolCamelContext.start();
	}
	
	@Deactivate
	public void deactivate(ComponentContext ce) throws Exception {
		stanbolCamelContext.stop();
	}
	
	protected void bindRoute(RoutesBuilder e) throws Exception {
		RouteBuilder srb = (RouteBuilder) e;
		addRouteToContext(srb);
	}
	
	public Boolean addRoutes(InputStream is, String group) {
		try {
			RoutesDefinition rds = 
					stanbolCamelContext.loadRoutesDefinition(is);
			stanbolCamelContext.addRouteDefinitions(rds.getRoutes());
			rds.setCamelContext(stanbolCamelContext);
			for (RouteDefinition rd : rds.getRoutes()) {
				rd.setGroup(group);
				registerRoute(rd, rds.getCamelContext());
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
		List<RouteDefinition> routeDefinitions = stanbolCamelContext
				.getRouteDefinitions();
		for (final RouteDefinition rd : routeDefinitions) {
			if (rd.getGroup() != null && rd.getGroup().equals(group)) {
				try {
					unregisterRoute(rd);
					return true;
				} catch (Exception e) {

				}
			}
		}
		
		return false;
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
		for (RouteDefinition routeDefs : srb.getRouteCollection().getRoutes())
			unregisterRoute(routeDefs);
	}

	private void addRouteToContext(RouteBuilder rb) throws Exception {
		stanbolCamelContext.addRoutes(rb);
		RoutesDefinition routes = rb.getRouteCollection();
		for (RouteDefinition rd : routes.getRoutes())
			registerRoute(rd, routes.getCamelContext());
	}

	private void registerRoute(RouteDefinition rd, CamelContext camelContext){
		StanbolRoute nextRoute = new CamelStanbolRoute(rd, camelContext);
		logger.info("Registering Route with Id " + rd.getId() + " and Name " + nextRoute.getName());
		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put(StanbolRoute.PROPERTY_NAME, nextRoute.getName());
		ServiceRegistration reg = context.registerService(StanbolRoute.class.getName(), nextRoute, properties);
		routeServices.put(nextRoute, reg);
	}
	
	private void unregisterRoute(RouteDefinition rd) throws Exception{
		StanbolRoute route = routeManager.getRoute(rd.getId());
		route.stopRoute();
		stanbolCamelContext.removeRouteDefinition(rd);
		routeServices.get(route).unregister();;
		routeServices.remove(route);
	}
}
