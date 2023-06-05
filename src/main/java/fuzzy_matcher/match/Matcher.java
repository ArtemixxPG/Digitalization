package fuzzy_matcher.match;

import fuzzy_matcher.preprocessing.Preprocessing;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Matcher implements FuzzyMatcher {


    private Preprocessing preprocessing;
    private ArrayList<ArrayList<String>> directoryDataSet;
    private ArrayList<ArrayList<String>> workDataSet;


    public Matcher() {
        this.preprocessing = new Preprocessing();
    }

    @Override
    public void match(int indexNomenclature, int indexArticle, int indexDirectoryNomenclature,
                      int indexDirectoryArticle, double ratio) {
        int points = 0;
        int limit = 0;
        int limitDirectory = 0;
        double prevRatio = 0;
        for (ArrayList<String> rowWork : workDataSet) {

            if(rowWork.size() == 2){
                continue;
            }

            String[] currentTargets = createArrayForPoints(rowWork, indexNomenclature, indexArticle);

            if (currentTargets.length == 0) {
                continue;
            }


            for (ArrayList<String> rowDirectory : directoryDataSet) {

                if(rowDirectory.size() == 2){
                    continue;
                }

                String[] currentDirectoryTargets = createArrayForPoints(rowDirectory, indexDirectoryNomenclature,
                        indexDirectoryArticle);

                if (currentDirectoryTargets.length == 0 ) {
                    continue;
                }

                for (int i = 0; i < currentTargets.length; i++) {
                        for (int j = 0; j < currentDirectoryTargets.length; j++) {
                                if (currentTargets[i].equals(currentDirectoryTargets[j])) {
                                    System.out.println(currentTargets[i] + " " + currentDirectoryTargets[j]);
                                    points++;
                                    System.out.println(points);
                                    break;
                                }
                            }
                }

                double currentRatio = (double) points / currentTargets.length;
                if (currentRatio == ratio) {
                        rowWork.set(2, rowDirectory.get(2));
                        rowWork.set(3, rowDirectory.get(3));
                       break;
                } else {
                    if(limitDirectory == directoryDataSet.size() - 1){
                        rowWork.set(2, "");
                        rowWork.set(3, "");
                    }
                }

                limitDirectory++;
                points = 0;


            }
            prevRatio = 0;
            limit ++;
        }
    }

    @Override
    public void match(int indexNomenclature, int indexArticleNomenclature, int indexDirectoryNomenclature, int indexDirectoryArticle) {
        for(ArrayList<String> rowWork : workDataSet){
            String nomenclature = rowWork.get(indexNomenclature);
            String article = rowWork.get(indexArticleNomenclature);
            for(ArrayList<String> directoryRow : directoryDataSet){
                String directoryNomenclature = directoryRow.get(indexDirectoryNomenclature);
                String directoryArticle = directoryRow.get(indexDirectoryArticle);

                if(nomenclature.equals(directoryNomenclature) && article.equals(directoryArticle)){
                    if(directoryRow.size() == 4) {
                        rowWork.set(2, directoryRow.get(2));
                        rowWork.set(3, directoryRow.get(3));
                    } else if(directoryRow.size() > 4) {
                        rowWork.set(2, directoryRow.get(4));
                        rowWork.set(3, directoryRow.get(5));
                    }
                    break;
                }

            }
        }
    }



    public void setDirectoryDataSet(ArrayList<ArrayList<String>> directoryDataSet, int indexNomenclature,
                                    int indexArticle) {
        this.directoryDataSet = directoryDataSet;
        preprocessing.preprocessingDataSet(this.directoryDataSet, indexNomenclature, indexArticle);
    }

    public void setWorkDataSet(ArrayList<ArrayList<String>> workDataSet, int indexNomenclature,
                               int indexArticle) {
        this.workDataSet = workDataSet;
        preprocessing.preprocessingDataSet(this.workDataSet, indexNomenclature, indexArticle);
    }


    private String[] concatArrayWithCollection(String[] firstArray, String[] secondArray) {
        List<String> result = new ArrayList<>(firstArray.length + secondArray.length);
        Collections.addAll(result, firstArray);
        Collections.addAll(result, secondArray);

        String[] resultArray = getStringTypeArray(firstArray);

        return result.toArray(resultArray);
    }

    private String[] isArray(String[] firstTargets, String[] secondTargets) {

        String[] currentTargets = null;



        if (firstTargets.length > 0 && secondTargets.length > 0) {
            currentTargets = concatArrayWithCollection(firstTargets, secondTargets);
        } else if (secondTargets.length == 0) {
            currentTargets = firstTargets;
        } else if (firstTargets.length == 0) {
            currentTargets = secondTargets;
        }
        return currentTargets;
    }

    private String[] createArrayForPoints(ArrayList<String> row, int indexNomenclature, int indexArticle) {
        String nomenclature = row.get(indexNomenclature);
        String article = row.get(indexArticle);

        String[] nomenclatureTargets = nomenclature.split(" ");
        String[] articleTargets = article.split(" ");

        String[] currentTargets = isArray(nomenclatureTargets, articleTargets);

        ArrayList<String> preCurrentTargets = new ArrayList<>(List.of(currentTargets));

       removeVoidElement(preCurrentTargets);

        return preCurrentTargets.toArray(getStringTypeArray(currentTargets));
    }

    private void removeVoidElement(ArrayList<String> targets){
        Iterator<String> elIterator = targets.iterator();
        while (elIterator.hasNext()){
            String el = elIterator.next();
            if(el.equals("")){
                elIterator.remove();
            }
        }
    }

    private String[] getStringTypeArray(String [] array){
        String[] resultArray = (String[]) Array.newInstance(array.getClass().getComponentType(), 0);

        return resultArray;
    }

}


