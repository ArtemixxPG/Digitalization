package model;

import lombok.Data;

import java.util.List;

@Data
public class CostPrice {

    private List<List<String>> ordersToSuppliers;
    private List<List<String>> transferOrders;
    private List<List<String>> purchases;
}
