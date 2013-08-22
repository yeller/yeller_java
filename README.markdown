This is the java notifier library for integrating your app with the Yeller
exception notifier.

When an uncaught exception occurs, this library will record the exception in
Yeller, letting you diagnose bugs in your running app.

# Integration

Note that yeller-java is a lowlevel client. If you happen to use clojure's
ring, we have a higher level wrapper that builds on this library for your use:

## Adding to your project

yeller-java is distributed via maven. Assuming your build system is maven
compatible, you can add the following xml snippet to your pom file to import
it:

```pom.xml snippet here```

Alternatively, if you just want a jar file, you can grab it from here:
http:://yeller-java-file.com.

## Integrating

Once you've got the jar installed, you'll need to get running with a Yeller
client. This means you'll need the api key from your Yeller project (which you
can find on your project's setting screen). Once you have the api key,
you instantiate a client with it thusly:

```java
YellerClient yellerClient = new YellerClient('YOUR_API_KEY_HERE');
```

There are some more configuration options, which you can read about at
http://www.yellerapp.com/docs/java-client#configuration

To report an exception, simply pass it into ```java yellerClient.report```.
That's all there is to it, we report the exception to
the server. You'll be fixing your bugs in no time.

### Report your own exception

```java
try {
  // Your code here
} catch (Throwable t) {
  yellerClient.report(t);
  throw t;
}
```

### Reporting more data

Yeller's api lets you include more detail with your exception. Yeller's Java
client exposes two additional arguments to yellerClient.report, one that takes
a `HashMap<String, Object>` of custom data to include (e.g. params, session,
cookies, user information etc), and one that adds some extra information (url,
location, which environment the app is running in).

**Custom Data**

Simply pass a HashMap<String, Object> to report.

```java
try {
  // Your code here
} catch (Throwable t) {
  HashMap<String, Object> custom = new HashMap<String, Object>();
  custom.put("user_email", "foo@example.com");
  yellerClient.report(t, custom);
  throw t;
}
```

**Extra Detail**

Construct and pass a `YellerExtraDetail` (which has a builder like interface):

```java
try {
  // Your code here
} catch (Throwable t) {
  YellerExtraDetail detail = new YellerExtraDetail()
    .withApplicationEnvironment("production")
    .withUrl("http://example.com/error")
    .withLocation("UserServlet");
  yellerClient.report(t, extraDetail);
  throw t;
}
```

All of the fields are optional, so just miss out any that you can't fill in.

** Bolth **

Of course, you can add both custom data and extra detail:

```java
try {
  // Your code here
} catch (Throwable t) {
  YellerExtraDetail detail = new YellerExtraDetail()
    .withApplicationEnvironment("production")
    .withUrl("http://example.com/error")
    .withLocation("UserServlet");
  HashMap<String, Object> custom = new HashMap<String, Object>();
  custom.put("user_email", "foo@example.com");
  yellerClient.report(t, detail, custom);
  throw t;
}
```


## Configuration

YellerClient (at the moment) exposes a few single configuration options.
The one you are most likely to look at is `setErrorHandler`. This lets you plug
in how you handle exceptions out of Yeller's servers. You can implement a new
error handler by implementing the interface `YellerErrorHandler`, maybe logging
to a particular logging framework, or sending to syslog, etc.

The standard error handler doesn't report IO exceptions at all, and prints
authorization errors to stderr (these will only happen if you get your api key
wrong, or your project got deleted).

**Customizing the error handler:**

```java
client.setErrorHandler(myErrorHandler);
```

**Writing your own error handler:**

Here's the source code of the `STDERRErrorHandler`, which prints authorization errors to STDERR.
Writing your own error handler looks much like this, just implement `YellerErrorHandler`, then pass it
into `client.setErrorHandler`. You might want to do this for e.g. integration with a logging library you use.

```java
package com.yellerapp.client;

public class STDERRErrorHandler implements YellerErrorHandler {

    public void reportAuthError(String backend, Throwable e) {
        System.err.println(backend);
        e.printStackTrace(System.err);
    }

    public void reportIOError(String backend, Throwable e) {
        // purposefully do nothing
    }

}
```


The other configuration option, which is only really exposed for testing purposes is
`setUrls`. This lets you set which yeller backends to talk to, but is not
intended for production use at all.

# Robustness

This client does some basic roundtripping/timeouts, so it can handle problems
with individual yeller servers. After trying all the servers twice, it will
stop reporting the current exception, then try each one again for the next one.
