package fuzzy_matcher.preprocessing;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class Preprocessing implements FuzzyPreprocessing{




    private String trim(String cellValue) {
       return cellValue.trim();
    }


    private String lowerCase(String cellValue) {
        return cellValue.toLowerCase();
    }


    private String replaceSpecialCharacters(String cellValue) {
        return cellValue.replaceAll("[^A-ZА-Яa-zа-я0-9]", " ");
    }



    private void checkRowDataSet(List<String> row, int indexCell){
        if(!row.get(indexCell).equals("")) {
            row.set(indexCell, row.get(indexCell));
        }
    }





    @Override
    public String preprocessingRow(String row) {
        row = trim(row);
        row = lowerCase(row);
        row = replaceSpecialCharacters(row);
        return row;
    }

    @Override
    public void preprocessingDataSet(ArrayList<ArrayList<String>> rows, int indexNomenclatureCell, int indexArticleCell) {
        for(ArrayList<String> row : rows){
            checkRowDataSet(row, indexNomenclatureCell);
            checkRowDataSet(row, indexArticleCell);
        }
    }




}
