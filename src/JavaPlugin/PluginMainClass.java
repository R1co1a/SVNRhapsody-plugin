
package JavaPlugin;

import java.awt.Dialog;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import com.telelogic.rhapsody.core.*;

enum Test {
	HIGH,
	MEDIUM,
	LOW
}
/**
 * The plug-in main class 
 */
public class PluginMainClass {
	
	static IRPApplication rhpApp = null;
	static IRPProject rhpProject = null;
	
	static double[][]  valuePathQuantificationArray = {{0.3, 0.5, 0.95},{0.2, 0.4, 0.8},{0.1, 0.2, 0.4}};
	
	private static Collection<IRPPackage> topPackage = new ArrayList<>();
	private static Collection<IRPActor> projectActors = new ArrayList<>();
	private static Collection<IRPClass> projectOrganisations = new ArrayList<>();
	private static Collection<IRPDependency> projectDependency = new ArrayList<>();
	//the plugin extended classes factory
	
	//called when the plug-in is loaded
	/**
	 * @param rpyApplication
	 */
	public static void RhpPluginInit(final IRPApplication rpyApplication) {
		
		PluginMainClass.rhpApp = rpyApplication;
		PluginMainClass.rhpProject = rpyApplication.activeProject();
		
		for (Object obj : rhpProject.getAllNestedElements().toList()) {
			if(obj.getClass() == RPPackage.class) {
				IRPPackage pkg = (IRPPackage)obj;
				topPackage.add(pkg);
			}
		}
		for(IRPPackage pkg : topPackage) {
			System.out.println(pkg.getName());
			for (Object obj : pkg.getAllNestedElements().toList()) {
				System.out.println(obj.getClass());
				if (obj.getClass() == RPActor.class) {
					IRPActor actor = (IRPActor)obj;
					projectActors.add(actor);
				}
				/*
				if (obj.getClass() == RPObjectModelDiagram.class) {
					IRPObjectModelDiagram diag = (IRPObjectModelDiagram)obj;
					IRPCollection image = diag.getPictureAs("\\\\utbm.local\\racine-dfs\\rep_perso_etudiants\\adumez\\Bureau\\DEBUG\\DEBUG\\ImageTest", "JPG", 0, null);
					BufferedImage test = null;
					try {
						test = ImageIO.read(new File("\\\\utbm.local\\racine-dfs\\rep_perso_etudiants\\adumez\\Bureau\\DEBUG\\DEBUG\\ImageTest"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					//ImageDialog dialog = new ImageDialog(null, test);
					//dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
					//dialog.setVisible(true);
					for (Object im : image.toList()) {
						String dir = (String)im;
						System.out.println(dir);
					}
					
				}*/
			}
		}
		calculateValuePath(projectActors);
		calculateValuePath(projectOrganisations);
		getValueLoops(projectActors);
		getValueLoops(projectOrganisations);
		//resetDiagram();
	}
	
	//called when the plug-in menu item under the "Tools" menu is selected
	/**
	 * 
	 */
	public static void RhpPluginInvokeItem() {
		JOptionPane.showMessageDialog(null, "Invoke");

	}
	
	//called when the plug-in popup menu (if applicable) is selected
	/**
	 * @param cmd
	 */
	public static void OnMenuItemSelect(String cmd) {
		JOptionPane.showMessageDialog(null, "OnMenuItemSelected");	
	}
	
	//called when the project is closed
	/**
	 * 
	 */
	public static void RhpPluginCleanup(){
				
	}
	
	//called when Rhapsody exits
	/**
	 * 
	 */
	public static void RhpPluginFinalCleanup(){
		PluginMainClass.rhpApp = null;
		
	}
	
	
	
	/**
	 * 
	 */
	public static void resetDiagram() {
		for(IRPActor actor: projectActors) {
			for (Object obj : actor.getDependencies().toList()) {
				IRPDependency dep = (IRPDependency)obj;
				dep.changeTo("Lien");
				for (Object obj2 : dep.getLocalTags().toList()) {
					IRPTag tag = (IRPTag)obj2;
					System.out.println(tag.getName());
					if (tag.getName().contentEquals("Valeur")) {
						System.out.println("uhgeighei");
						tag.deleteFromProject();
					}
				}
			}
		}
	}
	
	/**
	 * @param <T>
	 * @param stakeholders
	 */
	public static <T extends IRPModelElement> void calculateValuePath(Collection<T> stakeholders) {
		for(T stakeholder: stakeholders) {
			
			for (Object obj : stakeholder.getDependencies().toList()) {
				IRPDependency dependency = (IRPDependency)obj;
				
				int supplyImportance;
				int benefitRanking;
				
				switch (dependency.getTag("Supply_Importance").getValue()) {
				case "Low":
					supplyImportance = 2;
					break;
				case "Medium":
					supplyImportance = 1;
					break;
				case "High":
					supplyImportance = 0;
					break;
				default:
					supplyImportance = 1;
				}
				switch (dependency.getTag("Benefit_Ranking").getValue()) {
				case "Might_Be":
					benefitRanking = 0;
					break;
				case "Should_Be":
					benefitRanking = 1;
					break;
				case "Must_Be":
					benefitRanking = 2;
					break;
				default:
					benefitRanking = 1;
				}
				double valuePath = valuePathQuantificationArray[supplyImportance][benefitRanking];
				if (valuePath < 0.3) {
					dependency.changeTo("Green_dependency");
				}else if (valuePath > 0.4) {
					dependency.changeTo("Red_dependency");
				}else {
					dependency.changeTo("Yellow_dependency");
				}
				dependency.setTagValue(dependency.getTag("Valeur"), Double.toString(valuePath));
				
			}
			
		}
	
	}
	
	/**
	 * @param <T>
	 * @param stakeholders
	 */
	public static <T extends IRPModelElement> void getValueLoops(Collection<T> stakeholders) {
		IRPDependency[] dependencyCheckList = new Dependency[];
		for (T stakeholder : stakeholders) {
			for (Object obj : stakeholder.getDependencies().toList()) {
				IRPDependency dependency = (IRPDependency)obj;
				System.out.println(dependency.getDependent().getName());
				System.out.println(dependency.getDependsOn().getName());
			}
		}
	}

}
