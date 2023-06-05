package parsers;


import constants.Article;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.apache.poi.ss.usermodel.CellType.STRING;

public class ParametersParser {

    private static final String HARTING_CREATOR_NAME = "Harting";

    private static volatile ParametersParser instance;

    private List<List<String>> nomenclatureReference;

    private DateParser dateParser;

    private ParametersParser() {
        dateParser = DateParser.getInstance();
    }

    public static ParametersParser getInstance() {
        if (instance == null) {
            synchronized (DateParser.class) {
                if (instance == null) {
                    instance = new ParametersParser();
                }
            }
        }
        return instance;
    }


    public void filterRPIByArticle(Sheet exportSheet, Sheet referenceSheet ,List<List<String>> checkPriceList, int indexRpi, int indexExportRpi,
                                  int indexExportArticle, int indexFindArticle, int indexCheckRpi, int indexExportPrice, int indexFindPrice){
        for(Row referenceRow : referenceSheet) {
            if (referenceRow.getRowNum() > 1) {
                if(referenceRow.getCell(indexFindArticle).getCellType() == CellType.STRING &&
                        referenceRow.getCell(indexFindArticle).getStringCellValue().equals("")){
                    continue;
                }
                if (referenceRow.getCell(indexFindArticle).getCellType() != CellType.BLANK) {
                    String article = "";
                    if (referenceRow.getCell(indexFindArticle).getCellType() == CellType.STRING) {
                        article = referenceRow.getCell(indexFindArticle).getStringCellValue();
                    } else if (referenceRow.getCell(indexFindArticle).getCellType() == CellType.NUMERIC) {
                        article += referenceRow.getCell(indexFindArticle).getNumericCellValue();
                    }
                    if (referenceRow.getCell(indexFindArticle + 1).getCellType() != CellType.BLANK &&
                            referenceRow.getCell(indexFindArticle + 1).getCellType() == CellType.STRING) {
                        if (referenceRow.getCell(indexFindArticle + 1).getStringCellValue().equals(HARTING_CREATOR_NAME)) {
                            if(!article.equals("")) {
                                article = article.substring(0, 2) + " " + article.substring(2, 4) + " " +
                                        article.substring(4, 7) + " " + article.substring(7, 11);
                            }
                        }
                    }

                    article = article.trim();

                    int rowNum = referenceRow.getRowNum();

                    if (!article.equals("")) {

                        for (Row exportRow : exportSheet) {

                            if (exportRow.getRowNum() > 7) {

                                if(exportRow.getCell(indexExportArticle).getCellType() == CellType.STRING &&
                                        exportRow.getCell(indexExportArticle).getStringCellValue().equals("")){
                                    continue;
                                }

                                if (exportRow.getCell(indexExportArticle).getCellType() != CellType.BLANK) {
                                    if (article.contains(getTypeArticle(Article.ITSI)) || article.contains(getTypeArticle(Article.UTSA))) {

                                        String modifyArticle = article.substring(0, 4) + " " + article.substring(5, article.length() - 1);
                                        if (exportRow.getCell(indexExportArticle).getStringCellValue().contains(article) ||
                                                exportRow.getCell(indexExportArticle).getStringCellValue().contains(modifyArticle)) {
                                            exportRow.getCell(indexExportRpi).setCellValue(referenceRow.getCell(indexRpi).getStringCellValue());
                                        }
                                    } else {

                                        if (exportRow.getCell(indexExportArticle).getCellType() == CellType.STRING &&
                                                exportRow.getCell(indexExportArticle).getStringCellValue().contains(article)) {
                                            exportRow.getCell(indexExportRpi).setCellValue(referenceRow.getCell(indexRpi).getStringCellValue());
                                        } else if (exportRow.getCell(indexExportArticle).getCellType() == CellType.NUMERIC) {
                                            String exportArticle = "" + exportRow.getCell(indexExportArticle).getNumericCellValue();
                                            if (exportArticle.contains(article)) {
                                                exportRow.getCell(indexExportRpi).setCellValue(referenceRow.getCell(indexRpi).getStringCellValue());
                                            }
                                        }
                                    }
                                }

                                if (!checkPriceList.isEmpty() && !exportRow.getCell(indexExportRpi).getStringCellValue().equals("")) {
                                    String finalRpi = exportRow.getCell(indexExportRpi).getStringCellValue();
                                    List<List<String>> prices = checkPriceList.stream().
                                            filter(item -> item.get(indexCheckRpi).contains(finalRpi)).toList();

                                    if (prices.size() > 1) {
                                        exportRow.getCell(indexExportPrice).setCellValue(dateParser.getLastPrice((ArrayList<List<String>>) prices));
                                    } else if (prices.size() == 1) {
                                        exportRow.getCell(indexExportPrice).setCellValue(prices.get(0).get(indexFindPrice));
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    public void filterByNomenclatureForRPI(Sheet exportSheet, ArrayList<ArrayList<String>> checkPriceList, int indexExportRpi, int indexExportNomenclature,
                                           int indexCheckRpi, int indexAnalogRpi) {

        for (Row row : exportSheet) {

            if (row.getRowNum() > 2) {
                if (row.getCell(indexExportNomenclature).getCellType() == STRING  &&row.getCell(indexExportNomenclature).getStringCellValue().equals("")) {
                    continue;
                }
//                String article = "";
//                String exportNomenclature = row.getCell(indexExportNomenclature).getStringCellValue();
//                List<List<String>> rpis = nomenclatureReference.stream().filter(item -> item.get(indexFindNomenclature).contains(exportNomenclature)).toList();
//                String rpi = "";
//
//                String findNomenckature = "";
//                if (!rpis.isEmpty()) {
//                    rpi = rpis.get(0).get(indexRpi);
//                    findNomenckature = rpis.get(0).get(1);
//                }
//
//                if (!rpi.equals("")) {
//                    if(row.getCell(indexExportRpi).getStringCellValue().equals("")) {
//                        row.getCell(indexExportRpi).setCellValue(rpi);
//                        row.getCell(indexExportRpi+1).setCellValue(findNomenckature);
//                    }
//                    continue;
//                } else {
//                    if (row.getCell(indexExportArticle).getCellType() != CellType.BLANK) {
//
//                        if(row.getCell(indexExportArticle).getCellType() == CellType.STRING) {
//                         article = row.getCell(indexExportArticle).getStringCellValue();
//                        }
//                        if(row.getCell(indexExportArticle).getCellType() == CellType.NUMERIC){
//                            article += row.getCell(indexExportArticle).getNumericCellValue();
//                        }
//                        if (article.contains(getTypeArticle(Article.ITSI)) || article.contains(getTypeArticle(Article.UTSA))) {
//                            article = article.trim();
//                            if (article.split(" ").length > 1) {
//                                article = String.join(".", article.split(" "));
//                            }
//                        }
//
//                        String finalArticle = article;
//                        List<List<String>> articleRpis = nomenclatureReference.stream().filter(item -> item.size() >= 3
//                                && item.get(2).contains(finalArticle)).toList();
//
//                        if(!articleRpis.isEmpty()) {
//                            rpi = articleRpis.get(0).get(indexRpi);
//                            findNomenckature = articleRpis.get(0).get(1);
//                        }
//
//                        if (!Objects.equals(rpi, "")) {
//                            if(row.getCell(indexExportRpi).getStringCellValue().equals("")) {
//                                if(row.getCell(indexExportRpi).getStringCellValue().equals("")) {
//                                    row.getCell(indexExportRpi).setCellValue(rpi);
//                                    row.getCell(indexExportRpi+1).setCellValue(findNomenckature);
//                                }
//                            }
//                            continue;
//                        }
//                        String shortArticle = "";
//
//                        if(article.split(" ").length > 1) {
//
//                                if (article.contains(getArticle(Article.LONG_ART)) ||
//                                        article.contains(getArticle(Article.SHORT_ART)) || article.contains(getArticle(Article.CODE))) {
//                                    for (int i = 0; i < article.split(" ").length; i++) {
//                                        if (article.split(" ")[i].contains("№")){
//                                            continue;
//                                        }
//
//                                    shortArticle += article.split(" ")[i];
//                                }
//                            }
//                        } else {
//                            if(
//                                    article.contains(getArticle(Article.SHORT_ART))) {
//                                int indexDot = article.indexOf('.');
//                                shortArticle = article.substring(indexDot, article.length() - 1);
//                            }
//                        }
//
//                        if (!shortArticle.equals("")) {
//                            String finalShortArticle = shortArticle;
//                            List<List<String>> findArticleRpis = nomenclatureReference.stream().filter(item ->
//                                    item.size() > 2 && item.get(indexFindArticle).contains(finalShortArticle)).toList();
//                            if(!findArticleRpis.isEmpty()) {
//                                rpi = findArticleRpis.get(0).get(indexRpi);
//                                if(findArticleRpis.size() > 2) {
//                                    findNomenckature = findArticleRpis.get(1).get(indexRpi);
//                                }
//                            }
//
//                            if (rpi != null) {
//                                if(row.getCell(indexExportRpi).getStringCellValue().equals("")) {
//                                    row.getCell(indexExportRpi).setCellValue(rpi);
//                                    row.getCell(indexExportRpi+1).setCellValue(findNomenckature);
//                                }
//                            }
//                        }
//                    }
//                }


                String finalRpi = row.getCell(indexExportRpi).getStringCellValue();
                if(row.getCell(indexAnalogRpi)!= null) {
                    finalRpi = row.getCell(indexAnalogRpi).getStringCellValue();
                }
                if (!finalRpi.equals("")) {
                    String finalRpi1 = finalRpi;
                    ArrayList<List<String>> prices = new ArrayList<>(checkPriceList.stream().
                            filter(item -> item.get(indexCheckRpi).contains(finalRpi1)).toList());

                    if (prices.size() > 1) {
                        createCellNumeric(row, 8, dateParser.getLastPrice(prices), row.getCell(1).getCellStyle());
                    } else if (prices.size() == 1) {
                        createCellNumeric(row, 8, Double.valueOf(prices.get(0).get(10)), row.getCell(1).getCellStyle());

                    } else {
                        createCellNumeric(row, 8, 0.0, row.getCell(1).getCellStyle());
                    }
                }  else {
                    createCellNumeric(row, 8, 0.0, row.getCell(1).getCellStyle());
                }
            }
        }
    }


    public List<List<Object>> getPricesByRpi(List<List<String>> listRpi, ArrayList<ArrayList<String>> checkPriceList,
                                             int indexERP){
        List prices = new ArrayList();
        for(List<String> rpi : listRpi) {
            if (rpi.size() > 0) {
            String erp = rpi.get(0);

                ArrayList<List<String>> currentPrices = new ArrayList<>(checkPriceList.stream().
                        filter(item -> item.get(indexERP).contains(erp)).toList());

                if (currentPrices.size() > 1) {
                    List<Double> price = new ArrayList<>();
                    price.add(Double.valueOf(dateParser.getLastPrice(currentPrices)));
                    prices.add(price);
                } else if (currentPrices.size() == 1) {
                    List<Double> price = new ArrayList<>();
                    price.add(Double.valueOf(currentPrices.get(0).get(10)));
                    prices.add(price);
                } else {
                    List<Double> price = new ArrayList<>();
                    price.add(0.0);
                    prices.add(price);
                }
            } else {
                List<Double> price = new ArrayList<>();
                price.add(0.0);
                prices.add(price);
            }
        }
        return prices;
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

    private String getArticle(Article article) {
        if (article == Article.LONG_ART) {
            return "Артикул:";
        }  if (article == Article.SHORT_ART) {
            return "Арт.";
        }
     else if (article == Article.CODE) {
        return "Код:";
    }
        return "";
    }

    private String getTypeArticle(Article article) {
        if (article == Article.ITSI) {
            return "ИТСИ";
        } else if (article == Article.UTSA) {
            return "ЮТСА";
        }
        return "";
    }

    public void setNomenclatureReference(List<List<String>> nomenclatureReference) {
        this.nomenclatureReference = nomenclatureReference;
    }
}

