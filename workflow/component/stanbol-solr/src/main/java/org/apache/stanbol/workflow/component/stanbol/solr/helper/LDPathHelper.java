package org.apache.stanbol.workflow.component.stanbol.solr.helper;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.marmotta.ldpath.LDPath;
import org.apache.marmotta.ldpath.api.backend.RDFBackend;
import org.apache.marmotta.ldpath.exception.LDPathParseException;
import org.apache.marmotta.ldpath.model.programs.Program;
import org.apache.stanbol.entityhub.ldpath.backend.AbstractBackend;
import org.apache.stanbol.entityhub.model.clerezza.RdfValueFactory;
import org.apache.stanbol.entityhub.servicesapi.EntityhubException;
import org.apache.stanbol.entityhub.servicesapi.model.Representation;
import org.apache.stanbol.entityhub.servicesapi.model.ValueFactory;
import org.apache.stanbol.entityhub.servicesapi.query.FieldQuery;
import org.apache.stanbol.entityhub.servicesapi.query.QueryResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * LDPathHelper class
 * </p>
 * <p>
 * Contains some utility methods to deal with LDPath programs
 * </p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 * 
 */
public class LDPathHelper {

	private static final Logger log = LoggerFactory
			.getLogger(LDPathHelper.class);

	/**
	 * <p>
	 * Gets the configured fields from the LDPath program
	 * </p>
	 * 
	 * @param program
	 *            The LDPath program in string format
	 * @return a {@code Set} containing the field names
	 */
	public static Set<String> getFieldsFromProgramString(String program) {
		Program<Object> ldpathProgram = parseProgram(program);
		return getFieldsFromProgram(ldpathProgram);
	}

	/**
	 * <p>
	 * Gets the configured fields from the LDPath program
	 * </p>
	 * 
	 * @param program
	 *            The LDPath program
	 * @return a {@code Set} containing the field names
	 */
	public static Set<String> getFieldsFromProgram(Program<Object> program) {

		Set<String> contextFields = new HashSet<String>();
		if (program != null) {
			for (org.apache.marmotta.ldpath.model.fields.FieldMapping<?, Object> mapping : program
					.getFields()) {
				try {
					new URI(mapping.getFieldName());
					contextFields.add(mapping.getFieldName());
				} catch (URISyntaxException e) {
					log.error("Parsed LDPath MUST use valid URIs as field names (invalid field name: '"
							+ mapping.getFieldName() + "')!");
				}
			}
		}

		return contextFields;
	}

	/**
	 * <p>
	 * Parses the given LDPath program in String format
	 * </p>
	 * 
	 * @param program
	 *            The LDPath program in String format
	 * @return The {@code Program<Object>} instance containing the LDPath
	 *         program
	 */
	private static Program<Object> parseProgram(String program) {

		Program<Object> ldpathProgram = null;
		RdfValueFactory valueFactory = RdfValueFactory.getInstance();
		RDFBackend<Object> parseBackend = new ParseBackend<Object>(valueFactory);
		LDPath<Object> parseLdPath = new LDPath<Object>(parseBackend);
		try {
			log.info("Parsing LDPath program");
			ldpathProgram = parseLdPath.parseProgram(new StringReader(program));
			log.info("LDPath program parsed successfully");
		} catch (LDPathParseException e) {
			log.error("Unable to parse Context LDPath pogram: \n {}", program);
		}

		return ldpathProgram;

	}

	/**
	 * <p>
	 * ParseBackend class
	 * </p>
	 * <p>
	 * Used for LDPath object to create and convert object types found in the
	 * program
	 * </p>
	 * 
	 * @author Antonio David Pérez Morales <adperezmorales@gmail.com>
	 * 
	 */
	private static final class ParseBackend<T> extends AbstractBackend {
		/**
	     * 
	     */
		private final ValueFactory valueFactory;

		/**
		 * @param trackingDereferencerBase
		 */
		public ParseBackend(ValueFactory vf) {
			this.valueFactory = vf;
		}

		@Override
		protected QueryResultList<String> query(FieldQuery query)
				throws EntityhubException {
			throw new UnsupportedOperationException("Not expected to be called");
		}

		@Override
		protected ValueFactory getValueFactory() {
			return valueFactory;
		}

		@Override
		protected Representation getRepresentation(String id)
				throws EntityhubException {
			throw new UnsupportedOperationException("Not expected to be called");
		}

		@Override
		protected FieldQuery createQuery() {
			throw new UnsupportedOperationException("Not expected to be called");
		}
	}
}
