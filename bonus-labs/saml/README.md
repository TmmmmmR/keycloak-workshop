Spring Boot Application with SAML 2.0
=============================================================

This Lab demonstrates how to write an application that
authenticates using <span>Keycloak</span> over the SAML protocol. 

### Configuration in Keycloak

Prior to running the lab you need import the [Realm config](./config/realm-export.json) in <span>Keycloak</span>.


Make sure you have the following config for the client http://localhost:8081/saml-servlet-filter/ :


* Valid Redirect URIs: `http://localhost:8081/saml-servlet-filter/*`
* Base URL: `http://localhost:8081/saml-servlet-filter/`
* Master SAML Processing URL: `http://localhost:8081/saml-servlet-filter/saml`
* Force Name ID Format: `ON`

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

### Configuration in the Spring boot app

Finally you need to configure the adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak SAML Adapter keycloak-saml.xml`
* Click `Download`
* Edit `keycloak-saml.xml` and replace `SPECIFY YOUR LOGOUT PAGE!` with `/logout.jsp`
* Move the file `keycloak-saml.xml` to the `config/` directory in the root of the quickstart


### Build and Deploy the Lab

Build the application using mvn command :

   ````
   mvn spring-boot:run

   ````

### Access the Lab


You can access the application with the following URL: <http://localhost:8081/app-profile-saml>

