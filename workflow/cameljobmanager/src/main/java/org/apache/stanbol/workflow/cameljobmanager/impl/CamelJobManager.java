package org.apache.stanbol.workflow.cameljobmanager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.servicesapi.Chain;
import org.apache.stanbol.enhancer.servicesapi.ChainException;
import org.apache.stanbol.enhancer.servicesapi.ChainManager;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.EnhancementException;
import org.apache.stanbol.workflow.component.core.impl.BaseStanbolCamelComponent;
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
public class CamelJobManager implements FlowJobManager {

	List<BaseStanbolCamelComponent> camelComponents = new ArrayList();

	@Reference(policy = ReferencePolicy.STATIC)
	private OsgiDefaultCamelContext stanbolCamelContext;

	private static final Logger logger = LoggerFactory
			.getLogger(CamelJobManager.class);

	@Reference
	protected RouteManager routeManager;
	
	@Reference
	protected ChainManager chainManager;
	
	protected void bindStanbolCamelComponent(BaseStanbolCamelComponent e) {
		camelComponents.add(e);
		registerComponent(e);
	}

	protected void unbindStanbolCamelComponent(BaseStanbolCamelComponent e) {
		camelComponents.remove(e);
		unregisterComponent(e);
	}

	@Activate
	public void activate(ComponentContext ce) throws IOException {
		try {
			for (BaseStanbolCamelComponent sc : camelComponents) {
				registerComponent(sc);
			}
			stanbolCamelContext.start();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private void registerComponent(BaseStanbolCamelComponent component) {
		if (stanbolCamelContext == null)
			return;
		
		if(stanbolCamelContext.getComponent(component.getURIScheme()) == null)
		stanbolCamelContext.addComponent(component.getURIScheme(), component);
		

	}

	private void unregisterComponent(BaseStanbolCamelComponent component) {
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


}
