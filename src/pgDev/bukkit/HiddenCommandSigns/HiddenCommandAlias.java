package pgDev.bukkit.HiddenCommandSigns;

public class HiddenCommandAlias {
	// Values (in order they will show up in database)
	public String author;
	public String[] commands;
	public String[] permissions;
	
	// Le Constructor
	public HiddenCommandAlias(String authorI, String[] commandsI, String[] permissionsI) {
		author = authorI;
		commands = commandsI;
		permissions = permissionsI;
	}
}
