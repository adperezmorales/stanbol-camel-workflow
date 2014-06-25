package org.apache.stanbol.workflow.servicesapi.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.workflow.servicesapi.RouteManager;
import org.osgi.service.component.ComponentContext;

/**
 * <p>RouteManagerImpl class</p>
 * <p>RouteManager implementation exposed as OSGI component</p>
 * <p>It uses the OSGI registry to retrieve the required routes</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
@Component(immediate=true,enabled=true)
@Service(value=RouteManager.class)
public class RouteManagerImpl extends RoutesTracker implements RouteManager {

	public RouteManagerImpl() {
		super();
	}

	@Activate
    public void activate(ComponentContext ctx){
        initEngineTracker(ctx.getBundleContext(), null);
        open();
    }
    @Deactivate
    public void deactivate(ComponentContext ctx){
        close();
    }
}
