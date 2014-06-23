/**
 * 
 */
package org.apache.stanbol.workflow.servicesapi;

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
     * @return The {@link EnhancementEngine} or <code>null</code> if no Engine
     * with the given name is active
     * @throws IllegalArgumentException if the parsed name is <code>null</code>,
     * empty or not tracked by this tracker instance.
     */
    StanbolRoute getRoute(String name);
    
    /**
     * Getter for the {@link EnhancementEngine} service for the parsed
     * service Reference. This method allows to also retrieve the service for
     * other engines than the one with the highest service ranking by using
     * <code><pre>
     *     for(ServiceReference engineRef : tracker.getReferences("test")){
     *         EnhancementEngine engine = tracker.getEngine(engineRef)
     *         if(engine != null) { //may become inactive in the meantime
     *             //save the world by using this engine!
     *         }
     *     }
     * </pre></code>
     * @param engineReference the service reference for an engine tracked by this
     * component
     * @return the referenced {@link EnhancementEngine} or <code>null</code>
     * if no longer available.
     */
    StanbolRoute getRoute(ServiceReference engineReference);
}
