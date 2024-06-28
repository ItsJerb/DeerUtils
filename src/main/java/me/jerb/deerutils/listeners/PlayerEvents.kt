package me.jerb.deerutils.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.luckperms.api.LuckPerms
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerEvents(private val plugin: JavaPlugin, private val luckPerms: LuckPerms) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val displayName = player.name

        event.joinMessage(Component.text("[").color(NamedTextColor.GRAY)
            .append(Component.text(" + ").color(NamedTextColor.GREEN))
            .append(Component.text("] ").color(NamedTextColor.GRAY))
            .append(Component.text(displayName).color(NamedTextColor.WHITE)))
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val displayName = player.name

        event.quitMessage(Component.text("[").color(NamedTextColor.GRAY)
            .append(Component.text(" - ").color(NamedTextColor.RED))
            .append(Component.text("] ").color(NamedTextColor.GRAY))
            .append(Component.text(displayName).color(NamedTextColor.WHITE)))
    }
}