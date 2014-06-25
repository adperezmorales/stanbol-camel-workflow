package org.apache.stanbol.workflow.context.activator;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class StanbolCamelContextActivator implements BundleActivator {  

    /**
     * BundleContext instance
     */
    private BundleContext bundleContext;
    
    /**
     * Registration of StanbolCamelContext
     */
    private ServiceRegistration registration;  

    /**
     * <p>Bundle activation</p>
     * 
     * @param context The BundleContext being activated
     */
    public void start(BundleContext context) throws Exception {  
        this.bundleContext = context;  
        OsgiDefaultCamelContext stanbolContext = new OsgiDefaultCamelContext(context);
        registration = bundleContext.registerService(OsgiDefaultCamelContext.class.getName(), stanbolContext, null);
    }  

    /**
     * <p>Bundle deactivation</p>
     * 
     * @param context The BundleContext being deactivated
     */
    public void stop(BundleContext context) throws Exception {  
        registration.unregister();
    }  

}  

