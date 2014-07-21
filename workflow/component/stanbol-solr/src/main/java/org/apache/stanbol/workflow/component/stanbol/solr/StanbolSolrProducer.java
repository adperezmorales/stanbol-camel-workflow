package org.apache.stanbol.workflow.component.stanbol.solr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.solr.SolrConstants;
import org.apache.camel.component.solr.SolrProducer;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.Resource;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.stanbol.commons.namespaceprefix.NamespacePrefixService;
import org.apache.stanbol.enhancer.engines.dereference.DereferenceConstants;
import org.apache.stanbol.enhancer.servicesapi.Blob;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.NoSuchPartException;
import org.apache.stanbol.enhancer.servicesapi.helper.ContentItemHelper;
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.apache.stanbol.workflow.component.stanbol.solr.helper.LDPathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * <p>
 * StanbolSolrProducer class
 * </p>
 * <p>
 * Camel Producer for Stanbol Solr component
 * </p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 * 
 */
public class StanbolSolrProducer extends SolrProducer {

	/**
	 * <p>
	 * Logger
	 * </p>
	 */
	private final Logger log = LoggerFactory
			.getLogger(StanbolSolrProducer.class);

	/**
	 * <p>
	 * StanbolSolrProducer constructor
	 * </p>
	 * 
	 * @param endpoint
	 *            The {@code StanbolSolrEndpoint} used to create this producer
	 * @param solrServer
	 *            The Solr server
	 * @param streamingSolrServer
	 *            The streaming Solr server
	 */
	public StanbolSolrProducer(StanbolSolrEndpoint endpoint,
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

		Set<String> fieldNames = new HashSet<String>();

		/* Checking configured LDPath */
		String ldpath = this.extractLdpath(ci, endpoint);

		if (ldpath != null) {
			log.info("LDPath program found. Processing configured fields");
			fieldNames.addAll(LDPathHelper.getFieldsFromProgramString(ldpath));
		}

		/* Checking configured fields */
		List<String> configuredFields = endpoint.getFields();
		if (configuredFields != null) {
			fieldNames.addAll(configuredFields);
		}

		Multimap<String, String> enhancementMetadata = processFields(ci,
				fieldNames, endpoint.getComponent().getNamespacePrefixService());
		prepareExchangeToSolr(exchange, enhancementMetadata);

		super.process(exchange);

	}

	/**
	 * <p>
	 * Extracts the ldpath program to be used according to the endpoint
	 * configuration
	 * </p>
	 * 
	 * @param ci
	 *            The {@code ContentItem} object where to get the dereference
	 *            engine from
	 * @param endpoint
	 *            The {@code StanbolSolrEndpoint} instance
	 * @return
	 */
	private String extractLdpath(ContentItem ci, StanbolSolrEndpoint endpoint) {
		/* Return ldpath if exists in the configuration */
		if (endpoint.getLdpath() != null) {
			return endpoint.getLdpath();
		}
		/*
		 * If no ldpath defined and useDereferenceLdpath flag is enabled, obtain
		 * the ldpath from enhancer request properties
		 */
		else if (endpoint.getUseDereferenceLdpath()) {
			Map<String, Object> enhancerRequestProperties = ContentItemHelper
					.getRequestPropertiesContentPart(ci);
			return (String) enhancerRequestProperties
					.get(DereferenceConstants.DEREFERENCE_ENTITIES_LDPATH);
		} else {
			return null;
		}
	}

	/**
	 * <p>
	 * Process the content item to extract the content of the fields
	 * </p>
	 * 
	 * @param ci
	 *            The {@code ContentItem} object
	 * @param fields
	 *            The {@code Set} of fields to be extracted
	 * 
	 * @param nsService
	 *            The {@code NamespacePrefixService} used to transform prefixed
	 *            defined fields in its full form
	 * @return a {@code Multimap}
	 */
	private Multimap<String, String> processFields(ContentItem ci,
			Set<String> fields, NamespacePrefixService nsService) {
		Multimap<String, String> properties = HashMultimap.create();

		MGraph metadata = ci.getMetadata();

		/* Process entity-reference configured properties */
		for (UriRef referenceProperty : DereferenceConstants.DEFAULT_ENTITY_REFERENCES) {
			Iterator<Triple> entityReferences = metadata.filter(null,
					referenceProperty, null);
			/* Process the references */
			while (entityReferences.hasNext()) {
				Triple triple = entityReferences.next();
				Resource entityReference = triple.getObject();
				/* Process the entities */
				if ((entityReference instanceof UriRef)) {
					UriRef entityUri = (UriRef) entityReference;
					/* Process fields per entity */
					for (String field : fields) {
						/*
						 * Transform to full form if NamespacePrefixService
						 * exists
						 */
						String fullFieldName = nsService != null ? nsService
								.getFullName(field) : field;
						if (fullFieldName != null) {
							Iterator<String> it = EnhancementEngineHelper
									.getStrings(metadata, entityUri,
											new UriRef(field));
							/* Adding the field values */
							while (it.hasNext()) {
								String s = it.next();
								properties.put(field, s);
							}
						} else {
							log.info("No mapping found for namespace of the field "
									+ field);
						}
					}
				} else if (log.isTraceEnabled()) {
					log.trace(" ... ignore Entity {} (referenced-by: {})",
							entityReference, referenceProperty);
				}
			}
		}
		return properties;
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
	private void prepareExchangeToSolr(Exchange exchange,
			Multimap<String, String> enhancementMetadata) {
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
		ContentItem ci = exchange.getIn().getBody(ContentItem.class);
		UriRef ref = ci.getUri();

		/* Set ID field */
		in.setHeader(SolrConstants.FIELD + "id", ref.getUnicodeString());

		/* Set content field */
		String content = getRawContent(ci);
		in.setHeader(SolrConstants.FIELD + "content", content);

	}

	/**
	 * <p>
	 * Finds the raw content of the ContentItem if tika is present in the
	 * enhancement chain
	 * </p>
	 * <p>
	 * If not, the ContentItem content is used and converted to String
	 * </p>
	 * 
	 * @param ci
	 *            The {@code ContentItem} used to find the text
	 * @return a {@code String} object representing the content
	 */
	private String getRawContent(ContentItem ci) {
		InputStream is = null;
		UriRef uriRef = null;
		for (int i = 0; true; i++) {
			try {
				uriRef = ci.getPartUri(i);
				if (uriRef.getUnicodeString().startsWith("urn:tika:text:")) {
					is = ci.getPart(uriRef, Blob.class).getStream();
					break;
				}
			} catch (NoSuchPartException nspe) {
				log.info("No tika raw content found");
				is = ci.getBlob().getStream();
				break;
			}

		}

		String result = "";
		if (is != null) {
			try {
				result = IOUtils.toString(ci.getStream());
			} catch (IOException e) {
				log.info("Error converting ContentItem stream to String");

			}
		}

		return result;

	}
}
