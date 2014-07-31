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
package org.apache.stanbol.workflow.artifact.route;

import java.io.File;
import java.io.FileInputStream;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.workflow.servicesapi.impl.StanbolRoutesRegistrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Apache Felix Fileinstall ArtifactInstaller for routes
 * </p>
 * <p>
 * Manages files with 'route' extension and load the routes in the Stanbol Camel
 * Context
 * </p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 * 
 */
@Component(immediate = true)
@Service(ArtifactInstaller.class)
public class RouteInstaller implements ArtifactInstaller {
	@Reference
	protected StanbolRoutesRegistrator routeRegistrator;

	private static final Logger log = LoggerFactory
			.getLogger(RouteInstaller.class);

	public RouteInstaller() {

	}

	public boolean canHandle(File artifact) {
		return !artifact.getName().startsWith(".")
				&& artifact.getName().endsWith(".route");
	}

	public void install(File artifact) throws Exception {
		setConfig(artifact);
	}

	public void update(File artifact) throws Exception {
		setConfig(artifact);
	}

	public void uninstall(File artifact) throws Exception {
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
	boolean setConfig(final File f) throws Exception {
		// Add the route to the context
		log.info("Registering Routes Contained in File " + f.getAbsolutePath());
		FileInputStream fis = new FileInputStream(f);
		Boolean result = routeRegistrator.addRoutes(fis, f.getName());
		if (!result) {
			// If the route could not be added, then remove the file so that in
			// the next Stanbol startup, this route is not tried to be added
			// again
			f.delete();
		}
		return result;
	}

	/**
	 * Remove the configuration.
	 * 
	 * @param f
	 *            File where the configuration in was defined.
	 * @return <code>true</code>
	 * @throws Exception
	 */
	boolean deleteConfig(File f) throws Exception {
		log.info("Unregistering Routes Contained in File "
				+ f.getAbsolutePath());
		return routeRegistrator.removeRoutes(f.getName());

	}

}
