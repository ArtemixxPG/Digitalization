package model;

import lombok.Data;

import java.util.Date;

@Data
public class Model {
    private String document;
    private String desiredDate;
    private String planningDate;
    private String salaryDate;
    private String currentDate;
    private Integer factSalaryDate;
    private String deliveryDate;

}
