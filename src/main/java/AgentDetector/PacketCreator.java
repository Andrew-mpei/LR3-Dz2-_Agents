package AgentDetector;

import java.nio.ByteBuffer;

public class PacketCreator {
    public byte[] create(String data, int port){
        byte[] byteData = new byte[100];
        byte[] headers = new byte[]{2, 0, 0, 0, 69, 0, 0, 58, 45, -4, 0, 0, -128, 17, 0, 0, 127, 0, 0, 1, 127, 0, 0, 1, -39, -41, 4, -80, 0, 38, -91, -36};
        byte[] payLoad = data.getBytes();

        for(int i = 0; i < headers.length; i ++){
            byteData[i] = headers[i];
        }
        for (int i = 0; i < payLoad.length;i++) {
            byteData[headers.length+i] = payLoad[i];
        }
        for (int i = 26, j = 6; j < 8; i++, j++){
            byteData[i] = longToBytes(port)[j];
        }

        return byteData;
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

}

//        byte[] bytePort = new byte[]{0,0};
//        if (port>100){
//            bytePort[0] = Byte.parseByte(Integer.toString(port / 100));
//            bytePort[1] = Byte.parseByte(Integer.toString(port%100));
//        }else{
//            bytePort[1]=(byte) port;
//        }
//        System.out.println();
//        System.out.println("System.out.println(Byte.parseByte(Integer.toString(port/100)))   "+Byte.parseByte(Integer.toString(port/100)));
//        System.out.println("(port / 100)  "+(port / 100));
//        System.out.println("bytePort[0]  "+bytePort[0]+"    bytePort[1]  "+bytePort[1]);

//        String sbite = Integer.toString(port);
//        byte[] bytePort = new byte[]{Byte.parseByte(sbite)};
//        System.out.println(bytePort);








//        for (int i : longToBytes(port)) System.out.println(i);
        //        for(int i = 0 ;i < bytePort.length;i++){
//            byteData[26 + i] = bytePort[i];
//        }
