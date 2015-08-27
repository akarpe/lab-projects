package com.fusesource.camel.test;

import org.apache.camel.builder.RouteBuilder;

public class MqRouteTestExtension extends RouteBuilder {
    @Override
    public void configure() throws Exception {        
        from("webspheremq:queue:incomingPayments").to("mock:orders");
    }

}