/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lab.examples.zookeeper.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.zookeeper.ZooKeeperMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class RouteMutationProcessor implements Processor {
    private static final transient Log LOG = LogFactory.getLog(RouteMutationProcessor.class);
    private static final String DELIMITER= "<->";
    private CamelContext camelContext;
    private ZooKeeperConnector zookeeperConnector;
    List<String> routeIdList = new ArrayList<String>();

    /* (non-Javadoc)
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    public void process(Exchange exchange) throws Exception {
        ZooKeeperMessage message = (ZooKeeperMessage) exchange.getIn();
        List<String> childNodes = (List<String>)message.getBody();
        
        ZooKeeper zookeeper = zookeeperConnector.getZooKeeper();
        Stat stat = new Stat();
        
        if (!routeIdList.isEmpty()) {
            if (childNodes.size() < routeIdList.size()) {
                for (String routeId : (List<String>) CollectionUtils.subtract(routeIdList, childNodes)) {
                    //camelContext.stopRoute(routeId,5,TimeUnit.MILLISECONDS,true);
                    //camelContext.removeRoute(routeId);
                    camelContext.shutdownRoute(routeId,5,TimeUnit.MILLISECONDS);
                    routeIdList.remove(routeId);
                }
            }
        }
        
        for (String routeId : childNodes) {                                    
            // Add notification received
            if (camelContext.getRoute(routeId) == null) {   // Route does not already exist
                String[] destinations = new String(zookeeper.getData("/DEMO/" + routeId, null, stat)).split(DELIMITER);
                camelContext.addRoutes(new MyDynamicRouteBuilder(camelContext, routeId, destinations[0].trim(), destinations[1].trim()));
                routeIdList.add(routeId);
            }
        }
    }
    
    /**
     * This route builder is a skeleton to add new routes at runtime
     */
    private static final class MyDynamicRouteBuilder extends RouteBuilder {
        private final String routeId;
        private final String from;
        private final String to;

        private MyDynamicRouteBuilder(CamelContext context, String routeId, String from, String to) {
            super(context);
            this.routeId = routeId;
            this.from = from;
            this.to = to;
        }

        @Override
        public void configure() throws Exception {
            LOG.info("Adding Route : from(\"" + from + "\").id(\"" + routeId + "\").to(\"" + to +"\")");
            from(from).id(routeId).to(to);
            LOG.info("Route " + routeId + " added successfully");
        }
    }
    
    public CamelContext getCamelContext() {
        return camelContext;
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public ZooKeeperConnector getZookeeperConnector() {
        return zookeeperConnector;
    }

    public void setZookeeperConnector(ZooKeeperConnector zookeeperConnector) {
        this.zookeeperConnector = zookeeperConnector;
    }

}
