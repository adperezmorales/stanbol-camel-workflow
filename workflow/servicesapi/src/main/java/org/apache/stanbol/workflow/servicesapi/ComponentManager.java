/**
 * 
 */
package org.apache.stanbol.workflow.servicesapi;

import org.apache.stanbol.workflow.component.core.StanbolCamelComponent;

/**
 * <p>ComponentManager interface</p>
 * <p>Allows to register and unregister Stanbol Camel components</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public interface ComponentManager {
	
	/**
	 * <p>Gets a registered {@code StanbolCamelComponent} based on the scheme managed by the component</p>
	 * @param uriScheme The URI scheme used to retrieve the component
	 * @return the StanbolCamelComponent if exists or null otherwise
	 */
    StanbolCamelComponent getComponent(String uriScheme);
    
    /**
     * <p>Registers a {@code StanbolCamelComponent} in order to be used in routes</p>
     * @param component the {@code StanbolCamelComponent} to be registered
     * @return a {@code Boolean} indicating whether the registry was successful or not
     */
    Boolean registerComponent(StanbolCamelComponent component);
    
    /**
     * <p>Unregisters a {@code StanbolCamelComponent}, so it will no longer be available in the routes</p>
     * @param component the {@code StanbolCamelComponent} to be unregistered
     * @return a {@code Boolean} indicating whether the component was successfully unregistered or not
     */
    Boolean unregisterComponent(StanbolCamelComponent component);
    
    /**
     * <p>Unregisters a {@code StanbolCamelComponent} using the given URI scheme</p>
     * <p>The component managing the specified URI scheme will no longer be available in the routes</p>
     * @param uriScheme
     * @return
     */
    Boolean unregisterComponent(String uriScheme);
}
