package Agent;

import AgentDetector.AgentDetector;
import jade.core.Agent;

public class MyAgent extends Agent {
    @Override
    protected void setup() {
        AgentDetector ad = new AgentDetector(this.getAID());
        ad.startDiscovering(1400);
        ad.startPublishing( 1400);
        ad.isAlive();
        this.addBehaviour(new CheckBehaviour(this, 1000, ad));
    }

}
