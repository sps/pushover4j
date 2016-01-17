Pushover4j
--------

[![Build Status](https://travis-ci.org/sps/pushover4j.png?branch=master)](https://travis-ci.org/sps/pushover4j)
[![Coverage Status](https://coveralls.io/repos/sps/pushover4j/badge.png?branch=master)](https://coveralls.io/r/sps/pushover4j?branch=master)

A java client for [pushover](https://pushover.net/)

```
PushoverClient client = new PushoverRestClient();        

client.pushMessage(PushoverMessage.builderWithApiToken("MY_APP_API_TOKEN")
        .setUserId("USER_ID_TOKEN")
        .setMessage("testing!")
        .build());

// push a message with optional fields
Status result = client.pushMessage(PushoverMessage.builderWithApiToken("MY_APP_API_TOKEN")
      .setUserId("USER_ID_TOKEN")
      .setMessage("testing!")
      .setDevice("device")
      .setPriority(MessagePriority.HIGH) // HIGH|NORMAL|QUIET
      .setTitle("title")
      .setUrl("https://github.com/sps/pushover4j")
      .setTitleForURL("pushover4j github repo")
      .setSound("magic")
      .build());
      
System.out.println(String.format("status: %d, request id: %s", result.getStatus(), result.getRequestId()));

// get and print out the list of available sounds:
for (PushOverSound sound : client.getSounds() ) {
    System.out.println(String.format("name: %s, id: %s", sound.getName(), sound.getId()));
}              
```


for the non-maven types, here are the required dependencies

* [pushover4j](http://github.com/sps/pushover4j/downloads)
* [gson 2.2.2](http://code.google.com/p/google-gson/downloads/list)
* [apache commons httpclient 4.3.6](http://hc.apache.org/downloads.cgi)
