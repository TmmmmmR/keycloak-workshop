# Lab 19 : The Gatekeeper

The Keycloak Gatekeeper offers a very simple and flexible Reverse Proxy that handles all the OIDC-related authentication for upstream services. 

In this lab we provide a very basic example on how to run a keycloak gatekeeper with an upstream app (wordpress website).

To build the lab, run the following command from the gatekeeper source folder :

```bash
docker-compose up
```

For more information about the keycloak gatekeeper, check the [documentation page](https://github.com/louketo/louketo-proxy/blob/master/docs/user-guide.md)
