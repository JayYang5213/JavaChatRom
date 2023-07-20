
import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

public class ChatServer {
    private static int clientNum = 0;  // 当前连接的用户数
    private int maxClients = 10;  // 允许连接的用户最大数
    private ServerSocket ss;  // 服务器套接字
    private CommunicationThread[] ccs;  // 管理用户连接信息的线程数组
    private int[] logOutUserID = new int[maxClients];  // 下线数组
    private int logOutUserNUms = 0;  // 当前下线的用户数

    public ChatServer() throws IOException {
        logOutUserID[0] = -1;
//        在指定的端口上监听客户端的连接请求
        ss = new ServerSocket(50000, maxClients);

//        管理用户连接信息的线程数组
        ccs = new CommunicationThread[maxClients];

//        循环等待用户连接
        for (int i = 0; i < maxClients; i++) {
            /*
             * 监听客户端的连接请求，等待用户连接
             * 当有客户端连接请求到达时，accept() 方法将返回一个新的 Socket 对象
             * 该对象代表与客户端的通信通道
             * */
            Socket s = ss.accept();  // s 代表与客户端的通信通道

//                创建server端通信线程，并放入线程数组
            ccs[i] = new CommunicationThread(s, i);

//                启动所有的线程
            ccs[i].start();

//                广播发送，clientNum表示当前连接的用户数
            for (int m = 0; m < clientNum; m++) {
                int flag = 0;
                for (int j = 0; j < logOutUserNUms; j++) {
                    if (m == logOutUserID[j]) {  // 初始数据：logOutUserID[0] = -1;
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0) {
                    ccs[m].output.writeUTF("提示：#&。|。&#" + "用户" + (i + 1) + "上线了；" + "在线人数为" + String.valueOf(clientNum - logOutUserNUms + 1));
                }
            }
            clientNum++;
        }
    }

    private class CommunicationThread extends Thread {
        private Socket socket;  // 通过 Socket 对象，客户端可以与服务器端进行数据交换和通信
        private int currentUserID;
        private DataInputStream input;
        private DataOutputStream output;
        String message;

        /**
         * @param ss     通信通道
         * @param number 分配给用户的ID
         */
        public CommunicationThread(Socket ss, int number) throws IOException {
            socket = ss;
            currentUserID = number;

//            从Socket得到输入/输出流
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                output.writeInt(currentUserID);  // 给用户发送clientID
                output.writeUTF("提示：#&。|。&#" + "在线人数：" + String.valueOf(clientNum - logOutUserNUms));
            } catch (IOException e) {
                System.out.println("clientID没有发出去");
            }

//            循环读用户clientID发送来的信息，发送给在线的各个用户
            while (true) {
                try {
                    String message = input.readUTF();  // 读用户clientID发送来的信息
                    System.out.println("【服务端】message 变量 ==>" + message);

//                    发送私密消息，在私密消息发送时，加了*号
                    if (message.startsWith("*")) {
//                        使用指定的分隔符将给定的字符串进行分割
                        StringTokenizer min = new StringTokenizer(message, "*");

//                        去除 message 的*后的实际真实内容
                        String si = min.nextToken();

//                        将实际真实内容解析为整数，并将解析后的整数值减去 1，并存储到 str1 变量中
//                        猜测应该是当前用户指定接收私密消息的那个人
                        int aimUserId = Integer.parseInt(si) - 1;
                        if (aimUserId < clientNum) {
                            message = min.nextToken();
                            int flag = 0;
                            for (int j = 0; j < logOutUserNUms; j++) {
//                                筛选出在线用户
                                if (aimUserId == logOutUserID[j]) {
//                                    若私聊的人在离开的人中，就把 flag 设为1，并退出这层循环
                                    flag = 1;
                                    ccs[currentUserID].output.writeUTF("提示：#&。|。&#" + "对不起，你找的人不在线 ");
                                    break;
                                }
                            }
                            if (flag == 0) {
                                if (aimUserId != currentUserID) {
                                    ccs[aimUserId].output.writeUTF("私密 " + (currentUserID + 1) + message);
                                    ccs[currentUserID].output.writeUTF("(你发的私密信息) \n" + message);
                                } else if (aimUserId == currentUserID) {
                                    ccs[currentUserID].output.writeUTF("提示：#&。|。&#不允许向自己发送信息？ ");
                                }
                            }
                        } else if (aimUserId >= clientNum) {
                            ccs[currentUserID].output.writeUTF("提示：#&。|。&#对不起，你找的人不存在。");
                        }
                    } else {
//                    发送群消息发送给在线的各个用户
                        for (int i = 0; i < clientNum; i++) {
                            int flag = 0;
//                            筛选出了不在线用户的id
                            for (int j = 0; j < logOutUserNUms; j++) {
                                if (i == logOutUserID[j]) {
                                    flag = 1;
                                    break;
                                }
                            }
                            if (flag == 0) {
                                ccs[i].output.writeUTF(" " + message);
                            }
                        }
                    }

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    //当客户端离开
                    logOutUserID[logOutUserNUms] = currentUserID;
                    System.out.println(logOutUserID[logOutUserNUms] + "客户下线了");
//                    广播通知某人下线了
                    for (int m = 0; m < clientNum; m++) {
                        int flag = 0;
                        for (int j = 0; j < logOutUserNUms; j++) {
                            if (m == logOutUserID[j]) {
                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 0) {
                            try {
                                ccs[m].output.writeUTF("提示：#&。|。&#" + "用户" + (logOutUserID[logOutUserNUms] + 1) + "下线了。" + "群聊人数：" + String.valueOf(clientNum - logOutUserNUms - 1));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                System.out.print("发不出去" + (clientNum - logOutUserNUms - 1) + ";");

                            }
                        }

                    }
                    logOutUserNUms++;
                    break;
                }
            }
            try {
                output.close();
                input.close();
                socket.close();
            } catch (EOFException ioException) {
                System.err.println("Client terminated connection");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }

        }

    }

    public static void main(String[] args) throws IOException {
        new ChatServer();
    }
}