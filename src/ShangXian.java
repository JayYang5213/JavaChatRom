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

	// ���캯��
	ShangXian() {
		super("Java����"); // ���ô��ڱ���
		Container con = getContentPane(); // ��ȡ�������
		con.setLayout(new FlowLayout()); // ���ò���Ϊ��ʽ����

		label1 = new JLabel("�û�����"); // ������ǩ
		userName = new JTextField("--�������û���--"); // �����ı������
		userName.addMouseListener(new MouseAdapter() {
			// Ϊ�û����ı���������������¼�������
			public void mouseClicked(MouseEvent arg0) {
				if (userName.getText().equals("--�������û���--")) {
					userName.setText(""); // ������û����ı������ʱ������ı�����Ϊ"--�������û���--"��������ı�������
				}
			}
		});

		getIn = new JButton("����"); // ����"����"��ť
		getIn.addActionListener(this); // Ϊ��ť��Ӷ����¼�������
		panel = new JPanel(); // �������
		panel.setLayout(new FlowLayout()); // ������岼��Ϊ��ʽ����
		panel.add(label1); // ����ǩ��ӵ����
		panel.add(userName); // ���û����ı��������ӵ����
		panel.add(getIn); // ��"����"��ť��ӵ����
		con.add(panel); // �������ӵ��������

		setBounds(800, 300, 400, 160); // ���ô��ڴ�С��λ��
		setResizable(false); // ��ֹ�������ڴ�С
		setVisible(true); // ���ô��ڿɼ�
		validate(); // ��֤���������νṹ
	}

	// "����"��ť�����¼�����������
	public void actionPerformed(ActionEvent arg0) {
		if (!userName.getText().equals("") && !userName.getText().equals("--�������û���--")) {
			// ���û�����Ϊ���Ҳ�Ϊ"--�������û���--"ʱ������Client����
			try {
				Client client = new Client(userName.getText());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			setVisible(false); // ���ص�ǰ����
		}
	}

	// ���������
	public static void main(String[] args) {
		new ShangXian(); // ����ShangXian����
	}
}
