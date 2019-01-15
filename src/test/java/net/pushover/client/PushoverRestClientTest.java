package net.pushover.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import static org.junit.Assert.assertEquals;
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
                .setUserId("")
                .setMessage("")
                .setPriority(expectedPriority)
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();
        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"priority\"\r\n" +
                "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n\r\n" + expectedPriority.getPriority()));

    }
    
     @Test
    public void testPushMessageWithEmergencyPriority() throws Exception {

        final MessagePriority expectedPriority = MessagePriority.EMERGENCY;
        final int requestedRetry = 120;
        final int requestedExpire = 7200;
        final String expectedReceipt = "asdfghjkl";

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"receipt\":\""+expectedReceipt+"\"}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("")
                .setUserId("")
                .setMessage("")
                .setPriority(expectedPriority)
                .setRetry(requestedRetry)
                .setExpire(requestedExpire)
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();

        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"priority\"\r\n" +
                 "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                 "Content-Transfer-Encoding: 8bit\r\n\r\n" + expectedPriority.getPriority()));
        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"retry\"\r\n" +
                 "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                 "Content-Transfer-Encoding: 8bit\r\n\r\n" + requestedRetry));
        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"expire\"\r\n" +
                 "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                 "Content-Transfer-Encoding: 8bit\r\n\r\n" + requestedExpire));

    }

    @Test
    public void testPushMessageWithAttachment() throws Exception {

        final MessagePriority expectedPriority = MessagePriority.EMERGENCY;
        final int requestedRetry = 120;
        final int requestedExpire = 7200;
        final String expectedReceipt = "asdfghjkl";

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"receipt\":\""+expectedReceipt+"\"}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("")
                .setUserId("")
                .setMessage("")
                .setPriority(expectedPriority)
                .setRetry(requestedRetry)
                .setExpire(requestedExpire)
                .setAttachment(new File("attachment\\test_attachment.jpg"))
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();

        assertTrue(postBody.contains(
                "Content-Disposition: form-data; name=\"attachment\"; filename=\"test_attachment.jpg\"\r\n" +
                        "Content-Type: image/jpeg\r\n" +
                        "Content-Transfer-Encoding: binary")
        );
    }
    
    @Test
    public void testRequestVerification() throws Exception {

       final String expectedUser = "bnmUaSdfqwER";;
        final String expectedDevice = "testPad";

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("")
                .setMessage("")
                .setUserId(expectedUser)
                .setDevice(expectedDevice)
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();

        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"user\"\r\n" +
                "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n\r\n" + expectedUser));
        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"device\"\r\n" +
                "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n\r\n" + expectedDevice));

    }
    
    @Test
    public void testRequestEmergencyReceipt() throws Exception {

       final String expectedToken = "qwerasdfzxcv";
       final String receipt = "atestdevice";
        
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.requestEmergencyReceipt(expectedToken, receipt);

        ArgumentCaptor<HttpGet> captor = ArgumentCaptor.forClass(HttpGet.class);

        verify(httpClient).execute(captor.capture());

        final String url = captor.getValue().getURI().toASCIIString();
        final String expectedUrl = PushoverRestClient.RECEIPT_CHECK_URL_FRAGMENT + receipt + ".json?token=" + expectedToken;
        assertEquals(expectedUrl, url);
        
    }

    @Test
    public void testCancelEmergencyMessage() throws Exception {

       final String expectedToken = "qwerasdfzxcv";
       final String receipt = "atestdevice";
        
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.cancelEmergencyMessage(expectedToken, receipt);

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final String url = captor.getValue().getURI().toASCIIString();
        final String expectedUrl = PushoverRestClient.RECEIPT_CHECK_URL_FRAGMENT + receipt + "/cancel.json";
        final HttpPost post = captor.getValue();
        final String postBody = EntityUtils.toString(post.getEntity());
        assertEquals(expectedUrl, url);
        assertTrue(postBody.contains("token=" + expectedToken));

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
