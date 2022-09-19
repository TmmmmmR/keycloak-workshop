Lab 8 : Spring Security Application using Authorization Services
===================================================

This lab demonstrates how to write a Spring Security application where both authentication and
authorization aspects are managed by <span>Keycloak</span>.

This application tries to focus on the authorization features provided by <span>Keycloak</span> Authorization Services, where resources are
protected by a set of permissions and policies defined in Keycloak itself and access to these resources are enforced by a policy enforcer
that intercepts every single request to the application.

In this application, there are three paths protected by specific permissions in <span>Keycloak</span>:

* **/protected**, where access to this page is based on the evaluation of permissions associated with a resource **Protected Resource** in <span>Keycloak</span>. Basically,
any user with a role *user* is allowed to access this page.

* **/protected/premium**, where access to this page is based on the evaluation of permissions associated with a resource **Premium Resource** in <span>Keycloak</span>. Basically,
only users with a role *user-premium* is allowed to access this page.

* **/protected/alice**, where access to this page is based on the evaluation of permissions associated with a resource **Alice Resource** in <span>Keycloak</span>. Basically,
only user *alice* is allowed to access this page.

The home page (home.ftl) also demonstrates how to use a ``AuthorizationContext`` instance to check for user's permissions and hide/show
things in a page. Where the ``AuthorizationContext`` encapsulates all permissions granted by a <span>Keycloak</span> server and provides methods
to check these permissions.

You can use two distinct users to access this application:

|Username|Password|Roles|
|---|---|---|
|alice|alice|user|
|jdoe|jdoe|user, user-premium|


System Requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.3.3 or later.


Configuration in <span>Keycloak</span>
-----------------------

Prior to running the lab you need to create a `realm` in <span>Keycloak</span> with all the necessary configuration to deploy and run the lab.

You should also deploy some JS policies into the Keycloak Server. For that, perform the following steps:

   ````
   mvn -f ../lab9/js-authz-policies clean install && cp ../js-authz-policies/target/js-authz-policies-*.jar {KEYCLOAK_HOME}/providers
   ````

And then install the policy using using the following the command :
   ````
   cd {KEYCLOAK_HOME}/bin
   ./kc.sh build
   ````


Make sure your <span>Keycloak</span> server is running on <http://localhost:8080/>. For that, you can start the server using the command below:

   ````
   ./kc.sh start-dev
   ````

To create the realm required for this lab, follow these steps:

1. Open the <span>Keycloak</span> admin console
2. In the top left corner dropdown menu that is titled `Master`, click `Add Realm`. If you are logged in to the master realm this dropdown menu lists all the realms created.
3. For this quickstart we are not going to manually create the realm, but import all configuration from a JSON file. Click on `Select File` and import the [config/lab8.json](config/lab8.json).
4. Click `Create`

The steps above will result on a new `Lab8` realm.

Build and Run the lab
-------------------------------

If your server is up and running, perform the following steps to start the application:

1. Open a terminal and navigate to the root directory of this lab.

2. The following shows the command to deploy the lab:

   ````
   ./mvnw spring-boot:run

   ````

Access & Play with the lab
---------------------

You can access the application with the following URL: <http://localhost:8081/>.

If you want to play around, try the following steps:

* Create a scope, define a policy and permission for it, and test it on the application side. Can the user perform an action (or anything else represented by the scope you created)?

* Create different types of policies such as role-based, user-based, time-based, aggregated policies, or rule-based, and associate these policies with the Default Permission.

* Apply multiple policies to the Default Permission and test the behavior. For example, combine multiple policies and change the Decision Strategy accordingly.

For more information, please consult the Authorization Services documentation.