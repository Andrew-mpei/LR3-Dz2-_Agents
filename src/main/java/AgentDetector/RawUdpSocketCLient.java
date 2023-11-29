package AgentDetector;

import com.sun.jna.NativeLibrary;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;

import java.util.List;

@Slf4j
public class RawUdpSocketCLient {
    private PcapHandle pcapHandle;
    static {
        //install wireshark and check npcap was installed
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            NativeLibrary.addSearchPath("wpcap", "C:\\Windows\\System32\\Npcap");
        }
    }

    public void startThread(byte[] data, long period) {
        initialize();

        new Thread( ()-> {
            while(true) {
                this.send(data);
                try {
                    Thread.sleep(period);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void send(byte[] data){
        try {
            pcapHandle.sendPacket(data);
        } catch (NotOpenException e) {
            throw new RuntimeException(e);
        } catch (PcapNativeException e) {
            log.error("can not send packet");
            throw new RuntimeException(e);
        }
    }


    public void initialize(){
        try{
            List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
            PcapNetworkInterface networkInterface = null;
            for (PcapNetworkInterface allDev : allDevs) {
                if (allDev.getName().equals("\\Device\\NPF_Loopback")){
                    networkInterface = allDev;
                    break;
                }
            }
            pcapHandle = networkInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);

        } catch (PcapNativeException e) {
            log.error("can not find any devs");
            throw new RuntimeException(e);
        }
    }



}