package org.apache.stanbol.workflow.component.stanbol.solr;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.component.solr.SolrProducer;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.workflow.component.stanbol.solr.helper.LDPathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * SolrProducerWrapper class
 * </p>
 * <p>
 * Camel Producer Wrapper for Stanbol Solr component. It will be used by {@code StanbolSolrProducer} and {@SirenProducer
 * </p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 * 
 */
public abstract class SolrProducerWrapper extends SolrProducer {

	/**
	 * <p>
	 * Logger
	 * </p>
	 */
	private final Logger log = LoggerFactory
			.getLogger(SolrProducerWrapper.class);

	/**
	 * <p>
	 * SolrProducerWrapper constructor
	 * </p>
	 * 
	 * @param endpoint
	 *            The {@code StanbolSolrEndpoint} used to create this producer
	 * @param solrServer
	 *            The Solr server
	 * @param streamingSolrServer
	 *            The streaming Solr server
	 */
	public SolrProducerWrapper(StanbolSolrEndpoint endpoint,
			HttpSolrServer solrServer,
			ConcurrentUpdateSolrServer streamingSolrServer) {
		super(endpoint, solrServer, streamingSolrServer);
	}

	/**
	 * <p>
	 * Process the exchange received
	 * </p>
	 * <p>
	 * The contained content item contained in the message body will be
	 * processed to extract enhancement metadata to
	 */
	public void process(Exchange exchange) throws Exception {

		log.info("Processing exchange in Stanbol Solr component");
		ContentItem ci = exchange.getIn().getBody(ContentItem.class);
		StanbolSolrEndpoint endpoint = (StanbolSolrEndpoint) this.getEndpoint();
		
		Set<String> fieldNames = getFieldsToExtract(endpoint, ci);
		
		prepareExchangeToSolr(exchange, ci, fieldNames);
		super.process(exchange);

	}
	
	protected Set<String> getFieldsToExtract(StanbolSolrEndpoint endpoint, ContentItem ci){
		Set<String> fieldNames = new HashSet<String>();

		/* Checking configured LDPath */
		String ldpath = endpoint.getLdpath();
		if((ldpath == null || ldpath.isEmpty()) && endpoint.getUseDereferenceLdpath())
			ldpath = LDPathHelper.extractLdpath(ci); 

		if (ldpath != null && !ldpath.isEmpty()) {
			log.info("LDPath program found. Processing configured fields");
			fieldNames.addAll(LDPathHelper.getFieldsFromProgramString(ldpath));
		}

		/* Checking configured fields */
		List<String> configuredFields = endpoint.getFields();
		if (configuredFields != null) {
			fieldNames.addAll(configuredFields);
		}

		return fieldNames;
	}

	/**
	 * <p>
	 * Adds the needed headers to the exchange object used by the
	 * {@link SolrProducer}
	 * </p>
	 * <p>
	 * These headers are used to prepare the Solr document to be indexed in Solr
	 * </p>
	 * 
	 * @param exchange
	 *            the {@code Exchange} object
	 * @param enhancementMetadata
	 *            the {@code Set} containing the fields and values to be indexed
	 *            in Solr
	 */
	protected abstract void prepareExchangeToSolr(Exchange exchange,
			ContentItem ci, Set<String> fieldNames);	
}
