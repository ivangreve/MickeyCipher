package Mickey;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class Mickey {

	private JFrame frame;
	private JTextField textField;
	private File imageFileToEncode;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Mickey window = new Mickey();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Mickey() {
		initialize();
        
	}
	
	private byte[] imageToByteArray(File imageToConvert) {
		
		try {
			
			return Files.readAllBytes(imageToConvert.toPath());

		}catch(IOException e) {
			return null;
		}
		
	}
	
	private void bytesArrayToImg(byte[] arr) {
		String pathImgFile = "C://koko.jpg";
		try {			
			InputStream in = new ByteArrayInputStream(arr);
			BufferedImage bImageFromConvert = ImageIO.read(in);
			ImageIO.write(bImageFromConvert, "jpg", new File(pathImgFile));
			BufferedImage image = ImageIO.read(new File(pathImgFile));

		    
		}catch(IOException e) {
		   e.printStackTrace();
		}

	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 627, 551);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Previsualizaci\u00F3n de imagen");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(48, 90, 481, 191);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblNingnArchivoSeleccionado = new JLabel("Ning\u00FAn archivo seleccionado");
		lblNingnArchivoSeleccionado.setBounds(210, 60, 319, 14);
		frame.getContentPane().add(lblNingnArchivoSeleccionado);
		
		
		JButton btnAbrirArchivo = new JButton("Seleccionar archivo...");
		btnAbrirArchivo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();

				   if(fileChooser.showOpenDialog(frame) ==  JFileChooser.APPROVE_OPTION) {
					   imageFileToEncode = fileChooser.getSelectedFile();
					   BufferedImage img;
					   lblNingnArchivoSeleccionado.setText(imageFileToEncode.getName());
					   try {
						img = ImageIO.read(imageFileToEncode);
						Image image = img.getScaledInstance(lblNewLabel.getWidth(), lblNewLabel.getHeight(), BufferedImage.SCALE_DEFAULT);
						ImageIcon icon = new ImageIcon(image);
						lblNewLabel.setIcon(icon);
							   
					} catch (Exception e1) {
						lblNewLabel.setIcon(null);
					}
			            
				      } else {
				         System.out.println("Ning�n archivo fue seleccionado!");
				      };
					 		
			}
		});
		btnAbrirArchivo.setBounds(32, 56, 137, 23);
		frame.getContentPane().add(btnAbrirArchivo);
		
		
		JButton button = new JButton("Cifrar / Descifrar...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				   if(fileChooser.showSaveDialog(frame) ==  JFileChooser.APPROVE_OPTION) {
					   final int BITMAP_START = 154;
					   File fileToSave = fileChooser.getSelectedFile();
					   byte [] b = imageToByteArray(imageFileToEncode);
					   int [] cipherBytes = new int [b.length - BITMAP_START]; //Integer.
					   int [] headerBytes = new int [BITMAP_START]; //Integer.
					   for (int i = 0; i < BITMAP_START; i++) {
						   headerBytes[i] = b[i];
					   }
					   for (int i = BITMAP_START; i < b.length; i++) {
						   cipherBytes[i - BITMAP_START] = b[i];
					   }
					   
					   String key = textField.getText().substring(0, 10);
					   System.out.println(key);
					   String iv = textField.getText().substring(10);
					   
					   int [] keyBytes = new int [key.length()];
					   for (int i = 0; i < key.length(); i++) {
						   keyBytes[i] = key.charAt(i);
					   }
					   
					   int [] ivBytes = new int [iv.length()];
					   for (int i = 0; i < iv.length(); i++) {
						   ivBytes[i] = iv.charAt(i);
					   }
					   
					   MickeyCipher cipher = new MickeyCipher(keyBytes, ivBytes);
					   int[] fileContent = cipher.encrypt(cipherBytes);
					   byte[] array = new byte[fileContent.length + headerBytes.length] ;
					   for (int i = 0; i < headerBytes.length; i++) {
						   array[i] = (byte) headerBytes[i];
					   }
					   for (int i = 0; i < fileContent.length; i++) {
						   array[i + headerBytes.length] = (byte) fileContent[i];
					   }
					   try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
						   fos.write(array);
					   } catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Save as file: " + fileToSave.getAbsolutePath());
				   }
					 						
			}
		});
		button.setBounds(210, 460, 137, 23);
		frame.getContentPane().add(button);
		
		textField = new JTextField();
		textField.setBounds(210, 316, 320, 23);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblClaveCaracteres = new JLabel("Clave (8-16 caracteres):");
		lblClaveCaracteres.setBounds(32, 320, 137, 14);
		frame.getContentPane().add(lblClaveCaracteres);
		
        ImageIcon image = new ImageIcon(new ImageIcon("C://koko.jpg").getImage().getScaledInstance(frame.getWidth(), frame.getHeight(),
                Image.SCALE_DEFAULT));
		

	}
}
