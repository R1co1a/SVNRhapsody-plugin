package JavaPlugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Dialog;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.telelogic.rhapsody.core.IRPActor;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPObjectModelDiagram;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.RPActor;
import com.telelogic.rhapsody.core.RPClass;
import com.telelogic.rhapsody.core.RPObjectModelDiagram;

/**
 * @author adumez
 *
 */
public class UserInterface {
	public static void HelloWorld() {
		System.out.println("Hello World");
	}
	
	/**
	 * 
	 */
	public static void showResultDialog() {
		for(IRPPackage pkg : Model.getTopPackage()) {
			System.out.println(pkg.getName());
			for (Object obj : pkg.getAllNestedElements().toList()) {
				if (obj.getClass() == RPObjectModelDiagram.class) {
					IRPObjectModelDiagram diag = (IRPObjectModelDiagram)obj;
					IRPCollection image = diag.getPictureAs("\\\\utbm.local\\racine-dfs\\rep_perso_etudiants\\adumez\\Bureau\\DEBUG\\DEBUG\\ImageTest", "JPG", 0, null);
					BufferedImage test = null;
					try {
						test = ImageIO.read(new File("\\\\utbm.local\\racine-dfs\\rep_perso_etudiants\\adumez\\Bureau\\DEBUG\\DEBUG\\ImageTest"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					ImageDialog dialog = new ImageDialog(null, test);
					dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
					dialog.setVisible(true);
					for (Object im : image.toList()) {
						String dir = (String)im;
						System.out.println(dir);
					}
					
				}
			}
		}
	}
}
