package loaders;

import model.CostPrice;
import model.Model;
import model.Nomenclature;
import model.Product;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.apache.poi.ss.usermodel.CellType.STRING;

public class XSLXLoader {

    private static final String[] headers = {"Код", "Номенклатура", "ЖО", "ДЗП", "ДП", "ДОПЛ", "ДПО", "ДПр"};
    private static final String KVL_ARKS_013 = "ВИКС 2022-2024 / 013 КВЛ-АРКС";
    private static final String KVL_ARKS_012 = "ВИКС 2022-2024 / 012 КВЛ-АРКС";

//    private static final String

    private Workbook workbook;
    private Workbook writeWorkbook;

    private Workbook nomenclaturesWorkbook;

    private Workbook secondWorkbook;

    private CostPrice costPrice;

    private Workbook importWorkBook;
    private Workbook exportWorkbook;

    private List<List<String>> transferOrders;
    private List<List<String>> suppliersOrders;
    private List<List<String>> paymentOrders;
    private List<List<String>> purchaseByCustomer;
    private FileInputStream file;

    private List<List<String>> listKVLARKS013;

    private FileInputStream secondFile;

    private List<Product> products;

    private List<Model> tableModels;
    private String fileName;

    private List<List<String>> targetProducts;
    private List<List<String>> priceList;

    private List<Nomenclature> nomenclatures;
    private String outputFileName;

    private String nomenclaturesFileName;

    public XSLXLoader(String fileName, String secondFileName, String nomenclaturesFileName, boolean isExportImportMode) throws IOException {
        if(!isExportImportMode) {
            this.fileName = fileName;
            this.file = new FileInputStream(new File(fileName));
            this.workbook = new XSSFWorkbook(file);
            this.writeWorkbook = new XSSFWorkbook();
            this.transferOrders = new ArrayList();
            this.suppliersOrders = new ArrayList();
            this.paymentOrders = new ArrayList();
            this.purchaseByCustomer = new ArrayList();
            this.tableModels = new ArrayList<>();
            this.targetProducts = new ArrayList<>();
            this.priceList = new ArrayList<>();
        }
        else {
            ZipSecureFile.setMinInflateRatio(0);
            this.importWorkBook = new XSSFWorkbook(new FileInputStream(fileName));
            this.exportWorkbook = new XSSFWorkbook(new FileInputStream(secondFileName));
        }


//        this.secondFile = new FileInputStream(new File(secondFileName));
//        this.secondWorkbook = new XSSFWorkbook(secondFile);
//        this.products = new ArrayList<>();
//        this.outputFileName = secondFileName;
//        this.costPrice = new CostPrice();
//        this.nomenclatures = new ArrayList<>();
//        this.nomenclaturesFileName = nomenclaturesFileName;
//        this.nomenclaturesWorkbook = new XSSFWorkbook(new FileInputStream(nomenclaturesFileName));
//        this.listKVLARKS013 = new ArrayList<>();
    }

