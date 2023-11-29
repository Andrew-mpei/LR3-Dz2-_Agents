package AgentDetector;

import com.sun.jna.WString;
import jade.core.AID;

import java.util.List;

public interface AgentDetectorInterface {
    void startPublishing(int port);
    void startDiscovering(int port);
    List<AID> getActiveAgents();
}
