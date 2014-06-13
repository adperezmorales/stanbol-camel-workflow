package org.apache.stanbol.workflow.cameljobmanager.chainprotocol;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.felix.scr.annotations.Reference;
import org.apache.stanbol.enhancer.servicesapi.Chain;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EnhancementJobManager;

public class ChainProducer extends DefaultProducer {
	
	public ChainProducer(ChainEndpoint endpoint) {
		super(endpoint);
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		ContentItem ci = exchange.getIn().getBody(ContentItem.class);
    	
    	Chain chain = ((ChainEndpoint)getEndpoint()).chain;
    	
    	((ChainEndpoint)getEndpoint()).jobManager.enhanceContent(ci, chain);
    	exchange.getIn().setBody(ci);
	}

}
