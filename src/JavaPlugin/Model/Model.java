package JavaPlugin.Model;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

import com.telelogic.rhapsody.core.IRPActor;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.RPActor;
import com.telelogic.rhapsody.core.RPClass;
import com.telelogic.rhapsody.core.RPPackage;

import JavaPlugin.PluginMainClass;

/**
 * @author R1co1a
 * The Model of the Plugin contains the logic
 */

@SuppressWarnings("unchecked")
public final class Model {
	
	// Project
	private static IRPProject rhpProject = PluginMainClass.getRhpApp().activeProject();
	
	// Package
	private static ArrayList<IRPPackage> topPackage = new ArrayList<>();
	
	// Diagram elements
	private static Collection<IRPModelElement> projectStakeholders = new ArrayList<>();
	private static Collection<IRPActor> projectActors = new ArrayList<>();
	private static ArrayList<IRPClass> projectOrganisations = new ArrayList<>();
	
	// ValueLoops
	private static Collection<ArrayList<IRPDependency>> dependencyValueLoops = new ArrayList<>();
	private static Collection<ArrayList<IRPModelElement>> stakeholderValueLoops = new ArrayList<>();
	
	// Queue
	private static Deque<IRPModelElement> stakeholderQueue = new ArrayDeque<>();
	private static Deque<IRPDependency> valueLoopQueue = new ArrayDeque<>();
	
	// Tables
	private static ArrayList<Object[]> valueLoopDataTable = new ArrayList<>();
	private static ArrayList<Object[]> valuePathDataTable = new ArrayList<>();
	private static ArrayList<Object[]> stakeholderDataTable = new ArrayList<>();
	
	// Utils
	private static Collection<String> altDependenciesNames = new ArrayList<>();
	private static ArrayList<Object[]> valueLoopData = new ArrayList<>();
	private static double[][] valuePathQuantificationArray = {{0.3, 0.5, 0.95},{0.2, 0.4, 0.8},{0.1, 0.2, 0.4}};
	private static IRPClass projectCentralStakeholder;
	private static DecimalFormat decimalFormat = new DecimalFormat("#0.00"); //$NON-NLS-1$
	
	/**
	 * Retrieves top level packages of the project
	 */
	public static void getPackages() {
		for (Object obj : rhpProject.getAllNestedElements().toList()) {
			if(obj.getClass() == RPPackage.class) {
				IRPPackage pkg = (IRPPackage)obj;
				topPackage.add(pkg);
			}
		}
	}
	
	/**
	 * Retrieves all the diagram elements
	 * @param pkg The selected package the be analysis
	 */
	private static void initModel(IRPPackage pkg) {
		for (Object obj : pkg.getAllNestedElements().toList()) {
			if (obj.getClass() == RPActor.class) {
				IRPActor actor = (IRPActor)obj;
				projectActors.add(actor);
				projectStakeholders.add(actor);
			} else if (obj.getClass() == RPClass.class) {
				IRPClass org = (IRPClass)obj;
				projectOrganisations.add(org);
				projectStakeholders.add(org);			}
		}
		projectCentralStakeholder = projectOrganisations.get(0);
	}
	
	/** 
	 * @return The top level packages of the project
	 */
	public static ArrayList<IRPPackage> getTopPackage(){
		return  topPackage;
	}
	
	/**
	 * Resets all the model resources 
	 */
	public static void resetModel() {
		projectStakeholders.clear();
		projectActors.clear();
		projectOrganisations.clear();
		dependencyValueLoops.clear();
		stakeholderValueLoops.clear();
		stakeholderQueue.clear();
		stakeholderQueue.clear();
		valueLoopData.clear();
		getValueLoopDataTable().clear();
		getValuePathDataTable().clear();
		getStakeholderDataTable().clear();
		altDependenciesNames.clear();
	}
	
