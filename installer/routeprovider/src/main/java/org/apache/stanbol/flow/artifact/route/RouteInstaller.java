/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.stanbol.flow.artifact.route;

import java.io.File;
import java.io.FileInputStream;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.enhancer.servicesapi.FlowJobManager;
import org.apache.stanbol.flow.cameljobmanager.impl.CamelJobManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ArtifactInstaller for routes.
 * TODO: This service lifecycle should be bound to the ConfigurationAdmin service lifecycle.
 */
@Component(immediate=true)
@Service(ArtifactInstaller.class)
public class RouteInstaller implements ArtifactInstaller
{
    private BundleContext context;
    
    @Reference
    protected FlowJobManager jobManager;
    
    private static final Logger log = LoggerFactory.getLogger(RouteInstaller.class);
//    private final FileInstall fileInstall;
    @SuppressWarnings("rawtypes")
	private ServiceRegistration registration;

    public RouteInstaller(){
    	
    }
   
    public void destroy()
    {
        registration.unregister();
    }

    @Activate
    public void activate(ComponentContext cc) {
    	this.context = cc.getBundleContext();
    }
    
    public boolean canHandle(File artifact)
    {
        return !artifact.getName().startsWith(".") && artifact.getName().endsWith(".route");
    }

    public void install(File artifact) throws Exception
    {
        setConfig(artifact);
    }

    public void update(File artifact) throws Exception
    {
        setConfig(artifact);
    }

    public void uninstall(File artifact) throws Exception
    {
        deleteConfig(artifact);
    }

    /**
     * Set the configuration based on the config file.
     *
     * @param f
     *            Configuration file
     * @return <code>true</code> if the configuration has been updated
     * @throws Exception
     */
    boolean setConfig(final File f) throws Exception
    {
    	//Add the route to the context
    	FileInputStream fis = new FileInputStream(f);
    	CamelJobManager manager = (CamelJobManager) jobManager;
    	return manager.addRoutes(fis, f.getName());
    }

    /**
     * Remove the configuration.
     *
     * @param f
     *            File where the configuration in was defined.
     * @return <code>true</code>
     * @throws Exception
     */
    boolean deleteConfig(File f) throws Exception
    {
        //Delete route from Context
    	CamelJobManager manager = (CamelJobManager) jobManager;
    	return manager.removeRoutes(f.getName());
        
    }

}
