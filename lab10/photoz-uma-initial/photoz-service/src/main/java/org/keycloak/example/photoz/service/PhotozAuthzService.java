package org.keycloak.example.photoz.service;

import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.resource.ProtectionResource;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class PhotozAuthzService {

    private static final Logger log = Logger.getLogger(PhotozAuthzService.class);

    static final String SCOPE_CREATE = "photo:create";
    static final String SCOPE_VIEW = "photo:view";
    static final String SCOPE_VIEW_DETAIL = "photo:view-detail";
    static final String SCOPE_DELETE = "photo:delete";

    private @Autowired
    HttpServletRequest request;

    public void createProtectedResource(InMemoryPhotozDB.Photo photo) {
        try {
            HashSet<ScopeRepresentation> scopes = new HashSet<ScopeRepresentation>();

            scopes.add(new ScopeRepresentation(SCOPE_VIEW));
            scopes.add(new ScopeRepresentation(SCOPE_VIEW_DETAIL));
            scopes.add(new ScopeRepresentation(SCOPE_DELETE));

            ResourceRepresentation photoResource = new ResourceRepresentation(photo.getName(), scopes, "/photoz/" + photo.getId(), "http://photoz.com/photo");

            ResourceOwnerRepresentation resourceOwner = new ResourceOwnerRepresentation();
            resourceOwner.setId(photo.getOwner().getId());
            resourceOwner.setName(photo.getOwner().getUsername());
            photoResource.setOwner(resourceOwner);
            photoResource.setOwnerManagedAccess(true);

            ResourceRepresentation response = getAuthzClient().protection().resource().create(photoResource);

            photo.setExternalId(response.getId());
        } catch (Exception e) {
            throw new RuntimeException("Could not register protected resource.", e);
        }
    }


    public void deleteProtectedResource(InMemoryPhotozDB.Photo photo) {
        String uri = "/photoz/" + photo.getId();

        try {
            ProtectionResource protection = getAuthzClient().protection();
            List<ResourceRepresentation> search = protection.resource().findByUri(uri);

            if (search.isEmpty()) {
                throw new RuntimeException("Could not find protected resource with URI [" + uri + "]");
            }

            protection.resource().delete(search.get(0).getId());
        } catch (Exception e) {
            throw new RuntimeException("Could not search protected resource.", e);
        }
    }


    public List<PermissionTicketRepresentation> getPhotozPermissions() {
        return getAuthzClient().protection().permission()
                .find(null, null, null, getKeycloakSecurityContext().getToken().getSubject(), true, true, null, null);
    }



    private AuthzClient getAuthzClient() {
        return getAuthorizationContext().getClient();
    }

    private ClientAuthorizationContext getAuthorizationContext() {
        return ClientAuthorizationContext.class.cast(getKeycloakSecurityContext().getAuthorizationContext());
    }

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        return KeycloakSecurityContext.class.cast(request.getAttribute(KeycloakSecurityContext.class.getName()));
    }
}