	/**
	 * Resets all the diagram elements
	 */
	public static void resetDiagram() {
		
		for (IRPActor actor: projectActors) {
			for (IRPDependency dependency : (Collection<IRPDependency>)actor.getDependencies().toList()) {
				dependency.changeTo("Lien"); //$NON-NLS-1$
				for (IRPTag tag : (Collection<IRPTag>)dependency.getLocalTags().toList()) {
					if (tag.getName().contentEquals("Valeur")) { //$NON-NLS-1$
						tag.deleteFromProject();
					}
				}
			}
			actor.changeTo("Actor"); //$NON-NLS-1$
		}
		for (IRPClass organisation : projectOrganisations) {
			for (IRPDependency dependency : (Collection<IRPDependency>)organisation.getDependencies().toList()) {
				dependency.changeTo("Lien"); //$NON-NLS-1$
				for (IRPTag tag : (Collection<IRPTag>)dependency.getLocalTags().toList()) {
					if (tag.getName().contentEquals("Valeur")) { //$NON-NLS-1$
						tag.deleteFromProject();
					}
				}
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
	
	@SuppressWarnings("boxing")
	private static void changeStakeholderColor(ArrayList<Stakeholder> stakeholders) {
		
		Integer third = stakeholders.size() % 3;
		for (int i = 0; i < stakeholders.size(); i++) {
			if (i < third) {
				stakeholders.get(i).getItself().changeTo("Red_stakeholder"); //$NON-NLS-1$
			}else if (i >= stakeholders.size() - third) {
				stakeholders.get(i).getItself().changeTo("Green_stakeholder"); //$NON-NLS-1$
			}else {
				stakeholders.get(i).getItself().changeTo("Yellow_stakeholder"); //$NON-NLS-1$
			}
				
		}
		
		
	}
	
	/**
	 * Calculate all the value paths of the SVN
	 * @param stakeholders the list of stakeholders of the diagram
	 */
	@SuppressWarnings("boxing")
	private static void calculateValuePath(Collection<IRPModelElement> stakeholders) {
		int index = 0;
		for (IRPModelElement stakeholder: stakeholders) {
			for (IRPDependency dependency : (Collection<IRPDependency>)stakeholder.getDependencies().toList()) {
				int supplyImportance;
				int benefitRanking;
				
				switch (dependency.getTag("Supply_Importance").getValue()) { //$NON-NLS-1$
				case "Low": //$NON-NLS-1$
					supplyImportance = 2;
					break;
				case "Medium": //$NON-NLS-1$
					supplyImportance = 1;
					break;
				case "High": //$NON-NLS-1$
					supplyImportance = 0;
					break;
				default: 
					supplyImportance = 1;
				}
				
				switch (dependency.getTag("Benefit_Ranking").getValue()) { //$NON-NLS-1$
				case "Might_Be": //$NON-NLS-1$
					benefitRanking = 0;
					break;
				case "Should_Be": //$NON-NLS-1$
					benefitRanking = 1;
					break;
				case "Must_Be": //$NON-NLS-1$
					benefitRanking = 2;
					break;
				default:
					benefitRanking = 1;
				}
				
				double valuePath = valuePathQuantificationArray[supplyImportance][benefitRanking];
				String valueTagName;
				if (valuePath == 0.2) {
					dependency.changeTo("Green_dependency"); //$NON-NLS-1$
					valueTagName = "Valeur_G"; //$NON-NLS-1$
				}else if (valuePath == 0.5 || valuePath == 0.8) {
					dependency.changeTo("Red_dependency"); //$NON-NLS-1$
					valueTagName = "Valeur_R"; //$NON-NLS-1$
				}else if (valuePath == 0.4 || valuePath == 0.3) {
					dependency.changeTo("Yellow_dependency"); //$NON-NLS-1$
					valueTagName = "Valeur_Y"; //$NON-NLS-1$
				}else if (valuePath == 0.95){
					dependency.changeTo("Purple_dependency"); //$NON-NLS-1$
					valueTagName = "Valeur_P"; //$NON-NLS-1$
				}else {
					dependency.changeTo("Blue_dependency"); //$NON-NLS-1$
					valueTagName = "Valeur_B"; //$NON-NLS-1$
				}
				dependency.setTagValue(dependency.getTag(valueTagName), Double.toString(valuePath));
				index++;
				Object[] tableRow = { index, dependency.getDependent().getName() + " to " + dependency.getDependsOn().getName(), dependency.getName(), dependency.getTag("Benefit_Ranking").getValue(), dependency.getTag("Supply_Importance").getValue(), dependency.getTag(valueTagName).getValue() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				getValuePathDataTable().add(tableRow);
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
				String ValueTag = null;
				for (Object obj2 : dependency.getLocalTags().toList()) {
					IRPTag tag = (IRPTag)obj2;
					switch(tag.getName()) {
					case "Valeur_Y" :  //$NON-NLS-1$
						ValueTag = "Valeur_Y"; //$NON-NLS-1$
						break;
					case "Valeur_R" : //$NON-NLS-1$
						ValueTag = "Valeur_R"; //$NON-NLS-1$
						break;
					case "Valeur_G" : //$NON-NLS-1$
						ValueTag = "Valeur_G"; //$NON-NLS-1$
						break;
					case "Valeur_P" : //$NON-NLS-1$
						ValueTag = "Valeur_P"; //$NON-NLS-1$
						break;
					case "Valeur_B" : //$NON-NLS-1$
						ValueTag = "Valeur_B"; //$NON-NLS-1$
						break;
					default:
						continue;
					}
				}
				
				loopValue = loopValue * Double.parseDouble(dependency.getTag(ValueTag).getValue());
				
				if (loopName == null) {
					loopName = dependency.getDependent().getName();
					firstLoopStakeholder = loopName;
				}else {
					loopName = loopName + "->" + dependency.getDependent().getName(); //$NON-NLS-1$
				}
				
				if (altDependenciesNames.contains(dependency.getName())) {
					if (loopDependencies == null) {
						loopDependencies = dependency.getName();
					}else {
						loopDependencies = loopDependencies + ", " + dependency.getName(); //$NON-NLS-1$
					}
				}
			}
			if (loopDependencies == null) {
				loopName = loopName + "->" + firstLoopStakeholder; //$NON-NLS-1$
			}else {
				loopName = loopName + "->" + firstLoopStakeholder + " (" + loopDependencies + ") "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
			Object[] row = { loopName, loopValue, stakeholderList };
			Object[] tableRow = { getValueLoopDataTable().size() + 1, loopName, decimalFormat.format(loopValue) };
			valueLoopData.add(row);
			getValueLoopDataTable().add(tableRow);
		}
	}
	
	@SuppressWarnings("boxing")
	private static void computeStakeholderValue() {
		Double sumAllValueLoops = 0.0;
		ArrayList<Stakeholder> stakeholders = new ArrayList<>();
		for (Object[] valueLoop : valueLoopData) {
			sumAllValueLoops = sumAllValueLoops + (Double)valueLoop[1];
		}
		for (IRPActor stakeholder : projectActors) {
			
			Double sumStakeholderValueLoops = 0.0;
			for (Object[] valueLoop : valueLoopData) {
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
		Collections.sort(stakeholders);
		
		int index = 0;
		for (Stakeholder row : stakeholders) {
			index++;
			Object[] tableRow = { index, row.getItself().getName(), decimalFormat.format(row.getValue())};
			getStakeholderDataTable().add(tableRow);
		}
		
		changeStakeholderColor(stakeholders);
	}
	
	/**
	 * Runs all the diagram analysis steps
	 * @param pkg The scope package
	 */
	public static void analyzeDiagram(IRPPackage pkg) {
		initModel(pkg);
		calculateValuePath(projectStakeholders);
		searchLoops(projectCentralStakeholder);
		checkForMultipleDependencies();
		computeValueLoop();
		computeStakeholderValue();
	}

	/**
	 * @return The valuePath table
	 */
	public static ArrayList<Object[]> getValuePathDataTable() {
		return valuePathDataTable;
	}

	/**
	 * @param valuePathDataTable
	 */
	public static void setValuePathDataTable(ArrayList<Object[]> valuePathDataTable) {
		Model.valuePathDataTable = valuePathDataTable;
	}

	/**
	 * @return The valueLoop table
	 */
	public static ArrayList<Object[]> getValueLoopDataTable() {
		return valueLoopDataTable;
	}

	/**
	 * @param valueLoopDataTable
	 */
	public static void setValueLoopDataTable(ArrayList<Object[]> valueLoopDataTable) {
		Model.valueLoopDataTable = valueLoopDataTable;
	}

	/**
	 * @return The stakeholder table
	 */
	public static ArrayList<Object[]> getStakeholderDataTable() {
		return stakeholderDataTable;
	}

	/**
	 * @param stakeholderDataTable
	 */
	public static void setStakeholderDataTable(ArrayList<Object[]> stakeholderDataTable) {
		Model.stakeholderDataTable = stakeholderDataTable;
	}
	
	private Model() {}
}
