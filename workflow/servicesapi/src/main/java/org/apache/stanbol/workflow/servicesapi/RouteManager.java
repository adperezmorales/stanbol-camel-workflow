/**
 * 
 */
package org.apache.stanbol.workflow.servicesapi;

import java.util.List;

import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.osgi.framework.ServiceReference;

/**
 * <p>RouteManager interface</p>
 * <p>Used to retrieve routes registered in Stanbol</p>
 *
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public interface RouteManager {
	
	/**
     * Getter for the ServiceReference of the Route for the parsed
     * name
     * @param name The name - MUST NOT be <code>null</code> empty and tracked
     * by this tracker
     * @return the {@link ServiceReference} or <code>null</code> if no Route
     * exists
     * @throws IllegalArgumentException if the parsed name is <code>null</code>,
     * empty or not tracked by this tracker instance.
     */
    ServiceReference getReference(String name);
    
    /**
     * Getter for the Route for the parsed name
     * @param name The name - MUST NOT be <code>null</code> empty and tracked
     * by this tracker
     * @return The {@link StanbolRoute} or <code>null</code> if no route
     * with the given name is active
     * @throws IllegalArgumentException if the parsed name is <code>null</code>,
     * empty or not tracked by this tracker instance.
     */
    StanbolRoute getRoute(String name);
    
    /**
     * Getter for the Route service for the parsed
     * service Reference. 
     * 
     * @param routeReference the service reference for a route tracked by this
     * component
     * @return the referenced {@link StanbolRoute} or <code>null</code>
     * if no longer available.
     */
    StanbolRoute getRoute(ServiceReference routeReference);
    
    /**
     * Getter for all the registered Routes
     * @return a {@code List} containing all the registered routes
     */
    List<StanbolRoute> getRoutes();
}
