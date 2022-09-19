package com.example.library.client.credentials.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Profile("!testing")
@Configuration
public class WebClientConfiguration {

  @Bean
  ReactiveClientRegistrationRepository clientRegistrations() {
    ClientRegistration clientRegistration = ClientRegistrations
            .fromOidcIssuerLocation("http://localhost:8080/realms/workshop")
            .registrationId("library_client")
            .clientId("library-client")
            .clientSecret("9584640c-3804-4dcd-997b-93593cfb9ea7")
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .build();
    return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
  }

  @Bean
  ReactiveOAuth2AuthorizedClientService authorizedClientService() {
    return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations());
  }

  @Bean
  WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations, ReactiveOAuth2AuthorizedClientService authorizedClientService) {
    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
            new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                    new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations, authorizedClientService));
    oauth.setDefaultClientRegistrationId("library_client");
    return WebClient.builder()
            .filter(oauth)
            .build();
  }
}
