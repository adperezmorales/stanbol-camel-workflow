package org.apache.stanbol.workflow.servicesapi.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.stanbol.enhancer.servicesapi.impl.NameBasedServiceTrackingState;
import org.apache.stanbol.workflow.servicesapi.RouteManager;
import org.apache.stanbol.workflow.servicesapi.StanbolRoute;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>RoutesTracker class</p>
 * <p>RouteManager implementation using OSGI service tracker to retrieve routes</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class RoutesTracker implements RouteManager {

	private static final Logger log = LoggerFactory.getLogger(RoutesTracker.class);
	    
	private NameBasedServiceTrackingState nameTracker;
	 
	public RoutesTracker(BundleContext context){
		if(context == null){
            throw new IllegalArgumentException("The parsed BundleContext MUST NOT be NULL!");
        }

		initEngineTracker(context, null);
    }
	
	public RoutesTracker(BundleContext context, ServiceTrackerCustomizer customizer){
        if(context == null){
            throw new IllegalArgumentException("The parsed BundleContext MUST NOT be NULL!");
        }
        initEngineTracker(context, customizer);
    }
	
	RoutesTracker() {
		// TODO Auto-generated constructor stub
	}

	protected void initEngineTracker(BundleContext context, ServiceTrackerCustomizer customiser) {
        
		if(nameTracker != null){ //if this is a re-initialisation
            nameTracker.close(); //try to close the existing service tracker instance
        }
        if(context == null){
            throw new IllegalStateException("Unable to initialise tracking if NULL is parsed as Bundle Context!");
        }
        
        log.info("configured tracking for all Routes");
        this.nameTracker = new NameBasedServiceTrackingState(context, StanbolRoute.class.getName(), StanbolRoute.PROPERTY_NAME, customiser);
    }
	
	/**
     * Starts tracking based on the configuration parsed in the constructor
     */
    public void open(){
        nameTracker.open();
    }
    /**
     * Closes this tracker
     */
    public void close(){
        nameTracker.close();
        nameTracker = null;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.stanbol.workflow.servicesapi.RouteManager#getReference(java.lang.String)
     */
	@Override
	public ServiceReference getReference(String name) {
		if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("The parsed name MUST NOT be NULL or empty");
        }
		return nameTracker.getReference(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.servicesapi.RouteManager#getRoute(java.lang.String)
	 */
	@Override
	public StanbolRoute getRoute(String name) {
		ServiceReference reference = getReference(name);
		return reference == null ? null : (StanbolRoute) nameTracker.getService(reference);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.servicesapi.RouteManager#getRoute(org.osgi.framework.ServiceReference)
	 */
	@Override
	public StanbolRoute getRoute(ServiceReference engineReference) {
		return (StanbolRoute) nameTracker.getService(engineReference);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.servicesapi.RouteManager#getRoutes()
	 */
	@Override
	public List<StanbolRoute> getRoutes() {
		Object[] services = nameTracker.getServices();
		return services == null || services.length == 0 ? new ArrayList<StanbolRoute>() : Arrays.asList(Arrays.copyOf(services, services.length, StanbolRoute[].class));
		
	}
}
