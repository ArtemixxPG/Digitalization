package model;


import lombok.Data;

@Data
public class CalculatedDifferenceDate {

    private String erp;
    private String nomenclature;
    private double lastDifferenceByDesired;
    private double lastDifferenceByEntry;
    private double lastDifferenceByPurchase;
    private double lastDifferenceByComplete;

    private int maxDifferenceByDesired;
    private int maxDifferenceByEntry;
    private int maxDifferenceByPurchase;
    private int maxDifferenceByComplete;
}
