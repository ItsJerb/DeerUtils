package me.jerb.deerutils.commands

import me.jerb.deerutils.utils.MessageUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Messaging : CommandExecutor, TabCompleter {
    private val lastMessenger = mutableMapOf<Player, Player>()
    private val afkPlayers = mutableSetOf<Player>()
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            MessageUtils.formattedMessage(sender, "This command can only be run by players.")
            return true
        }

        when (label.lowercase()) {
            "m", "msg", "w", "pm", "t" -> {
                if (args.size < 2) {
                    MessageUtils.formattedMessage(sender, "Usage: /msg <player> <message>")
                    return true
                }

                val targetPlayer = Bukkit.getPlayer(args[0])
                if (targetPlayer == null || !targetPlayer.isOnline) {
                    MessageUtils.formattedMessage(sender, "The player you tried to message either doesn't exist or isn't online.")
                    return true
                }

                val message = args.drop(1).joinToString (" ")
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
                    return true
                }

                val message = args.joinToString (" ")
                MessageUtils.targetPM(targetPlayer,sender, message)
                MessageUtils.senderPM(sender, targetPlayer, message)
                lastMessenger[sender] = targetPlayer
            }

            "afk" -> {
                if (afkPlayers.contains(sender)) {
                    afkPlayers.remove(sender)
                    val backAfkMsg = Component.text("${sender.name} is back from being afk!", NamedTextColor.GRAY)
                    Bukkit.broadcast(backAfkMsg)
                    MessageUtils.formattedMessage(sender, "You are no longer afk!")
                } else {
                    val afkMsg = Component.text("${sender.name} went afk", NamedTextColor.GRAY)
                    afkPlayers.add(sender)
                    Bukkit.broadcast(afkMsg)
                    MessageUtils.formattedMessage(sender, "You are now afk!")
                }
            }

            "broadcast", "bc" -> {
                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Usage: /broadcast <message>")
                    return true
                }

                val message = args.joinToString(" ")
                val broadcastMsg = Component.text("Deer Utils").color(TextColor.fromHexString("#c3c1a8")).decorate(TextDecoration.BOLD)
                    .append(Component.text(" → ").color(TextColor.fromHexString("#413827")).decoration(TextDecoration.BOLD, false))
                    .append(Component.text(message).color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))
                Bukkit.broadcast(broadcastMsg)
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