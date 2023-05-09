package model;

import lombok.Data;

import java.util.Date;

@Data
public class Model {
    //private String document;
    private String code;
    private String nomenclature;
    private String desiredDate;
    private String supplierDate;
    private String flowDate;
    private String salaryDate;
    private String currentDate;
    private String factSalaryDate;

}
