/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.flow.jersey.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.N3;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.N_TRIPLE;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.RDF_JSON;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.RDF_XML;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.TURTLE;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.X_TURTLE;
import static org.apache.stanbol.commons.web.base.utils.MediaTypeUtil.JSON_LD;
import static org.apache.stanbol.flow.jersey.utils.EnhancerUtils.addActiveChains;
import static org.apache.stanbol.flow.jersey.utils.EnhancerUtils.addActiveEngines;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.apache.clerezza.rdf.core.impl.TripleImpl;
import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.core.sparql.QueryEngine;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.web.base.resource.BaseStanbolResource;
import org.apache.stanbol.commons.web.base.resource.LayoutConfiguration;
import org.apache.stanbol.enhancer.servicesapi.ChainManager;
import org.apache.stanbol.enhancer.servicesapi.ContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngineManager;
import org.apache.stanbol.enhancer.servicesapi.EnhancementJobManager;
import org.apache.stanbol.enhancer.servicesapi.FlowJobManager;
import org.apache.stanbol.enhancer.servicesapi.rdf.Enhancer;

/**
 * RESTful interface to browse the list of available engines and allow to call
 * them in a stateless, synchronous way.
 * <p>
 * If you need the content of the extractions to be stored on the server, use
 * the StoreRootResource API instead.
 */
@Component
@Service(Object.class)
@Property(name = "javax.ws.rs", boolValue = true)
@Path("/flow")
public final class FlowRootResource extends BaseStanbolResource {

    @Reference
    private FlowJobManager jobManager;
    @Reference
    private EnhancementEngineManager engineManager;
    @Reference
    private ChainManager chainManager;
    @Reference
    private ContentItemFactory ciFactory;
    @Reference
    private Serializer serializer;
    @Reference
    private QueryEngine queryEngine;
    
    @Path("")
    public EnhancerResource get() {
    	return new EnhancerResource(jobManager, engineManager, 
                chainManager, ciFactory, serializer, getLayoutConfiguration(),
                getUriInfo());
    }
            
    public class EnhancerResource extends GenericEnhancerUiResource {
        public EnhancerResource(
            FlowJobManager jobManager, 
            EnhancementEngineManager engineManager, 
            ChainManager chainManager, 
            ContentItemFactory ciFactory,
            Serializer serializer,
            LayoutConfiguration layoutConfiguration, 
            UriInfo uriInfo) {
            super(null, jobManager, engineManager, chainManager, ciFactory, 
                    serializer, layoutConfiguration, uriInfo);
        }

        @GET
        @Produces(value = {JSON_LD, APPLICATION_JSON, N3, N_TRIPLE, RDF_JSON, RDF_XML, TURTLE, X_TURTLE})
        public Response getEngines(@Context HttpHeaders headers) {
            MGraph graph = getEnhancerConfigGraph();
            ResponseBuilder res = Response.ok(graph);
            //addCORSOrigin(servletContext,res, headers);
            return res.build();
        }

        /**
         * Creates the RDF graph for the current Stanbol Enhancer configuration
         *
         * @return the graph with the configuration
         */
        private MGraph getEnhancerConfigGraph() {
            String rootUrl = getUriInfo().getBaseUriBuilder().path(getRootUrl()).build().toString();
            UriRef enhancerResource = new UriRef(rootUrl + "enhancer");
            MGraph graph = new SimpleMGraph();
            graph.add(new TripleImpl(enhancerResource, RDF.type, Enhancer.ENHANCER));
            addActiveEngines(engineManager, graph, rootUrl);
            addActiveChains(chainManager, graph, rootUrl);
            return graph;
        }

    }
}
