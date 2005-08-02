package org.apache.ti.config.mapper;

import org.apache.commons.chain.web.WebContext;

/**
 * Handles creation of ActionMapping and reconstruction of URI's from one
 */
public interface ActionMapper {
    ActionMapping getMapping(WebContext ctx);

    String getUriFromActionMapping(ActionMapping mapping);
}
