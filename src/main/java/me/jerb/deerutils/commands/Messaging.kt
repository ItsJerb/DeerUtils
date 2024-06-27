package me.jerb.deerutils.commands

import me.jerb.deerutils.utils.MessageUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Messaging : CommandExecutor, TabCompleter {
    private val lastMessenger = mutableMapOf<Player, Player>()
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            MessageUtils.formattedMessage(sender, "This command can only be run by players.")
            return true
        }

        when (label.lowercase()) {
            "m", "msg", "w", "pm", "t" -> {
                if (args.size < 2) {
                    MessageUtils.formattedMessage(sender, "Usage: /msg <player> <message>")
                }

                val targetPlayer = Bukkit.getPlayer(args[0])
                if (targetPlayer == null || !targetPlayer.isOnline) {
                    MessageUtils.formattedMessage(sender, "The player you tried to message either doesn't exist or isn't online.")
                    return true
                }

                val message = args.drop(1).joinToString { " " }
                MessageUtils.targetPM(targetPlayer, sender, message)
                MessageUtils.senderPM(sender, targetPlayer, message)

                lastMessenger[sender] = targetPlayer
                lastMessenger[targetPlayer] = sender
            }
            "r", "reply" -> {
                val targetPlayer = lastMessenger[sender]
                if (targetPlayer == null || !targetPlayer.isOnline) {
                    MessageUtils.formattedMessage(sender, "There is nobody to reply to!")
                    return true
                }

                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Usage: /reply <message>")
                }

                val message = args.joinToString { " " }
                MessageUtils.targetPM(targetPlayer,sender, message)
                MessageUtils.senderPM(sender, targetPlayer, message)
                lastMessenger[sender] = targetPlayer
            }

            "afk" -> {

            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (args.size == 1) {
            return Bukkit.getOnlinePlayers().map{it.name}.filter{it.startsWith(args[0], ignoreCase = true)}.toMutableList()
        }
        return null
    }
}