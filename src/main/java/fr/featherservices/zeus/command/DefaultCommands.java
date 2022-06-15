package fr.featherservices.zeus.command;

import fr.featherservices.zeus.ZeusServer;
import fr.featherservices.zeus.logger.LogType;
import fr.featherservices.zeus.plugins.ZeusPlugin;

public class DefaultCommands implements CommandExecutor {

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            return;
        }

        if (args[0].equalsIgnoreCase("version")) {
            ZeusServer.getInstance().getConsole().sendMessage("Current version running " + ZeusServer.VERSION);
            ZeusServer.getInstance().getConsole().sendMessage("You can visit www.featherservices.fr/zeus to check for a newer version");
            return;
        }

        if (args[0].equalsIgnoreCase("stop")) {
            ZeusServer.getInstance().stopServer();
            return;
        }
    }
}
