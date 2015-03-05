package net.pushover.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;

public class PushoverResponseFactoryTest {

    private HttpResponse response;

    @Before
    public void setUp() throws Exception {
        assertNotNull(new PushoverResponseFactory());
        response = mock(HttpResponse.class);
    }

    @Test(expected = IOException.class)
    public void testNullStausResponse() throws Exception {
        assertNull(PushoverResponseFactory.createStatus(null));
    }

    @Test(expected = IOException.class)
    public void testNullEntityStatusResponse() throws Exception {
        assertNull(PushoverResponseFactory.createStatus(response));
    }

    @Test(expected = IOException.class)
    public void testMalformedStatusResponse() throws IOException {
        when(response.getEntity()).thenReturn(new StringEntity("{"));
        PushoverResponseFactory.createStatus(response);        
    }
    
    @Test
    public void testEmptyStatus() throws IOException {
        when(response.getEntity()).thenReturn(new StringEntity("{}"));
        Status status = PushoverResponseFactory.createStatus(response);
        assertNotNull(status);
    }

    @Test
    public void testOKStatus() throws IOException {
        final String expectedRequestId = "1234";

        when(response.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"request\":\"" + expectedRequestId +"\"}"));

        final Status status = PushoverResponseFactory.createStatus(response);
        
        assertNotNull(status);
        assertEquals(status.getStatus(), 1);
        assertEquals(status.getRequestId(), expectedRequestId);
    }
    
    @Test
    public void testOKResponse() throws IOException {
        final String expectedRequestId = "1234";
        final int expectedRemaining = 4321;

        when(response.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"request\":\"" + expectedRequestId +"\"}"));

        when(response.getFirstHeader(PushoverResponseFactory.REQUEST_REMAINING_HEADER)).thenReturn(new BasicHeader(PushoverResponseFactory.REQUEST_REMAINING_HEADER,
                String.valueOf(expectedRemaining)));

        final Response status = PushoverResponseFactory.createResponse(response);
        assertNotNull(status);
        assertEquals(status.getStatus(), 1);
        assertEquals(status.getRequest(), expectedRequestId);
        assertEquals(status.getRemaining(), expectedRemaining);
    }
    
    @Test
    public void testOKEmergencyResponse() throws IOException {
        final String expectedRequestId = "1234";
        final String expectedReceipt = "qwertyuiop";

        when(response.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"request\":\"" + expectedRequestId +"\",\"receipt\":\"" + expectedReceipt +"\"}"));

        final Response resp = PushoverResponseFactory.createResponse(response);
        assertNotNull(resp);
        assertEquals(resp.getStatus(), 1);
        assertEquals(resp.getRequest(), expectedRequestId);
        assertEquals(resp.getReceipt(), expectedReceipt);
    }

    @Test
    public void testCreateSoundResponse() throws IOException {

        when(response.getEntity()).thenReturn(new StringEntity("{\"sounds\":{\"id\":\"name\"},\"status\":1}"));

        final Set<PushOverSound> sounds = PushoverResponseFactory.createSoundSet(response);
        assertNotNull(sounds);
        assertFalse(sounds.isEmpty());
        PushOverSound sound = sounds.iterator().next(); 
        assertEquals(sound.getId(), "id");
        assertEquals(sound.getName(), "name");
    }

    @Test(expected = IOException.class)
    public void testMalformedCreateSoundResponse() throws IOException {
        when(response.getEntity()).thenReturn(new StringEntity("{"));
        PushoverResponseFactory.createSoundSet(response);        
    }

    @Test
    public void testEmptyCreateSoundResponse() throws IOException {
        when(response.getEntity()).thenReturn(new StringEntity("{}"));
        final Set<PushOverSound> sounds = PushoverResponseFactory.createSoundSet(response);
        assertNotNull(sounds);
        assertTrue(sounds.isEmpty());
    }
    
    @Test(expected = IOException.class)
    public void testNullCreateSoundResponse() throws Exception {
        assertNull(PushoverResponseFactory.createSoundSet(null));
    }

    @Test(expected = IOException.class)
    public void testNullEntityCreateSoundResponse() throws Exception {
        assertNull(PushoverResponseFactory.createSoundSet(response));
    }
}
