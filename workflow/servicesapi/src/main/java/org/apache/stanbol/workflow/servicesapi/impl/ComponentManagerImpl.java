package org.apache.stanbol.workflow.servicesapi.impl;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.workflow.component.core.StanbolCamelComponent;
import org.apache.stanbol.workflow.servicesapi.ComponentManager;
import org.osgi.framework.BundleContext;

@Component(immediate=true)
@Service(ComponentManager.class)
@References({
	@Reference(referenceInterface = StanbolCamelComponent.class, name = "StanbolCamelComponent", policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE)})
public class ComponentManagerImpl implements ComponentManager {

	private BundleContext bundleContext;
	
	@Reference(policy=ReferencePolicy.STATIC)
	private OsgiDefaultCamelContext camelContext;
	
	/**
	 * <p>Default Constructor</p>
	 */
	public ComponentManagerImpl() {
		
	}
	
	/**
	 * <p>Method called when the component is activated</p>
	 * @param ctx the {@code BundleContext} instance
	 */
	@Activate
	protected void activate(BundleContext ctx) {
		this.bundleContext = ctx;
	}
	
	/**
	 * <p>Method called when the component is deactivated</p>
	 * @param ctx the {@code BundleContext} instance
	 */
	@Deactivate
	protected void deactivate(BundleContext ctx) {
		this.bundleContext = null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.servicesapi.ComponentManager#getComponent(java.lang.String)
	 */
	@Override
	public StanbolCamelComponent getComponent(String uriScheme) {
		Object object = this.camelContext.getComponent(uriScheme);
		return resolveObject(object);
	}

	/**
	 * <p>Resolves the object reference in order to return a {@code StanbolCamelComponent}</p>
	 * @param object the object to be resolved
	 * @return an instance of {@code StanbolCamelComponent} or null if the object can not be resolved as an instance of {@code StanbolCamelComponent}
	 */
	private StanbolCamelComponent resolveObject(Object object) {
		if(StanbolCamelComponent.class.isAssignableFrom(object.getClass()))
			return (StanbolCamelComponent) object;
		else
			return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.servicesapi.ComponentManager#registerComponent(org.apache.stanbol.workflow.component.core.StanbolCamelComponent)
	 */
	@Override
	public Boolean registerComponent(StanbolCamelComponent component) {
		if(component.getURIScheme() != null) {
			this.camelContext.addComponent(component.getURIScheme(), component);
			return true;
		}
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.servicesapi.ComponentManager#unregisterComponent(org.apache.stanbol.workflow.component.core.StanbolCamelComponent)
	 */
	@Override
	public Boolean unregisterComponent(StanbolCamelComponent component) {
		if(component.getURIScheme() != null) {
			this.camelContext.removeComponent(component.getURIScheme());
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.servicesapi.ComponentManager#unregisterComponent(java.lang.String)
	 */
	@Override
	public Boolean unregisterComponent(String uriScheme) {
		Object object = this.camelContext.removeComponent(uriScheme);
		return object == null ? false : true;
	}

	/**
	 * <p>Method called by OSGI when a StanbolCamelComponent reference is available in the registry</p>
	 * <p>Binds the new StanbolCamelComponent to the Camel context in order to be available in the routes</p>
	 * @param component the {@code StanbolCamelComponent} to be bound
	 */
	protected void bindStanbolCamelComponent(StanbolCamelComponent component) {
		this.registerComponent(component);
	}
	
	/**
	 * <p>Method called by OSGI when a StanbolCamelComponent reference is no longer available in the registry</p>
	 * <p>Unbinds the StanbolCamelComponent from the Camel context, so the component is no longer available to be used in routes</p>
	 * @param component the {@code StanbolCamelComponent} to be unbound
	 */
	protected void unbindStanbolCamelComponent(StanbolCamelComponent component) {
		this.unregisterComponent(component);
	}
}
