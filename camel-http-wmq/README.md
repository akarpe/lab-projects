MQ Example
==============

This example shows how Camel can communicate with Websphere MQ, using the standard
Camel JMS component in a route.

The example listens for HTTP requests using the Camel Jetty componenent.
When a request arrives over HTTP, it triggers the route. The next component in 
the route (JMS producer) puts the message onto an MQ Queue called outgoingPayments.
Under the covers, the route implicitely waits for a response on a designated response
queue called incomingPayments. The replyTo queue is specified as an option to the 
JMS Producer endpoint URL. Once the response is received, the route will return the 
response back to the external client.

Key Points
------------

-camel route for request/reply using JMS (WebSphere MQ)
-need for IBM runtime libraries 
-jms config based on spring (see camel-context.xml)


Build
-----

First make sure you have built the example and installed into local maven repo:

mvn install -DskipTests=true


You can see the routing rules by looking at the java code in the
src/main/java directory. The Spring XML configuration lives in
src/main/resources/META-INF/spring/camel-context.xml



IBM libraries (OSGI run-time) dependencies
----------------------------------------------------------------------------
There are references to IBM classes inside the Spring configuration (see camel-context.xml)
Because these references are in Spring configuration only, and not in Java code, then
it is possible to build the project without introducing any build-time dependencies on
IBM MQ libraries.

For runtime, a set of MQ libraries needs to be installed into FUSE ESB (Servicemix) making them available 
at run time. This set of dependent runtime JARs is provided as part of a MQ installation. I used
Websphere MQ version 7. These JARS are packaged as OSGI bundles by IBM, and suitable for installing into an OSGI
container such as Servicemix. 

To install the OSGI runtime files, from a Servicemix console you should type (making sure the path
do the directory holding the libs is correct)

osgi:install -s file:C:/PROGRA~1/IBM/WEBSPH~1/java/lib/OSGi/com.ibm.msg.client.osgi.jms_7.0.1.3.jar
osgi:install -s file:C:/PROGRA~1/IBM/WEBSPH~1/java/lib/OSGi/com.ibm.msg.client.osgi.jms.prereq_7.0.1.3.jar
osgi:install -s file:C:/PROGRA~1/IBM/WEBSPH~1/java/lib/OSGi/com.ibm.msg.client.osgi.nls_7.0.1.3.jar
osgi:install -s file:C:/PROGRA~1/IBM/WEBSPH~1/java/lib/OSGi/com.ibm.msg.client.osgi.commonservices.j2se_7.0.1.3.jar
osgi:install -s file:C:/PROGRA~1/IBM/WEBSPH~1/java/lib/OSGI/com.ibm.msg.client.osgi.wmq.prereq_7.0.1.3.jar 
osgi:install -s file:C:/PROGRA~1/IBM/WEBSPH~1/java/lib/OSGI/com.ibm.msg.client.osgi.wmq_7.0.1.3.jar
osgi:install -s file:C:/PROGRA~1/IBM/WEBSPH~1/java/lib/OSGi/com.ibm.mq.osgi.directip_7.0.1.3.jar
osgi:install file:C:/PROGRA~1/IBM/WEBSPH~1/java/lib/OSGi/com.ibm.msg.client.osgi.wmq.nls_7.0.1.3.jar

All but the last bundle are deployed with the ‘-s’ option (meaning to auto-start the bundle). 
The last bundle above is a bundle “fragment” so no start is required.

It should be noted that I took the liberty to define groupId and artifactId for the Maven 
artifacts associated with these IBM libraries. These names were based on common Maven 
conventions. It is unknown whether IBM maintains an appropriate public Maven repository that 
contains such “official” artifacts but none could be found when implementing the POC. If it 
is determined that such a public repo exists, then this step might be avoided. 

As a convenience, an optional step is to install these jars into your local maven repo (or to a corporate
Nexus repository.) If you do this, you would be able to use the Servicemix "mvn:" prefix with osgi:install (rather than "file:") 
telling Servicemix to use the local Maven settings to find the dependent artifact and install it into the container.
First it tries to find "mvn:" prefixed artifacts in the local Maven repository,
it then tries any external configured repository. 

Other Dependencies
------------------
There are a few features you need to make sure are installed into the Servicemix container prior
to deploying the sample.

features:install  camel-jetty
features:install  camel-jms
features:install  camel-spring

Deploying the sample into the Karaf Container
---------------------------------------------
Once all the dependent runtime MQ bundles are installed into Servicemix, you can install 
the example bundle into Servicemix using the following from the servicemix command console:

osgi:install -s mvn:com.fusesource.examples/camel-mq-osgi/1.1

To enable logging for this example type:
log:set DEBUG com.fusesource.camel
For an even higher level of logging (logging Camel specifics)
log:set DEBUG org.apache.camel


Running outside of Apache Karaf
-----------------------------
As an alternative to running this route in Servicemix, you can also run the camel route
in standalone mode outside of Servicemix using the camel maven plugin (see pom).
To run in standlalone mode you will need to install the minimal set of IBM runtime libraries
in your local maven repository. These jars can usually be found in a installation of IBM 
For example, the following applies to Websphere MQ version 7. 

mvn install:install-file -Dfile=C:\PROGRA~1\IBM\WEBSPH~1\java\lib\com.ibm.mqjms.jar -DgroupId=com.ibm  -DartifactId=com.ibm.mqjms -Dversion=7.0.1.3  -Dpackaging=jar
mvn install:install-file -Dfile=C:\PROGRA~1\IBM\WEBSPH~1\java\lib\dhbcore.jar -DgroupId=com.ibm.mq.dhbcore  -DartifactId=dhbcore -Dversion=7.0.1.3  -Dpackaging=jar
mvn install:install-file -Dfile=C:\PROGRA~1\IBM\WEBSPH~1\java\lib\com.ibm.mq.jmqi.jar -DgroupId=com.ibm.mq  -DartifactId=com.ibm.mq.jmqi -Dversion=7.0.1.3  -Dpackaging=jar

As with the other IBM libraries installed in the local maven repo, the names used (groupId and artifactId)
were devised by me rather than being official groupID/artifactID/version from IBM.
It is unknown whether IBM maintains an appropriate public Maven repository that 
contains such “official” artifacts but none could be found when implementing the POC. If it 
is determined that such a public repo exists, then this step might be avoided. 

Now, you can run the route in standalone mode by typing (make sure your Websphere MQ broker is running)
  
  mvn camel:run



Testing the Route
-----------------------------
Whether running inside RedHat JBoss Fuse, Apache Karaf or in standlone mode, the technique for triggering the route will
be the same. To trigger the route you can perform an HTTP POST at the URL "http://localhost:8888/placeorder", sending
in any valid XML as the content of the message. (I use a simple tool like SOAPUI or CURL to send http requests)
The route will take the content of the incoming HTTP post and put it on the appropriate queue (as specified in the
"camel-context.xml".)

As a convenience for testing, a secondary "server simulator" route has been added (see "MqRoute.java") that
will listen on the outgoing queue. The route that is listening on this queue will pick up 
the message and place a response on the designated response queue (copying
messageId into correlationId) per JMS request/reply convention. The primary route
is then able to receive the response on expected queue and correlate it with the request.


