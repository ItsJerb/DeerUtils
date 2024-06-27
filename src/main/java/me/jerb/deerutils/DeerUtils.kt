package me.jerb.deerutils

import me.jerb.deerutils.commands.*
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.bukkit.plugin.java.JavaPlugin

class DeerUtils : JavaPlugin() {
    private var luckPerms : LuckPerms? = null
    private lateinit var teleporting: Teleporting
    override fun onEnable() {
        teleporting = Teleporting()
        this.getCommand("spawn")?.setExecutor(teleporting)
        this.getCommand("tpa")?.setExecutor(teleporting)
        this.getCommand("tpaccept")?.setExecutor(teleporting)
        this.getCommand("tptoggle")?.setExecutor(teleporting)
        this.getCommand("back")?.setExecutor(teleporting)
        this.getCommand("sethome")?.setExecutor(teleporting)
        this.getCommand("delhome")?.setExecutor(teleporting)
        this.getCommand("home")?.setExecutor(teleporting)
        this.getCommand("homes")?.setExecutor(teleporting)
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
        getCommand("gamemode")?.setExecutor(Admin())
        getCommand("kick")?.setExecutor(Admin())
        getCommand("warp")?.setExecutor(Teleporting())
        getCommand("setwarp")?.setExecutor(Teleporting())
        getCommand("delwarp")?.setExecutor(Teleporting())
        getCommand("renamewarp")?.setExecutor(Teleporting())
        getCommand("warps")?.setExecutor(Teleporting())
        getCommand("msg")?.setExecutor(Messaging())
        getCommand("reply")?.setExecutor(Messaging())
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("DeerUtils has been shut down!")
    }
}
