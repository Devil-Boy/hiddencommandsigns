package pgDev.bukkit.HiddenCommandSigns;

import java.io.*;
import java.util.HashMap;

public class HCSDatabaseIO {
	// The ever unique separators
	public static String commandSeparator = " -#cs#- ";
	public static String permissionSeparator = " -#ps#- ";
	
	public static HashMap<String, HiddenCommand> getDB(String dbLocation) {
		HashMap<String, HiddenCommand> output = new HashMap<String, HiddenCommand>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(dbLocation)));
			String line = null;
			int lineCount = 0;
			String signText = null;
			HiddenCommand hcToAdd = new HiddenCommand();
			while ((line = br.readLine()) != null) {
				lineCount++;
				
				// Line set system
				int virtLine;
				if (lineCount < 6) {
					virtLine = lineCount;
				} else {
					virtLine = lineCount % 5;
				}
				
				// Start messing with variables
				if (virtLine == 1) {
					signText = line;
				} else if (virtLine == 2) {
					hcToAdd.author = line;
				} else if (virtLine == 3) {
					hcToAdd.commands = line.split(commandSeparator);
				} else if (virtLine == 4) {
					if (!(line == "")) {
						hcToAdd.permissions = line.split(permissionSeparator);
					}
					
					// Add it to the DB
					output.put(signText, hcToAdd);
				} else if (virtLine == 5) {
					// Wipe the variables
					signText = null;
					hcToAdd = new HiddenCommand();
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("HiddenCommandSigns database not found while attempting to load.");
		} catch (IOException e) {
			System.out.println("IO Exception during database loading.");
		}
		return output;
	}
	
	public static boolean checkIntegrity(String dbLoc) {
		boolean dbOK = true;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(dbLoc)));
			String line = null;
			int lineCount = 0;
			while ((line = br.readLine()) != null) {
				lineCount++;
				if (lineCount < 5) {
					if (lineCount == 2 && line.equals("")) {
						dbOK = false;
					}
				} else {
					if (lineCount % 5 == 2 && line.equals("")) {
						dbOK = false;
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Can't check for database integrity because it is not there?");
		} catch (IOException e) {
			System.out.println("IO Exception during database integrity check.");
		}
		return dbOK;
	}
	
	public static void saveDB(String dbLocation, HashMap<String, HiddenCommand> toSave) {
		try {
    		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dbLocation)));
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
    			if (toSave.get(alias).permissions != null) {
    				for (String perm : toSave.get(alias).permissions) {
    					if (permsString.equals("")) {
    						permsString = perm;
    					} else {
    						permsString = permsString + permissionSeparator + perm;
    					}
    				}
    			}
    			
    			// Start writing
    			out.write(alias + "\r\n");
        		out.write(toSave.get(alias).author + "\r\n");
        		out.write(commandsString + "\r\n");
        		out.write(permsString + "\r\n");
        		out.write("\r\n");
    		}
    		out.close();
    	} catch (Exception e) {
    		System.out.println(e);
    	}
	}
}
