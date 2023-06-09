package JavaPlugin.Controller;

import java.io.File;
import java.util.ArrayList;

import com.telelogic.rhapsody.core.IRPPackage;

import JavaPlugin.PluginMainClass;
import JavaPlugin.Model.Model;
import JavaPlugin.UI.UserInterface;

/**
 * @author adumez
 * The controller class handles the logic and flow of the plugin
 */
public final class Controller {
	
	private static String selectedPkg;
	
	
	/**
	 * Initialize the plugin
	 */
	public static void init() {
		createTmpFolder();
		Model.getPackages();
		UserInterface.showStartPluginDialog();
	}
	/**
	 * Executes the analysis of the diagram
	 * @param selectedPkg The selected package for analysis
	 */
	public static void execute() {
		for (IRPPackage pkg : Model.getTopPackage()) {
			if (pkg.getName().equals(selectedPkg)) {
				Model.analyzeDiagram(pkg);
				break;
			}
		}
	}
	
	/**
	 * Displays the results of the plugin
	 */
	public static void showResults() {
		for (IRPPackage pkg : Model.getTopPackage()) {
			if (pkg.getName().equals(selectedPkg)) {
				UserInterface.showResultDialog(pkg,convertArrayToObject(Model.getValuePathDataTable()), convertArrayToObject(Model.getValueLoopDataTable()), convertArrayToObject(Model.getStakeholderDataTable()));
				break;
			}
		}
	}
	
	/**
	 * Waits for Rhapsody to update
	 */
	public static void waitRhapsodyUpdate() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets the Model and the diagram
	 */
	public static void reset() {
		UserInterface.showResetDialog();
		Model.resetDiagram();
		Model.resetModel();
	}
	
	/**
	 * Sets the selected package for analysis
	 * @param pkg 
	 */
	public static void setSelectedPkg(String pkg) {
		selectedPkg = pkg;
		
	}
	
	/**
	 * Converts an ArrayList of Objects arrays to a 2D Object array
	 * @param arrayList The ArrayList to convert
	 * @return The converted 2D Object array.
	 */
	public static Object[][] convertArrayToObject(ArrayList<Object[]> arrayList){
        Object[][] objectArray = new Object[arrayList.size()][];
        for (int i = 0; i < arrayList.size(); i++) {
            objectArray[i] = arrayList.get(i);
        }
		return objectArray;
		
	}
	
	/**
	 * Creates a temporary folder for the plugin.
	 */
	public static void createTmpFolder() {
		
		String folderPath = new File("").getAbsolutePath(); //$NON-NLS-1$
		File folder = new File(folderPath + "\\Tmp"); //$NON-NLS-1$
		if (!folder.exists()) {
			boolean success = folder.mkdir();
			if (!success) {
				PluginMainClass.getRhpApp().writeToOutputWindow("Log", "Failed to create the temporary folder (Tmp) execution aborted"); //$NON-NLS-1$ //$NON-NLS-2$
			}
				
		}

	}
	
	private Controller(){}
}
