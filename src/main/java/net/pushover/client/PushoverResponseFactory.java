package net.pushover.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * encapsulate service response parsing / building
 */
public class PushoverResponseFactory {

    private static final Gson GSON = new Gson();

    public static final String REQUEST_REMAINING_HEADER = "X-Limit-App-Remaining";

    /**
     * Parses the returned HttpResponse into a simplified Status 
     * 
     * @param response HttpResponse returned from the HttpPost to the message API
     * @return {@link Status} Simplified status and response only.
     * @throws IOException on null response and parse failures
     */
    
    public static Status createStatus(HttpResponse response) throws IOException {

        if (response == null || response.getEntity() == null) {
            throw new IOException("unreadable response!");
        }

        final String body = EntityUtils.toString(response.getEntity());

        final Status toReturn;

        try {
            toReturn = GSON.fromJson(body, Status.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e.getCause());
        }

        return toReturn;
    }
    
    /**
     * Parses the returned HttpResponse into a more complete Response 
     * 
     * @param response HttpResponse returned from the HttpPost to the message API
     * @return {@link Response} Advanced handler of most/all known response fields the api can return.
     * @throws IOException on null response and parse failures
     */
    public static Response createResponse(HttpResponse response) throws IOException {
          if (response == null || response.getEntity() == null) {
            throw new IOException("unreadable response!");
        }

        final String body = EntityUtils.toString(response.getEntity());

        final Response toReturn;

        try {
            toReturn = GSON.fromJson(body, Response.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e.getCause());
        }
        
        final Header responseId = response.getFirstHeader(REQUEST_REMAINING_HEADER);

        if (responseId != null) {
              try
              {
                    toReturn.setRemaining(Integer.parseInt(responseId.getValue()));
              }
              catch (Exception ex)
              {
                    toReturn.setRemaining(Integer.MIN_VALUE);  //on failure to parse just set to minimum value. easy to check for. You should never be that far in the hole.
              }
        }

        return toReturn;
    }
        
    /**
     * Parses the returned HttpResponse from a Receipt request into a receipt object
     * 
     * @param response HttpResponse returned from the HttpPost to the receipt API
     * @return {@link Receipt} Handler that contains all known fields from the receipt check API.
     * @throws IOException on null response and parse failures
     */
    static Receipt createReceipt(HttpResponse response) throws IOException {
        if (response == null || response.getEntity() == null) {
            throw new IOException("unreadable response!");
        }

        final String body = EntityUtils.toString(response.getEntity());

        final Receipt toReturn;

        try {
            toReturn = GSON.fromJson(body, Receipt.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e.getCause());
        }

        return toReturn;
    }
    
    /**
     * Parses the returned HttpResponse from a sound list request into a SoundSet object
     * 
     * @param response HttpResponse returned from the HttpPost to the receipt API
     * @return Set<{@link PushOverSound}> Set of PushOverSounds for supported sounds
     * @throws IOException on null response and parse failures
     */
    public static Set<PushOverSound> createSoundSet(HttpResponse response) throws IOException {

        if (response == null || response.getEntity() == null) {
            throw new IOException("unreadable response!");
        }

        final String body = EntityUtils.toString(response.getEntity());

        SoundResponse r;
        try {
            r = GSON.fromJson(body, SoundResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e.getCause());
        }

        final Set<PushOverSound> sounds = new HashSet<PushOverSound>();
        if (r.sounds != null) {
            for (Map.Entry<String, String> e : r.sounds.entrySet()) {
                sounds.add(new PushOverSound(e.getKey(), e.getValue()));
            }
        }
        return sounds;
    }

    // {"sounds":{"id":"name",...},"status":1}
    private static class SoundResponse {
        Map<String, String> sounds;
    }
}
