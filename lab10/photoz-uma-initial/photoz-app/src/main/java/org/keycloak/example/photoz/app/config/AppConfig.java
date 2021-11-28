package org.keycloak.example.photoz.app.config;

import org.keycloak.example.photoz.service.InMemoryPhotozDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class AppConfig {

    @Autowired
    private ApplicationContext appContext;

    public String getAuthServerUrl() {
        return appContext.getEnvironment().getProperty("keycloak.auth-server-url");
    }

    public String getRealmName() {
        return appContext.getEnvironment().getProperty("keycloak.realm");
    }

    public String getClientId() {
        return appContext.getEnvironment().getProperty("keycloak.resource");
    }
}
