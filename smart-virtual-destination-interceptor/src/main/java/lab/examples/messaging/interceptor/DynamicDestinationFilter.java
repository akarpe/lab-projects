package lab.examples.messaging.interceptor;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.DestinationFilter;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicDestinationFilter extends DestinationFilter {
    private static Logger logger = LoggerFactory.getLogger(DynamicDestinationFilter.class);
    private String sourceNode;
    
    public DynamicDestinationFilter(Destination next, String sourceNode) {
        super(next);
        this.sourceNode = sourceNode;
    }
    
    public void send(ProducerBrokerExchange context, Message message) throws Exception {
    	ActiveMQDestination destination = null;
        Broker broker = context.getConnectionContext().getBroker();
        
        /*
         * Add Message Distribution Logic here
         */
        
        // destination = ActiveMQDestination.createDestination("TestQueue", ActiveMQDestination.TOPIC_TYPE);
        //sendToDestination(broker, context, message, destination);

    }
    
    private void sendToDestination(Broker broker, ProducerBrokerExchange context, Message message, ActiveMQDestination destination) throws Exception {
        if (broker.getDestinations(destination).isEmpty()) {
            broker.getBrokerService().getRegionBroker().addDestination(broker.getAdminConnectionContext(), destination,true);   
        }

        for (Destination dest : broker.getDestinations(destination)) {
            logger.debug("Following search sending request to Destination " + dest.getName());
            dest.send(context, message.copy());
        }    
    }
}
