package org.keycloak.example.photoz.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.jboss.logging.Logger;
import org.keycloak.example.photoz.util.ImgUtil;
import org.springframework.stereotype.Component;

/**
 * Just in-memory DB. Doesn't do any authz checks
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class InMemoryPhotozDB {

    private static final Logger log = Logger.getLogger(InMemoryPhotozDB.class);

    private final List<Photo> photoz = new ArrayList<>();

    private final Object lock = new Object();

    public InMemoryPhotozDB() {
        log.info("Reading photoz DB");

        try {
            photoz.add(new Photo("1", "Yellow Van DHL", ImgUtil.readImage("IMG_20190118_082440.jpg")));

            photoz.add(new Photo("2", "Yellow Towing Vehicle", ImgUtil.readImage("IMG_20190118_083037.jpg")));
            photoz.add(new Photo("3", "VW Transporter - Green Bus", ImgUtil.readImage("IMG_20190118_083128.jpg")));
            photoz.add(new Photo("4", "White Alpha Romeo", ImgUtil.readImage("IMG_20190118_083223.jpg")));
            photoz.add(new Photo("5", "Very Cool Green Photo", ImgUtil.readImage("IMG_20190118_083253.jpg")));
            photoz.add(new Photo("6", "Red Mixer", ImgUtil.readImage("IMG_20190118_083333.jpg")));

            photoz.add(new Photo("7", "Red Train", ImgUtil.readImage("IMG_20190118_083404.jpg")));
            photoz.add(new Photo("8", "Red Ferrari", ImgUtil.readImage("IMG_20190118_083457.jpg")));
            photoz.add(new Photo("9", "VW Transporter - Hippies Bus", ImgUtil.readImage("IMG_20190118_083547.jpg")));

            photoz.add(new Photo("10", "White Cistern Truck", ImgUtil.readImage("IMG_20190118_083607.jpg")));
            photoz.add(new Photo("11", "Blue BMW", ImgUtil.readImage("IMG_20190118_083638.jpg")));
            photoz.add(new Photo("12", "Snowplow", ImgUtil.readImage("IMG_20190118_083734.jpg")));
            photoz.add(new Photo("13", "Blue Rescue Jeep", ImgUtil.readImage("IMG_20190118_083831.jpg")));
            photoz.add(new Photo("14", "Black Photoabinieri", ImgUtil.readImage("IMG_20190118_084023.jpg")));
            photoz.add(new Photo("15", "Yellow Truck - Deere", ImgUtil.readImage("IMG_20190118_084119.jpg")));
        } catch (IOException ioe) {
            throw new RuntimeException("Error when initializing photoz", ioe);
        }

        log.infof("Successfully read %d photoz", photoz.size());
    }


    public Photo giveRandomPhotoToUser(String userId, String username) {
        Random r = new Random();

        synchronized(lock) {
            for (int i=0 ; i<1000 ; i++) {
                int photoIndex = r.nextInt(photoz.size());
                Photo photo = photoz.get(photoIndex);

                if (photo.getOwner() == null) {
                    photo.setOwner(new OwnerRepresentation(userId, username));
                    return photo;
                }
            }

            throw new IllegalStateException("Wasn't able to give the photo to user " + username + ". Maybe all photoz are occupied");
        }

    }


    public Photo getPhotoById(String photoId) {
        // TODO: Better handle case when photo doesn't exists...
        return photoz.stream().filter((Photo photo) -> photo.getId().equals(photoId)).findFirst().get();
    }


    public Photo deletePhotoById(String photoId) {
        // TODO: Better handle case when photo doesn't exists...
        Photo photo = getPhotoById(photoId);

        // This means just deleting owner
        synchronized(lock) {
            photo.setOwner(null);
            photo.setExternalId(null);
        }

        return photo;
    }


    // Return only the photoz with non-null owner
    public Stream<Photo> getPhotozWithOwner() {
        return photoz.stream()
                .filter((Photo photo) -> photo.getOwner() != null);
    }


    public class Photo {

        private final String id;
        private final String name;
        private final String base64Img;

        // Null when photo is still "free". It is not very nice to track both ID and username here, but should be fine for the example purpose
        private OwnerRepresentation owner;
        private String externalId;

        public Photo(String id, String name, String base64Img) {
            this.id = id;
            this.name = name;
            this.base64Img = base64Img;
        }


        public String getId() {
            return id;
        }

        public String getBase64Img() {
            return base64Img;
        }

        public String getName() {
            return name;
        }

        public OwnerRepresentation getOwner() {
            return owner;
        }

        public void setOwner(OwnerRepresentation owner) {
            this.owner = owner;
        }

        public String getExternalId() {
            return externalId;
        }

        public void setExternalId(String externalId) {
            this.externalId = externalId;
        }
    }
}
