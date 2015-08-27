package com.fusesource.camel;

import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanInitializationException;


public class MqRoute extends SpringRouteBuilder implements InitializingBean,
DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger( MqRoute.class );

	private String incomingQueue;
	private String outgoingQueue; 
	
	String sourceUri;
	String targetUri;
	
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}

	public void afterPropertiesSet() throws Exception {
		if ((incomingQueue == null) || (incomingQueue.equals(""))) { 
			throw new BeanInitializationException("You must set a value for incomingQueue");
		}
		
		if ((outgoingQueue == null) || (outgoingQueue.equals(""))) { 
			throw new BeanInitializationException("You must set a value for outgoingQueue");
		}
		logger.debug("Setting sourceUri to " + incomingQueue);

		logger.debug("Setting outgoingUri to " + outgoingQueue);
		
		sourceUri = "wmq:queue:" + incomingQueue;

		targetUri = "wmq:queue:" + outgoingQueue;
	}
	
	public void configure() throws Exception {

        from("jetty:http://0.0.0.0:8888/placeorder")
        .to("log:com.fusesource.camel?showAll=true&multiline=true")
        .to(targetUri + "?replyTo=" + incomingQueue + "&useMessageIDAsCorrelationID=true")
        .to("log:com.fusesource.camel?showAll=true&multiline=true");

        // This can be used to "Mock" Server "simulator"
        from(targetUri + "?useMessageIDAsCorrelationID=true")    	
        	.to("myTransform")
        	.to("log:com.fusesource.camel?showAll=true&multiline=true");
        	
	}

	public String getSourceUri() {
		return sourceUri;
	}

	public void setSourceUri(String sourceUri) {
		this.sourceUri = sourceUri;
	}

	public String getTargetUri() {
		return targetUri;
	}

	public void setTargetUri(String targetUri) {
		this.targetUri = targetUri;
	}

	public String getIncomingQueue() {
		return incomingQueue;
	}

	public void setIncomingQueue(String incomingQueue) {
		this.incomingQueue = incomingQueue;
	}

	public String getOutgoingQueue() {
		return outgoingQueue;
	}

	public void setOutgoingQueue(String outgoingQueue) {
		this.outgoingQueue = outgoingQueue;
	}

}
