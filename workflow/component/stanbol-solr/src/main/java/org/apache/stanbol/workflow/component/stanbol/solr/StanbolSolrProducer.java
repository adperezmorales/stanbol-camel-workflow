package org.apache.stanbol.workflow.component.stanbol.solr;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.solr.SolrConstants;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.workflow.component.stanbol.solr.helper.ContentItemHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;

/**
 * <p>StanbolSolrProducer class</p>
 * <p>Camel producer for {@code StanbolSolrComponent} component</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class StanbolSolrProducer extends SolrProducerWrapper {
	
	private static final Logger log = LoggerFactory
			.getLogger(StanbolSolrProducer.class);

	public StanbolSolrProducer(StanbolSolrEndpoint endpoint,
			HttpSolrServer solrServer,
			ConcurrentUpdateSolrServer streamingSolrServer) {
		super(endpoint, solrServer, streamingSolrServer);
	}

	@Override
	protected void prepareExchangeToSolr(Exchange exchange, ContentItem ci,
			Set<String> fieldNames) {
		
		{
			StanbolSolrEndpoint endpoint = (StanbolSolrEndpoint) this.getEndpoint();
			Multimap<String, String> enhancementMetadata = ContentItemHelper.extractFields(ci,
					fieldNames, endpoint.getComponent().getNamespacePrefixService());
			Message in = exchange.getIn();
			/* Set insert operation */
			in.setHeader(SolrConstants.OPERATION, SolrConstants.OPERATION_INSERT);
			Map<String, Collection<String>> map = enhancementMetadata.asMap();

			/* Add fields */
			for (String key : map.keySet()) {
				if (map.containsKey(key)) {
					in.setHeader(SolrConstants.FIELD + key, map.get(key));
				}
			}

			/* Set the content field */
			/*
			 * If tika has been used in the enhancement chain, use the parsed tika
			 * content If not, transform the ContentItem stream to string to be the
			 * content
			 */
			UriRef ref = ci.getUri();

			/* Set ID field */
			in.setHeader(SolrConstants.FIELD + "id", ref.getUnicodeString());

			/* Set content field */
			String content = new String();
			try {
				content = ContentItemHelper.getRawContent(ci);
			} catch (IOException e) {
				log.error("Unable to Extract Text Content from ContentItem", e);
			}
			in.setHeader(SolrConstants.FIELD + "content", content);

		}
	}

}
