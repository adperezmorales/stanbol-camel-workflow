package org.apache.stanbol.workflow.component.activemq;

import java.util.Dictionary;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.Component;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
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
public class ActiveMQStanbolCamelComponent extends ActiveMQComponent implements StanbolCamelComponent {

	/**
	 * <p>Default URI Scheme used if no one is configured</p>
	 */
	public static final String DEFAULT_URI_SCHEME = "activemq";
	
	/**
	 * <p>Configured broker URL property</p>
	 */
	@Property
	static final String BROKER_URL = "workflow.component.activemq.brokerurl";
	
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
		String brokerUrl = (String) props.get(BROKER_URL);
		if(brokerUrl != null)
			setBrokerURL(brokerUrl);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.stanbol.workflow.component.core.StanbolCamelComponent#getAsCamelComponent()
	 */
	@Override
	public Component getAsCamelComponent() {
		return (Component) this;
	}
	
	

}
