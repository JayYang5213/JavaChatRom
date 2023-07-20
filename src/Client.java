import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

public class Client extends JFrame implements ActionListener {
    //    ��Ա����
    private JPanel pN, pS, pW, pE;
    private JTextArea in_message;
    private JTextField out_message, secret;
    private JButton putOut;
    private JLabel label1, labelgif;
    private JScrollPane scroll;
    private String username;
    private String messageGetout;
    private boolean siliao; // ˽�ı��
    private int clientID;
    private Socket connection;
    private DataInputStream input;
    private DataOutputStream output;
    // ������
    private static String winTitle = "Java����";
    boolean InorNot = false; // �ж��Ƿ������Ϸ�����

    // ���캯��
    public Client(String ss) throws Exception, IOException {
        super(winTitle);
        Container con = getContentPane();
        username = ss;

        // �������Ͱ�ť
        putOut = new JButton("����");
        putOut.setForeground(Color.black);
        putOut.setBackground(Color.green);

        // ��ϵĻ�ӭ���
        Icon bug = new ImageIcon("4.gif");
        labelgif = new JLabel(bug);
        label1 = new JLabel(ss + "������Ϊ�����");
//        label1.setForeground(Color.BLUE);
        label1.setFont(new Font("����", Font.BOLD, 12));

        // ˽�Ŀ�
        secret = new JTextField("����Է�id���ɷ���...", 12);
        secret.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (secret.getText().equals("����Է�id���ɷ���...")) {
                    secret.setText("");
                }
            }
        });

        // ������Ϣ��ʾ��
        out_message = new JTextField("�����ı�...", 60);
        out_message.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (out_message.getText().equals("���ðɣ�")) {
                    out_message.setText("");
                }
            }
        });

        // ��ʾ������Ϣ��
        in_message = new JTextArea(30, 15);
        in_message.setEditable(false);

        // �������ֺ�������
        pN = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pS = new JPanel();
        pW = new JPanel();
        pE = new JPanel();

        pN.add(labelgif);
        // in_message��ӵ�scroll��
        scroll = new JScrollPane(in_message);
        // pS ��� ���Ϳ򡢷��Ͱ�ť��˽�Ŀ�
        pS.setLayout(new GridLayout(2, 1));
        pS.add(out_message);
        JPanel pSpanel = new JPanel();
        pSpanel.add(secret);
        pSpanel.add(putOut);
        pSpanel.add(label1);
        pS.add(pSpanel);

        // ע�������
        putOut.addActionListener(this);
        out_message.addActionListener(this);
        secret.addActionListener(new secretText());

        // ��ӵ�����
        con.add(pN, BorderLayout.NORTH);
        con.add(scroll, BorderLayout.CENTER);
        con.add(pS, BorderLayout.SOUTH);
        con.add(pW, BorderLayout.WEST);
        con.add(pE, BorderLayout.EAST);

        // ����������������Ϣ�߳�
        Thread thread = new Thread(new Informaintion());
        thread.start();

        // �������
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(600, 260, 800, 600);
        setVisible(true);
        validate();
    }

    // ����һ���̶���СΪ10���̳߳�
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    // ˽���ı��������
    private class secretText implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            // ��ȡ˽���ı�������
            messageGetout = secret.getText();
            // �ж��Ƿ�Ϊ��Ч˽���û�
            if (!messageGetout.equals("����ta��id���ɷ�����") && !messageGetout.equals("") && !messageGetout.equals("0")) {
                char sis[] = messageGetout.toCharArray();
                int havezimu = 0; // �ж���û�г�����������ַ���0��ʾû�У�1��ʾ�У�
                for (int x = 0; x < sis.length; x++) {
                    if (sis[x] < 48 || sis[x] > 57) // 0��9��ASCIIֵΪ48--57
                    {
                        havezimu = 1;
                        break;
                    }
                }
                if (havezimu == 0) {
                    siliao = true;
                    winTitle = "˽����";
                    setTitle(winTitle);
                } else {
                    siliao = false;
                    in_message.append("�������������֣�\n");
                    secret.setText("����ta��id���ɷ�����");
                    out_message.setText("");
                    winTitle = "Ⱥ����";
                    setTitle(winTitle);
                }
            } else {
                siliao = false;
                secret.setText("����ta��id���ɷ�����");
                winTitle = "Ⱥ��ģʽ";
                setTitle(winTitle);
            }
        }
    }


    /**
     * ���Ͱ�ť���ı���ļ�����
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        // �ж��Ƿ����˷��Ͱ�ť�����˻س�����������������ݲ�Ϊ��
        if ((e.getSource() == putOut || e.getSource() == out_message) && !out_message.getText().equals("")) {
            // �ж��Ƿ��������˷�����
            if (InorNot) {
                if (siliao) { // ˽��ģʽ
                    try {
                        String s = "*" + messageGetout + "*" + username + "��" + "#&��|��&#" + out_message.getText();
                        output.writeUTF(s);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    out_message.setText("");
                } else { // Ⱥ��ģʽ
                    try {
                        String s = " " + username + "��" + "#&��|��&#" + out_message.getText();
                        output.writeUTF(s);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    out_message.setText("");
                }
            } else {
                in_message.append("����������ӳ�ʱ��" + "��" + out_message.getText() + "��" + "û�з��ͳɹ�" + "\n");
            }
        }
    }


    /**
     * ������Ϣ�߳�
     */
    private class Informaintion implements Runnable {
        public void run() {
            try {
                // make connection
                connection = new Socket("localhost", 50000);
                // get streams
                input = new DataInputStream(connection.getInputStream());
                output = new DataOutputStream(connection.getOutputStream());
                clientID = input.readInt() + 1;
                label1.setText(username + "����ı���ǣ�" + clientID);
                InorNot = true;
            } catch (IOException ioException) {
                System.out.println("���Ӳ��Ϸ�����");
                label1.setText(username + "�����������ӳ�ʱ��");
            }
            while (true) {
                try {
                    String message = new String(input.readUTF());
                    StringTokenizer min = new StringTokenizer(message, "#&��|��&#");
                    String str1 = min.nextToken();
                    String str2 = min.nextToken();
                    // ��������Ϣ�Ĳ��ַ����̳߳�ִ��
                    threadPool.execute(new HandleReceivedMessage(str1 + str2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * ���ڴ�����յ���Ϣ
     */
    private class HandleReceivedMessage implements Runnable {
        private String receivedMessage;

        public HandleReceivedMessage(String message) {
            this.receivedMessage = message;
        }

        public void run() {
            // �ڴ˴�������յ�����Ϣ�����½���Ȳ���
            in_message.append(receivedMessage + "\n");
        }
    }

    // ���������
    public static void main(String[] args) throws IOException, Exception {
        new Client("ҹ��һ����");
    }
}
