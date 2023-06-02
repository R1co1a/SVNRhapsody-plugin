package JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RPPackage;

/**
 * @author adumez
 *
 */
public final class Model {
	
	private static IRPApplication rhpApp = PluginMainClass.rhpApp;
	private static IRPProject rhpProject = PluginMainClass.rhpProject;
	
	private static Collection<IRPPackage> topPackage = new ArrayList<>();
	
	
	/**
	 * 
	 */
	public static void initModel() {
		for (Object obj : rhpProject.getAllNestedElements().toList()) {
			if(obj.getClass() == RPPackage.class) {
				IRPPackage pkg = (IRPPackage)obj;
				topPackage.add(pkg);
			}
		}
	}
	
	/**
	 * @return
	 */
	public static Collection<IRPPackage> getTopPackage(){
		return Collections.unmodifiableCollection(topPackage);
	}
	
	
	
	private Model() {};
	
	
}
