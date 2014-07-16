package org.apache.stanbol.workflow.component.chain;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.stanbol.enhancer.servicesapi.Chain;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EnhancementJobManager;
import org.apache.stanbol.workflow.servicesapi.helper.ContentItemWorkflowHelper;

/**
 * <p>ChainProducer class</p>
 * <p>Camel Producer for Chain component</p>
 *  
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class ChainProducer extends DefaultProducer {
	
	public ChainProducer(ChainEndpoint endpoint) {
		super(endpoint);
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		ContentItem ci = exchange.getIn().getBody(ContentItem.class);
    	
		ContentItemWorkflowHelper.setEnhancementRequestProperties(ci, ((ChainEndpoint)getEndpoint()).parameters);
    	Chain chain = ((ChainEndpoint)getEndpoint()).chain;
    	EnhancementJobManager jobManager = ((ChainEndpoint)getEndpoint()).jobManager;
    	jobManager.enhanceContent(ci, chain);
    	exchange.getIn().setBody(ci);
	}

}
