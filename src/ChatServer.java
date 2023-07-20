
import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

public class ChatServer {
    private static int clientNum = 0;  // ��ǰ���ӵ��û���
    private int maxClients = 10;  // �������ӵ��û������
    private ServerSocket ss;  // �������׽���
    private CommunicationThread[] ccs;  // �����û�������Ϣ���߳�����
    private int[] logOutUserID = new int[maxClients];  // ��������
    private int logOutUserNUms = 0;  // ��ǰ���ߵ��û���

    public ChatServer() throws IOException {
        logOutUserID[0] = -1;
//        ��ָ���Ķ˿��ϼ����ͻ��˵���������
        ss = new ServerSocket(50000, maxClients);

//        �����û�������Ϣ���߳�����
        ccs = new CommunicationThread[maxClients];

//        ѭ���ȴ��û�����
        for (int i = 0; i < maxClients; i++) {
            /*
             * �����ͻ��˵��������󣬵ȴ��û�����
             * ���пͻ����������󵽴�ʱ��accept() ����������һ���µ� Socket ����
             * �ö��������ͻ��˵�ͨ��ͨ��
             * */
            Socket s = ss.accept();  // s ������ͻ��˵�ͨ��ͨ��

//                ����server��ͨ���̣߳��������߳�����
            ccs[i] = new CommunicationThread(s, i);

//                �������е��߳�
            ccs[i].start();

//                �㲥���ͣ�clientNum��ʾ��ǰ���ӵ��û���
            for (int m = 0; m < clientNum; m++) {
                int flag = 0;
                for (int j = 0; j < logOutUserNUms; j++) {
                    if (m == logOutUserID[j]) {  // ��ʼ���ݣ�logOutUserID[0] = -1;
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0) {
                    ccs[m].output.writeUTF("��ʾ��#&��|��&#" + "�û�" + (i + 1) + "�����ˣ�" + "��������Ϊ" + String.valueOf(clientNum - logOutUserNUms + 1));
                }
            }
            clientNum++;
        }
    }

    private class CommunicationThread extends Thread {
        private Socket socket;  // ͨ�� Socket ���󣬿ͻ��˿�����������˽������ݽ�����ͨ��
        private int currentUserID;
        private DataInputStream input;
        private DataOutputStream output;
        String message;

        /**
         * @param ss     ͨ��ͨ��
         * @param number ������û���ID
         */
        public CommunicationThread(Socket ss, int number) throws IOException {
            socket = ss;
            currentUserID = number;

//            ��Socket�õ�����/�����
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                output.writeInt(currentUserID);  // ���û�����clientID
                output.writeUTF("��ʾ��#&��|��&#" + "����������" + String.valueOf(clientNum - logOutUserNUms));
            } catch (IOException e) {
                System.out.println("clientIDû�з���ȥ");
            }

//            ѭ�����û�clientID����������Ϣ�����͸����ߵĸ����û�
            while (true) {
                try {
                    String message = input.readUTF();  // ���û�clientID����������Ϣ
                    System.out.println("������ˡ�message ���� ==>" + message);

//                    ����˽����Ϣ����˽����Ϣ����ʱ������*��
                    if (message.startsWith("*")) {
//                        ʹ��ָ���ķָ������������ַ������зָ�
                        StringTokenizer min = new StringTokenizer(message, "*");

//                        ȥ�� message ��*���ʵ����ʵ����
                        String si = min.nextToken();

//                        ��ʵ����ʵ���ݽ���Ϊ���������������������ֵ��ȥ 1�����洢�� str1 ������
//                        �²�Ӧ���ǵ�ǰ�û�ָ������˽����Ϣ���Ǹ���
                        int aimUserId = Integer.parseInt(si) - 1;
                        if (aimUserId < clientNum) {
                            message = min.nextToken();
                            int flag = 0;
                            for (int j = 0; j < logOutUserNUms; j++) {
//                                ɸѡ�������û�
                                if (aimUserId == logOutUserID[j]) {
//                                    ��˽�ĵ������뿪�����У��Ͱ� flag ��Ϊ1�����˳����ѭ��
                                    flag = 1;
                                    ccs[currentUserID].output.writeUTF("��ʾ��#&��|��&#" + "�Բ������ҵ��˲����� ");
                                    break;
                                }
                            }
                            if (flag == 0) {
                                if (aimUserId != currentUserID) {
                                    ccs[aimUserId].output.writeUTF("˽�� " + (currentUserID + 1) + message);
                                    ccs[currentUserID].output.writeUTF("(�㷢��˽����Ϣ) \n" + message);
                                } else if (aimUserId == currentUserID) {
                                    ccs[currentUserID].output.writeUTF("��ʾ��#&��|��&#���������Լ�������Ϣ�� ");
                                }
                            }
                        } else if (aimUserId >= clientNum) {
                            ccs[currentUserID].output.writeUTF("��ʾ��#&��|��&#�Բ������ҵ��˲����ڡ�");
                        }
                    } else {
//                    ����Ⱥ��Ϣ���͸����ߵĸ����û�
                        for (int i = 0; i < clientNum; i++) {
                            int flag = 0;
//                            ɸѡ���˲������û���id
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
                    //���ͻ����뿪
                    logOutUserID[logOutUserNUms] = currentUserID;
                    System.out.println(logOutUserID[logOutUserNUms] + "�ͻ�������");
//                    �㲥֪ͨĳ��������
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
                                ccs[m].output.writeUTF("��ʾ��#&��|��&#" + "�û�" + (logOutUserID[logOutUserNUms] + 1) + "�����ˡ�" + "Ⱥ��������" + String.valueOf(clientNum - logOutUserNUms - 1));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                System.out.print("������ȥ" + (clientNum - logOutUserNUms - 1) + ";");

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