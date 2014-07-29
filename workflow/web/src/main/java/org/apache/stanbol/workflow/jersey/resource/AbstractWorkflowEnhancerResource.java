/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.stanbol.workflow.jersey.resource;

import static javax.ws.rs.core.MediaType.WILDCARD;
import static org.apache.stanbol.workflow.jersey.utils.RequestPropertiesHelper.INCLUDE_EXECUTION_METADATA;
import static org.apache.stanbol.workflow.jersey.utils.RequestPropertiesHelper.OMIT_METADATA;
import static org.apache.stanbol.workflow.jersey.utils.RequestPropertiesHelper.OMIT_PARSED_CONTENT;
import static org.apache.stanbol.workflow.jersey.utils.RequestPropertiesHelper.OUTPUT_CONTENT;
import static org.apache.stanbol.workflow.jersey.utils.RequestPropertiesHelper.OUTPUT_CONTENT_PART;
import static org.apache.stanbol.workflow.jersey.utils.RequestPropertiesHelper.RDF_FORMAT;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.stanbol.commons.web.base.resource.LayoutConfiguration;
import org.apache.stanbol.commons.web.base.resource.TemplateLayoutConfiguration;
import org.apache.stanbol.commons.web.base.utils.MediaTypeUtil;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.ContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementException;
import org.apache.stanbol.enhancer.servicesapi.NoSuchPartException;
import org.apache.stanbol.enhancer.servicesapi.helper.ContentItemHelper;
import org.apache.stanbol.enhancer.servicesapi.rdf.ExecutionMetadata;
import org.apache.stanbol.workflow.jersey.utils.RequestPropertiesHelper;
import org.apache.stanbol.workflow.servicesapi.WorkflowJobManager;

/**
 * Abstract super class for all workflow endpoints that do not use/support
 * the default Enhancer Web UI.<p>
 * This is mainly used for supporting enhancement requests to single
 * route.
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public abstract class AbstractWorkflowEnhancerResource extends TemplateLayoutConfiguration {

    protected final WorkflowJobManager workflowJobManager;
    protected final ContentItemFactory ciFactory;
    
    private LayoutConfiguration layoutConfiguration;
    private UriInfo uriInfo;

    public AbstractWorkflowEnhancerResource(
            WorkflowJobManager jobManager, 
            ContentItemFactory ciFactory,
            LayoutConfiguration layoutConfiguration,
            UriInfo uriInfo) {
        this.workflowJobManager = jobManager;
        this.ciFactory = ciFactory;
        this.layoutConfiguration = layoutConfiguration;
        this.uriInfo = uriInfo;
    }
    
    protected LayoutConfiguration getLayoutConfiguration() {
        return layoutConfiguration;
    }
    
    protected UriInfo getUriInfo() {
        return uriInfo;
    }

    /**
     * Getter for the Enhancement route
     * @return the route id
     */
    protected abstract String getRouteId();
    
    /**
     * Media-Type based handling of the raw POST data.
     * 
     * @param data
     *            binary payload to analyze
     * @param uri
     *            optional URI for the content items (to be used as an identifier in the enhancement graph)
     * @throws EngineException
     *             if the content is somehow corrupted
     * @throws IOException
     */
    @POST
    @Consumes(WILDCARD)
    public Response enhanceFromData(ContentItem ci,
            //NOTE: The 'uri' parameter is already consumed by the ContentItemReader
            //@QueryParam(value = "uri") String uri,
            @QueryParam(value = "executionmetadata") boolean inclExecMetadata,
            @QueryParam(value = "outputContent") Set<String> mediaTypes,
            @QueryParam(value = "omitParsed") boolean omitParsed,
            @QueryParam(value = "outputContentPart") Set<String> contentParts,
            @QueryParam(value = "omitMetadata") boolean omitMetadata,
            @QueryParam(value = "rdfFormat") String rdfFormat,
            @Context HttpHeaders headers) throws EnhancementException, IOException {
        Map<String,Object> reqProp = ContentItemHelper.initRequestPropertiesContentPart(ci);
        reqProp.put(INCLUDE_EXECUTION_METADATA, inclExecMetadata);
        if(mediaTypes != null && !mediaTypes.isEmpty()){
            reqProp.put(OUTPUT_CONTENT, mediaTypes);
        }
        reqProp.put(OMIT_PARSED_CONTENT, omitParsed);
        if(contentParts != null && !contentParts.isEmpty()){
            Set<UriRef> outputContentParts = new HashSet<UriRef>();
            for(String contentPartUri : contentParts){
                if(contentPartUri != null && !contentPartUri.isEmpty()){
                    if("*".equals(contentPartUri)){
                        outputContentParts.add(null); //indicated wildcard
                    } else {
                        outputContentParts.add(new UriRef(contentPartUri));
                    }
                }
            }
            reqProp.put(OUTPUT_CONTENT_PART, outputContentParts);
        }
        reqProp.put(OMIT_METADATA, omitMetadata);
        if(rdfFormat != null && !rdfFormat.isEmpty()){
            try {
                reqProp.put(RDF_FORMAT,MediaType.valueOf(rdfFormat).toString());
            } catch (IllegalArgumentException e) {
                throw new WebApplicationException(e, 
                    Response.status(Response.Status.BAD_REQUEST)
                    .entity(String.format("Unable to parse MediaType form parameter" +
                    		"rdfFormat=%s",rdfFormat))
                    .build());
            }
        }
        enhance(ci,reqProp);
        ResponseBuilder rb = Response.ok(ci);
        MediaType mediaType = MediaTypeUtil.getAcceptableMediaType(headers, null);
        if (mediaType != null) {
            rb.header(HttpHeaders.CONTENT_TYPE, mediaType);
        }
        //addCORSOrigin(servletContext, rb, headers);
        return rb.build();
    }

    /**
     * Enhances the parsed ContentItem
     * @param ci the content item to enhance
     * @param reqProp the request properties or <code>null</code> if none
     * @throws EnhancementException
     */
    protected void enhance(ContentItem ci, Map<String,Object> reqProp) throws EnhancementException {
        if (workflowJobManager != null) {
            workflowJobManager.enhanceContent(ci, getRouteId());
        }
        MGraph graph = ci.getMetadata();
        Boolean includeExecutionMetadata = RequestPropertiesHelper.isIncludeExecutionMetadata(reqProp);
        if (includeExecutionMetadata != null && includeExecutionMetadata.booleanValue()) {
            try {
                graph.addAll(ci.getPart(ExecutionMetadata.CHAIN_EXECUTION, TripleCollection.class));
            } catch (NoSuchPartException e) {
                // no executionMetadata available
            }
        }
    }

}