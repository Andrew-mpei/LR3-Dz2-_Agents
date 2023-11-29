package Agent;

import AgentDetector.AgentDetector;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckBehaviour extends TickerBehaviour {
    private AgentDetector ad;
    private int counter = 0;
    public CheckBehaviour(Agent a, long period, AgentDetector ad) {
        super(a, period);
        this.ad = ad;
    }

    @Override
    protected void onTick() {
        log.error("{} find nex agents: {}", myAgent.getLocalName(), this.ad.getActiveAgents());

        if (myAgent.getLocalName().equals("Agent3") && counter == 5) {
            this.ad.stop();
        }
        counter++;
    }
}
