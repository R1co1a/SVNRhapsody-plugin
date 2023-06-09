package JavaPlugin.UI.Dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

/**
 * @author adumez
 *
 */
public class ResultDigramDialog extends JDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7633590706245162829L;
	private JLabel imageLabel;
	private BufferedImage image;
	
	/**
	 * @param owner
	 * @param image
	 */
	public ResultDigramDialog(Frame owner, BufferedImage image) {
		super(owner, "Image Dialog", true); //$NON-NLS-1$
		this.image = image;
		this.imageLabel = new JLabel(new ImageIcon(image));
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		JMenu fileMenu = new JMenu("File"); //$NON-NLS-1$
		JMenuItem saveMenuItem = new JMenuItem("Save"); //$NON-NLS-1$
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
        contentPane.add(this.imageLabel, BorderLayout.CENTER);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
    void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png"); //$NON-NLS-1$ //$NON-NLS-2$
        fileChooser.setFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            try {
                ImageIO.write(this.image, "png", new File(filePath)); //$NON-NLS-1$
                JOptionPane.showMessageDialog(this, "Image saved successfully!"); //$NON-NLS-1$
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
        
}