    private List<List<String>> parse(int numberOfSheet, Workbook workbook) throws FileNotFoundException {
        List<List<String>> dataSheet = new ArrayList();
        int i = 0;
        Sheet sheet = workbook.getSheetAt(numberOfSheet);
        for (Row row : sheet) {
            dataSheet.add(new ArrayList());
            for (Cell cell : row) {

                switch (cell.getCellType()) {

                    case STRING:
                        dataSheet.get(i).add(cell.getRichStringCellValue().getString());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            dataSheet.get(i).add(cell.getDateCellValue() + "");
                        } else {
                            dataSheet.get(i).add(cell.getNumericCellValue() + "");
                        }
                        break;
                    case BLANK:
                        dataSheet.get(i).add(null);
                        break;
                }
            }
            i++;
        }
        dataSheet.remove(0);
        return dataSheet;
    }




    public void loadNomenclatures() throws FileNotFoundException {

        List<List<String>> prevNomenclatures = parse(0, nomenclaturesWorkbook);

        for (List<String> item : prevNomenclatures) {
            Nomenclature nomenclature = new Nomenclature();
            if (item.size() == 4) {

                nomenclature.setCode(item.get(0));
                nomenclature.setName(item.get(1));
                nomenclature.setArticle(item.get(2));
                nomenclature.setCreator(item.get(3));
            }
            if (item.size() == 2) {
                nomenclature.setCode(item.get(0));
                nomenclature.setName(item.get(1));
            }
            if (item.size() == 3) {
                nomenclature.setCode(item.get(0));
                nomenclature.setName(item.get(1));
                nomenclature.setArticle(item.get(2));
            }
            nomenclatures.add(nomenclature);
        }

    }


    private CellStyle getRPIStyle(){
        CellStyle cellRpiStyle = secondWorkbook.createCellStyle();

        cellRpiStyle.setBorderBottom(BorderStyle.HAIR);
        cellRpiStyle.setBorderTop(BorderStyle.HAIR);
        cellRpiStyle.setBorderRight(BorderStyle.HAIR);
        cellRpiStyle.setBorderLeft(BorderStyle.HAIR);

        cellRpiStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellRpiStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont rpiFont = ((XSSFWorkbook) secondWorkbook).createFont();
        rpiFont.setFontName("Times New Roman");
        rpiFont.setFontHeightInPoints((short) 9);
        rpiFont.setBold(true);
        rpiFont.setItalic(true);
        cellRpiStyle.setFont(rpiFont);
        return cellRpiStyle;
    }

    public void load() throws FileNotFoundException {
        transferOrders = parse(0, workbook);
        suppliersOrders = parse(1, workbook);
        paymentOrders = parse(2, workbook);
        purchaseByCustomer = parse(3, workbook);



        transferOrders.remove(0);
        suppliersOrders.remove(0);
        paymentOrders.remove(0);
        purchaseByCustomer.remove(0);
    }

    public void loadCostPrice() throws FileNotFoundException {
        costPrice.setOrdersToSuppliers(parse(0, workbook));
        costPrice.setTransferOrders(parse(1, workbook));
        costPrice.setPurchases(parse(2, workbook));

    }

    public void loadSupportCatalog() throws FileNotFoundException {
        listKVLARKS013 = parse(9, secondWorkbook);
        int index = 0;
        while (index < 9){
            listKVLARKS013.remove(0);
            index++;
        }
    }

    public void loadFilePrice() throws FileNotFoundException {
        priceList = parse(0, workbook);
        int voidStroke = 0;
        while (voidStroke < 9) {
            priceList.remove(0);
            voidStroke++;
        }
        priceList.remove(priceList.size() - 1);
        for (List<String> item : priceList) {
            if (item.size() > 6) {
                Product product = new Product();
                product.setName(item.get(1));

                if (!item.get(6).equals("")) {
                    String price = item.get(6);
                    product.setPrice(Double.parseDouble(price));
                }
                products.add(product);
            }
        }
    }


    public void updateSheet(int sheetIndex, boolean isCreateRows) throws IOException {
        Sheet sheet = secondWorkbook.getSheetAt(sheetIndex);
        int index = 0;

        CellStyle cellStyle = secondWorkbook.createCellStyle();

        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.HAIR);
        cellStyle.setBorderTop(BorderStyle.HAIR);
        cellStyle.setBorderRight(BorderStyle.HAIR);
        cellStyle.setBorderLeft(BorderStyle.HAIR);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        cellStyle.setFont(font);

        CellStyle cellRpiStyle = secondWorkbook.createCellStyle();

        cellRpiStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellRpiStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cellStyle.setFont(font);

        boolean notRPIFound = false;

        for (Row row : sheet) {
            if(row.getCell(2).getCellType() == CellType.BLANK || row.getCell(0).getCellType() != CellType.BLANK){
                continue;
            }
            if (row.getRowNum() >= 7) {

                if (isCreateRows) {
                    if (row.getRowNum() == 3) {
                        createCellString(row, 18, "НДС", sheet.getColumnStyle(0));
                        createCellString(row, 19, "Цена", sheet.getColumnStyle(0));
                        createCellString(row, 20, "Код ноиенклатуры", sheet.getColumnStyle(0));
                        createCellString(row, 21, "Номенклатура", sheet.getColumnStyle(0));
                    }
                } else {
                    String rpi = row.getCell(1).getStringCellValue();
                    if (rpi.equals("")) {
                        String name = row.getCell(2).getStringCellValue();

                        List<Nomenclature> rpiNomenclatures = nomenclatures.stream()
                                .filter(item -> (item.getName() != null && item.getName().contains(name))
                                        || (item.getArticle() != null && item.getArticle().contains(name)) ||
                                        (item.getCreator() != null && item.getCreator().contains(name))).toList();
                        if (!rpiNomenclatures.isEmpty()) {
                            rpi = rpiNomenclatures.get(0).getCode();
                            createCellString(row, 1, rpi, getRPIStyle());
                        }
                        String finalRpi1 = rpi;
                        List<List<String>> nameFromListKVLARKS013 =  listKVLARKS013.stream().filter(
                                item -> item.get(0).equals(finalRpi1)
                        ).toList();
                        if(nameFromListKVLARKS013.isEmpty()) {
                            Cell cell = row.getCell(2);
                            String nameNomenclature = cell.getStringCellValue();
                            findNomenclatureBySplitName(nameNomenclature, row, cellStyle, listKVLARKS013);
                        }

                    }
                    else  {
                        String finalRpi = rpi;
                        List<List<String>> orders = new ArrayList<>(listKVLARKS013.stream().filter(item ->
                                item.get(0).equals(finalRpi)).toList());
                        if (orders.size() >=2) {
                            checkElementInSupportCatalog(rpi, row, cellStyle);
                            createPricesAndNDS(rpi, orders, row, cellStyle);

                        } else if (orders.size() == 1) {

                            createCell(row, cellStyle, orders);

                        } else {
                            Cell cell = row.getCell(2);
                            String nameNomenclature = cell.getStringCellValue();
                            findNomenclatureBySplitName(nameNomenclature, row, cellStyle, listKVLARKS013);
                        }

                    }

                    if (isCreateRows) {
                        Cell cell = row.getCell(2);

                        Cell cellOrder = row.getCell(4);
                        Cell cellSpecification = row.getCell(3);
                        if (cell.getCellType() == STRING && cellOrder.getCellType() == STRING) {
                            String cellData = cell.getStringCellValue();
                            if (cellOrder.getStringCellValue().split(" ")[0]
                                    .split("-").length > 1) {
                                String order = cellOrder.getStringCellValue().split(" ")[0]
                                        .split("-")[1];
                                if (!costPrice.getOrdersToSuppliers().stream().filter(
                                        item -> item.get(3).contains(order)
                                ).toList().isEmpty()) {
                                    List<List<String>> orderToSuppliers = costPrice.getOrdersToSuppliers().stream().filter(
                                            item -> item.get(3).contains(order)
                                    ).toList();

                                    List<String> splitName = new ArrayList<>();
                                    splitName.addAll(Arrays.asList(cellData.split(" ")));

                                    List<List<String>> currentOrderToSuppliers = null;
                                    while (!splitName.isEmpty()) {
                                        for (String word : splitName) {
                                            currentOrderToSuppliers = orderToSuppliers.stream().filter(item ->
                                                    item.get(2).contains(StringUtils.capitalize(word)) ||
                                                            item.get(2).contains(word)).toList();
                                            if (currentOrderToSuppliers.size() > 1) {
                                                break;
                                            }
                                        }
                                        if (currentOrderToSuppliers.size() > 1) {
                                            break;
                                        }
                                        splitName.remove(0);
                                    }

                                    currentOrderToSuppliers = getLists(splitName, currentOrderToSuppliers);


                                    if (!currentOrderToSuppliers.isEmpty()) {
                                        String orderToSupplier = currentOrderToSuppliers.get(0).get(0);
                                        String nomenclature = currentOrderToSuppliers.get(0).get(1);
                                        if (!orderToSupplier.equals("") && !nomenclature.equals("")) {
                                            List<List<String>> finds = new ArrayList<>(costPrice.getPurchases().stream().filter(item ->
                                                    item.get(4).contains(orderToSupplier) && item.get(1).contains(nomenclature)).toList());
                                            if (!finds.isEmpty()) {


                                                if (finds.size() > 1) {
                                                    createPricesAndNDS(rpi, finds, row, cellStyle);
                                                } else {
                                                    createCell(row, cellStyle, finds);

                                                }
                                            } else {
                                                findPriceWithOutOrderToSupplier(rpi, cellSpecification, cellData, row, cellStyle);
                                            }
                                        }
                                    } else {
                                        findPriceWithOutOrderToSupplier(rpi, cellSpecification, cellData, row, cellStyle);
                                    }
                                }
                            } else {
                                findPriceWithOutOrderToSupplier(rpi,
                                        cellSpecification, cellData, row, cellStyle);
                            }
                        }
                    }
                }
            }
            if((row.getCell(14).getCellType() == CellType.BLANK && row.getCell(15).getCellType() == CellType.BLANK &&
            row.getCell(16).getCellType() == CellType.BLANK) && (row.getCell(8).getCellType() != CellType.BLANK
                    && row.getCell(9).getCellType() != CellType.BLANK &&
                    row.getCell(10).getCellType() != CellType.BLANK)){

                createCellString(row, 14, row.getCell(8).getStringCellValue(), cellStyle);
                createCellNumeric(row, 15, row.getCell(9).getNumericCellValue(), cellStyle);
                createCellNumeric(row, 16, row.getCell(10).getNumericCellValue(), cellStyle);
            }
            index++;
        }
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "updated_" + outputFileName;

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        secondWorkbook.write(outputStream);
        secondWorkbook.close();

    }

    private void checkElementInSupportCatalog(String rpi, Row row, CellStyle cellStyle){

        List<List<String>> findersKVLARKS13 = new ArrayList<>(listKVLARKS013.stream().filter(item ->

                item.get(0).equals(rpi) && (item.get(4).equals(KVL_ARKS_012) ||
                        item.get(4).equals(KVL_ARKS_013))).toList());


        if(!findersKVLARKS13.isEmpty()) {
            if(findersKVLARKS13.size() >= 2) {

                createPricesAndNDSPTIU(findersKVLARKS13, row, cellStyle);
            } else {
                if(findersKVLARKS13.get(0).get(4).equals(KVL_ARKS_012)) {
                    createCell(row,  cellStyle, findersKVLARKS13);
                } else {
                    createCellString(row, 14, findersKVLARKS13.get(0).get(3), cellStyle);
                    createCellNumeric(row, 15, Double.parseDouble(findersKVLARKS13.get(0).get(6)), cellStyle);
                    createCellNumeric(row, 16, Double.parseDouble(findersKVLARKS13.get(0).get(6)), cellStyle);
                }
            }
        }
    }

    private List<List<String>> getLists(List<String> splitName, List<List<String>> currentOrderToSuppliers) {
        if (currentOrderToSuppliers.size() > 1) {
            while (!splitName.isEmpty()) {
                for (String word : splitName) {
                    if (!currentOrderToSuppliers.stream().filter(item -> item.get(2).contains(StringUtils.capitalize(word)) ||
                            item.get(2).contains(word)).toList().isEmpty()) {
                        currentOrderToSuppliers = currentOrderToSuppliers.stream().filter(item -> item.get(2).contains(StringUtils.capitalize(word)) ||
                                item.get(2).contains(word)).toList();

                    }

                }
                splitName.remove(0);
            }
        }
        return currentOrderToSuppliers;
    }

    private void createPricesAndNDS(String rpi, List<List<String>> orders, Row row, CellStyle cellStyle) {
        orders.sort((o1, o2) -> {
            if (o1.isEmpty() || o2.isEmpty()) {
                return 0;
            }
            if (o1.get(0).equals("") || o2.get(0).equals("")) {
                return 0;
            }

            return dateFormatter(o1, o2);
        });

        double lastPrice =  0;
        if(orders.get(orders.size() - 1).size() == 8) {
             lastPrice = Double.parseDouble(orders.get(orders.size() - 1).get(6));
        }

        if(orders.get(orders.size() - 1).size() == 8) {
            lastPrice = Double.parseDouble(orders.get(orders.size() - 1).get(5));
        }
        orders.sort(new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                if (o1.isEmpty() || o2.isEmpty()) {
                    return 0;
                }
                if (o1.get(6).equals("") || o2.get(6).equals("")) {
                    return 0;
                }

                Double firstPrice = Double.valueOf(0);
                Double secondPrice = Double.valueOf(0);

                if(o1.size() == 9 && o2.size() == 9) {
                   firstPrice = Double.parseDouble(o1.get(6));
                   secondPrice = Double.parseDouble(o2.get(6));
                }

                if(o1.size() == 8 && o2.size() == 8) {
                    firstPrice = Double.parseDouble(o1.get(5));
                    secondPrice = Double.parseDouble(o2.get(5));
                }

                if(o1.size() == 9 && o2.size() == 8) {
                    firstPrice = Double.parseDouble(o1.get(6));
                    secondPrice = Double.parseDouble(o2.get(5));
                }

                if(o1.size() == 8 && o2.size() == 9) {
                    firstPrice = Double.parseDouble(o1.get(5));
                    secondPrice = Double.parseDouble(o2.get(6));
                }

                return firstPrice.compareTo(secondPrice);
            }
        });
        double maxPrice = 0;
        if(orders.get(orders.size() - 1).size() == 8) {
            maxPrice = Double.parseDouble(orders.get(orders.size() - 1).get(5));
        }
        if(orders.get(orders.size() - 1).size() == 9) {
            maxPrice = Double.parseDouble(orders.get(orders.size() - 1).get(6));
        }

        String nds = orders.get(0).get(3);



        createCellString(row, 8, nds, cellStyle);
        createCellNumeric(row, 9, lastPrice, cellStyle);
        createCellNumeric(row, 10, maxPrice, cellStyle);
        checkElementInSupportCatalog(rpi, row, cellStyle);
    }

    private void createPricesAndNDSPTIU(List<List<String>> orders, Row row, CellStyle cellStyle) {
        orders.sort((o1, o2) -> {
            if (o1.isEmpty() || o2.isEmpty()) {
                return 0;
            }
            if (o1.get(1).equals("") || o2.get(1).equals("")) {
                return 0;
            }

            return dateFormatter(o1, o2);
        });
        Double lastPrice = Double.parseDouble(orders.get(orders.size() - 1).get(6));

        orders.sort(new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                if (o1.isEmpty() || o2.isEmpty()) {
                    return 0;
                }
                if (o1.get(7).equals("") || o2.get(7).equals("")) {
                    return 0;
                }

                Double firstPrice = Double.parseDouble(o1.get(7));
                Double secondPrice = Double.parseDouble(o2.get(7));

                return firstPrice.compareTo(secondPrice);
            }
        });

        Double maxPrice = Double.parseDouble(orders.get(orders.size() - 1).get(7));

        String nds = orders.get(0).get(4);

        createCellString(row, 8, nds, cellStyle);
        createCellNumeric(row, 9, lastPrice, cellStyle);
        createCellNumeric(row, 10, maxPrice, cellStyle);

        if(orders.get(0).get(4).equals(KVL_ARKS_013)) {
            createCellString(row, 8, nds, cellStyle);
            createCellNumeric(row, 9, lastPrice, cellStyle);
            createCellNumeric(row, 10, maxPrice, cellStyle);
        } else {
            createCellString(row, 14, nds, cellStyle);
            createCellNumeric(row, 15, lastPrice, cellStyle);
            createCellNumeric(row, 16, maxPrice, cellStyle);
        }
    }

    private int dateFormatter(List<String> o1, List<String> o2) {
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("" + "[dd.MM.yyyy'T'HH:mm:ss]" + "[dd.MM.yyyy'T'H:mm:ss]"));
        DateTimeFormatter formatter = dateTimeFormatterBuilder.toFormatter();
        int firstSize = o1.get(1).split(" ").length;
        int secondSize = o1.get(1).split(" ").length;
        LocalDate firstDate = LocalDate.parse(o1.get(1).split(" ")[firstSize - 2] + "T" + o1.get(1).split(" ")[firstSize - 1], formatter);
        LocalDate secondDate = LocalDate.parse(o2.get(1).split(" ")[secondSize - 2] + "T" + o2.get(1).split(" ")[secondSize - 1], formatter);

        return firstDate.compareTo(secondDate);
    }

    public void createDatePeriod(){
        int JO = 0;

    }

    private void findPriceWithOutOrderToSupplier(String rpi, Cell cellSpecification, String cellData, Row row, CellStyle cellStyle) {
        if (cellSpecification.getCellType() == STRING) {
            String specification = cellSpecification.getStringCellValue();
            if (!costPrice.getPurchases().stream().filter(
                    item -> item.get(2).contains(cellData) || item.get(2).contains(specification)
            ).toList().isEmpty()) {
                List<List<String>> purchases = new ArrayList<>(costPrice.getPurchases().stream().filter(
                        item -> item.get(2).contains(cellData) || item.get(2).contains(specification)
                ).toList());
                if (purchases.size() >= 2) {
                    createPricesAndNDS(rpi, purchases, row, cellStyle);
                } else {
                    List<String> find = purchases.get(0);
                    Double lastPrice = Double.parseDouble(find.get(6));
                    String nds = find.get(3);
                    createCellString(row, 1, find.get(1), getRPIStyle());
                    createCellString(row, 8, nds, cellStyle);
                    createCellNumeric(row, 9, lastPrice, cellStyle);
                    createCellNumeric(row, 10, lastPrice, cellStyle);
                }
            } else {
                List<String> splitName = new ArrayList<>(Arrays.asList(cellData.split(" ")));
                List<List<String>> purchases = null;


                while (!splitName.isEmpty()) {
                    for (String word : splitName) {
                        purchases = costPrice.getPurchases().stream().filter(item -> item.get(2)
                                .contains(StringUtils.capitalize(word)) ||
                                item.get(2).contains(word)).toList();

                        if (purchases.size() > 0) {
                            break;
                        }

                    }
                    splitName.remove(0);
                    if (purchases.size() > 0) {
                        break;
                    }

                }


                assert purchases != null;
                purchases = getLists(splitName, purchases);
                if (!purchases.isEmpty()) {
                    createCell(row, cellStyle, purchases);
                }

            }
        }
    }


    private void findNomenclatureBySplitName(String name, Row row, CellStyle cellStyle, List<List<String>> ptiu){


        List<String> splitName = new ArrayList<>(Arrays.asList(name.split(" ")));
        List<List<String>> purchases = null;


        while (!splitName.isEmpty()) {
            StringBuilder addName = new StringBuilder();
            boolean ifFindLowerCase = false;
            for (String word : splitName) {



                if(!word.equals("") && Character.isUpperCase(word.charAt(0))) {
                    addName.append(word);
                    ifFindLowerCase = false;
                } else {
                    addName.append(" ").append(word).append(" ");
                    ifFindLowerCase = true;
                }

                if(!ifFindLowerCase) {
                    purchases = ptiu.stream().filter(item -> item.get(2)
                            .contains(StringUtils.capitalize(word)) ||
                            item.get(2).contains(word)).toList();
                } else {
                    purchases = ptiu.stream().filter(item -> item.get(2)
                            .contains(StringUtils.capitalize(addName.toString())) ||
                            item.get(2).contains(addName.toString())).toList();
                }


                    if (purchases.size() > 0) {
                        break;
                    }
                }

            splitName.remove(0);
            if (purchases.size() > 0) {
                break;
            }

        }


        purchases = getLists(splitName, purchases);
        if (!purchases.isEmpty()) {
            createCell(row, cellStyle, purchases);
        }
    }

    private void createCell(Row row, CellStyle cellStyle, List<List<String>> purchases) {
        List<String> purchase = purchases.get(0);
        if(purchase.size() == 8) {
            Double lastPrice = Double.parseDouble(purchase.get(5));
            String nds = purchase.get(3);
            createCellString(row, 1, purchase.get(0), getRPIStyle());
            createCellString(row, 8, nds, cellStyle);
            createCellNumeric(row, 9, lastPrice, cellStyle);
            createCellNumeric(row, 10, lastPrice, cellStyle);
        }
        if(purchase.size() == 9) {
            Double lastPrice = Double.parseDouble(purchase.get(6));
            String nds = purchase.get(3);
            createCellString(row, 1, purchase.get(0), getRPIStyle());
            createCellString(row, 8, nds, cellStyle);
            createCellNumeric(row, 9, lastPrice, cellStyle);
            createCellNumeric(row, 10, lastPrice, cellStyle);
        }
    }

    public void createDataSet() throws ParseException {
        while (!transferOrders.isEmpty()) {
            List<String> document = transferOrders.get(0);
            String code = document.get(2);
            String nomenclature = document.get(3);
            String documentName = document.get(0).split(" ")[3];
//            List<List<String>> currentTransferOrders = transferOrders.stream()
//                    .filter(item -> item.get(0).contains(documentName)).toList();

            List<List<String>> currentTransferOrders = transferOrders.stream()
                    .filter(item -> item.get(2).contains(code) && item.get(3).contains(nomenclature)).toList();

//            List<List<String>> currentSuppliersOrders = new ArrayList<>(suppliersOrders.stream()
//                    .filter(item -> item.get(3).contains(documentName)).toList());

            List<List<String>> currentSuppliersOrders = new ArrayList<>(suppliersOrders.stream()
                    .filter(item ->  item.get(1).contains(code) && item.get(2).contains(nomenclature)).toList());

            if (currentSuppliersOrders.size() == 0) {
                Model model = new Model();
                model.setCode(code);
                model.setNomenclature(nomenclature);
                if(document.get(1) != null) {
                    model.setDesiredDate(document.get(1));
                }
                tableModels.add(model);
            }

            while (currentSuppliersOrders.size() > 0) {
                List<String> order = currentSuppliersOrders.get(0);
                String orderName = order.get(0).split(" ")[2];
                String supplierDate = order.get(0).split(" ")[4];

                if(orderName != null) {
                    List<List<String>> currentPaymentOrders = paymentOrders.stream()
                            .filter(item -> item.get(3) != null && item.get(3).contains(orderName)).toList();

                    List<List<String>> currentPurchaseByCustomer = purchaseByCustomer.stream()
                            .filter(item -> item.get(2) != null && item.get(2).contains(orderName)).toList();


                    Model model = new Model();
                    if (currentPurchaseByCustomer.size() > 0 && currentPaymentOrders.size() > 0) {

                        model.setCode(code);
                        model.setNomenclature(nomenclature);
                        if (document.get(1) != null) {
                            model.setDesiredDate(document.get(1));
                        }
                        model.setSupplierDate(supplierDate);
                        model.setFlowDate(order.get(5));
                        model.setSalaryDate(currentPaymentOrders.get(0).get(1));

                        if (model.getSalaryDate() != null && model.getFlowDate() != null
                                && model.getSupplierDate() != null) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                            Date firstDate = simpleDateFormat.parse(model.getFlowDate());
                            Date secondDate = simpleDateFormat.parse(model.getSupplierDate());
                            Date thridDate = simpleDateFormat.parse(model.getSalaryDate());

                            int curInterval = (int) Math.abs(TimeUnit.DAYS.convert(firstDate.getTime() - secondDate.getTime(), TimeUnit.MILLISECONDS));

                            int day = (int) Math.abs(TimeUnit.DAYS.convert(thridDate.getTime() - secondDate.getTime(), TimeUnit.MILLISECONDS));

                            int allDay = day + curInterval;

                            Calendar calendar = Calendar.getInstance();

                            calendar.setTime(secondDate);
                            calendar.add(Calendar.DATE, allDay);

                            Date factDate = calendar.getTime();

                            model.setFactSalaryDate(simpleDateFormat.format(calendar.getTime()));
                        }

                        model.setCurrentDate(currentPurchaseByCustomer.get(0).get(1));


                        tableModels.add(model);


                    }
                    List<List<String>> orders = currentSuppliersOrders.stream().filter(item -> item.get(0).contains(orderName) && item.get(5).contains(order.get(5))).toList();
                    currentSuppliersOrders.removeAll(orders);
                    suppliersOrders.removeAll(orders);
                    paymentOrders.removeAll(currentPaymentOrders);
                    purchaseByCustomer.removeAll(currentPurchaseByCustomer);
                }
            }
            transferOrders.remove(0);
        }
    }


    public void exportResources(int sheetImportIndex, int sheetExportIndex) throws IOException {

        Sheet importSheet = importWorkBook.getSheetAt(sheetImportIndex);
        Sheet exportSheet = exportWorkbook.getSheetAt(sheetExportIndex);

        int index = 0;

        for (Row row : exportSheet) {

            if (row.getRowNum() > 6) {
                if(row.getCell(1) == null) {
                    continue;
                }
                String name;
                String obj;
                if(row.getCell(1).getCellType() == STRING) {
                    name = row.getCell(1).getStringCellValue();
                } else {
                    name = "" + row.getCell(1).getNumericCellValue();
                }

                if(row.getCell(2) != null && row.getCell(2).getCellType() == STRING) {
                    obj = row.getCell(2).getStringCellValue();
                } else {
                    obj = "" + row.getCell(2).getNumericCellValue();
                }

                findERPInXLSXFile(importSheet, name, obj, row);
            }
        }

            File currDir = new File(".");
            String path = currDir.getAbsolutePath();
            String fileLocation = path.substring(0, path.length() - 1) + "updatedERP.xlsx";


        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        exportWorkbook.write(outputStream);
        exportWorkbook.close();

    }

    private void findERPInXLSXFile(Sheet importSheet, String name,  String obj, Row exportRow){
        for(Row row : importSheet) {
            if (row.getRowNum() > 7) {
                if (row.getCell(2).getStringCellValue().contains(name) ||
                        row.getCell(2).getStringCellValue().contains(obj)) {
                    String rpi = row.getCell(1).getStringCellValue();
                    Double price = row.getCell(9).getNumericCellValue();
                    Double maxPrice = row.getCell(10).getNumericCellValue();
                    String nds = row.getCell(8).getStringCellValue();
                    exportRow.getCell(3).setCellValue(rpi);
                    exportRow.getCell(5).setCellValue(price);
                    exportRow.getCell(6).setCellValue(maxPrice);
                    exportRow.getCell(7).setCellValue(nds);
                }
            }
        }
    }

    public void export(String sheetName, String fileName) throws IOException {

        try {


            Sheet sheet = writeWorkbook.createSheet(sheetName);

            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);

            Row header = sheet.createRow(0);

            CellStyle headerStyle = writeWorkbook.createCellStyle();

            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            headerStyle.setFont(font);


            int headerCellIndex = 0;
            for (String headerCellName : headers) {
                Cell headerCell = header.createCell(headerCellIndex);
                headerCell.setCellValue(headerCellName);
                headerCell.setCellStyle(headerStyle);

                headerCellIndex++;
            }

            CellStyle style = writeWorkbook.createCellStyle();
            style.setWrapText(true);

            int rowIndex = 2;

            for (Model model : tableModels) {
                Row row = sheet.createRow(rowIndex);
                createCellString(row, 0, model.getCode(), style);
                createCellString(row, 1, model.getNomenclature(), style);
                createCellString(row, 2, model.getDesiredDate(), style);
                createCellString(row, 3, model.getSupplierDate(), style);
                createCellString(row, 4, model.getFlowDate(), style);
                createCellString(row, 5, model.getSalaryDate(), style);
                createCellString(row, 6, model.getFactSalaryDate(), style);
                createCellString(row, 7, model.getCurrentDate(), style);


                rowIndex++;
            }

            File currDir = new File(".");
            String path = currDir.getAbsolutePath();
            String fileLocation = path.substring(0, path.length() - 1) + fileName;

            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            writeWorkbook.write(outputStream);

        } finally {
            if (writeWorkbook != null) {

                writeWorkbook.close();

            }
        }
    }



    private void createCellString(Row row, int index, String data, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(data);
        cell.setCellStyle(style);
    }

    private void createCellNumeric(Row row, int index, Double data, CellStyle style) {
        Cell cell = row.createCell(index);
        if (data != null) {
            cell.setCellValue(data);
        } else {
            cell.setCellValue(0);
        }
        cell.setCellStyle(style);
    }

    public List<List<String>> getTransferOrders() {
        return transferOrders;
    }

    public List<Model> getTableModels() {
        return tableModels;
    }


}
