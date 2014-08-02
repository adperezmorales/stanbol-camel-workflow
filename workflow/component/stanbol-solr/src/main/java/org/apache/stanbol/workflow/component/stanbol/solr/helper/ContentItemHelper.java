package org.apache.stanbol.workflow.component.stanbol.solr.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.Resource;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.commons.io.IOUtils;
import org.apache.stanbol.commons.namespaceprefix.NamespacePrefixService;
import org.apache.stanbol.enhancer.engines.dereference.DereferenceConstants;
import org.apache.stanbol.enhancer.servicesapi.Blob;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.NoSuchPartException;
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * <p>ContentItemHelper class</p>
 * <p>Helper class to deal with {@link ContentItemHelper} class</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class ContentItemHelper {
	
	private static final Logger log = LoggerFactory
			.getLogger(ContentItemHelper.class);

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
	 * @throws IOException 
	 */
	public static final String getRawContent(ContentItem ci) throws IOException {
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
				is = ci.getBlob().getStream();
				break;
			}

		}

		return IOUtils.toString(is);
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
	public static final Multimap<String, String> extractFields(ContentItem ci,
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
}
