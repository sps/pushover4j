/*
 * Copyright 2015 Hal-Hockersmith.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.pushover.client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hal-Hockersmith
 */
public class Response {
      private final int status;
      private final String request;
      private final List<String> errors;
      private final String receipt;
      private int remaining;
      private final List<String> devices;
      
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
           
      public void setRemaining(int remains)
      {
            this.remaining = remains;
      }
}
