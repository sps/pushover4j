package net.pushover.client;

import java.util.ArrayList;
import java.util.List;

/**
 * API response for receipt request. Returned when checking Emergency priority 
 * message acknowledgement or expiry. 
 * 
 * @author Hal Hockersmith
 * 
 * @since 2015-03-04
 */
public class Receipt {
      private final int status;     //indicated if request was successful
      private final int acknowledged;     //indicates if emergency priority was acked
      private final long acknowledged_at; //inidcates in unix time when acknoledged
      private final String acknowledged_by; //indicated the key that acked the message
      private final long last_delivered_at; //indicates when the last push was made
      private final int expired;          //indicates if the expire time has elapsed
      private final long expires_at;       //time at which the emergency expires
      private final int called_back;      //indicates if the callback to the server occured
      private final long called_back_at;  //indicates when the callback took place
      private final String request;
      private final List<String> errors;
      
      public Receipt()
      {
            status = 0;
            acknowledged = 0;
            acknowledged_at = 0;
            acknowledged_by = "";
            last_delivered_at = 0;
            expired = 1;     //going to set expired here just for safety so that the logic can know it didn't happen if it hasn't figured this out yet
            expires_at = 0;
            called_back = 0;
            called_back_at = 0;
            request = "";
            errors = new ArrayList<String>();
                  errors.add("Default constructor. No information available");
      }
      
      public Receipt(int status, String request)
      {
            this.status = status;
            this.request = request;
            errors = new ArrayList<String>();
            acknowledged = 0;
            acknowledged_at = 0;
            acknowledged_by = "";
            last_delivered_at = 0;
            expired = 1;     //going to set expired here just for safety so that the logic can know it didn't happen if it hasn't figured this out yet
            expires_at = 0;
            called_back = 0;
            called_back_at = 0;
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
      
      public int getAcknowledged() {
            return acknowledged;
      }
      
      public long getAcknowledgedAt() {
            return acknowledged_at;
      }
      
      public String getAcknowledgedBy() {
            return acknowledged_by;
      }
      
      public long getLastDeliveredAt() {
            return last_delivered_at;
      }
      
      public int getExpired() {
            return expired;
      }
      
      public long getExpiresAt() {
            return expires_at;
      }
      
      public int getCalledBack() {
            return called_back;
      }
      
      public long getCalledBackAt() {
            return called_back_at;
      }
      
}
