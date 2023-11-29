package AgentDetector;

import jade.core.AID;
import lombok.extern.slf4j.Slf4j;
import utils.JsonUtils;

import java.lang.management.ThreadInfo;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AgentDetector implements AgentDetectorInterface {
    private AID myAgentAid;
    private boolean flagStart;
    private boolean flagDiscovering;
    private boolean flagLive;
    // поле для хранения списка живых агентов
    private Map<AID, Long> activeAgents;
    private RawUdpSocketCLient client;
    private RawUdpSocketServer discovering;


    public AgentDetector(AID aid) {
        this.myAgentAid = aid;
        this.flagStart = false;
        this.flagDiscovering = false;
        this.activeAgents = new ConcurrentHashMap<>();

    }


    @Override
    public void startPublishing(int port) {
        if (!this.flagStart){
            AidDTO msgDTO = new AidDTO(this.myAgentAid.getLocalName(), false);
            String msg = JsonUtils.code(msgDTO);

            PacketCreator packetСreator = new PacketCreator();
            byte[] packet = packetСreator.create(msg, port);

            this.client = new RawUdpSocketCLient();
            client.startThread(packet, 500);
            this.flagStart = true;
        }else{
            log.warn("Thread already started");
        }
    }

    @Override
    public void startDiscovering(int port) {
        if (!this.flagDiscovering){
            this.discovering = new RawUdpSocketServer(this.activeAgents, this.myAgentAid.getLocalName());
            discovering.start(port);
        }else{
            log.warn("Thread already started");
        }
    }

    public void isAlive() {
        if (!this.flagLive) {
            this.flagLive = true;
            new Thread(() -> {
                while (this.flagLive) {
                    monitor();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
    }

    private void monitor(){
        Set<AID> keys = this.activeAgents.keySet();
        for (AID key : keys) {
            if ((System.currentTimeMillis() - this.activeAgents.get(key))/1000.0 >= 3) {
                this.activeAgents.remove(key);
            }
        }
    }

    @Override
    public List<AID> getActiveAgents() {
        return this.activeAgents.keySet().stream().toList();
    }

    public void stop() {
        this.client.stopThread();
        this.discovering.stopThread();
        this.flagStart = false;
        this.flagDiscovering = false;
        this.flagLive = false;
        this.activeAgents.clear();

    }


    // sending UDP with agentData (AgentName and isGUID) as JSON
    // receive UDP with agentData, parse it and collect into LIST
    // detect agent missing

    //List<AID> getCurrentAgents()

//    void test() {
//        Thread t = new Thread();
//        t.interrupt();
//        t.stop();
//    }

}