package com.realizer.salladodriver.databasemodel;

/**
 * Created by Win on 29-06-2017.
 */

public class Location {

    public double latitude;
    public double longitude;
    UserDietDelivery userDietDelivery;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public UserDietDelivery getUserDietDelivery() {
        return userDietDelivery;
    }

    public void setUserDietDelivery(UserDietDelivery userDietDelivery) {
        this.userDietDelivery = userDietDelivery;
    }
}
