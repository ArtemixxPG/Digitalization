package google.sheets.range;

import lombok.SneakyThrows;

import java.util.List;

public interface Range {

    List<List<String>> getRange(String range);
    void updateRange(String range, List<List<Object>> values);
}
