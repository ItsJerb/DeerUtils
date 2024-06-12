package me.jerb.deerutils.commands

import me.jerb.deerutils.utils.MessageUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class TpaCommand : CommandExecutor {
    private val teleportRequests = mutableMapOf<UUID, UUID>()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            MessageUtils.formattedMessage(sender,"This command can only be run by players")
            return true
        }

        val targetPlayer = Bukkit.getPlayer(args[0])

        if (targetPlayer == null || !targetPlayer.isOnline) {
            MessageUtils.formattedMessage(sender, "Player not found")
            return true
        }

        if (targetPlayer.uniqueId == sender.uniqueId) {
            MessageUtils.formattedMessage(sender,"You cannot teleport to yourself")
            return true
        }

        MessageUtils.formattedMessage(targetPlayer," ${sender.name} wants to teleport to you. Type /tpaccept if you want them to teleport to you.")
        MessageUtils.formattedMessage(sender,"Teleport request sent to ${targetPlayer.name}.")

        teleportRequests[sender.uniqueId] = targetPlayer.uniqueId

        return true
    }

    fun acceptRequest(targetPlayer: Player) {
        val requesterUUID = teleportRequests.remove(targetPlayer.uniqueId)
        if (teleportRequests.isNotEmpty()) {
            val requester = requesterUUID?.let { Bukkit.getPlayer(it) }
            if (requester != null && requester.isOnline) {
                requester.teleport(targetPlayer.location)
                MessageUtils.formattedMessage(requester, "You have been teleported to ${targetPlayer.name}")
                MessageUtils.formattedMessage(targetPlayer,"${requester.name} has been teleported to you.")
                return
            } else { MessageUtils.formattedMessage(targetPlayer, "That player is no longer online.") }
        } else { MessageUtils.formattedMessage(targetPlayer, "No pending teleport requests.") }
    }
}