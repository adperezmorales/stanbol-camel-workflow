package org.apache.stanbol.workflow.servicesapi;

import org.apache.stanbol.enhancer.servicesapi.ContentItem;

/**
 * <p>StanbolRoute interface</p>
 * <p>Represents a route in Stanbol</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public interface StanbolRoute {

	/**
     * The property used to provide the name of a route
     */
    public final static String PROPERTY_NAME = "stanbol.workflow.route.name";
    
    void startRoute() throws Exception;
    
    void executeRoute(ContentItem ci);
    
    void stopRoute() throws Exception;
    
    String getRoutePath();
    
    String getName();
}
