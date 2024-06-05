package me.jerb.deerutils.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.w3c.dom.Text
import java.util.*

class TpaCommand : CommandExecutor {
    private val teleportRequests = mutableMapOf<UUID, UUID>()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be run by players")
            return true
        }

        val targetPlayer = Bukkit.getPlayer(args[0])
        if (targetPlayer == null || !targetPlayer.isOnline) {
            sender.sendMessage("Player not found")
            return true
        }

        if (targetPlayer.uniqueId == sender.uniqueId) {
            sender.sendMessage("You cannot teleport to yourself")
            return true
        }

        teleportRequests[targetPlayer.uniqueId] = sender.uniqueId
        targetPlayer.sendMessage(
            Component.text("Deer Utils ➔ ${sender.name} wants to teleport to you. Type /tpaccept if you want them to teleport to you.")
                .decorate(TextDecoration.BOLD)
                .color(TextColor.color(195,193,168))
        )
        sender.sendMessage(
            Component.text("Deer Utils ➔ Teleport request sent to ${targetPlayer.name}.")
                .decorate(TextDecoration.BOLD)
                .color(TextColor.color(195,193,168))
        )

        return true
    }

    fun acceptRequest(targetPlayer: Player) {
        val requesterUUID = teleportRequests.remove(targetPlayer.uniqueId)
        if (requesterUUID != null) {
            val requester = Bukkit.getPlayer(requesterUUID)
            if (requester != null && requester.isOnline) {
                requester.teleport(targetPlayer.location)
                requester.sendMessage(
                    Component.text("Deer Utils ➔ You have been teleported to ${targetPlayer.name}")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(195,193,168))
                )
                targetPlayer.sendMessage(
                    Component.text("Deer Utils ➔ ${requester.name} has been teleported to you.")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(195,193,168))
                )
            } else {
                targetPlayer.sendMessage(
                    Component.text("Deer Utils ➔ There are no pending teleport requests or the player requesting is no longer online.")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(195,193,168))
                )
            }
        } else {
            targetPlayer.sendMessage(
                Component.text("Deer Utils ➔ No pending teleport requests.")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(195,193,168))
            )
        }
    }
}