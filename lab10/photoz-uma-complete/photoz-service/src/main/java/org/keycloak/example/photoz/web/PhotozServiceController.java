package org.keycloak.example.photoz.web;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.example.photoz.service.PhotoRepresentation;
import org.keycloak.example.photoz.service.PhotozService;
import org.keycloak.example.photoz.util.ServiceTokenUtil;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@RestController
public class PhotozServiceController {

    @Autowired
    private PhotozService photozService;

    private @Autowired
    HttpServletRequest request;

    @GetMapping(value = "/photoz", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<PhotoRepresentation>> getPhotoz(Principal principal) {
        AccessToken accessToken = ServiceTokenUtil.getAccessToken(principal);

        // This is temporary...
        if (accessToken.getRealmAccess().isUserInRole("admin")) {
            return photozService.getPhotoz(null);
        } else {
            return photozService.getPhotoz(accessToken.getPreferredUsername());
        }
    }


    // Create (generate) new photo for authenticated user. Then return the newly created photo
    @PostMapping(value = "/photoz/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public PhotoRepresentation generatePhoto(Principal principal) {
        AccessToken token = ServiceTokenUtil.getAccessToken(principal);
        return photozService.generatePhotoForUser(token.getSubject(), token.getPreferredUsername());
    }


    @GetMapping(value = "/photoz/{photoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PhotoRepresentation getPhotoDetails(Principal principal, @PathVariable String photoId) {
        return photozService.getPhotoById(photoId);
    }


    @DeleteMapping(value = "/photoz/{photoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deletePhoto(Principal principal, @PathVariable String photoId) {
        photozService.deletePhotoById(photoId);
    }

}


