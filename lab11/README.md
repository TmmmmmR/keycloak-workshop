Lab 11 : Spring Boot Application with SAML 2.0
=============================================================

This Lab demonstrates how to write an application that
authenticates using <span>Keycloak</span> over the SAML protocol. 

Configuration in <span>Keycloak</span>
-----------------------

Prior to running the quickstart you need to create a client in <span>Keycloak</span> and download the installation file.

The following steps shows how to create the client required for this quickstart:

* Open the <span>Keycloak</span> admin console
* Select `Clients` from the menu
* Click `Create`
* Add the following values:
  * Client ID: You choose (for example `app-profile-saml`)
  * Client Protocol: `saml`
* Click `Save`

Once saved you need to change the following values:

* Valid Redirect URIs: `http://localhost:8080/app-profile-saml/*`
* Base URL: `http://localhost:8080/app-profile-saml/`
* Master SAML Processing URL: `http://localhost:8080/app-profile-saml/saml`
* Force Name ID Format: `ON`

If you deploy the application somewhere else change the hostname and port of the URLs accordingly.

To be able to retrieve the profile details next click on `Mappers`. Then click on `Add Builtin` and select all the
mappers before clicking `Add selected`.

Finally you need to configure the adapter, this is done by retrieving the adapter configuration file:

* Click on `Installation` in the tab for the client you created
* Select `Keycloak SAML Adapter keycloak-saml.xml`
* Click `Download`
* Edit `keycloak-saml.xml` and replace `SPECIFY YOUR LOGOUT PAGE!` with `/index.jsp`
* Move the file `keycloak-saml.xml` to the `config/` directory in the root of the quickstart

As an alternative you can create the client by importing the file [testsaml.json](config/client-import.json) and
copying [config/keycloak-saml-example.xml](config/keycloak-saml-example.xml) to `config/keycloak-saml.xml`. 
In case you work with a realm that you created, you need to edit `config/keycloak-saml.xml` 
and replace `<PrivateKeyPem>` and `<CertificatePem>` with the actual realm certificate.
You can retrieve the realm certificate from the admin console from `Keys` under `Realm Settings`.


### Build and Deploy the Lab


TO DO


### Access the Lab


You can access the application with the following URL: <http://localhost:8080/app-profile-saml>

