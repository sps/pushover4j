package net.pushover.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * 
 * @since Dec 19, 2012
 */
public class PushoverRestClientTest {

    private HttpClient httpClient;
    private PushoverRestClient client;
    private HttpResponse mockHttpResponse;

    @Before
    public void setUp() throws Exception {
        httpClient = mock(HttpClient.class);
        mockHttpResponse = mock(HttpResponse.class);

        client = new PushoverRestClient();
        client.setHttpClient(httpClient);
    }

    @Test(expected = PushoverException.class)
    public void testPushMessageWithPostFailure() throws Exception {
        when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(new IOException("nope!"));
        client.pushMessage(PushoverMessage.builderWithApiToken("").build());
    }

    @Test
    public void testPushMessageWithNonDefaultPriority() throws Exception {

        final MessagePriority expectedPriority = MessagePriority.HIGH;

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("")
                .setPriority(expectedPriority)
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final String postBody = EntityUtils.toString(post.getEntity());
        assertTrue(postBody.contains("priority=" + expectedPriority.getPriority()));

    }

    @Test(expected = PushoverException.class)
    public void testGetSoundsWithFailure() throws Exception {
        when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(new IOException("nope!"));
        client.getSounds();
    }

    @Test
    public void testGetSounds() throws Exception {

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        Set<PushOverSound> sounds = client.getSounds();
        assertNotNull(sounds);
        verify(httpClient).execute(any(HttpUriRequest.class));

        sounds = client.getSounds();
        assertNotNull(sounds);
        verifyNoMoreInteractions(httpClient);
    }
}
