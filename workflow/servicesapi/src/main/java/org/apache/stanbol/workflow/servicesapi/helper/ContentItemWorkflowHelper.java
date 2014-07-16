package org.apache.stanbol.workflow.servicesapi.helper;

import java.util.Map;

import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.helper.ContentItemHelper;

public class ContentItemWorkflowHelper {

	/**
     * <p>Sets the configured engine/chain properties into the {@link ContentItem} request properties part</p>
     * 
     * @param ci the {@code ContentItem} object
     * @param parameters the {@code Map} containing the enhancement request parameters
     */
    public static void setEnhancementRequestProperties(ContentItem ci, Map<String,Object> parameters) {
    	Map<String,Object> reqProp = ContentItemHelper.initRequestPropertiesContentPart(ci);
        reqProp.putAll(parameters);
    }
}
