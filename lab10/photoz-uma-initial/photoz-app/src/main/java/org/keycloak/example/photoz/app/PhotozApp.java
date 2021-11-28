package org.keycloak.example.photoz.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.rossillo.spring.web.mvc.CacheControlHandlerInterceptor;
import org.jboss.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@SpringBootApplication
public class PhotozApp {

    private static final Logger log = Logger.getLogger(PhotozApp.class);

    @Bean
    protected ServletContextListener listener() {
        return new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                log.info("ServletContext initialized");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                log.info("ServletContext destroyed");
            }

        };
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PhotozApp.class, args);
    }

    @Bean
    public CacheControlHandlerInterceptor cacheControlHandlerInterceptor() {
        return new CacheControlHandlerInterceptor();
    }
}
