import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import javax.swing.*;

public class ShangXian extends JFrame implements ActionListener {
	private JLabel label1;
	private JTextField userName;
	private JButton getIn;
	private JPanel panel;

	// 构造函数
	ShangXian() {
		super("Java聊撩"); // 设置窗口标题
		Container con = getContentPane(); // 获取内容面板
		con.setLayout(new FlowLayout()); // 设置布局为流式布局

		label1 = new JLabel("用户名："); // 创建标签
		userName = new JTextField("--请输入用户名--"); // 创建文本输入框
		userName.addMouseListener(new MouseAdapter() {
			// 为用户名文本输入框添加鼠标点击事件监听器
			public void mouseClicked(MouseEvent arg0) {
				if (userName.getText().equals("--请输入用户名--")) {
					userName.setText(""); // 当点击用户名文本输入框时，如果文本内容为"--请输入用户名--"，则清空文本框内容
				}
			}
		});

		getIn = new JButton("上线"); // 创建"上线"按钮
		getIn.addActionListener(this); // 为按钮添加动作事件监听器
		panel = new JPanel(); // 创建面板
		panel.setLayout(new FlowLayout()); // 设置面板布局为流式布局
		panel.add(label1); // 将标签添加到面板
		panel.add(userName); // 将用户名文本输入框添加到面板
		panel.add(getIn); // 将"上线"按钮添加到面板
		con.add(panel); // 将面板添加到内容面板

		setBounds(800, 300, 400, 160); // 设置窗口大小和位置
		setResizable(false); // 禁止调整窗口大小
		setVisible(true); // 设置窗口可见
		validate(); // 验证容器组件层次结构
	}

	// "上线"按钮动作事件监听器方法
	public void actionPerformed(ActionEvent arg0) {
		if (!userName.getText().equals("") && !userName.getText().equals("--请输入用户名--")) {
			// 当用户名不为空且不为"--请输入用户名--"时，创建Client对象
			try {
				Client client = new Client(userName.getText());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			setVisible(false); // 隐藏当前窗口
		}
	}

	// 主方法入口
	public static void main(String[] args) {
		new ShangXian(); // 创建ShangXian对象
	}
}
