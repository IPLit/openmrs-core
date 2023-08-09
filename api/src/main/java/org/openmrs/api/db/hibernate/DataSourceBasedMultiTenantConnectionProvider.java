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

package org.openmrs.api.db.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.openmrs.api.db.hibernate.HibernateSessionFactoryBean;
import org.openmrs.util.OpenmrsConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Properties;

import javax.sql.DataSource;


@Component
public class DataSourceBasedMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final Logger log = LogManager.getLogger(DataSourceBasedMultiTenantConnectionProvider.class);
    
    private static final String DEFAULT_TENANT_ID = OpenmrsConstants.DATABASE_NAME;

    private static HashMap<String, DataSource> connections = new HashMap<String, DataSource>();

    public DataSourceBasedMultiTenantConnectionProvider() {
    }

    public DataSource getConnectionProvider(String name) {
        if (connections.get(name) != null) {
            return connections.get(name);
        }
        DataSource connectionProvider = createConnectionProvider(name);
        if (connectionProvider != null) {
            connections.put(name, connectionProvider);
        }
        return connectionProvider;
    }

    private DataSource createConnectionProvider(String tenantIdentifier) {
        Properties connProp = HibernateSessionFactoryBean.getConnPropMT();
        String url = connProp.getProperty(Environment.URL);
        url = url.replace("3306/openmrs?", "3306/"+tenantIdentifier+"?");
        DriverManagerDataSource ds = new DriverManagerDataSource(url, connProp);
        ds.setConnectionProperties(connProp);
        ds.setUsername(connProp.getProperty(Environment.USER));
        ds.setPassword(connProp.getProperty(Environment.PASS));
        ds.setUrl(url);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        log.debug("connectionProvider ds configured for name: " + tenantIdentifier);
        return ds;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return getConnectionProvider(DEFAULT_TENANT_ID);
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        log.debug("tenantIdentifier: " + tenantIdentifier);
        return StringUtils.isNotBlank(tenantIdentifier) ?
            getConnectionProvider(tenantIdentifier) : getConnectionProvider(DEFAULT_TENANT_ID);
    }

}
