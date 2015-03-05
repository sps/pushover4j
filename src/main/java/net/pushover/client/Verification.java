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

import java.util.List;

/**
 *
 * @author Hal-Hockersmith
 */
public class Verification {
      
    private Integer status;
    private List<String> errors;
    private String request;
    private List<String> devices;

    public Verification(Integer status, String request) {
        this.status = status;
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public String getRequest() {
        return request;
    }

    public List<String> getErrors()
    {
         return errors;
    }

    public List<String> getDevices()
    {
         return devices;
    }
    
    @Override
    public String toString() {
        return String.format("status: %s, requestId: %s, errors: %s", status != null ? String.valueOf(status) : "??", request, errors != null ? errors.toString() : "-none-");
    }
}
