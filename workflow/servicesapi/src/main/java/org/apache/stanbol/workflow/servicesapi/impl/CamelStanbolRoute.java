package org.apache.stanbol.workflow.servicesapi.impl;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.apache.camel.model.RouteDefinition;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.workflow.servicesapi.StanbolRoute;

/**
 * <p>CamelStanbolRoute class</p>
 * <p>Specific Camel-based Stanbol route implementation</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class CamelStanbolRoute implements StanbolRoute {
	
	protected RouteDefinition routeDefinition;
	
	protected CamelContext context;

	public CamelStanbolRoute(RouteDefinition routeDefinition, CamelContext context){
		this.routeDefinition = routeDefinition;
		this.context = context;
	}
		
	@Override
	public void startRoute() throws Exception {
		context.startRoute(routeDefinition.getId());
	}

	@Override
	public void stopRoute() throws Exception {
		context.stopRoute(routeDefinition.getId());
	}

	@Override
	public void executeRoute(ContentItem ci) {
		ProducerTemplate tpl = context.createProducerTemplate();
		Route route = context.getRoute(routeDefinition.getId());
		tpl.requestBody(route.getEndpoint(), ci,
				ContentItem.class);
	}
	
	@Override
	public String getRoutePath() {
		return routeDefinition.getDescriptionText();
	}

	@Override
	public String getName() {
		return routeDefinition.getId();
	}

}
