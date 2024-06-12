package me.jerb.deerutils

import me.jerb.deerutils.commands.*
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.bukkit.plugin.java.JavaPlugin

class DeerUtils : JavaPlugin() {
    private var luckPerms : LuckPerms? = null
    override fun onEnable() {
        // Plugin startup logic
        luckPerms = LuckPermsProvider.get()

        if (luckPerms == null) {
            logger.severe("LuckPerms failed to load. Disabling DeerUtils...")
            server.pluginManager.disablePlugin(this)
        }
        logger.info("LuckPerms found and initialized!")
        logger.info("DeerUtils has been loaded!")
        registerCommands()
    }

    private fun registerCommands() {
        getCommand("tpa")?.setExecutor(TpaCommand())
        getCommand("tpaccept")?.setExecutor(TpAcceptCommand(TpaCommand()))
        getCommand("gamemode")?.setExecutor(Gamemode())
        getCommand("kick")?.setExecutor(Kick())
        getCommand("warp")?.setExecutor(Warp())
        getCommand("home")?.setExecutor(Homes())
        getCommand("msg")?.setExecutor(Messaging())
        getCommand("reply")?.setExecutor(Messaging())
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("DeerUtils has been shut down!")
    }
}
