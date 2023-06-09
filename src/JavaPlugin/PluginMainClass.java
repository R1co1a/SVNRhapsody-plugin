
package JavaPlugin;

import javax.swing.JOptionPane;

import com.telelogic.rhapsody.core.*;

import JavaPlugin.Controller.Controller;

/**
 * The plug-in main class 
 */
public class PluginMainClass {
	
	private static IRPApplication rhpApp = null;
	
	//the plugin extended classes factory

	/**
	 * called when the plug-in is loaded
	 * @param rpyApplication
	 */
	public static void RhpPluginInit(final IRPApplication rpyApplication) {
		
		setRhpApp(rpyApplication);
		
	}
	
	/**
	 * called when the plug-in menu item inder the "Tools" menu is selected
	 */
	public static void RhpPluginInvokeItem() {
		Controller.init();
		Controller.execute();
		Controller.waitRhapsodyUpdate();
		Controller.showResults();
		Controller.reset();
	}
	
	/**
	 * called when the plug-in popup menu (if applicable) is selected
	 * @param cmd
	 */
	public static void OnMenuItemSelect(String cmd) {
		JOptionPane.showMessageDialog(null, "OnMenuItemSelected");	 //$NON-NLS-1$
	}
	
	/**
	 * called when the project is closed
	 */
	public static void RhpPluginCleanup(){
		PluginMainClass.setRhpApp(null);		
	}
	
	/**
	 * called when Rhapsody exits
	 */
	public static void RhpPluginFinalCleanup(){
		PluginMainClass.setRhpApp(null);
		
	}

	/**
	 * @return the rhapsody application
	 */
	public static IRPApplication getRhpApp() {
		return rhpApp;
	}

	/**
	 * @param rhpApp
	 */
	private static void setRhpApp(IRPApplication rhpApp) {
		PluginMainClass.rhpApp = rhpApp;
	}
	

}


























