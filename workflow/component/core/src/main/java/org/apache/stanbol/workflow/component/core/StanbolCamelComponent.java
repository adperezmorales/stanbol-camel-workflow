package org.apache.stanbol.workflow.component.core;

import org.apache.camel.Component;

/**
 * <p>StanbolCamelComponent interface</p>
 * <p>It provides a configurable Osgi property to specify the name of the protocol managed by this component</p>
 * <p>It will be used by {@link ComponentManager} to register the components in the OSGI Camel context</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public interface StanbolCamelComponent {
	/**
	 * <p>Constant specifying the protocol name property<p>
	 */
	public static final String URI_SCHEME_PROPERTY = "workflow.component.uri.scheme";
	
	/**
	 * <p>Gets the URI Scheme managed by the component</p>
	 * @return String the URI scheme
	 */
	public String getURIScheme();

	/**
	 * <p>Sets the URI Scheme managed by the component</p>
	 * @param uriScheme The URI scheme to be used
	 */
	public void setURIScheme(String uriScheme);
	
	/**
	 * <p>Gets the StanbolCamelComponent instance as raw Camel Component</p>
	 * @return a {@code Component} instance
	 */
	public Component getAsCamelComponent();
		
}
