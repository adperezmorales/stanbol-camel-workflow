package org.apache.stanbol.workflow.jersey.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;

import com.google.common.io.Files;

/**
 * <p>WorkflowRouteUploaderService class</p>
 * <p>Service class allowing to upload a route into the Workflow Camel context</p>
 * <p>It leverages the functionality of the {@code RouteInstaller} component to install the route in the next watching cycle</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
@Component(immediate=true)
@Service(WorkflowRouteUploaderService.class)
public class WorkflowRouteUploaderService {

	/**
	 * <p>
	 * Property where the RouteInstaller
	 */
	private static final String ROUTES_DIR_PROPERTY = "felix.fileinstall.dir";
	
	private BundleContext bundleContext;
	private File routesFolder;
	
	@Activate
	public void activate(ComponentContext ce) throws ConfigurationException {
		this.bundleContext = ce.getBundleContext();

		String routesDir = this.bundleContext.getProperty(ROUTES_DIR_PROPERTY);
		if (routesDir != null) {
			routesFolder = new File(routesDir);
		} else {
			String slingHome = this.bundleContext.getProperty("sling.home");
			if (slingHome != null) {
				routesFolder = new File(slingHome, "routes");
			} else {
				routesFolder = new File("routes");
			}
		}

		if (!routesFolder.exists() && !routesFolder.mkdirs()) {
			throw new ConfigurationException(
					"RoutesFolder",
					"Unable to create the routes directory where the routes are placed in order to be installed");
		} else if (!routesFolder.isDirectory()) {
			throw new ConfigurationException("RoutesFolder",
					"The configured routes directory " + routesFolder
							+ " does already exists but is not a directory!");
		} // else exists and is a directory!
	}
	
	/**
	 * <p>Uploads a route specified as {@code InputStream} to the configured directory for routes</p>
	 * <p>This method will only create a file with the route in the routes directory 
	 * in order to this new route can be installed by the {@code RouteInstaller} component</p>
	 * 
	 * @param inputStream The route definition
	 * @throws IOException if the file does not exist, can not be created or an error occurred while copying the content
	 */
	public void uploadRoute(String routeFilename, InputStream inputStream) throws IOException {
		String randomString = UUID.randomUUID().toString();
    	File fileName = new File(routesFolder, Files.getNameWithoutExtension(routeFilename)+"."+randomString+".route");
		FileWriter writer = new FileWriter(fileName);
		IOUtils.copy(inputStream, writer);
		writer.close();
		return;
	}
}
