package JavaPlugin.UI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Dialog;
import javax.imageio.ImageIO;

import com.telelogic.rhapsody.core.IRPObjectModelDiagram;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.RPObjectModelDiagram;

import JavaPlugin.Model.Model;
import JavaPlugin.UI.Dialog.PkgSelectionDialog;
import JavaPlugin.UI.Dialog.ResetDialog;
import JavaPlugin.UI.Dialog.ResultDigramDialog;
import JavaPlugin.UI.Dialog.ResultTableDialog;

/**
 * @author R1co1a
 *
 */
public class UserInterface {
	
	private static boolean shouldShowResetDialog = true;
	
	/**
	 * @param pkg 
	 * @param valuePathData 
	 * @param valueLoopData 
	 * @param stakeholderData 
	 * 
	 */
	public static void showResultDialog(IRPPackage pkg,Object[][] valuePathData, Object[][] valueLoopData, Object[][] stakeholderData) {
		String relativePath = new File("").getAbsolutePath(); //$NON-NLS-1$
		String filePath = relativePath.concat("\\Tmp"); //$NON-NLS-1$
		
		final String[] valuePathHeader = { "No.",  "Path Description", "Value Description", "Benefit Ranking", "Supply Importance", "Score" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		final String[] valueLoopHeader = { "No.", "Loop Description", "Score"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		final String[] stakeholderHeader = { "Rank", "Stakeholder", "Score"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		ResultTableDialog valuePathDialog = new ResultTableDialog(null, valuePathData, valuePathHeader);
		ResultTableDialog valueLoopDialog = new ResultTableDialog(null, valueLoopData, valueLoopHeader);
		ResultTableDialog stakeholderDialog = new ResultTableDialog(null, stakeholderData, stakeholderHeader);
		
		valuePathDialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		valueLoopDialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		stakeholderDialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		
		valuePathDialog.setVisible(true);
		valueLoopDialog.setVisible(true);
		stakeholderDialog.setVisible(true);
		
		for (Object obj : pkg.getAllNestedElements().toList()) {
			if (obj.getClass() == RPObjectModelDiagram.class) {
				IRPObjectModelDiagram diag = (IRPObjectModelDiagram)obj;
				
				// necessary to refresh the diagram
				diag.closeDiagram();
				diag.openDiagram();
				
				diag.getPictureAs(filePath + "\\TmpImageDiag", "JPG", 0, null); //$NON-NLS-1$ //$NON-NLS-2$

				BufferedImage image = null;
				try {
					image = ImageIO.read(new File(filePath + "\\TmpImageDiag")); //$NON-NLS-1$
				} catch (IOException e) {
					e.printStackTrace();
				}
				ResultDigramDialog dialog = new ResultDigramDialog(null,image);
				dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
				dialog.setVisible(true);
					
			}
		}
	}
	
	/**
	 * @param pkg 
	 * 
	 */
	public static void showStartPluginDialog() {
		ArrayList<String> options = new ArrayList<>();
		for (IRPPackage pkg: Model.getTopPackage()) {
			options.add(pkg.getName());
		}
		PkgSelectionDialog dialog = new PkgSelectionDialog(null, options.toArray(new String[options.size()]));
		dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		dialog.setVisible(true);
	}
	
	/**
	 * 
	 */
	public static void showResetDialog() {
		if (shouldShowResetDialog) {
			ResetDialog dialog = new ResetDialog(null);
			dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
			dialog.setVisible(true);
		}
	}
	/**
	 * @param bool 
	 */
	public static void setShouldShowResetDialog(boolean bool) {
		shouldShowResetDialog = bool;
		
	}
}
