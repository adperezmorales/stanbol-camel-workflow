package org.apache.stanbol.workflow.component.stanbol.siren;

import java.util.Dictionary;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.workflow.component.core.StanbolCamelComponent;
import org.apache.stanbol.workflow.component.stanbol.solr.StanbolSolrComponent;
import org.osgi.service.component.ComponentContext;

/**
 * <p>StanbolSirenComponent class</p>
 * <p>Camel component to deal with Siren IR in routes</p>
 * <p>This component leverages most of the functionality of {@link StanbolSolrComponent}</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
@org.apache.felix.scr.annotations.Component(immediate=true)
@Service(StanbolCamelComponent.class)
public class StanbolSirenComponent extends StanbolSolrComponent{

	/**
	 * <p>Default URI Scheme used if no one is configured</p>
	 */
	public static final String DEFAULT_URI_SCHEME = "stanbol-siren";
	
	/**
	 * <p>
	 * Activate method
	 * </p>
	 * 
	 * @param ce
	 *            The {@code ComponentContext} object
	 */
	@Activate
	public void activate(ComponentContext ce) {
		@SuppressWarnings("rawtypes")
		Dictionary props = ce.getProperties();
		uriScheme = (String) props
				.get(StanbolCamelComponent.URI_SCHEME_PROPERTY);
		uriScheme = uriScheme == null ? DEFAULT_URI_SCHEME : uriScheme;
	}
	
	/**
	 * <p>
	 * Creates the Endpoint using the component configuration
	 * </p>
	 */
	@Override
	protected Endpoint createEndpoint(String uri, String remaining,
			Map<String, Object> parameters) throws Exception {
		StanbolSirenEndpoint endpoint = new StanbolSirenEndpoint(uri, this,
				remaining, parameters);
		setProperties(endpoint, parameters);
		parameters.clear();
		return endpoint;

	}
}
