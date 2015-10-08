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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 * Implementation of {@link PushoverClient}
 * 
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * 
 * @since Dec 18, 2012
 */
public class PushoverRestClient implements PushoverClient {

    public static final String PUSH_MESSAGE_URL = "https://api.pushover.net/1/messages.json";
    public static final String SOUND_LIST_URL = "https://api.pushover.net/1/sounds.json";
    public static final String VALIDATE_USERGROUP_URL = "https://api.pushover.net/1/users/validate.json";
    public static final String RECEIPT_CHECK_URL_FRAGMENT = "https://api.pushover.net/1/receipts/"; //needs receipt and then action attached to the end.
    
    private static final HttpUriRequest SOUND_LIST_REQUEST = new HttpGet(SOUND_LIST_URL);

    private HttpClient httpClient = HttpClients.custom().useSystemProperties().build();

    private static final AtomicReference<Set<PushOverSound>> SOUND_CACHE = new AtomicReference<Set<PushOverSound>>();

    /**
     * Takes a PushoverMessage and requests to the push message API. Upon response 
     * will parse the returned values into a simplified Status type. 
     * 
     * @param msg A builder constructed {@link PushoverMessage}. Must have at least API token, receiver, and message
     * @return {@link Status} Simplified response handler that contains just the status and request token.
     * @throws PushoverException 
     */
    @Override
    public Status pushMessage(PushoverMessage msg) throws PushoverException {

        try {
            HttpResponse response = postToMessageApi(msg);
            return PushoverResponseFactory.createStatus(response);
        } catch (Exception e) {
            throw new PushoverException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Takes a PushoverMessage and requests to the push message API. Upon response 
     * will parse the returned values into a more verbose Response type. 
     * 
     * @param msg A builder constructed {@link PushoverMessage}. Must have at least API token, receiver, and message
     * @return {@link Response} Advanced response handler that contains most/all known response fields.
     * @throws PushoverException 
     */
    public Response pushMessageResponse(PushoverMessage msg) throws PushoverException {
        try {
            HttpResponse response = postToMessageApi(msg);
            return PushoverResponseFactory.createResponse(response);
        } catch (Exception e) {
            throw new PushoverException(e.getMessage(), e.getCause());
        }
    }
    
    /**
     * Takes a PushoverMessage and requests to the verification API. Upon response
     * will parse the returned values into a Response type. 
     * 
     * @param msg A builder constructed {@link PushoverMessage}. Must have at least API token and receiver.
     * @return {@link Response} Advanced response handler that contains most/all known response fields.
     * @throws PushoverException 
     */
    public Response requestVerification(PushoverMessage msg) throws PushoverException {

        final HttpPost post = new HttpPost(VALIDATE_USERGROUP_URL);

        final List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("token", msg.getApiToken()));
        nvps.add(new BasicNameValuePair("user", msg.getUserId()));
        
        addPairIfNotNull(nvps, "device", msg.getDevice());
        
        post.setEntity(new UrlEncodedFormEntity(nvps, Charset.defaultCharset()));

        try {
            HttpResponse response = httpClient.execute(post);
            return PushoverResponseFactory.createResponse(response);
        } catch (Exception e) {
            throw new PushoverException(e.getMessage(), e.getCause());
        }
    }
    
    /**
     * Takes a key and receipt token and requests to the receipts API. Upon response
     * will parse the returned values into a targeted Receipt type. Uses direct 
     * fields rather than a PushoverMessage.
     * 
     * @param apiToken API key for the application
     * @param receipt receipt key returned after emergency priority message post
     * @return {@link Receipt} Purpose built handler for the many filed Receipt response.
     * @throws PushoverException 
     */
    public Receipt requestEmergencyReceipt(String apiToken, String receipt) throws PushoverException{
          
          final HttpGet get = new HttpGet(RECEIPT_CHECK_URL_FRAGMENT + receipt +".json?token="+apiToken);
          
          try {
            HttpResponse response = httpClient.execute(get);
            return PushoverResponseFactory.createReceipt(response);
        } catch (Exception e) {
            throw new PushoverException(e.getMessage(), e.getCause());
        }                 
    }
    
    /**
     * Takes a key and receipt token and requests to the receipts API to cancel 
     * an active Emergency message. The API is vague about what is returned when
     * a call to the cancel is fielded but a Response type will catch most of the
     * relevant parts if any response is made.
     * 
     * @param apiToken API key for the application
     * @param receipt receipt key returned after emergency priority message post
     * @return {@link Response} Likely just a status and request token but errors could exist so a Response is better suited.
     * @throws PushoverException 
     */
    public Response cancelEmergencyMessage(String apiToken, String receipt) throws PushoverException {
         final HttpPost post = new HttpPost(RECEIPT_CHECK_URL_FRAGMENT + receipt +"/cancel.json");

        final List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("token", apiToken));
        
        post.setEntity(new UrlEncodedFormEntity(nvps, Charset.defaultCharset()));

        try {
            HttpResponse response = httpClient.execute(post);
            return PushoverResponseFactory.createResponse(response);
        } catch (Exception e) {
            throw new PushoverException(e.getMessage(), e.getCause());
        }
    }
    
    private HttpResponse postToMessageApi(PushoverMessage msg) throws PushoverException {

        final HttpPost post = new HttpPost(PUSH_MESSAGE_URL);
        HttpResponse response = null;
        
        final List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("token", msg.getApiToken()));
        nvps.add(new BasicNameValuePair("user", msg.getUserId()));
        nvps.add(new BasicNameValuePair("message", msg.getMessage()));

        addPairIfNotNull(nvps, "title", msg.getTitle());

        addPairIfNotNull(nvps, "url", msg.getUrl());
        addPairIfNotNull(nvps, "url_title", msg.getTitleForURL());

        addPairIfNotNull(nvps, "device", msg.getDevice());
        addPairIfNotNull(nvps, "timestamp", msg.getTimestamp());
        addPairIfNotNull(nvps, "sound", msg.getSound());

        if (!MessagePriority.NORMAL.equals(msg.getPriority())) {
            
              addPairIfNotNull(nvps, "priority", msg.getPriority());
            
              if (MessagePriority.EMERGENCY.equals(msg.getPriority())) {
                  nvps.add(new BasicNameValuePair("retry", String.valueOf(msg.getRetry())));
                  nvps.add(new BasicNameValuePair("expire", String.valueOf(msg.getExpire())));
                  
                  addPairIfNotNull(nvps, "callback", msg.getCallbackUrl());
              }
        }

        post.setEntity(new UrlEncodedFormEntity(nvps, Charset.defaultCharset()));

        try {
            response = httpClient.execute(post);
        } catch (Exception e) {
            throw new PushoverException(e.getMessage(), e.getCause());
        }
        return response;
    }
    
    /**
     * Populates a Set of PushOverSound that contains the latest list of API 
     * supported sounds. Sounds can be used to override a customer default. It
     * is advised to invalidate and recheck periodically to ensure Pushover has
     * not changed or added any sounds. 
     * 
     * @return Set of {@link PushOverSound} that contains all known supported sounds at time of call. 
     * @throws PushoverException 
     */
    @Override
    public Set<PushOverSound> getSounds() throws PushoverException {

        Set<PushOverSound> cachedSounds = SOUND_CACHE.get();
        if (cachedSounds == null) {
            try {
                cachedSounds = PushoverResponseFactory.createSoundSet(httpClient.execute(SOUND_LIST_REQUEST));
            } catch (Exception e) {
                throw new PushoverException(e.getMessage(), e.getCause());
            }
            SOUND_CACHE.set(cachedSounds);
        }
        return cachedSounds;
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
