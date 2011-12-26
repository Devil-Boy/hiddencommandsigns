package pgDev.bukkit.HiddenCommandSigns;

public class HiddenCommand {
	// Values (in order they will show up in database)
	public String author;
	public String[] commands;
	public String[] permissions;
	
	// Le Constructors
	public HiddenCommand(String authorI, String[] commandsI) {
		author = authorI;
		commands = commandsI;
	}
	
	public HiddenCommand addPerms(String[] perms) {
		permissions = perms;
		return this;
	}
}
