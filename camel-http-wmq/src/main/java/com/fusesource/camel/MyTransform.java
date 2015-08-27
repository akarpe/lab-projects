package com.fusesource.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;




public class MyTransform implements Processor, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(MyTransform.class);
	
	public void process(Exchange exchange) throws Exception {
		logger.debug("Entered Transform.");

		try {
			
			// Transforming the payload
			logger.debug("got JMSMessageID = " + exchange.getIn().getHeader("JMSMessageID"));
			exchange.setOut(exchange.getIn());
			exchange.getOut().setBody(exchange.getIn().getBody());
			logger.debug("Message body = " + exchange.getOut().getBody());
			logger.debug("Finished Transform.");
		} catch (Exception e) {
			logger.warn("Error while invoking on the Sample Service", e);
			exchange.setException(e);
		}
	}

	public void afterPropertiesSet() throws BeanInitializationException {
	}

}
