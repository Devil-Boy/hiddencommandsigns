package pgDev.bukkit.HiddenCommandSigns;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class HCSDatabaseIO {
	// The ever unique separators
	public static String commandSeparator = " -#command separator#- ";
	public static String permissionSeparator = " -#permission separator#- ";
	
	public static HashMap<String, HiddenCommand> getDB(String dbLocation) {
		HashMap<String, HiddenCommand> output = new HashMap<String, HiddenCommand>();
		
		// Stuff gets done in here
		
		return output;
	}
	
	public static void saveDB(String dbLocation, HashMap<String, HiddenCommand> toSave) {
		for (String alias : toSave.keySet()) {
			// Turn the lists into single strings
			String commandsString = "";
			for (String command : toSave.get(alias).commands) {
				if (commandsString.equals("")) {
					commandsString = command;
				} else {
					commandsString = commandsString + commandSeparator + command;
				}
			}
			String permsString = "";
			for (String perm : toSave.get(alias).permissions) {
				if (permsString.equals("")) {
					permsString = perm;
				} else {
					permsString = permsString + permissionSeparator + perm;
				}
			}
			
			// Start writing
			try{
	    		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dbLocation))); // I'd hate to keep making a new writer for each alias... but it didn't seem to want to work the proper way...
	    		out.write(alias + "\r\n");
	    		out.write(toSave.get(alias).author + "\r\n");
	    		out.write(commandsString + "\r\n");
	    		out.write(permsString + "\r\n");
	    		out.write("\r\n");
	    		out.close();
	    	} catch (Exception e) {
	    		System.out.println(e);
	    	}
		}
	}
}
