package me.jerb.deerutils.commands

import me.jerb.deerutils.utils.MessageUtils
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Gamemode : CommandExecutor , TabCompleter{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (sender.isOp) {
                if (args.isNotEmpty()) {
                    var mode = args[0].lowercase()
                    when (mode) {
                        "survival", "s" -> sender.gameMode = GameMode.SURVIVAL
                        "creative", "c" -> sender.gameMode = GameMode.CREATIVE
                        "spectator", "spec" -> sender.gameMode = GameMode.SPECTATOR
                        "adventure", "a" -> sender.gameMode = GameMode.ADVENTURE
                    }
                    when (mode) {
                        "s" -> mode = "survival"
                        "c" -> mode = "creative"
                        "spec" -> mode = "spectator"
                        "a" -> mode = "adventure"
                    }
                    MessageUtils.formattedMessage(sender,"Gamemode changed to ${mode}!")
                } else { MessageUtils.formattedMessage(sender,"Please specify a gamemode") }
            } else { MessageUtils.formattedMessage(sender,"You cannot change your gamemode") }
        } else { MessageUtils.formattedMessage(sender,"This command can only be run by players") }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (args.size == 1) {
            val gamemodes = listOf("survival", "creative", "spectator", "adventure")
            return gamemodes.filter { it.startsWith(args[0], ignoreCase = true) }.toMutableList()
        }
        return null
    }
}