package com.fusesource.camel;

import static org.junit.Assert.assertEquals;
import com.fusesource.camel.test.*;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.camel.Exchange;
import org.junit.Test;

//@RunWith(SpringJUnit4ClassRunner.class)
//ApplicationContext will be loaded from "/spring-camel-context" in the root of the classpath
//@ContextConfiguration(locations={"/spring-camel-context.xml"})

public class MqRouteTest extends CamelSpringTestSupport {
	

	//@Autowired
    //protected CamelContext camelContext;


	private ProducerTemplate producer;
	private static final String MOCK_ORDERS = "mock:orders";
	//private MqRoute mqRoute;
	private MockEndpoint mockOrders;
	

    protected CamelContext getCamelContext() throws Exception {   
        applicationContext = createApplicationContext();
        return SpringCamelContext.springCamelContext(applicationContext);
    }    
	
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/test-camel-context.xml");
    }
    
	  
	@Before
	public void setUp() throws Exception {


		//com.ibm.mq.jms.MQConnectionFactory cf = new com.ibm.mq.jms.MQConnectionFactory();
		//cf.setHostName("localhost");
		//cf.setPort(1414);
		//cf.setTransportType(1);
		//cf.setQueueManager("QM_TEST");
		//cf.setChannel("jmstojmsBridgeQueue");
		
		System.out.println("Calling setUp");
		
		CamelContext camelContext = getCamelContext();
		mockOrders = camelContext.getEndpoint( MOCK_ORDERS,  MockEndpoint.class );
		
		//MqRouteTestExtension route4Test = new MqRouteTestExtension(); 
			
		//camelContext.addRoutes( route4Test );
		camelContext.start();

		producer = camelContext.createProducerTemplate();

        
	}
	
	@Test
	public void invokeMQRouteTest() throws Exception {

		System.out.println("Calling invokeMQRouteTest");
		
		// Set expectations
		int SEND_COUNT = 1;
		//mockOrders.expectedBodiesReceived(new Order("gearbox", 5));

		System.out.println("sending message.");
		
		// Perform Test
       String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
            + "<order name=\"gearbox\" amount=\"5\"/>";
       
       Object response = null;
       for (int i=0; i<=SEND_COUNT; i++) {
        response = producer.requestBody("http://localhost:8888/placeorder", body);
        
       }
        // convert the response to a String
        //String responseString = camelContext.getTypeConverter().convertTo(String.class, response);
        //assertEquals("OK", responseString);
        
        // ensure that the order got through to the mock endpoint
        //mockOrders.setResultWaitTime(1000);
        //mockOrders.assertIsSatisfied();
        assertNotNull(response);
        
		
       
	        
	}



}
