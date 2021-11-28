[![License](https://img.shields.io/badge/License-Apache%20License%202.0-brightgreen.svg)][1]

# KeyCloak Workshop

Authentication and authorization with KeyCloak.
This contains both, theory parts on all important concepts, and hands-on practice labs.

__Table of Contents (Tu Update)__

* [Requirements and Setup](setup)
* [Hands-On Workshop](#hands-on-workshop)    
  * [Intro Labs](#intro-labs)
    * [Lab: Authorization Grant Flows in Action](intro-labs/oauth-grants)
    * [Demo: Auth Code Flow in Action](intro-labs/auth-code-demo)
    * [Demo: GitHub Client](intro-labs/github-client)
  * [Hands-On Labs](#hands-on-labs)
    * [Lab 1: Resource Server](lab1)
    * [Lab 2: Client (Auth Code)](lab2)
    * [Lab 3: Client (Client-Credentials)](lab3)
    * [Lab 4: Testing JWT Auth&Authz](lab4)
    * [Lab 5: JWT Testing Server](lab5)
    * [Lab 6: SPA Client (Authz Code with PKCE)](lab6)
  * [Bonus Labs](#bonus-labs)  
    * [Demo: Multi-Tenant Resource Server](bonus-labs/multi-tenant-server-app)
    * [Demo: Resource Server with Micronaut](bonus-labs/micronaut-server-app)
    * [Demo: Resource Server with Quarkus](bonus-labs/quarkus-server-app)
    * [Lab: Keycloak Testcontainers](bonus-labs/keycloak-test-containers)
* [Feedback](#feedback)
* [License](#license)    

## Workshop Tutorial

To follow the hands-on workshop please open the [workshop tutorial](https://tmmmmmr.gitbook.io/keycloak-workshop).

## Requirements and Setup

To check system requirements and setup for this workshop please follow the [setup guide](setup).

## Hands-On Workshop

### Intro Labs

* [Lab: Authorization Grant Flows in Action](intro-labs/oauth-grants)
* [Demo: Authorization Code Grant Flow in Action](intro-labs/auth-code-demo)
* [Demo: A pre-defined OAuth2 client for GitHub](intro-labs/github-client)

### Part 1 : OAuth 2.0 and OpenID Connect 1.0

For the hands-on workshop you will extend a provided sample application along with guided tutorials.

The components you will build (and use) look like this:

![Architecture](docs/images/demo-architecture.png)

__Please check out the [complete documentation](application-architecture) for the sample application before 
starting with the first hands-on lab__.
 
* [Lab 1: OAuth2/OIDC Resource Server](lab1)
* [Lab 2: OAuth2/OIDC Web Client (Auth Code Flow)](lab2)
* [Lab 3: OAuth2/OIDC Batch Job Client (Client-Credentials Flow)](lab3)
* [Lab 4: OAuth2/OIDC Testing Environment](lab4)
* [Lab 5: JWT Testing Server](lab5)
* [Lab 6: OAuth2/OIDC Angular Client](lab6)

### Part 2 : Fine-grained authorization
* [Lab 7: SpringBoot REST Service Protected Using Keycloak Authorization Services](lab7)
* [Lab 8: Spring Security Application using Authorization Services](lab8)
* [Lab 9: SpringBoot REST Service Protected Using Keycloak Authorization Services (Javascript Policies)](lab9)
* [Lab 10: User-Managed Access (UMA 2.0)](lab10)

### Part 3 : SAML
* [Lab 11: Spring Boot Application with SAML 2.0](lab11)
* [Lab 12: SAML SSO using an external identity provider](lab12)

### Part 4 : User Storage Federation
* [Lab 13: User federation using LDAP](lab13)
* [Lab 14: Keycloak User Storage SPI](lab14)

### Part 5 : MFA
* [Lab 15: Keycloak 2FA SMS Authenticator](lab15)
* [Lab 16: Conditional Keycloak 2FA SMS Authenticator](lab16)

### Part 6 : Identity Brokering
* [Lab 17: Using Github as an identity provider](lab17)
* [Lab 18: Using Franceconnect as an identity provider](lab18)

### Bonus Labs

* [Lab 1 : Multi-Tenant Resource Server](bonus-labs/multi-tenant-server-app)
* [Lab 2 : OAuth2/OIDC Resource Server with Micronaut](bonus-labs/micronaut-server-app)
* [Lab 3 : OAuth2/OIDC Resource Server with Quarkus](bonus-labs/quarkus-server-app)
* [Lab 4 : Keycloak Testcontainers](bonus-labs/keycloak-test-containers)


## Reference

This workshop is based on the following resources : 

* [Keycloak Quickstarts Examples](https://github.com/keycloak/keycloak-quickstarts)
* [Securing Microservices with OpenID Connect and Spring Security 5.1 @ Spring I/O 2019](https://github.com/andifalk/oidc-workshop-spring-io-2019) by _@andifalk_.
* [Keycloak/Authorization SpringBoot Example - devconf 2019](https://github.com/mposolda/devconf2019-authz) by _@mposolda_

Any feedback on this hands-on workshop is highly appreciated.

## License

Apache 2.0 licensed

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt
