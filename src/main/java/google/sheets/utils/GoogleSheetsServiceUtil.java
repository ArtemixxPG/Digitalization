package google.sheets.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.oauth2.GoogleCredentials;
import google.sheets.authorize.GoogleAuthorize;
import google.sheets.authorize.GoogleShitsAuthorize;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.Credentials;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleSheetsServiceUtil {
    private static final String APPLICATION_NAME = "Digitalization";

    private GoogleAuthorize googleAuthorize;
    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    public GoogleSheetsServiceUtil() throws GeneralSecurityException, IOException {

    }

    public Sheets getSheetsService() throws IOException, GeneralSecurityException {
        this.googleAuthorize = new GoogleShitsAuthorize();
        Credential credential = googleAuthorize.authorize(HTTP_TRANSPORT);
        return new Sheets.Builder(
               new NetHttpTransport(),
               GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
