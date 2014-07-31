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

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.stanbol.commons.web.base.resource.LayoutConfiguration;
import org.apache.stanbol.commons.web.viewable.Viewable;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.ContentItemFactory;
import org.apache.stanbol.enhancer.servicesapi.EnhancementException;
import org.apache.stanbol.enhancer.servicesapi.impl.StringSource;
import org.apache.stanbol.workflow.jersey.service.WorkflowRouteUploaderService;
import org.apache.stanbol.workflow.servicesapi.RouteManager;
import org.apache.stanbol.workflow.servicesapi.StanbolRoute;
import org.apache.stanbol.workflow.servicesapi.WorkflowJobManager;
import org.apache.stanbol.workflow.servicesapi.exception.WorkflowException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract super class for all workflow endpoint that do also provide the
 * Stanbol Enhancer Web UI. This includes "/workflow" and /workflow/{workflowRoute}
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class GenericWorkflowEnhancerUiResource extends AbstractWorkflowEnhancerResource {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    protected final RouteManager routeManager;
    protected final Serializer serializer;
    protected final String routeId;
    private List<StanbolRoute> routes;
    private WorkflowRouteUploaderService routeUploaderService;
    
    public GenericWorkflowEnhancerUiResource(String routeId,
    		WorkflowRouteUploaderService routeUploaderService,
            WorkflowJobManager jobManager, 
            RouteManager routeManager,
            ContentItemFactory ciFactory,
            Serializer serializer, 
            LayoutConfiguration layoutConfiguration, 
            UriInfo uriInfo) {
    	super(jobManager, ciFactory, layoutConfiguration, uriInfo);
        this.serializer = serializer;
        this.routeManager = routeManager;
        this.routeUploaderService = routeUploaderService;
        
        if(routeId == null){
            this.routeId = WorkflowJobManager.DEFAULT_ROUTE_NAME;
        } else {
            this.routeId = routeId;
        }
        if(this.routeId == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    /**
     * <p>Get the registered routes</p>
     * @return a {@code List} containing the registered routes
     */
    public List<StanbolRoute> getRoutes() {
    	if(this.routes == null)
    		this.routes = routeManager.getRoutes();
    	
    	return this.routes;
    }
    
    @GET
    @Produces(TEXT_HTML)
    public Response get(@Context HttpHeaders headers) {
        ResponseBuilder res = Response.ok(new Viewable("index", this, GenericWorkflowEnhancerUiResource.class), TEXT_HTML);
//        addCORSOrigin(servletContext, res, headers);
        return res.build();
    }

    /**
     * Form-based OpenCalais-compatible interface
     * 
     * TODO: should we parse the OpenCalais paramsXML and find the closest Stanbol Enhancer semantics too?
     * 
     * Note: the format parameter is not part of the official API
     * 
     * @throws WorkflowException
     *             if the content is somehow corrupted
     * @throws IOException
     */
    @POST
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response enhanceFromForm(@FormParam("content") String content, 
                                    @FormParam("format") String format, 
                                    @FormParam("ajax") boolean buildAjaxview, 
                                    @Context HttpHeaders headers) throws EnhancementException,
                                                                         IOException {
        log.info("enhance from From: " + content);
        ContentItem ci = ciFactory.createContentItem(new StringSource(content));
        if(!buildAjaxview){ //rewrite to a normal EnhancementRequest
            return enhanceFromData(ci, false, null, false, null, false, null, headers);
        } else { //enhance and build the AJAX response
            EnhancementException enhancementException;
            try {
                enhance(ci, null);
                enhancementException = null;
            } catch (EnhancementException e){
                enhancementException = e;
            }
            ContentItemResource contentItemResource = new ContentItemResource(null, ci, getUriInfo(), "",
                    serializer, getLayoutConfiguration(), enhancementException);
            contentItemResource.setRdfSerializationFormat(format);
            Viewable ajaxView = new Viewable("/ajax/contentitem", contentItemResource, ContentItemResource.class);
            ResponseBuilder rb = Response.ok(ajaxView);
            rb.header(HttpHeaders.CONTENT_TYPE, TEXT_HTML + "; charset=UTF-8");
            //addCORSOrigin(servletContext, rb, headers);
            return rb.build();
        }
    }

    /**
     * <p>Uploads a route to the routes directory, so that the {@code RouteInstaller} component can install the new route</p>
     * <p>It should be noted that if the route file can be copied to the routes directory successfully, the method will return an OK response although
     * later, the {@code RouteInstaller} component can install or not the route in the Camel context</p>
     * 
     * @param inputStream the {@code InputStream} object containing the file content
     * @param cd The {@code FormDataContentDisposition} object containing some information about the uploaded file
     * @return the {@code Response} object specifying the status code and a message if it is an error response
     */
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public Response installRoute(@FormDataParam("file") InputStream inputStream, @FormDataParam("file") FormDataContentDisposition cd) {
    	log.debug("Installing route");
    	try {
    		routeUploaderService.uploadRoute(cd.getFileName(), inputStream);
			return Response.ok().build();
		} catch (IOException e) {
			log.debug("Error installing route. Reason: "+e.getMessage());
			return Response.serverError().entity("Error creating and installing the uploaded route: "+e.getMessage()).build();
		}
    	
    }
    
    public String getServiceUrl() {
        String uri = getUriInfo().getAbsolutePath().toString();
        return uri.charAt(uri.length()-1) == '/' ?
            uri.substring(0, uri.length()-1) : uri;
    }

	@Override
	public String getRouteId() {
		return this.routeId;
	}

    

}