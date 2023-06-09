package JavaPlugin.UI.Dialog;


import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import JavaPlugin.UI.UserInterface;

/**
 * @author adumez
 *
 */
public class ResetDialog extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 660691614952295940L;
	
	/**
	 * @param owner
	 */
	public ResetDialog(Frame owner) {
		super(owner, "Reset Dialog", true); //$NON-NLS-1$
		JLabel informationLabel = new JLabel("The diagram will now be reset"); //$NON-NLS-1$
        JCheckBox checkBox = new JCheckBox("do not show this again"); //$NON-NLS-1$

        JButton button = new JButton("OK"); //$NON-NLS-1$
        button.addActionListener(e -> {
            UserInterface.setShouldShowResetDialog(!checkBox.isSelected());
            setVisible(false);
            dispose();
        });
        
        JPanel panel = new JPanel();
        panel.add(informationLabel);
        panel.add(checkBox);
        panel.add(button);
        setContentPane(panel);
        setSize(220, 130);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
}
