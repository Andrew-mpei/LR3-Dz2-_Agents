package AgentDetector;

import jade.core.AID;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;
import utils.JsonUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@NoArgsConstructor
public class RawUdpSocketServer {

    private Map<AID, Long> activeAgents;
    private String myAgentName;

    public RawUdpSocketServer(Map<AID, Long> activeAgents, String myAgentName) {
        this.activeAgents = activeAgents;
        this.myAgentName = myAgentName;
    }

    protected boolean run = true;


    public void start(int port){
        try{
            run = true;
            List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
            PcapNetworkInterface networkInterface = null;
            for (PcapNetworkInterface allDev : allDevs) {
                if (allDev.getName().equals("\\Device\\NPF_Loopback")){
                    networkInterface = allDev;
                    break;
                }
            }
            //TODO: handle if interface was not found
            PcapHandle pcapHandle = networkInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
            pcapHandle.setFilter("ip proto \\udp && dst port "+port, BpfProgram.BpfCompileMode.NONOPTIMIZE);

            runInThread(pcapHandle);
        } catch (NotOpenException e) {
            log.error("can't apply filter");
            throw new RuntimeException(e);
        } catch (PcapNativeException e) {
            log.error("can not find any devs");
            throw new RuntimeException(e);
        }
    }

    protected void runInThread(PcapHandle pcapHandle) {
        new Thread( ()-> {
            grabPackets(pcapHandle);
        }).start();
    }

    protected void grabPackets(PcapHandle pcapHandle) {
        try {
            pcapHandle.loop(0, (PacketListener) packet -> {
                byte[] rawData = packet.getRawData();
                byte[] data = new byte[rawData.length-32];
//                System.out.println(Arrays.toString(rawData));
                System.arraycopy(rawData, 32, data, 0, data.length);
//                System.out.println(new String(data).replace("\000", ""));
                //обработать пакет
                handlePacket(new String(data).replace("\000", ""));
                if (!run){
                    try {
                        pcapHandle.breakLoop();
                    } catch (NotOpenException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (PcapNativeException | InterruptedException | NotOpenException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePacket(String packetData) {
        Optional<AidDTO> packetDataOp = JsonUtils.decode(packetData, AidDTO.class);
        if (!packetDataOp.isEmpty()){
            AidDTO dto = packetDataOp.get();
            if (!dto.getName().equals(myAgentName)){
                AID receivedAid = new AID(dto.getName(), dto.isGuid());
                Long time = System.currentTimeMillis();
                this.activeAgents.put(receivedAid, time);
            }
        }
    }


}