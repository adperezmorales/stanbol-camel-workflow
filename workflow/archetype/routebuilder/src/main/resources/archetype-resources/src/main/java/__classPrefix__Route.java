#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.engines.langdetect.LanguageDetectionEnhancementEngine;
import org.apache.stanbol.enhancer.engines.tika.TikaEngine;

/**
 * ${classPrefix} Camel Route
 * 
 */
@Component(immediate=true)
@Service(RoutesBuilder.class)
public class ${classPrefix}Route extends RouteBuilder {
	
	/**
	 * Constant specifying the route name
	 */
	private static final String ROUTE_NAME = "${routeName}";
	
 	@Override
	public void configure() throws Exception {
   		RouteDefinition rd = from("direct://"+this.getRouteName()); 
		rd.setId("${routeName}Route");
		createRoute(rd);        
	}

	/**
	 * <p>Obtains the name of the route</p>
	 * @return the route name
	 */
	public String getRouteName() {
		return ROUTE_NAME;
	}
	
	/**
	 * <p>Method to create the route which starts with the direct endpoint called ${route.name}</p>
	 * @param rd an instance of {@code RouteDefinition}
	 */
	private void createRoute(RouteDefinition rd) {
		/* Create the route. For example, adding the 
		 * Enhancement Engines producers TikaEngine and LanguageDetectionEnhancementEngine
		 */
		rd = rd.to("engine://tika");
	        rd = rd.to("engine://langdetect");
	}
}
