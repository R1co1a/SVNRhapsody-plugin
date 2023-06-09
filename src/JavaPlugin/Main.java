package JavaPlugin;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

public class Main {

	public static void main(String[] args) {
		PluginMainClass myPlugin = new PluginMainClass ();
		//get Rhapsody application that is currently running
	      IRPApplication app                                                
	              =RhapsodyAppServer.getActiveRhapsodyApplication();
		//init the plugin
		myPlugin.RhpPluginInit(app);
		//imitate a call to the plugin
		myPlugin.RhpPluginInvokeItem();
	}

}
