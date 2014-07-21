package org.apache.stanbol.workflow.component.stanbol.solr;

import java.util.Dictionary;
import java.util.Map;

import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.component.solr.SolrComponent;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.stanbol.commons.namespaceprefix.NamespacePrefixService;
import org.apache.stanbol.workflow.component.core.StanbolCamelComponent;
import org.osgi.service.component.ComponentContext;

/**
 * <p>ActiveMQStanbolCamelComponent class</p>
 * <p>Camel component to deal with activemq in routes</p>
 * <p>This component simply wraps
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
@org.apache.felix.scr.annotations.Component(immediate=true)
@Service(StanbolCamelComponent.class)
public class StanbolSolrComponent extends SolrComponent implements StanbolCamelComponent {

	/**
	 * <p>Default URI Scheme used if no one is configured</p>
	 */
	public static final String DEFAULT_URI_SCHEME = "stanbol-solr";
	
	/**
	 * <p>Namespace prefix service</p>
	 */
	@Reference
	private NamespacePrefixService namespacePrefixService;
	
	/**
	 * <p>URI Scheme managed by this component</p>
	 */
	@Property(name=URI_SCHEME_PROPERTY)
	protected String uriScheme;
	
	/**
	 * <p>Gets the URI Scheme managed by the component</p>
	 * @return String the URI scheme
	 */
	public String getURIScheme() {
		return uriScheme;
	}

	/**
	 * <p>Sets the URI Scheme managed by the component</p>
	 * @param uriScheme The URI scheme to be used
	 */
	public void setURIScheme(String uriScheme) {
		this.uriScheme = uriScheme;
	}

	/**
	 * <p>Activate method</p>
	 * @param ce The {@code ComponentContext} object
	 */
	@Activate
	public void activate(ComponentContext ce) {
		@SuppressWarnings("rawtypes")
		Dictionary props = ce.getProperties();
		uriScheme = (String) props.get(StanbolCamelComponent.URI_SCHEME_PROPERTY);
		uriScheme = uriScheme == null ? DEFAULT_URI_SCHEME : uriScheme;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.component.core.StanbolCamelComponent#getAsCamelComponent()
	 */
	@Override
	public Component getAsCamelComponent() {
		return (Component) this;
	}

	/**
	 * <p>Creates the Endpoint using the component configuration</p>
	 */
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		StanbolSolrEndpoint endpoint = new StanbolSolrEndpoint(uri, this, remaining, parameters);
		setProperties(endpoint, parameters);
		parameters.clear();
		return endpoint;
		
	}
	
	/**
	 * <p>Gets the Solr Server used to insert, delete and commit documents</p>
	 * 
	 * @param endpoint The {@code StanbolSolrEndpoint} instance
	 * @return the Solr server associated with this endpoint
	 */
	public HttpSolrServer getSolrServer(StanbolSolrEndpoint endpoint) {
		return this.getSolrServers(endpoint).getSolrServer();
	}
	
	/**
	 * <p>Gets the Solr server used to insert documents in streaming mode</p>
	 * 
	 * @param endpoint The {@code StanbolSolrEndpoint} instance
	 * @return the streaming Solr Server associated with this endpoint
	 */
	public ConcurrentUpdateSolrServer getUpdateSolrServer(StanbolSolrEndpoint endpoint) {
		return this.getSolrServers(endpoint).getUpdateSolrServer();
	}
	
	/**
	 * <p>Sets the {@code NamespacePrefixProvider} instance</p>
	 * @param ns
	 */
	public void setNamespacePrefixService(NamespacePrefixService ns) {
		this.namespacePrefixService = ns;
	}
	
	/**
	 * <p>Gets the {@code NamespacePrefixProvider} instance</p>
	 * @param ns
	 */
	public NamespacePrefixService getNamespacePrefixService() {
		return this.namespacePrefixService;
	}
	

}
