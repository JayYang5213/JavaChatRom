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
    //    成员变量
    private JPanel pN, pS, pW, pE;
    private JTextArea in_message;
    private JTextField out_message, secret;
    private JButton putOut;
    private JLabel label1, labelgif;
    private JScrollPane scroll;
    private String username;
    private String messageGetout;
    private boolean siliao; // 私聊标记
    private int clientID;
    private Socket connection;
    private DataInputStream input;
    private DataOutputStream output;
    // 窗口名
    private static String winTitle = "Java聊撩";
    boolean InorNot = false; // 判断是否连接上服务器

    // 构造函数
    public Client(String ss) throws Exception, IOException {
        super(winTitle);
        Container con = getContentPane();
        username = ss;

        // 创建发送按钮
        putOut = new JButton("发送");
        putOut.setForeground(Color.black);
        putOut.setBackground(Color.green);

        // 最顶上的欢迎框架
        Icon bug = new ImageIcon("4.gif");
        labelgif = new JLabel(bug);
        label1 = new JLabel(ss + "，聊撩为您组局");
//        label1.setForeground(Color.BLUE);
        label1.setFont(new Font("黑体", Font.BOLD, 12));

        // 私聊框
        secret = new JTextField("输入对方id即可发车...", 12);
        secret.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (secret.getText().equals("输入对方id即可发车...")) {
                    secret.setText("");
                }
            }
        });

        // 发送信息显示框
        out_message = new JTextField("输入文本...", 60);
        out_message.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (out_message.getText().equals("聊撩吧！")) {
                    out_message.setText("");
                }
            }
        });

        // 显示接收信息框
        in_message = new JTextArea(30, 15);
        in_message.setEditable(false);

        // 创建布局和添加组件
        pN = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pS = new JPanel();
        pW = new JPanel();
        pE = new JPanel();

        pN.add(labelgif);
        // in_message添加到scroll中
        scroll = new JScrollPane(in_message);
        // pS 添加 发送框、发送按钮、私聊框
        pS.setLayout(new GridLayout(2, 1));
        pS.add(out_message);
        JPanel pSpanel = new JPanel();
        pSpanel.add(secret);
        pSpanel.add(putOut);
        pSpanel.add(label1);
        pS.add(pSpanel);

        // 注册监听器
        putOut.addActionListener(this);
        out_message.addActionListener(this);
        secret.addActionListener(new secretText());

        // 添加到容器
        con.add(pN, BorderLayout.NORTH);
        con.add(scroll, BorderLayout.CENTER);
        con.add(pS, BorderLayout.SOUTH);
        con.add(pW, BorderLayout.WEST);
        con.add(pE, BorderLayout.EAST);

        // 创建并启动接收信息线程
        Thread thread = new Thread(new Informaintion());
        thread.start();

        // 外观设置
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(600, 260, 800, 600);
        setVisible(true);
        validate();
    }

    // 创建一个固定大小为10的线程池
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    // 私聊文本框监听器
    private class secretText implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            // 获取私聊文本框内容
            messageGetout = secret.getText();
            // 判断是否为有效私聊用户
            if (!messageGetout.equals("输入ta的id即可发车！") && !messageGetout.equals("") && !messageGetout.equals("0")) {
                char sis[] = messageGetout.toCharArray();
                int havezimu = 0; // 判断有没有除数字以外的字符。0表示没有，1表示有；
                for (int x = 0; x < sis.length; x++) {
                    if (sis[x] < 48 || sis[x] > 57) // 0到9的ASCII值为48--57
                    {
                        havezimu = 1;
                        break;
                    }
                }
                if (havezimu == 0) {
                    siliao = true;
                    winTitle = "私聊中";
                    setTitle(winTitle);
                } else {
                    siliao = false;
                    in_message.append("请重新输入数字！\n");
                    secret.setText("输入ta的id即可发车！");
                    out_message.setText("");
                    winTitle = "群聊中";
                    setTitle(winTitle);
                }
            } else {
                siliao = false;
                secret.setText("输入ta的id即可发车！");
                winTitle = "群聊模式";
                setTitle(winTitle);
            }
        }
    }


    /**
     * 发送按钮和文本框的监听器
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        // 判断是否点击了发送按钮或按下了回车键，并且输入框内容不为空
        if ((e.getSource() == putOut || e.getSource() == out_message) && !out_message.getText().equals("")) {
            // 判断是否连接上了服务器
            if (InorNot) {
                if (siliao) { // 私聊模式
                    try {
                        String s = "*" + messageGetout + "*" + username + "：" + "#&。|。&#" + out_message.getText();
                        output.writeUTF(s);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    out_message.setText("");
                } else { // 群聊模式
                    try {
                        String s = " " + username + "：" + "#&。|。&#" + out_message.getText();
                        output.writeUTF(s);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    out_message.setText("");
                }
            } else {
                in_message.append("与服务器连接超时，" + "“" + out_message.getText() + "”" + "没有发送成功" + "\n");
            }
        }
    }


    /**
     * 接收信息线程
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
                label1.setText(username + "，你的编号是：" + clientID);
                InorNot = true;
            } catch (IOException ioException) {
                System.out.println("连接不上服务器");
                label1.setText(username + "，服务器连接超时了");
            }
            while (true) {
                try {
                    String message = new String(input.readUTF());
                    StringTokenizer min = new StringTokenizer(message, "#&。|。&#");
                    String str1 = min.nextToken();
                    String str2 = min.nextToken();
                    // 将接收信息的部分放入线程池执行
                    threadPool.execute(new HandleReceivedMessage(str1 + str2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 用于处理接收的信息
     */
    private class HandleReceivedMessage implements Runnable {
        private String receivedMessage;

        public HandleReceivedMessage(String message) {
            this.receivedMessage = message;
        }

        public void run() {
            // 在此处处理接收到的信息，更新界面等操作
            in_message.append(receivedMessage + "\n");
        }
    }

    // 主方法入口
    public static void main(String[] args) throws IOException, Exception {
        new Client("夜里一杯酒");
    }
}
