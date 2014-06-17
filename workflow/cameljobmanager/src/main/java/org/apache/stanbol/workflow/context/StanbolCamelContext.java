package org.apache.stanbol.workflow.context;

import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * Stanbol Camel Context class
 * </p>
 * <p>
 * Extends the {@code OsgiDefaultCamelContext} class to provide more
 * functionality
 * </p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 * 
 */
public class StanbolCamelContext extends OsgiDefaultCamelContext {

	/**
	 * <p>
	 * Creates a new instance using the provided bundle context
	 * </p>
	 * 
	 * @param bundleContext
	 *            a {@code BundleContext} instance providing access to the
	 *            context of the bundle containing this class
	 */
	public StanbolCamelContext(BundleContext bundleContext) {
		super(bundleContext);
	}

}
