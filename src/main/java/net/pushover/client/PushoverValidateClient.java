package net.pushover.client;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * Implementation of {@link PushoverClient}
 * 
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * 
 * @since Dec 18, 2012
 */
public class PushoverValidateClient {

    public static final String VALIDATE_USERGROUP_URL = "https://api.pushover.net/1/users/validate.json";

    private HttpClient httpClient = new DefaultHttpClient();

    public Verification requestVerification(PushoverMessage msg) throws PushoverException {

        final HttpPost post = new HttpPost(VALIDATE_USERGROUP_URL);

        final List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("token", msg.getApiToken()));
        nvps.add(new BasicNameValuePair("user", msg.getUserId()));
        
        addPairIfNotNull(nvps, "device", msg.getDevice());
        
        try {
            HttpResponse response = httpClient.execute(post);
            return PushoverResponseFactory.createVerification(response);
        } catch (Exception e) {
            throw new PushoverException(e.getMessage(), e.getCause());
        }
    }

    private void addPairIfNotNull(List<NameValuePair> nvps, String key, Object value) {
        if (value != null) {
            nvps.add(new BasicNameValuePair(key, value.toString()));
        }
    }

    /**
     * Optionally provide an alternative {@link HttpClient}
     * 
     * @param httpClient
     */
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

}
