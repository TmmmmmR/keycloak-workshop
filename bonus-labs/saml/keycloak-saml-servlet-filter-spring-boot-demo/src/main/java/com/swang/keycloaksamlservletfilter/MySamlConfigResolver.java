package com.swang.keycloaksamlservletfilter;

import org.keycloak.adapters.saml.SamlConfigResolver;
import org.keycloak.adapters.saml.SamlDeployment;
import org.keycloak.adapters.saml.config.parsers.DeploymentBuilder;
import org.keycloak.adapters.saml.config.parsers.ResourceLoader;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.saml.common.exceptions.ParsingException;

import java.io.InputStream;


public class MySamlConfigResolver implements SamlConfigResolver {

    @Override
    public SamlDeployment resolve(HttpFacade.Request facade) {
        InputStream is = getClass().getResourceAsStream("/keycloak-saml.xml");
        if (is == null) {
            throw new IllegalStateException("Not able to find the file /keycloak-saml.xml");
        }

        ResourceLoader loader = new ResourceLoader() {
            @Override
            public InputStream getResourceAsStream(String path) {
                return getClass().getResourceAsStream(path);
            }
        };

        try {
            return new DeploymentBuilder().build(is, loader);
        } catch (ParsingException e) {
            throw new IllegalStateException("Cannot load SAML deployment", e);
        }
    }
}
