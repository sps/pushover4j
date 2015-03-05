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

/**
 * encapsulate service response parsing / building
 */
public class PushoverResponseFactory {

    private static final Gson GSON = new Gson();

    public static final String REQUEST_REMAINING_HEADER = "X-Limit-App-Remaining";

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
