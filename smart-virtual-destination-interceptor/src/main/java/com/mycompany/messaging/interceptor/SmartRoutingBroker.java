package com.mycompany.messaging.interceptor;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.DestinationInterceptor;
import org.apache.activemq.broker.region.virtual.VirtualDestination;
import org.apache.activemq.broker.region.virtual.VirtualDestinationInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class SmartRoutingBroker implements InitializingBean, DisposableBean {
    protected static Logger logger = LoggerFactory.getLogger(SmartRoutingBroker.class);
    private BrokerService broker;
    private String brokerName;
    private DynamicRoutingInterceptor dynamicRoutingInterceptor;
    private VirtualDestinationInterceptor virtualDestinationInterceptor;
    
    @Override
    public void destroy() throws Exception {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        VirtualDestination[] virtualDestinationArray = null;
        DestinationInterceptor[] destinationInterceptorArray = null;
        
        if (getBroker() == null) {
            throw new BeanInitializationException("Broker not initialized. Cannot proceed without a  Broker");
        }
        
        //broker.stop();
        logger.info("Stopping DEX Messaging Node Broker to install Dynamic Destination Routing Logic");
        
        broker.setBrokerName(brokerName);
        logger.info("Setting Default Broker Name to " + brokerName);        
        
        if (broker.getDestinationInterceptors() == null) {            
            ArrayList<VirtualDestination> virtualDestinationList = new ArrayList<VirtualDestination>();
            virtualDestinationList.add(dynamicRoutingInterceptor);
            
            virtualDestinationInterceptor = new VirtualDestinationInterceptor();
            virtualDestinationArray = virtualDestinationList.toArray(new VirtualDestination[0]);
            virtualDestinationInterceptor.setVirtualDestinations(virtualDestinationArray);
            
            ArrayList<DestinationInterceptor> destinationInterceptorList = new ArrayList<DestinationInterceptor>();
            destinationInterceptorList.add(virtualDestinationInterceptor);
            destinationInterceptorArray = destinationInterceptorList.toArray(new DestinationInterceptor[0]);
            broker.setDestinationInterceptors(destinationInterceptorArray);
      
        } else {
            boolean found = false; 
            for (DestinationInterceptor destinationInterceptor : broker.getDestinationInterceptors()) {
                if (destinationInterceptor instanceof VirtualDestinationInterceptor) {
                    VirtualDestination[] virtualDestinations = ((VirtualDestinationInterceptor) destinationInterceptor).getVirtualDestinations();
                    ArrayList<VirtualDestination> virtualDestinationList = new ArrayList<VirtualDestination>(Arrays.asList(virtualDestinations));
                    virtualDestinationList.add(dynamicRoutingInterceptor);
                    ((VirtualDestinationInterceptor) destinationInterceptor).setVirtualDestinations((VirtualDestination[])virtualDestinationList.toArray());
                    found = true;
                }
            }
            if (!found) {
                ArrayList<VirtualDestination> virtualDestinationList = new ArrayList<VirtualDestination>();
                virtualDestinationList.add(dynamicRoutingInterceptor);
                
                virtualDestinationInterceptor = new VirtualDestinationInterceptor();
                virtualDestinationArray = virtualDestinationList.toArray(new VirtualDestination[0]);
                virtualDestinationInterceptor.setVirtualDestinations(virtualDestinationArray);
                
                ArrayList<DestinationInterceptor> destinationInterceptorList = new ArrayList<DestinationInterceptor>(Arrays.asList(broker.getDestinationInterceptors()));
                destinationInterceptorList.add(virtualDestinationInterceptor);
                destinationInterceptorArray = destinationInterceptorList.toArray(new DestinationInterceptor[destinationInterceptorList.size()]);
                broker.setDestinationInterceptors(destinationInterceptorArray);
            }
            
        }
        
        /*if (broker.isUseJmx()) {
            broker.setUseJmx(true);
        }*/
        //broker.start();

    }

    public BrokerService getBroker() {
        return broker;
    }

    public void setBroker(BrokerService broker) {
        this.broker = broker;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public DynamicRoutingInterceptor getDynamicRoutingInterceptor() {
        return dynamicRoutingInterceptor;
    }

    public void setDynamicRoutingInterceptor(
            DynamicRoutingInterceptor dynamicRoutingInterceptor) {
        this.dynamicRoutingInterceptor = dynamicRoutingInterceptor;
    }

}
