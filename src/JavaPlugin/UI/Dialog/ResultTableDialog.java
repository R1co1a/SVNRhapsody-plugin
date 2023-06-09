package JavaPlugin.UI.Dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * @author adumez
 *
 */
public class ResultTableDialog extends JDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2641858324334577025L;

	/**
	 * @param owner
	 * @param data
	 * @param header
	 */
	public ResultTableDialog(Frame owner, Object[][] data, String[] header) {
		super(owner, "Table Dialog", true); //$NON-NLS-1$
		

		
		DefaultTableModel model = new DefaultTableModel(data, header);
		
		JTable table = new JTable(model);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		resizeColumnWidth(table);
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		JMenu fileMenu = new JMenu("File"); //$NON-NLS-1$
		JMenuItem saveMenuItem = new JMenuItem("Save"); //$NON-NLS-1$
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableAsImage(table);
			}
		});
		fileMenu.add(saveMenuItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		toolbar.add(menuBar);
		

		JScrollPane scrollPane = new JScrollPane(table);
		
		//scrollPane.setMaximumSize(table.getPreferredSize());
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(toolbar, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	void tableAsImage(JTable table) {
		SwingUtilities.invokeLater(() -> {
			table.revalidate();
			table.repaint();
			
            BufferedImage image = new BufferedImage(table.getWidth(), table.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();

            // Print the table onto the image
            table.print(graphics);
            saveTable(image);
		});
	}
	private static void resizeColumnWidth(JTable table) {
	    final TableColumnModel columnModel = table.getColumnModel();
	    for (int column = 0; column < table.getColumnCount(); column++) {
	        int width = 30; // Min width
	        for (int row = 0; row < table.getRowCount(); row++) {
	            TableCellRenderer renderer = table.getCellRenderer(row, column);
	            Component comp = table.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        
	        if(width > 300)
	            width=300;
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}
	
    void saveTable(BufferedImage image) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png"); //$NON-NLS-1$ //$NON-NLS-2$
        fileChooser.setFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            try {
                ImageIO.write(image, "png", new File(filePath)); //$NON-NLS-1$
                JOptionPane.showMessageDialog(this, "Image saved successfully!"); //$NON-NLS-1$
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
}
