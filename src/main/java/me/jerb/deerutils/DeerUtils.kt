package me.jerb.deerutils

import me.jerb.deerutils.commands.TpaCommand
import me.jerb.deerutils.commands.TpAcceptCommand
import org.bukkit.plugin.java.JavaPlugin

class DeerUtils : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        logger.info("DeerUtils has been loaded!")
        registerCommands()
    }

    private fun registerCommands() {
        val tpaCommand = TpaCommand()
        getCommand("tpa")?.setExecutor(TpaCommand())
        getCommand("tpaccept")?.setExecutor(TpAcceptCommand(tpaCommand))
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("DeerUtils has been shut down!")
    }
}
