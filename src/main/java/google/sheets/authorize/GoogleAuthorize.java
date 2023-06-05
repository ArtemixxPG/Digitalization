package google.sheets.authorize;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

public interface GoogleAuthorize {

    Credential authorize(final NetHttpTransport HTTP_TRANSPORT);

    JsonFactory getJsonFactory();

}
