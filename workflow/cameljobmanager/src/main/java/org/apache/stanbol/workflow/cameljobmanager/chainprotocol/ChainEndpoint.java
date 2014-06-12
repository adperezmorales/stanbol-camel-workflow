package org.apache.stanbol.workflow.cameljobmanager.chainprotocol;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.stanbol.enhancer.servicesapi.Chain;

public class ChainEndpoint extends DefaultEndpoint {
	
	final Chain chain;
	
	public ChainEndpoint(String uri, ChainComponent component, Chain chain) {
        super(uri, component);
        this.chain = chain;
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
