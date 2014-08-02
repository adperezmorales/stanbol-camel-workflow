package org.apache.stanbol.workflow.component.stanbol.siren;
import java.io.IOException;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.solr.SolrConstants;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.workflow.component.stanbol.solr.SolrProducerWrapper;
import org.apache.stanbol.workflow.component.stanbol.solr.helper.ContentItemHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;

/**
 * <p>StanbolSirenProducer class</p>
 * <p>Camel producer for {@code StanbolSirenComponent} component</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class StanbolSirenProducer extends SolrProducerWrapper {
	
	private static final Logger log = LoggerFactory
			.getLogger(StanbolSirenProducer.class);

	public StanbolSirenProducer(StanbolSirenEndpoint endpoint,
			HttpSolrServer solrServer,
			ConcurrentUpdateSolrServer streamingSolrServer) {
		super(endpoint, solrServer, streamingSolrServer);
	}

	@Override
	protected void prepareExchangeToSolr(Exchange exchange, ContentItem ci,
			Set<String> fieldNames) {
		{
			Message in = exchange.getIn();
			/* Set insert operation */
			in.setHeader(SolrConstants.OPERATION, SolrConstants.OPERATION_INSERT);

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
			
			// Entities Json
			StanbolSirenEndpoint endpoint = (StanbolSirenEndpoint) this.getEndpoint();
			Multimap<String, String> enhancementMetadata = ContentItemHelper.extractFields(ci,
					fieldNames, endpoint.getComponent().getNamespacePrefixService());
			
			Gson gson = new Gson();
			String entitiesJson = gson.toJson(enhancementMetadata.asMap());
			in.setHeader(SolrConstants.FIELD + "json", entitiesJson);
		}
	}

}
