package me.jerb.deerutils

import me.jerb.deerutils.commands.*
import me.jerb.deerutils.listeners.PlayerEvents
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException

class DeerUtils : JavaPlugin() {
    private lateinit var luckPerms : LuckPerms
    private lateinit var teleporting: Teleporting
    private  lateinit var admin: Admin
    private lateinit var messaging: Messaging
    override fun onEnable() {
        teleporting = Teleporting(this)
        admin = Admin()
        messaging = Messaging()
        this.getCommand("spawn")?.setExecutor(teleporting)
        this.getCommand("tpa")?.setExecutor(teleporting)
        this.getCommand("tpaccept")?.setExecutor(teleporting)
        this.getCommand("tptoggle")?.setExecutor(teleporting)
        this.getCommand("back")?.setExecutor(teleporting)
        this.getCommand("sethome")?.setExecutor(teleporting)
        this.getCommand("delhome")?.setExecutor(teleporting)
        this.getCommand("home")?.setExecutor(teleporting)
        this.getCommand("homes")?.setExecutor(teleporting)
        this.getCommand("warp")?.setExecutor(teleporting)
        this.getCommand("setwarp")?.setExecutor(teleporting)
        this.getCommand("delwarp")?.setExecutor(teleporting)
        this.getCommand("renamewarp")?.setExecutor(teleporting)
        this.getCommand("warps")?.setExecutor(teleporting)
        this.getCommand("gamemode")?.setExecutor(admin)
        this.getCommand("kick")?.setExecutor(admin)
        this.getCommand("msg")?.setExecutor(messaging)
        this.getCommand("reply")?.setExecutor(messaging)
        this.getCommand("afk")?.setExecutor(messaging)
        this.getCommand("broadcast")?.setExecutor(messaging)
        luckPerms = LuckPermsProvider.get()

        if (luckPerms == null) {
            logger.severe("LuckPerms failed to load. Disabling DeerUtils...")
            server.pluginManager.disablePlugin(this)
        }

        logger.info("LuckPerms found and initialized!")
        server.pluginManager.registerEvents(PlayerEvents(this, luckPerms), this)

        val pluginFolder = Bukkit.getPluginManager().getPlugin("DeerUtils")?.dataFolder ?: throw IOException("Plugin folder not found")
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs()
        }

        saveDefaultConfig()

        logger.info("DeerUtils has been loaded!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("DeerUtils has been shut down!")
    }
}
