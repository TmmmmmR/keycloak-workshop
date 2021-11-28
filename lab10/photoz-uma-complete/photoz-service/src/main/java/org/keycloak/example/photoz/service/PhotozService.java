package org.keycloak.example.photoz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.keycloak.example.photoz.service.PhotozAuthzService.SCOPE_DELETE;
import static org.keycloak.example.photoz.service.PhotozAuthzService.SCOPE_VIEW;
import static org.keycloak.example.photoz.service.PhotozAuthzService.SCOPE_VIEW_DETAIL;

/**
 * Service, which among other things, also do authz integration (CRUD resources and permissions etc)
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class PhotozService {

    @Autowired
    private InMemoryPhotozDB db;

    @Autowired
    private PhotozAuthzService photozAuthzService;

    public PhotoRepresentation generatePhotoForUser(String userId, String username) {
        // Create in DB
        InMemoryPhotozDB.Photo photo = db.giveRandomPhotoToUser(userId, username);

        // Create authz resource
        photozAuthzService.createProtectedResource(photo);

        return new PhotoRepresentation(photo.getId(), photo.getName(), null, photo.getExternalId(), photo.getOwner());
    }


    /**
     * Return all the photoz, which current user is able to see. If "username" is null, we aim to return all the photoz of all the users
     *
     * @param username
     * @return Map with all the photoz user is able to see. Key is ownerUsername, Values are all photoz of this owner. Image is not set in the PhotoRepresentation instances.
     */
    public Map<String, List<PhotoRepresentation>> getPhotoz(String username) {
        Map<String, List<PhotoRepresentation>> photozRep = new HashMap<>();

        db.getPhotozWithOwner().forEach((InMemoryPhotozDB.Photo photo) -> {
            String ownerUsername = photo.getOwner().getUsername();

            List<PhotoRepresentation> ownerPhotoz;
            if (!photozRep.containsKey(ownerUsername)) {
                photozRep.put(ownerUsername, new ArrayList<>());
            }

            photozRep.get(ownerUsername).add(new PhotoRepresentation(photo.getId(), photo.getName(), null,
                    photo.getExternalId(), photo.getOwner()));
        });

        List<PermissionTicketRepresentation> sharedPermissions = photozAuthzService.getPhotozPermissions();
        for (PermissionTicketRepresentation ticket : sharedPermissions) {
            String ownerUsername = ticket.getOwnerName();
            String resourceId = ticket.getResource();

            for (PhotoRepresentation photoRep : photozRep.get(ownerUsername)) {
                if (photoRep.getExternalId().equals(resourceId)) {
                    photoRep.addScope(ticket.getScopeName());
                }
            }
        }

        for (Map.Entry<String, List<PhotoRepresentation>> userPhotoz : photozRep.entrySet()) {
            if (username == null || username.equals(userPhotoz.getKey())) {
                for (PhotoRepresentation photo : userPhotoz.getValue()) {
                    photo.addScope(SCOPE_VIEW);
                    photo.addScope(SCOPE_VIEW_DETAIL);
                    photo.addScope(SCOPE_DELETE);
                }
            }

            Set<PhotoRepresentation> toRemove = new HashSet<>();
            for (PhotoRepresentation photo : userPhotoz.getValue()) {
                if (photo.getScopes() == null || photo.getScopes().isEmpty()) {
                    toRemove.add(photo);
                }
            }

            for (PhotoRepresentation toRemove2 : toRemove) {
                userPhotoz.getValue().remove(toRemove2);
            }
        }

        Set<String> usernamesToRemove = new HashSet<>();
        for (Map.Entry<String, List<PhotoRepresentation>> userPhotoz : photozRep.entrySet()) {
            if (userPhotoz.getValue().isEmpty()) {
                usernamesToRemove.add(userPhotoz.getKey());
            }
        }
        for (String toRem : usernamesToRemove) {
            photozRep.remove(toRem);
        }

        return photozRep;
    }


    public PhotoRepresentation getPhotoById(String photoId) {
        InMemoryPhotozDB.Photo photo = db.getPhotoById(photoId);
        return new PhotoRepresentation(photo.getId(), photo.getName(), photo.getBase64Img(), photo.getExternalId(), photo.getOwner());
    }


    public void deletePhotoById(String photoId) {
        InMemoryPhotozDB.Photo photo = db.deletePhotoById(photoId);

        // Delete authz resource
        photozAuthzService.deleteProtectedResource(photo);
    }

}
