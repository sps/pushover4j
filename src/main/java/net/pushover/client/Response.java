package net.pushover.client;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON API response handler. Supports more of the fields that are returned from pushover
 * 
 * @author Hal Hockersmith
 * 
 * @since 2015-03-04
 */
public class Response {
      private final int status;
      private final String request;
      private final List<String> errors;
      private final String receipt;
      private int remaining;
      private final List<String> devices;       //only returned on verfication calls
      
      public Response()
      {
            status = 0;
            request = "";
            errors = new ArrayList<String>();
                  errors.add("Default constructor. No information available");
            receipt = "";
            remaining = Integer.MIN_VALUE;
            devices = new ArrayList<String>();
      }
      
      public Response(int status, String request)
      {
            this.status = status;
            this.request = request;
            errors = new ArrayList<String>();
            receipt = "";
            remaining = Integer.MIN_VALUE;
            devices = new ArrayList<String>();
      }
      
      public int getStatus() {
            return status;
      }
      
      public String getRequest() {
            return request;
      }
      
      public List<String> getErrors() {
            return errors;
      }
      
      public String getReceipt() {
            return receipt;
      }
      
      public int getRemaining() {
            return remaining;
      }
          
      public List<String> getDevices() {
            return devices;
      }
           
      /**
       * Sets the Remaining calls to the API. The remaining are reported in the
       * headers so this is used to set the value once received.
       * 
       * @param remains Number of calls to the API remaining for the month
       */
      public void setRemaining(int remains)
      {
            this.remaining = remains;
      }
}
