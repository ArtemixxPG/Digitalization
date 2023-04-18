package main;

import loaders.XSLXLoader;
import lombok.SneakyThrows;

import java.util.List;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        XSLXLoader loader = new XSLXLoader(args[1], args[0], args[2]);
//        loader.load();
//        loader.createDataSet();
     //  loader.export("test", "test.xlsx");
        loader.loadCostPrice();
        loader.loadNomenclatures();
        loader.loadSupportCatalog();
        loader.updateSheet(10, false);

        //System.out.println(loader.getTableModels());
    }
}
