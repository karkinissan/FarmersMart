package com.example.nissan.farmersmart;

import java.io.Serializable;

public class Product implements Serializable {

    private String productFirebaseKey;
    private String username;
    private String name;
    private int quantity;
    private String weightUnit;
    private int minimumQuantity;
    private int maximumQuantity;
    private String dateExpiry;
    private float rate;
    private String location;
    private float farmerPhoneNumber;
    private String farmerAddress;
    private String photoUrl;

    public Product() {
    }

    public Product(String username, String name,
                   int quantity, String weightUnit, int minimumQuantity, int maximumQuantity,
                   String dateExpiry, float rate, String location, float farmerPhoneNumber,
                   String farmerAddress, String photoUrl) {
        this.username = username;
        this.name = name;
        this.quantity = quantity;
        this.weightUnit = weightUnit;
        this.minimumQuantity = minimumQuantity;
        this.maximumQuantity = maximumQuantity;
        this.dateExpiry = dateExpiry;
        this.rate = rate;
        this.location = location;
        this.farmerPhoneNumber = farmerPhoneNumber;
        this.farmerAddress = farmerAddress;
        this.photoUrl = photoUrl;
    }

    public String printAll() {
        String toReturn = "";
        toReturn = toReturn.concat("FirebaseKey: " + productFirebaseKey);
        toReturn = toReturn.concat(" Username: " + username);
        toReturn = toReturn.concat(" productName: " + name);
        toReturn = toReturn.concat(" quantity: " + quantity);
        toReturn = toReturn.concat(" weightUnit: " + weightUnit);
        toReturn = toReturn.concat(" minimumQuantity: " + minimumQuantity);
        toReturn = toReturn.concat(" maximumQuantity: " + maximumQuantity);
        toReturn = toReturn.concat(" dateExpiry: " + dateExpiry);
        toReturn = toReturn.concat(" rate: " + rate);
        toReturn = toReturn.concat(" location: " + location);
        toReturn = toReturn.concat(" farmerAddress: " + farmerAddress);
        toReturn = toReturn.concat(" farmerPhoneNumber: " + farmerPhoneNumber);
        toReturn = toReturn.concat(" photoUrl: " + photoUrl);
        return toReturn;
    }

    public String getProductFirebaseKey() {
        return productFirebaseKey;
    }

    public void setProductFirebaseKey(String productFirebaseKey) {
        this.productFirebaseKey = productFirebaseKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public int getMaximumQuantity() {
        return maximumQuantity;
    }

    public void setMaximumQuantity(int maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    public String getDateExpiry() {
        return dateExpiry;
    }

    public void setDateExpiry(String dateExpiry) {
        this.dateExpiry = dateExpiry;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getFarmerPhoneNumber() {
        return farmerPhoneNumber;
    }

    public void setFarmerPhoneNumber(float farmerPhoneNumber) {
        this.farmerPhoneNumber = farmerPhoneNumber;
    }

    public String getFarmerAddress() {
        return farmerAddress;
    }

    public void setFarmerAddress(String farmerAddress) {
        this.farmerAddress = farmerAddress;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
