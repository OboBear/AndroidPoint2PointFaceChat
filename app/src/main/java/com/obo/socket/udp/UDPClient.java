package com.obo.socket.udp;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

/**
 * Created by obo on 2017/8/13.
 * Email:obo1993@gmail.com
 * Git:https://github.com/OboBear
 * Blog:http://blog.csdn.net/leilba
 */
public class UDPClient {

    private static String TAG;

    private static final int TIMEOUT = 5000;  //设置接收数据的超时时间
    private static final int MAXNUM = 5555;      //设置重发数据的最多次数

    public void startClient(String myName, String otherName, String serverAddress, int serverPort, int myPort) throws IOException {
        TAG = "UDPClient" + myName;

        String str_send = myName;
        byte[] buf = new byte[1024];
        //客户端在9000端口监听接收到的数据
        DatagramSocket ds = new DatagramSocket(myPort);

        InetAddress loc = InetAddress.getByName(serverAddress);


        //定义用来发送数据的DatagramPacket实例
        DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), loc, serverPort);
        //定义用来接收数据的DatagramPacket实例
        DatagramPacket dp_receive = new DatagramPacket(buf, 1024);
        //数据发向本地3000端口
        ds.setSoTimeout(TIMEOUT);              //设置接收数据时阻塞的最长时间
        int tries = 0;                         //重发数据的次数
        boolean receivedResponse = false;     //是否接收到数据的标志位
        //直到接收到数据，或者重发次数达到预定值，则退出循环
        while (!receivedResponse && tries < MAXNUM) {
            //发送数据
            ds.send(dp_send);
            try {
                //接收从服务端发送回来的数据
                ds.receive(dp_receive);
//                ds.receive(dp_receive);
                //如果接收到的数据不是来自目标地址，则抛出异常
                if (!dp_receive.getAddress().equals(loc)) {
                    throw new IOException("Received packet from an umknown source");
                }

                System.out.println("client received data from server：");
                String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength()) +
                        " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
                System.out.println(str_receive);

                String receiveStr = new String(dp_receive.getData(), 0, dp_receive.getLength());
                String[] addressIp = receiveStr.split(":");
                if (addressIp.length == 2) {
                    String address = addressIp[0];
                    String port = addressIp[1];

                    sendToClientA(ds, address, port);
//                    sendToClientA(ds, address, port);
//                    sendToClientA(ds, address, port);

                    System.out.println(TAG + "client send to Client" + otherName);
                    ds.receive(dp_receive);

                    String receiveFromClientB = new String(dp_receive.getData(), 0, dp_receive.getLength());
                    System.out.println(TAG + "receive from " + otherName + " :" + receiveFromClientB);

                    sendToClientA(ds, address, port);

                    ds.receive(dp_receive);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (InterruptedIOException e) {
                //如果接收数据时阻塞超时，重发并减少一次重发的次数
                tries += 1;
                System.out.println("Time out," + (MAXNUM - tries) + " more tries...");
            }
//            try {
//                Thread.sleep(TIMEOUT);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        ds.close();
    }


    private static void sendToClientA(DatagramSocket ds, String address, String port) {
        String sendToClientA = "This is Client B";
        InetAddress locToClientA = null;
        try {
            locToClientA = InetAddress.getByName(address);
            DatagramPacket sendToClientPacket = new DatagramPacket(sendToClientA.getBytes(), sendToClientA.length(), locToClientA, Integer.valueOf(port));
            ds.send(sendToClientPacket);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
