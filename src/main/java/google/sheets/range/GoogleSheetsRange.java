package google.sheets.range;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import google.sheets.utils.GoogleSheetsServiceUtil;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Objects;

public class GoogleSheetsRange implements Range{


    private String sheetId;

    private GoogleSheetsServiceUtil util;
    private Sheets credential;

    @SneakyThrows
    public GoogleSheetsRange(String sheetId){
        this.sheetId = sheetId;
    }


    @SneakyThrows
    @Override
    public List<List<String>> getRange(String range) {

        this.util = new GoogleSheetsServiceUtil();
        this.credential = util.getSheetsService();
        ValueRange response = credential.spreadsheets().values()
                .get(sheetId, range)
                .execute();

        List<List<String>> values = response.getValues().stream().map(
                list -> list.stream().map(
                        object -> Objects.toString(object, null)).toList()
                ).toList();
        return values;
    }




    @SneakyThrows
    @Override
    public void updateRange(String range, List<List<Object>> values) {

        this.util = new GoogleSheetsServiceUtil();
        this.credential = util.getSheetsService();

        ValueRange body = new ValueRange()
                .setValues(values);

        UpdateValuesResponse result = credential.spreadsheets().values().update(sheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }



}
