package main;

import loaders.XSLXLoader;
import lombok.SneakyThrows;

import java.util.List;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        XSLXLoader loader = new XSLXLoader(args[0], args[1], null, true);
        loader.exportResources(10, 0);

//        loader.load();
//        loader.createDataSet();
//       loader.export("date_with_nomenclature", "date_with_nomenclature.xlsx");
//        loader.loadCostPrice();
//        loader.loadNomenclatures();
//        loader.loadSupportCatalog();
//        loader.updateSheet(10, false);

        //System.out.println(loader.getTableModels());
    }
}
