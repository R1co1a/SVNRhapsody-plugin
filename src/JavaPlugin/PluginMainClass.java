
package JavaPlugin;

import java.awt.Dialog;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Queue;

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
	
	private static IRPClass projectCentralStakeholder;
	private static Deque<IRPModelElement> stakeholderQueue = new ArrayDeque<>();
	private static Deque<IRPDependency> valueLoopQueue = new ArrayDeque<>();
	private static Collection<ArrayList<IRPDependency>> dependencyValueLoops = new ArrayList<>();
	private static Collection<ArrayList<IRPModelElement>> stakeholderValueLoops = new ArrayList<>();
	private static Collection<String> altDependenciesNames = new ArrayList<>();
	private static ArrayList<Object[]> valueLoopData = new ArrayList<>();
	
	static double[][]  valuePathQuantificationArray = {{0.3, 0.5, 0.95},{0.2, 0.4, 0.8},{0.1, 0.2, 0.4}};
	
	private static Collection<IRPPackage> topPackage = new ArrayList<>();
	private static Collection<IRPActor> projectActors = new ArrayList<>();
	private static ArrayList<IRPClass> projectOrganisations = new ArrayList<>();
	private static Collection<IRPModelElement> projectStakeholders = new ArrayList<>();
	

	private static Deque<IRPModelElement> currentLoop = new ArrayDeque<>();
	static IRPModelElement firstStakeholder = null;
	static boolean foundLoop = false;

	private static DecimalFormat doubleFormat = new DecimalFormat("#.####");
	//the plugin extended classes factory
	
	//called when the plug-in is loaded
	/**
	 * @param rpyApplication
	 */
	public static void RhpPluginInit(final IRPApplication rpyApplication) {
		
		PluginMainClass.rhpApp = rpyApplication;
		PluginMainClass.rhpProject = rpyApplication.activeProject();
		
		Model.initModel();
		
		for (Object obj : rhpProject.getAllNestedElements().toList()) {
			if(obj.getClass() == RPPackage.class) {
				IRPPackage pkg = (IRPPackage)obj;
				topPackage.add(pkg);
			}
		}
		for(IRPPackage pkg : topPackage) {
			for (Object obj : pkg.getAllNestedElements().toList()) {
				if (obj.getClass() == RPActor.class) {
					IRPActor actor = (IRPActor)obj;
					projectActors.add(actor);
					projectStakeholders.add(actor);
				}else if (obj.getClass() == RPClass.class) {
					IRPClass org = (IRPClass)obj;
					projectOrganisations.add(org);
					projectStakeholders.add(org);
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
		
		projectCentralStakeholder = projectOrganisations.get(0);
		
		calculateValuePath(projectActors);
		calculateValuePath(projectOrganisations);
		searchLoops(projectCentralStakeholder);
		checkForMultipleDependencies();
		computeValueLoop();
		computeStakeholderValue();
		UserInterface.showResultDialog();
		
		//computeAllValueLoops();
        // Print all strings in one line	
		resetDiagram();
		
		UserInterface.HelloWorld();
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
	@SuppressWarnings("unchecked")
	public static void resetDiagram() {
		for(IRPActor actor: projectActors) {
			for (IRPDependency dependency : (Collection<IRPDependency>)actor.getDependencies().toList()) {
				dependency.changeTo("Lien");
				for (IRPTag tag : (Collection<IRPTag>)dependency.getLocalTags().toList()) {
					if (tag.getName().contentEquals("Valeur")) {
						tag.deleteFromProject();
					}
				}
			}
			actor.changeTo("Actor");
		}
		for (IRPClass organisation : projectOrganisations) {
			for (IRPDependency dependency : (Collection<IRPDependency>)organisation.getDependencies().toList()) {
				dependency.changeTo("Lien");
				for (IRPTag tag : (Collection<IRPTag>)dependency.getLocalTags().toList()) {
					if (tag.getName().contentEquals("Valeur")) {
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
	
	
	
	private static void checkForMultipleDependencies() {
		Collection<String> stakeholderBuffer = new ArrayList<>();
		
		for (ArrayList<IRPDependency> valueLoop : dependencyValueLoops) {
			
			for (IRPDependency dependency : valueLoop) {
				String stakeholder = dependency.getDependent().getName();
				
				if (!stakeholderBuffer.contains(stakeholder)) {
					stakeholderBuffer.add(stakeholder);
					
					Collection<String> targetBuffer = new ArrayList<>();
					Collection<String> dependenciesBuffer = new ArrayList<>();
					
					for (Object obj : dependency.getDependent().getDependencies().toList()) {
						IRPDependency dep = (IRPDependency)obj;
						
						if (targetBuffer.contains(dep.getDependsOn().getName())) {
							dependenciesBuffer.add(dep.getDependsOn().getName());
						}else {
							targetBuffer.add(dep.getDependsOn().getName());
						}
					}
					for (Object obj : dependency.getDependent().getDependencies().toList()) {
						IRPDependency dep = (IRPDependency)obj;
						if (dependenciesBuffer.contains(dep.getDependsOn().getName())) {
							altDependenciesNames.add(dep.getName());
						}
					}
				}
			}
		}
	}
	
	private static void searchLoops(IRPModelElement stakeholder) {
		stakeholderQueue.offerLast(stakeholder);

		for (Object obj : stakeholder.getDependencies().toList()) {
			
			IRPDependency dependency = (IRPDependency)obj;
			IRPModelElement nextStakeholder = dependency.getDependsOn();
			
			valueLoopQueue.offerLast(dependency);
			
			if(nextStakeholder.equals(projectCentralStakeholder)) {
				
				stakeholderQueue.offerLast(stakeholder);
				
				ArrayList<IRPDependency> tmpDependencyValueLoops = new ArrayList<>();
				ArrayList<IRPModelElement> tmpStakeholderValueLoops = new ArrayList<>();
				
				Collections.addAll(tmpDependencyValueLoops, valueLoopQueue.toArray(new IRPDependency[0]));
				Collections.addAll(tmpStakeholderValueLoops, stakeholderQueue.toArray(new IRPModelElement[0]));
				
				dependencyValueLoops.add(tmpDependencyValueLoops);
				stakeholderValueLoops.add(tmpStakeholderValueLoops);
				
				stakeholderQueue.removeLast();
				
			}else if (!nextStakeholder.getDependencies().toList().isEmpty() && !stakeholderQueue.contains(nextStakeholder)) {
				searchLoops(nextStakeholder);
			}
			valueLoopQueue.removeLast();
		}
		stakeholderQueue.removeLast();
	}
	
	@SuppressWarnings("boxing")
	private static void computeValueLoop() {
		
		for (ArrayList<IRPDependency> valueLoop : dependencyValueLoops) {
			Double loopValue = 1.0;
			String loopName = null;
			String firstLoopStakeholder = null;
			String loopDependencies = null;
			Collection<String> stakeholderList = new ArrayList<>();
			for (Object obj : valueLoop) {
				IRPDependency dependency = (IRPDependency)obj;
				
				stakeholderList.add(dependency.getDependent().getName());
				
				loopValue = loopValue * Double.parseDouble(dependency.getTag("Valeur").getValue());
				
				if (loopName == null) {
					loopName = dependency.getDependent().getName();
					firstLoopStakeholder = loopName;
				}else {
					loopName = loopName + "->" + dependency.getDependent().getName();
				}
				
				if (altDependenciesNames.contains(dependency.getName())) {
					if (loopDependencies == null) {
						loopDependencies = dependency.getName();
					}else {
						loopDependencies = loopDependencies + ", " + dependency.getName();
					}
				}
			}
			if (loopDependencies == null) {
				loopName = loopName + "->" + firstLoopStakeholder;
			}else {
				loopName = loopName + "->" + firstLoopStakeholder + " (" + loopDependencies + ") ";
			}
			
			Object[] row = { loopName, loopValue, stakeholderList };
			valueLoopData.add(row);

		}
	}
	
	private static void computeStakeholderValue() {
		Double sumAllValueLoops = 0.0;
		ArrayList<Stakeholder> stakeholders = new ArrayList<>();
		for (Object[] valueLoop : valueLoopData) {
			sumAllValueLoops = sumAllValueLoops + (Double)valueLoop[1];
		}
		for (IRPActor stakeholder : projectActors) {
			
			Double sumStakeholderValueLoops = 0.0;
			for (Object[] valueLoop : valueLoopData) {
				@SuppressWarnings("unchecked")
				ArrayList<String> stakeholderList = (ArrayList<String>)valueLoop[2];
				if (stakeholderList.contains(stakeholder.getName())) {
					sumStakeholderValueLoops = sumStakeholderValueLoops + (Double)valueLoop[1];
				}
			}
			
			if (sumAllValueLoops != 0.0) {
				Stakeholder row = new Stakeholder(stakeholder, sumStakeholderValueLoops / sumAllValueLoops);
				stakeholders.add(row);
			}
			
		}
		
		changeStakeholderColor(stakeholders);
	}
	
	private static void changeStakeholderColor(ArrayList<Stakeholder> stakeholders) {
		
		Collections.sort(stakeholders);
		Integer third = stakeholders.size() % 3;
		for (int i = 0; i < stakeholders.size(); i++) {
			if (i < third) {
				stakeholders.get(i).getItself().changeTo("Red_stackholder");
			}else if (i >= stakeholders.size() - third) {
				stakeholders.get(i).getItself().changeTo("Green_stakholder");
			}else {
				stakeholders.get(i).getItself().changeTo("Yellow_stakholder");
			}
				
		}
		
		
	}

}


























