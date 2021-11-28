package org.keycloak.example.photoz;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@SpringBootApplication
public class PhotozServiceApp {

    private static Log logger = LogFactory.getLog(PhotozServiceApp.class);

    @Bean
    protected ServletContextListener listener() {
        return new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                logger.info("ServletContext initialized");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                logger.info("ServletContext destroyed");
            }

        };
    }
    @Bean
    @Primary
    public KeycloakSpringBootProperties properties() {
        final KeycloakSpringBootProperties props = new KeycloakSpringBootProperties();
        final PolicyEnforcerConfig policyEnforcerConfig = new PolicyEnforcerConfig();
        policyEnforcerConfig.setEnforcementMode(PolicyEnforcerConfig.EnforcementMode.ENFORCING);
        policyEnforcerConfig.setUserManagedAccess(new PolicyEnforcerConfig.UserManagedAccessConfig());
        props.setPolicyEnforcerConfig(policyEnforcerConfig);
        return props;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PhotozServiceApp.class, args);
    }
}
