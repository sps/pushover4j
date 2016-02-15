Pushover4j
--------

[![Build Status](https://travis-ci.org/sps/pushover4j.png?branch=master)](https://travis-ci.org/sps/pushover4j)
[![Coverage Status](https://coveralls.io/repos/sps/pushover4j/badge.png?branch=master)](https://coveralls.io/r/sps/pushover4j?branch=master)

A simple java client for [pushover](https://pushover.net/) notification service. Example:
```
PushoverClient client = new PushoverRestClient();        

client.pushMessage(PushoverMessage.builderWithApiToken("MY_APP_API_TOKEN")
        .setUserId("USER_ID_TOKEN")
        .setMessage("testing!")
        .build());
```
Just that fast. Now the message can be customized with much more control as seen below. We also capture the result from the API call and can reference that for success or error message logging:
```
// push a message with optional fields
//(note we are reusing the client from the first example here)
Status result = client.pushMessage(PushoverMessage.builderWithApiToken("MY_APP_API_TOKEN")
      .setUserId("USER_ID_TOKEN")
      .setMessage("Welcome to pushover4j!")
      .setDevice("<your device name>")
      .setPriority(MessagePriority.HIGH) // LOWEST,QUIET,NORMAL,HIGH,EMERGENCY
      .setTitle("Testing")
      .setUrl("https://github.com/sps/pushover4j")
      .setTitleForURL("pushover4j github repo")
      .setSound("magic")
      .build());
System.out.println(String.format("status: %d, request id: %s", result.getStatus(), result.getRequestId()));
```
And you can keep up to date with the latest in available sounds with a quick call as well.
```
// get and print out the list of available sounds:
for (PushOverSound sound : client.getSounds() ) {
    System.out.println(String.format("name: %s, id: %s", sound.getName(), sound.getId()));
}              
```
Check the Wiki for more examples and features. If something is missing please raise a request. 
### Installing 
Installation is best done through the Maven build system. We should keep the maven system up to date with releases but you are free to manually install things. Java 6 or higher required. 
##### For maven
Just search the maven repo for ` pushover-client ` or add the following to your POM
```
<dependency>
  <groupId>com.github.sps.pushover.net</groupId>
  <artifactId>pushover-client</artifactId>
  <version>1.5.1</version>
</dependency>
```

##### Other build systems
for the non-maven types, here are the required dependencies
* [pushover4j](http://github.com/sps/pushover4j/downloads)
* [gson 2.3.1](https://github.com/google/gson)
* [apache commons httpclient 4.4.1](http://hc.apache.org/downloads.cgi)
 
##### Note: Java 6 users
For java 6 users you will need to enhance your security provider. Pushover uses a Diffie-Hillman 1024 for handshake on its new key which Java 6 does not support natively. If your country allows it you may install the Boucny Castle provider to continue using the service. Otherwise newer versions of Java 7 and Java 8 will work natively.

### Whats new for v1.5?
Version 1.5 addes a number of new features that help cover more of the available pushover API calls. Responses, user validation, emergency priorities. Check out the wiki for additional information. v1.0 is still valid for basic uses but version 1.5 expands on the functionality and is recommended.
