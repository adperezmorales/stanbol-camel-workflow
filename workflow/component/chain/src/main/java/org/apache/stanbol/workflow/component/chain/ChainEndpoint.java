package org.apache.stanbol.workflow.component.chain;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.stanbol.enhancer.servicesapi.Chain;
import org.apache.stanbol.enhancer.servicesapi.EnhancementJobManager;

/**
 * <p>ChainEndpoint class</p>
 * <p>Camel endpoint for chain scheme</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class ChainEndpoint extends DefaultEndpoint {
	
	final Chain chain;
	
	final EnhancementJobManager jobManager;
	final Map<String,Object> parameters;
	public ChainEndpoint(String uri, ChainComponent component, Chain chain, EnhancementJobManager jobManager, Map<String, Object> parameters) {
        super(uri, component);
        this.chain = chain;
        this.jobManager = jobManager;
        this.parameters = new HashMap<String,Object>(parameters);
    }

	@Override
	public Producer createProducer() throws Exception {
		return new ChainProducer(this);
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		throw new UnsupportedOperationException("You cannot get messages from this endpoint: " + getEndpointUri());
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
