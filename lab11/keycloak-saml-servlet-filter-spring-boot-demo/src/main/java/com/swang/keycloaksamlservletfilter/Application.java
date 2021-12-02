package com.swang.keycloaksamlservletfilter;

import org.keycloak.adapters.saml.servlet.SamlFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Bean
	public FilterRegistrationBean samlFilter(){
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new SamlFilter());
		registrationBean.setName("samlFilter");
		registrationBean.addUrlPatterns("/saml","/protected/*");
		registrationBean.addInitParameter("keycloak.config.resolver","com.swang.keycloaksamlservletfilter.MySamlConfigResolver");
		return registrationBean;
	}
}
