package model;

import lombok.Data;

@Data
public class DifferenceDate {

    private String erp;
    private String nomenclature;
    private int differenceByDesired;
    private int differenceByEntry;
    private int differenceByPurchase;
    private int differenceByComplete;


}
