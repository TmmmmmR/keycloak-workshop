package org.keycloak.example.photoz.app.web;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.rossillo.spring.web.mvc.CacheControl;
import net.rossillo.spring.web.mvc.CachePolicy;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.example.photoz.app.config.AppConfig;
import org.keycloak.example.photoz.app.config.AuthzClientRequestFactory;
import org.keycloak.example.photoz.app.config.RptStore;
import org.keycloak.example.photoz.app.service.PhotozClientService;
import org.keycloak.example.photoz.app.service.ObjectMapperProvider;
import org.keycloak.example.photoz.app.util.AppTokenUtil;
import org.keycloak.example.photoz.service.PhotoRepresentation;
import org.keycloak.example.photoz.service.InMemoryPhotozDB;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpServerErrorException;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Controller
@CacheControl(policy = CachePolicy.NO_CACHE)
public class PhotozAppController {

    private static final Logger log = Logger.getLogger(InMemoryPhotozDB.class);

    @Autowired
    private ObjectMapperProvider mapperProvider;

    @Autowired
    private PhotozClientService photozClientService;

    private @Autowired
    HttpServletRequest request;

    private @Autowired
    HttpServletResponse response;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RptStore rptStore;


    @RequestMapping(value = "/app", method = RequestMethod.GET)
    public String showPhotozPage(Principal principal, Model model) {
        boolean isCreatePhotoAllowed = photozClientService.isCreatePhotoAllowed(principal);
        model.addAttribute("create_photo_allowed", isCreatePhotoAllowed);

        Map<String, List<PhotoRepresentation>> photoz = photozClientService.getPhotoz();
        model.addAttribute("photoz", photoz);
        model.addAttribute("principal",  principal);

        String logoutUri = KeycloakUriBuilder.fromUri(appConfig.getAuthServerUrl()).path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
                .queryParam("redirect_uri", "http://localhost:8080/app").build(appConfig.getRealmName()).toString();
        model.addAttribute("logout",  logoutUri);

        String accountUri = KeycloakUriBuilder.fromUri(appConfig.getAuthServerUrl()).path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH)
                .queryParam("referrer", appConfig.getClientId()).build(appConfig.getRealmName()).toString();
        model.addAttribute("accountUri", accountUri);

        AccessToken token = AppTokenUtil.getAccessToken(principal);
        model.addAttribute("token", token);

        return "photoz";
    }





    @RequestMapping(value = "/app/create-photo", method = RequestMethod.GET)
    public String createRandomPhoto(Principal principal, Model model) {
        try {
            PhotoRepresentation newPhoto = photozClientService.createPhoto();
            log.infof("Created new photo %s for user %s", newPhoto.getId(), newPhoto.getOwner().getUsername());
        } catch (RuntimeException ex) {
            log.error("Failed to create photo for user " + principal.getName(), ex);
            model.addAttribute("app_error", "Failed to create new photo");
        }

        // Just re-show the page
        return showPhotozPage(principal, model);
    }


    @RequestMapping(value = "/app/details/{photoId}", method = RequestMethod.GET)
    public String getPhotoDetails(Principal principal, Model model, @PathVariable String photoId) {
        PhotozClientService.ClientCallResponse<PhotoRepresentation> response = photozClientService.getPhotoWithDetails(photoId);

        String reqSubmitted = handleRequestSubmitted(response, principal, model);
        if (reqSubmitted != null) return reqSubmitted;

        PhotoRepresentation detailedPhoto = response.getResult();
        model.addAttribute("photo", detailedPhoto);
        return "photo-detail";

    }


    private String handleRequestSubmitted(PhotozClientService.ClientCallResponse response, Principal principal, Model model) {
        if (response.isRequestSubmitted()) {
            model.addAttribute("app_error", "Submitted request to the photo owner to grant you a permission");

            // Just re-show the page
            return showPhotozPage(principal, model);
        } else {
            return null;
        }
    }


    @RequestMapping(value = "/app/delete/{photoId}", method = RequestMethod.GET)
    public String deletePhoto(Principal principal, Model model, @PathVariable String photoId) {
        PhotozClientService.ClientCallResponse<Void> clientResponse = photozClientService.deletePhoto(photoId);

        String reqSubmitted = handleRequestSubmitted(clientResponse, principal, model);
        if (reqSubmitted != null) return reqSubmitted;

        return showPhotozPage(principal, model);
    }


    @RequestMapping(value = "/app/img/{photoId}", method = RequestMethod.GET)
    @ResponseBody
    public void getPhotoImg(Principal principal, Model model, @PathVariable String photoId) throws IOException {
        PhotozClientService.ClientCallResponse<PhotoRepresentation> clientResponse = photozClientService.getPhotoWithDetails(photoId);

        String reqSubmitted = handleRequestSubmitted(clientResponse, principal, model);
        // Shoudln't happen
        if (reqSubmitted != null) return;



        PhotoRepresentation detailedPhoto = clientResponse.getResult();
        String imgString = detailedPhoto.getBase64Img();

        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();

        byte[] decodedPicture = Base64Url.decode(imgString);
        outputStream.write(decodedPicture);

        outputStream.flush();
    }


    @RequestMapping(value = "/app/show-token", method = RequestMethod.GET)
    public String showToken(Principal principal, Model model) throws ServletException, IOException {
        AccessToken token = AppTokenUtil.getAccessToken(principal);

        model.addAttribute("token", token);

        String tokenString = mapperProvider.getMapper().writeValueAsString(token);
        model.addAttribute("tokenString", tokenString);

        return "token";
    }


    @RequestMapping(value = "/app/show-rpt", method = RequestMethod.GET)
    public String showRpt(Principal principal, Model model) throws ServletException, IOException {
        KeycloakSecurityContext securityCtx = AppTokenUtil.getKeycloakSecurityContext(principal);
        AccessToken rptToken = rptStore.getParsedRpt(securityCtx);

        model.addAttribute("token", rptToken);

        String tokenString = mapperProvider.getMapper().writeValueAsString(rptToken);
        model.addAttribute("tokenString", tokenString);

        return "token";
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String handleLogoutt() throws ServletException, IOException {
        request.logout();

        response.sendRedirect("/app");
        return "foo";
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String landing() throws ServletException, IOException {
        response.sendRedirect("/app");
        return "foo";
    }
}
