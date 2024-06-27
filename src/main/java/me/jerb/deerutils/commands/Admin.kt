package me.jerb.deerutils.commands

import me.jerb.deerutils.utils.MessageUtils
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Admin : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            MessageUtils.formattedMessage(sender, "This command can only be run by players.")
            return true
        }

        if (!sender.isOp) {
            MessageUtils.formattedMessage(sender, "You cannot do that as you're not an operator.")
            return true
        }

        when (label.lowercase()) {
            "gamemode", "gm" -> {
                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Please specify a gamemode.")
                    return true
                }
                var mode = args[0].lowercase()
                when (mode) {
                    "survival", "s" -> sender.gameMode = GameMode.SURVIVAL
                    "creative", "c" -> sender.gameMode = GameMode.CREATIVE
                    "spectator", "spec" -> sender.gameMode = GameMode.SPECTATOR
                    "adventure", "a" -> sender.gameMode = GameMode.ADVENTURE
                    else -> {
                        MessageUtils.formattedMessage(sender, "Invalid gamemode.")
                        return true
                    }
                }
                when (mode) {
                    "s" -> mode = "survival"
                    "c" -> mode = "creative"
                    "spec" -> mode = "spectator"
                    "a" -> mode = "adventure"
                }
                MessageUtils.formattedMessage(sender, "Gamemode changed to $mode!")
                return true
            }

            "kick" -> {
                if (args.size < 2) {
                    MessageUtils.formattedMessage(sender, "Usage: /kick <player> <reason>")
                    return true
                }
                val targetName = args[0]
                val target = Bukkit.getPlayer(targetName)
                if (target == null) {
                    MessageUtils.formattedMessage(sender, "Player '$targetName' not found.")
                    return true
                }
                val reason = args.drop(1).joinToString(" ")
                target.kick(Component.text(reason))
                MessageUtils.formattedMessage(sender, "${target.name} has been kicked for: $reason")
                return true
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
        when (label.lowercase()) {
            "gamemode" -> {
                if (args.size == 1) {
                    val gamemodes = listOf("survival", "creative", "spectator", "adventure")
                    return gamemodes.filter { it.startsWith(args[0], ignoreCase = true) }.toMutableList()
                }
            }

            "kick" -> {
                if (args.size == 1) {
                    return Bukkit.getOnlinePlayers().map{it.name}.filter{it.startsWith(args[0], ignoreCase = true)}.toMutableList()
                }
            }
        }
        return null
    }
}