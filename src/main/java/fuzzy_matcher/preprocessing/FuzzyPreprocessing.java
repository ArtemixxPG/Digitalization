package fuzzy_matcher.preprocessing;

import java.util.ArrayList;
import java.util.Set;


public interface FuzzyPreprocessing {



    String preprocessingRow(String row);

    void preprocessingDataSet(ArrayList<ArrayList<String>> rows, int indexNomenclatureCell, int indexArticleCell);

}
