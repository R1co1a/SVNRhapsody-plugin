package JavaPlugin.UI.Dialog;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import JavaPlugin.Controller.Controller;

/**
 * @author adumez
 *
 */
public class PkgSelectionDialog extends JDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5039283719527606949L;
	
	/**
	 * @param owner
	 * @param options 
	 */
	public PkgSelectionDialog(Frame owner, String[] options) {
		super(owner, "Package Selection Dialog", true); //$NON-NLS-1$
		JLabel informationLabel = new JLabel("Please select desired package :"); //$NON-NLS-1$
		JComboBox<String> dropdown = new JComboBox<>(options);
		JButton selectButton = new JButton("OK"); //$NON-NLS-1$
        selectButton.addActionListener(e -> {
        	Controller.setSelectedPkg((String) dropdown.getSelectedItem());
            setVisible(false);
            dispose();
        });
		
		
        JPanel panel = new JPanel();
        panel.add(informationLabel);
        panel.add(dropdown, BorderLayout.CENTER);
        panel.add(selectButton, BorderLayout.SOUTH);
        setContentPane(panel);
        setSize(300, 110);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	
}
