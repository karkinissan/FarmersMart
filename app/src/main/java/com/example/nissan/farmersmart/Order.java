package com.example.nissan.farmersmart;

import java.io.Serializable;

public class Order implements Serializable {
    private Product product;
    private String firebaseKey;
    private int selectedWeight;
    private String selectedWeightUnit;
    private double subtotal;
    private double transportationCost;
    private double serviceCharge;
    private double grandTotal;
    private String customerName;
    private String customerAddress;
    private String customerPhoneNumber;

    public Order() {
    }

    public Order(Product product, int selectedWeight, String selectedWeightUnit, double subtotal, double transportationCost, double serviceCharge, double grandTotal) {
        this.product = product;
        this.selectedWeight = selectedWeight;
        this.selectedWeightUnit = selectedWeightUnit;
        this.subtotal = subtotal;
        this.transportationCost = transportationCost;
        this.serviceCharge = serviceCharge;
        this.grandTotal = grandTotal;
    }

    public Order(Order order, String customerName, String customerAddress, String customerPhoneNumber) {
        this.product = order.getProduct();
        this.selectedWeight = order.getSelectedWeight();
        this.subtotal = order.getSubtotal();
        this.transportationCost = order.getTransportationCost();
        this.serviceCharge = order.getServiceCharge();
        this.grandTotal = order.getGrandTotal();
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public Order(Product product, int selectedWeight, String selectedWeightUnit, double subtotal, double transportationCost, double serviceCharge, double grandTotal, String customerName, String customerAddress, String customerPhoneNumber) {
        this.product = product;
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

    public String printAll() {
        String toReturn = "";
        toReturn = toReturn.concat("product " + product.printAll());
        toReturn = toReturn.concat(" selectedWeight " + selectedWeight);
        toReturn = toReturn.concat(" selectedWeightUnit " + selectedWeightUnit);
        toReturn = toReturn.concat(" subtotal " + subtotal);
        toReturn = toReturn.concat(" transportationCost " + transportationCost);
        toReturn = toReturn.concat(" serviceCharge " + serviceCharge);
        toReturn = toReturn.concat(" grandTotal " + grandTotal);
        toReturn = toReturn.concat(" customerName " + customerName);
        toReturn = toReturn.concat(" customerAddress " + customerAddress);
        toReturn = toReturn.concat(" customerPhoneNumber " + customerPhoneNumber);
        return toReturn;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getSelectedWeight() {
        return selectedWeight;
    }

    public void setSelectedWeight(int selectedWeight) {
        this.selectedWeight = selectedWeight;
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

    public String getSelectedWeightUnit() {
        return selectedWeightUnit;
    }

    public void setSelectedWeightUnit(String selectedWeightUnit) {
        this.selectedWeightUnit = selectedWeightUnit;
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
