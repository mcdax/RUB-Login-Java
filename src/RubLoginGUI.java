import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Color;

public class RubLoginGUI {

	private static JFrame frmRublogin;
	private JTextField idField;
	private JTextField timeOfRepingField;
	private JPasswordField passwordField;
	private Crypter crypter;
	private JLabel lblStatus;
	private RubLogin rubLogin;
	public boolean autoLoginActive = false;
	JTextArea textArea;
	JCheckBox chckbxAutomatischesEinloggendas;
	static TrayIcon trayIcon = null;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(RubLoginGUI.class.getResource("/icon/icon2.png"));
			ActionListener listener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frmRublogin.setVisible(true);
				}
			};
			PopupMenu popup = new PopupMenu();

			MenuItem defaultItem = new MenuItem("Öffnen");
			defaultItem.addActionListener(listener);
			popup.add(defaultItem);

			MenuItem endMenuItem = new MenuItem("Beenden");
			endMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			popup.add(endMenuItem);
			trayIcon = new TrayIcon(image, "RubLogin", popup);
			trayIcon.addActionListener(listener);

			try {
				tray.add(trayIcon);

			} catch (AWTException e) {
				System.err.println(e);
			}

		} else {

		}

		if (trayIcon != null) {
			trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(RubLoginGUI.class.getResource("/icon/icon2.png")));
		}

		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(laf.getName())) {
				try {
					UIManager.setLookAndFeel(laf.getClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				break;
			}
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new RubLoginGUI();
					RubLoginGUI.frmRublogin.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public RubLoginGUI() {
		initialize();
	}

	private void initialize() {
		crypter = new Crypter("L!T$g6z4".getBytes(), "p.3Ffg§!".getBytes());
		rubLogin = new RubLogin();

		frmRublogin = new JFrame();
		frmRublogin.setIconImage(Toolkit.getDefaultToolkit().getImage(RubLoginGUI.class.getResource("/icon/icon.png")));
		frmRublogin.setTitle("RubLogin");
		frmRublogin.setResizable(false);
		frmRublogin.setBounds(100, 100, 673, 207);
		frmRublogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRublogin.getContentPane().setLayout(null);

		idField = new JTextField();
		idField.setBounds(75, 11, 149, 28);
		frmRublogin.getContentPane().add(idField);
		idField.setColumns(10);

		JLabel lblLoginid = new JLabel("LoginID:");
		lblLoginid.setBounds(10, 17, 55, 16);
		frmRublogin.getContentPane().add(lblLoginid);

		JLabel lblPasswort = new JLabel("Passwort:");
		lblPasswort.setBounds(10, 56, 55, 16);
		frmRublogin.getContentPane().add(lblPasswort);

		final JCheckBox chckbxDatenSpeichern = new JCheckBox("Daten Speichern");
		chckbxDatenSpeichern.setBounds(75, 86, 122, 18);
		frmRublogin.getContentPane().add(chckbxDatenSpeichern);

		chckbxAutomatischesEinloggendas = new JCheckBox(
				"Automatisch Einloggen alle");
		chckbxAutomatischesEinloggendas.setBounds(254, 11, 187, 28);
		frmRublogin.getContentPane().add(chckbxAutomatischesEinloggendas);

		timeOfRepingField = new JTextField();
		timeOfRepingField.setText("30");
		timeOfRepingField.setToolTipText("");
		timeOfRepingField.setBounds(433, 11, 38, 28);
		frmRublogin.getContentPane().add(timeOfRepingField);
		timeOfRepingField.setColumns(10);

		JLabel lblMinuten = new JLabel("Minuten (30 -> Dauerhaft Online)");
		lblMinuten.setBounds(483, 17, 209, 16);
		frmRublogin.getContentPane().add(lblMinuten);

		passwordField = new JPasswordField();
		passwordField.setBounds(75, 51, 149, 28);
		frmRublogin.getContentPane().add(passwordField);

		int len;
		FileInputStream fis;
		DataInputStream dis = null;
		try {
			if (new File("sot.sav").exists()) {
				fis = new FileInputStream("sot.sav");
				dis = new DataInputStream(fis);
				len = dis.readInt();
				if (len != 0) {
					byte[] bi = new byte[100];
					dis.read(bi, 0, len);
					String readString = new String(crypter.decrypt(bi, len));
					String[] subStrings = readString.split("--_--");
					idField.setText(subStrings[0]);
					passwordField.setText(subStrings[1].trim());
				}
				chckbxDatenSpeichern.setSelected(true);
			}
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (ShortBufferException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			idField.setText("");
			passwordField.setText("");
		}finally{
			try {
				if(dis != null){
					dis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (idField.getText().isEmpty())
						throw new NoEntriesException("Keine LoginID eingegeben");
					final String loginID = idField.getText();
					if (passwordField.getPassword().length == 0)
						throw new NoEntriesException("Kein Passwort eingegeben");
					final String password = new String(passwordField
							.getPassword());

					String ip = "";

					FileOutputStream fos = null;
					DataOutputStream dos = null;
					if (chckbxDatenSpeichern.isSelected()) {
						dos = new DataOutputStream(new FileOutputStream("sot.sav"));
						String writeString = loginID.concat("--_--" + password);
						byte[] bo = crypter.encrypt(writeString.getBytes());
						int len = writeString.getBytes().length;
						len += 8 - len % 8;
						dos.writeInt(len);
						dos.write(bo);

					} else {
						fos = new FileOutputStream("sot.sav", false);
						dos = new DataOutputStream(fos);
						dos.writeInt(0);
					}
					if(fos != null) fos.close();
					if(dos != null) dos.close();
					
					ip = rubLogin.getIP();

					final String IP = ip;

					final SimpleDateFormat sdf = new SimpleDateFormat(
							"dd.MM.yyyy HH:mm");
					if (!IP.equals("")) {
						if (chckbxAutomatischesEinloggendas.isSelected()) {
							frmRublogin
									.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
							autoLoginActive = true;

							Thread autoLogin = new Thread(new Runnable() {

								String timeOfRepingString = timeOfRepingField
										.getText();
								int timeOfReping = Integer
										.valueOf(timeOfRepingString);

//								Date time = new Date();

								@Override
								public void run() {

									while (autoLoginActive) {
										try {
											String result = rubLogin.login(
													loginID, password, IP);
											trayIcon.displayMessage("RUBLogin",
													result, MessageType.INFO);
											textArea.setText(textArea
													.getText()
													.concat("["
															+ sdf.format(new Date())
															+ "] IP: " + IP
															+ "\n" + result
															+ "\n\n"));
											Thread.sleep(timeOfReping * 60 * 1000);

										} catch (InterruptedException e) {
											textArea.setText("ERROR" + e);

										} catch (IOException e) {
											textArea.setText("ERROR" + e);
											e.printStackTrace();
										}
									}

								}
							});
							autoLogin.start();

							lblStatus.setText("Automatischer Login läuft..");
						} else {
							autoLoginActive = false;
							lblStatus.setText("");

							String result;

							result = rubLogin.login(loginID, password, IP);
							textArea.setText(textArea.getText().concat(
									"[" + sdf.format(new Date()) + "] IP: "
											+ IP + "\n" + result + "\n\n"));
							if (result.contains("Verbindung wurde hergestellt")) {
								lblStatus
										.setText("Einmaliger Login erfolgreich");
							}
						}
					} else {
						textArea.setText(textArea
								.getText()
								.concat("["
										+ sdf.format(new Date())
										+ "]\nDeine IP-Adresse konnte nicht abgerufen werden, vergewissere dich, dass das Gerät am HIRN-Port angeschlossen ist."
										+ "\n\n"));
					}
				} catch (NoEntriesException e) {
					e.printStackTrace();
					lblStatus.setText(e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (InvalidAlgorithmParameterException e) {
					e.printStackTrace();
				} catch (ShortBufferException e) {
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
			}
		});
		btnLogin.setBounds(10, 116, 90, 28);
		frmRublogin.getContentPane().add(btnLogin);

		JButton btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				autoLoginActive = false;
				try {
					if (idField.getText().isEmpty())
						throw new NoEntriesException("Keine LoginID eingegeben");
					String loginID = idField.getText();
					if (passwordField.getPassword().length == 0)
						throw new NoEntriesException("Kein Passwort eingegeben");
					String password = new String(passwordField.getPassword());

					String ip = "";

					FileOutputStream fos = null;
					DataOutputStream dos = null;
					if (chckbxDatenSpeichern.isSelected()) {
						fos = new FileOutputStream("sot.sav");
						dos = new DataOutputStream(fos);
						String writeString = loginID.concat("--_--" + password);
						byte[] bo = crypter.encrypt(writeString.getBytes());
						int len = writeString.getBytes().length;
						len += 8 - len % 8;
						dos.writeInt(len);
						dos.write(bo);

					} else {
						fos = new FileOutputStream("sot.sav",false);
						dos = new DataOutputStream(fos);
						dos.writeInt(0);
					}
					if(fos != null) fos.close();
					if(dos != null) dos.close();

					ip = rubLogin.getIP();

					final String IP = ip;

					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
					if (!IP.equals("")) {

						{
							lblStatus.setText("");

							String result;

							result = rubLogin.logout(loginID, password, IP);
							textArea.setText(textArea.getText().concat(
									"[" + sdf.format(new Date()) + "] IP: "
											+ IP + "\n" + result + "\n\n"));
							if (result.contains("Logout erfolgreich")) {
								lblStatus.setText("Logout erfolgreich");
							}
						}
					} else {
						textArea.setText(textArea
								.getText()
								.concat("["
										+ sdf.format(new Date())
										+ "]\nDeine IP-Adresse konnte nicht abgerufen werden, vergewissere dich, dass das Gerät am HIRN-Port angeschlossen ist."
										+ "\n\n"));
					}
				} catch (NoEntriesException e) {
					e.printStackTrace();
					lblStatus.setText(e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (InvalidAlgorithmParameterException e) {
					e.printStackTrace();
				} catch (ShortBufferException e) {
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
			}
		});
		btnLogout.setBounds(133, 116, 90, 28);
		frmRublogin.getContentPane().add(btnLogout);

		lblStatus = new JLabel("");
		lblStatus.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		lblStatus.setForeground(Color.RED);
		lblStatus.setBounds(20, 143, 214, 34);
		frmRublogin.getContentPane().add(lblStatus);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(254, 52, 403, 120);
		frmRublogin.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
		textArea.setEditable(false);
	}

	@SuppressWarnings("serial")
	class NoEntriesException extends Exception {
		public NoEntriesException() {

		}

		public NoEntriesException(String s) {
			super(s);
		}
	}
}
