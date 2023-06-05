package fuzzy_matcher.match;

public interface FuzzyMatcher {

    void match(int indexNomenclature, int indexArticleNomenclature, int indexDirectoryNomenclature,
               int indexDirectoryArticle, double ratio);

    void match(int indexNomenclature, int indexArticleNomenclature, int indexDirectoryNomenclature,
               int indexDirectoryArticle);

}
