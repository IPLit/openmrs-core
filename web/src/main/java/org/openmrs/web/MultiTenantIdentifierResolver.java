/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

// MT IPLit

package org.openmrs.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.openmrs.util.OpenmrsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Component
@RequestScope
public class MultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private static final Logger log = LoggerFactory.getLogger(MultiTenantIdentifierResolver.class);

    public MultiTenantIdentifierResolver() {
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = null;
        try {
            RequestAttributes requestAttributes = RequestContextHolder
                    .currentRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            HttpSession httpSession = request.getSession();
            tenantId = (String) httpSession.getAttribute(OpenmrsConstants.TENANT_HEADER_NAME);
        } catch (Exception e) {
        }
        if (StringUtils.isBlank(tenantId)) {
            tenantId = OpenmrsConstants.DATABASE_NAME;
        }
        log.debug("MT resolver: "+ tenantId);
        return tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}
