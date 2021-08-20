package client.gui.alts.zprestige.ias.tools;

import client.gui.alts.zprestige.ias.legacysupport.ILegacyCompat;
import client.gui.alts.zprestige.ias.legacysupport.NewJava;
import client.gui.alts.zprestige.ias.legacysupport.OldJava;

public class JavaTools {
	private static double getJavaVersion(){
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos+1);
		return Double.parseDouble(version.substring(0, pos));
	}
	public static ILegacyCompat getJavaCompat(){
		if(getJavaVersion() >= 1.8){
			return new NewJava();
		}else{
			return new OldJava();
		}
	}
}
