package main;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import google.sheets.range.GoogleSheetsRange;
import loaders.XSLXLoader;
import lombok.SneakyThrows;
import parsers.ParametersParser;

import java.util.*;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        XSLXLoader loader = new XSLXLoader(args[0], args[1], args[2], null, true);
//        loader.matchDataSet();
//        loader.exportDataSet();
        //loader.exportResources(10, 0, 10);

       // loader.load();
       // loader.createDataSetFromDifferenceDate();
       //loader.exportDifference("difference_date_with_nomenclature","calculated_difference_date_with_nomenclature.xlsx", "difference_date_with_nomenclature.xlsx");
//        loader.loadCostPrice();
//        loader.loadNomenclatures();
//        loader.loadSupportCatalog();
        //loader.updateSheet(10, false);

        //System.out.println(loader.getTableModels());

        //XSLXLoader loader = new XSLXLoader();
       // loader.loadWagonDataset(args[0], args[1], args[2]);
        //loader.exportUpdateWagon("complete_515_wagon_Iabanov_prices.xlsx");
//        GoogleTest test = new GoogleTest();
//        GoogleTest.setup();
//        test.whenWriteSheet_thenReadSheetOk();

        ParametersParser parametersParser = ParametersParser.getInstance();

        final String spreadsheetId = "1JDaB55Dnry1GGZf2_MCc6U2Q74Sm1tfjyIzTmUCs_r0";
        final String range = "413,414,415!B8:B";
        final String rangeUpdate = "413,414,415!H9";

        GoogleSheetsRange sheetsRange = new GoogleSheetsRange(spreadsheetId);
       // System.out.println(sheetsRange.getRange(range));
//        List<List<Object>> prices = parametersParser.getPricesByRpi(sheetsRange.getRange(range),
//                loader.getPtiuList(),0);
        sheetsRange.updateRange(rangeUpdate, null);

    }
}
