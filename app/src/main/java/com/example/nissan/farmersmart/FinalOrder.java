package com.example.nissan.farmersmart;

public class FinalOrder {
    private String productFirebaseKey;
    private int     selectedWeight;
    private String selectedWeightUnit;
    private double subtotal;
    private double transportationCost;
    private double serviceCharge;
    private double grandTotal;
    private String customerName;
    private String customerAddress;
    private String customerPhoneNumber;

    public FinalOrder() {
    }

    public FinalOrder(String productFirebaseKey, int selectedWeight, String selectedWeightUnit, double subtotal, double transportationCost, double serviceCharge, double grandTotal, String customerName, String customerAddress, String customerPhoneNumber) {
        this.productFirebaseKey = productFirebaseKey;
        this.selectedWeight = selectedWeight;
        this.selectedWeightUnit = selectedWeightUnit;
        this.subtotal = subtotal;
        this.transportationCost = transportationCost;
        this.serviceCharge = serviceCharge;
        this.grandTotal = grandTotal;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public FinalOrder(Order order, String customerName, String customerAddress, String customerPhoneNumber) {
        this.productFirebaseKey = order.getProduct().getProductFirebaseKey();
        this.selectedWeight = order.getSelectedWeight();
        this.selectedWeightUnit = order.getSelectedWeightUnit();
        this.subtotal = order.getSubtotal();
        this.transportationCost = order.getTransportationCost();
        this.serviceCharge = order.getServiceCharge();
        this.grandTotal = order.getGrandTotal();
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String printAll() {
        String toReturn = "";
        toReturn = toReturn.concat("productFirebaseKey "+productFirebaseKey);
        toReturn = toReturn.concat(" selectedWeight "+ selectedWeight);
        toReturn = toReturn.concat(" selectedWeightUnit "+selectedWeightUnit);
        toReturn = toReturn.concat(" subtotal "+subtotal);
        toReturn = toReturn.concat(" transportationCost " +transportationCost);
        toReturn = toReturn.concat(" serviceCharge "+serviceCharge);
        toReturn = toReturn.concat(" grandTotal "+grandTotal);
        toReturn = toReturn.concat(" customerName "+customerName);
        toReturn = toReturn.concat(" customerAddress "+customerAddress);
        toReturn = toReturn.concat(" customerPhoneNumber "+customerPhoneNumber);
        return toReturn;
    }

    public String getProductFirebaseKey() {
        return productFirebaseKey;
    }

    public void setProductFirebaseKey(String productFirebaseKey) {
        this.productFirebaseKey = productFirebaseKey;
    }

    public int getSelectedWeight() {
        return selectedWeight;
    }

    public void setSelectedWeight(int selectedWeight) {
        this.selectedWeight = selectedWeight;
    }

    public String getSelectedWeightUnit() {
        return selectedWeightUnit;
    }

    public void setSelectedWeightUnit(String selectedWeightUnit) {
        this.selectedWeightUnit = selectedWeightUnit;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTransportationCost() {
        return transportationCost;
    }

    public void setTransportationCost(double transportationCost) {
        this.transportationCost = transportationCost;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }
}
