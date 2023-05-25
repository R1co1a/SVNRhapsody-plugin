package JavaPlugin;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageDialog extends JDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7633590706245162829L;
	private JLabel imageLabel;
	private BufferedImage image;
	
	public ImageDialog(Frame owner, BufferedImage image) {
		super(owner, "Image Dialog", true);
		this.image = image;
		imageLabel = new JLabel(new ImageIcon(image));
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveImage();
			}
		});
		fileMenu.add(saveMenuItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		toolbar.add(menuBar);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(toolbar, BorderLayout.NORTH);
        contentPane.add(imageLabel, BorderLayout.CENTER);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
        fileChooser.setFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            try {
                ImageIO.write(image, "png", new File(filePath));
                JOptionPane.showMessageDialog(this, "Image saved successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
        
}
