package lab.examples.messaging.interceptor;

import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.virtual.CompositeDestination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

public class DynamicRoutingInterceptor extends CompositeDestination {
    private String inboundDestinationType;
    private String inboundDestinationName;
    private String sourceNode;
    
    @Override
    public ActiveMQDestination getVirtualDestination() {
        if (inboundDestinationType.equalsIgnoreCase("queue")) {
            return new ActiveMQQueue(getInboundDestinationName());
        } else {
            return new ActiveMQTopic(getInboundDestinationName());
        }
    }
    
    public Destination intercept(Destination destination) {        
        return new DynamicDestinationFilter(destination, sourceNode);
    }

    public String getInboundDestinationType() {
        return inboundDestinationType;
    }

    public void setInboundDestinationType(String inboundDestinationType) {
        this.inboundDestinationType = inboundDestinationType;
    }

    public String getInboundDestinationName() {
        return inboundDestinationName;
    }

    public void setInboundDestinationName(String inboundDestinationName) {
        this.inboundDestinationName = inboundDestinationName;
    }
    
    public String getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(String sourceNode) {
		this.sourceNode = sourceNode;
	}

}
