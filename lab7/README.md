# Lab 7 : SpringBoot REST Service Protected Using Keycloak Authorization Services

In this lab we are going to see how to protect a SpringBoot REST service using Keycloak Authorization Services.

## Learning Targets

This lab tries to focus on the authorization features provided by Keycloak Authorization Services, where access to resources depends on users profile.

In this application, there are three paths protected by specific permissions in Keycloak:

* **/unsecured**, access to this path is open to everyone
* **/user**  where access to this page is restricted to users with ADMIN role
* **/admin**  where any user with a role *USER* is allowed to access this path

By the end of this Lab, you will learn :

* How to secure APIs using stateless JWT token
* Adding a basic authorization layer to protect  
* Writing unit test for your code

### Start the lab

When running the initial version of this application, all APIs are reachable without authentication :

![Open Access to APIs](docs/images/lab7_open_access.png)


### Step 1 : Adding dependecies

So let's start by adding the following dependencies :

* First, we need to specify the keycloak version :

```xml
<properties>
  ...
  <keycloak.version>12.0.2</keycloak.version>
</properties>
```

* Add the Adapter BOM dependency:

```xml 
<dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.keycloak.bom</groupId>
        <artifactId>keycloak-adapter-bom</artifactId>
        <version>${keycloak.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
```

* Adapter Installation : The Keycloak Spring Boot adapter takes advantage of Spring Boot’s autoconfiguration so all you need to do is add the Keycloak Spring Boot starter to your project. To add it using Maven, add the following to your dependencies:

```xml 
<dependency>
  <groupId>org.keycloak</groupId>
  <artifactId>keycloak-spring-boot-starter</artifactId>
</dependency>
```

### Step 2 : Adding Keycloak configuration

We can configure the realm for the Spring Boot Keycloak adapter via the normal Spring Boot configuration. For example:

```yaml
########################################
# Spring Boot / Keycloak Configuration
########################################
keycloak.auth-server-url=http://localhost:8080/auth
keycloak.realm=ineat-realm
keycloak.resource=ineat-api
```

We are going to use a bearer token for authentication (so no need to set-up the client and secret id) :

```yaml
########################################
# Spring Boot / Keycloak Configuration
########################################
keycloak.auth-server-url=http://localhost:8080/auth
keycloak.realm=ineat-realm
keycloak.resource=ineat-api

keycloak.bearer-only=true
```

### Step 3 : Adding security constraints :

In the same spring boot configuration file, add the following in order to secure access to our APIs :

```yaml
keycloak.securityConstraints[0].securityCollections[0].name = insecure endpoint
keycloak.securityConstraints[0].securityCollections[0].patterns[0] = /unsecured
keycloak.securityConstraints[0].securityCollections[0].patterns[1] = /
keycloak.security-constraints[1].authRoles[0]=USER
keycloak.security-constraints[1].securityCollections[0].patterns[0]=/user
keycloak.security-constraints[2].authRoles[0]=ADMIN
keycloak.security-constraints[2].securityCollections[0].patterns[0]=/admin
```

### Step 4 : Java configuration

Keycloak provides a KeycloakWebSecurityConfigurerAdapter as a convenient base class for creating a WebSecurityConfigurer instance. The implementation allows customization by overriding methods. While its use is not required, it greatly simplifies your security context configuration :

Let's add this to our code :

```java
public class SecurityConfiguration {
    @KeycloakConfiguration
    @ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
    public static class KeycloakConfigurationAdapter extends KeycloakWebSecurityConfigurerAdapter {
        ...
    }
}
```

* ```@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)``` : make it easy to enable/disable our keycloak set-up
* The ``` @KeycloakConfiguration``` annotation is a metadata annotion that defines all annotations that are needed to integrate Keycloak in Spring Security. If you have a complexe Spring Security setup you can simply have a look ath the annotations of the ```@KeycloakConfiguration``` annotation and create your own custom meta annotation or just use specific Spring annotations for the Keycloak adapter.


