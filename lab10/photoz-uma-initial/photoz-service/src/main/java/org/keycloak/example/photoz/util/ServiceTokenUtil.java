package org.keycloak.example.photoz.util;

import java.security.Principal;


import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ServiceTokenUtil {

    public static AccessToken getAccessToken(Principal principal) {
        KeycloakPrincipal kcPrincipal = (KeycloakPrincipal) principal;
        return kcPrincipal.getKeycloakSecurityContext().getToken();
    }

    public static String getAccessTokenString(Principal principal) {
        KeycloakPrincipal kcPrincipal = (KeycloakPrincipal) principal;
        return kcPrincipal.getKeycloakSecurityContext().getTokenString();
    }
}