### Step 5 : Naming Security Roles
Spring Security, when using role-based authentication, requires that role names start with ROLE_. For example, an administrator role must be declared in Keycloak as ROLE_ADMIN or similar, not simply ADMIN.

The class ```org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider``` supports an optional ```org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper``` which can be used to map roles coming from Keycloak to roles recognized by Spring Security. Use, for example, ```org.springframework.security.core.authority.mapping.SimpleAuthorityMapper``` to insert the ```ROLE_``` prefix and convert the role name to upper case. The class is part of Spring Security Core module.

```java
...
        /**
         * Registers the KeycloakAuthenticationProvider with the authentication manager.
         */
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
            // simple Authority Mapper to avoid ROLE_
            keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
            auth.authenticationProvider(keycloakAuthenticationProvider);
        }
...
```


### Step 6 : Using Spring Boot Configuration
By Default, the Spring Security Adapter looks for a keycloak.json configuration file. You can make sure it looks at the configuration provided by the Spring Boot Adapter by adding this bean :

```java
...
        /**
         * Required to handle spring boot configurations
         * @return
         */
        @Bean
        public KeycloakConfigResolver KeycloakConfigResolver() {
            return new KeycloakSpringBootConfigResolver();
        }

```

**Note** : there is an [issue](https://issues.redhat.com/browse/KEYCLOAK-11282) with Keycloak for versions prior to 9, where you need to use a ```KeycloakSpringBootConfigResolver``` instead of this bean :
 
 Sample error message :
 
 ```
 ***************************
 APPLICATION FAILED TO START
 ***************************
 Description:
 Parameter 1 of method setKeycloakSpringBootProperties in org.keycloak.adapters.springboot.KeycloakBaseSpringBootConfiguration required a bean of type 'org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver' that could not be found.
 Action:
 Consider defining a bean of type 'org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver' in your configuration.
```

A work around will be to use the following code :

```java
/**
 * See <a href="https://issues.redhat.com/browse/KEYCLOAK-11282">Keycloak Issue</a>
 */
@Configuration
public class CustomKeycloakSpringBootConfigResolver extends KeycloakSpringBootConfigResolver {
    private final KeycloakDeployment keycloakDeployment;

    public CustomKeycloakSpringBootConfigResolver(KeycloakSpringBootProperties properties) {
        keycloakDeployment = KeycloakDeploymentBuilder.build(properties);
    }

    @Override
    public KeycloakDeployment resolve(HttpFacade.Request facade) {
        return keycloakDeployment;
    }
}

 ```

### Step 7 : Define Spring BSecurity Matchers 

This where we define how and which APIs we want to protect :

```java
...
        /**
         * Configuration spécifique à keycloak (ajouts de filtres, etc)
         * @param http
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception
        {
            http
                    // disable csrf because of API mode
                    .csrf().disable()

                    .sessionManagement()
                     // use previously declared bean
                        .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)


                    // keycloak filters for securisation
                    .and()
                        .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
                        .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class)
                        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())

                    // delegate logout endpoint to spring security

                    .and()
                        .logout()
                        .addLogoutHandler(keycloakLogoutHandler())
                        .logoutUrl("/logout").logoutSuccessHandler(
                        // logout handler for API
                        (HttpServletRequest request, HttpServletResponse response, Authentication authentication) ->
                                response.setStatus(HttpServletResponse.SC_OK)
                     )
                    .and()
                        // manage routes securisation here
                        .authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()


                        .antMatchers("/logout", "/", "/unsecured").permitAll()
                        .antMatchers("/user").hasRole("USER")
                        .antMatchers("/admin").hasRole("ADMIN")

                        .anyRequest().denyAll();


        }
```

### Let's do some tests :

Before testing our APIs, let's import the Realm configuration file related to this lab :

[realm-export.json](./realm-export.json)

Then, create 2 users :

* User `ineat-admin` / `password`  with `ADMIN` role associated
* User `ineat-user` / `password` With `USER` role associated

Then use the following [postman collection](./postman_collection.json) to test our APIs 