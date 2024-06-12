package me.jerb.deerutils.commands

import me.jerb.deerutils.utils.MessageUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.Location

class Homes : CommandExecutor, TabCompleter {
    private val homeList = mutableMapOf<String, Location>()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (args.isNotEmpty()) {
                val arg = args[0].lowercase()
                when (arg) {
                    "set" -> {
                        if (args.size > 1 && args[1].isNotEmpty()) {
                            val homeLocation = sender.location
                            val homeName = args[1]
                            homeList[homeName] = homeLocation
                            MessageUtils.formattedMessage(sender, "Home '$homeName' set at your location!")
                        } else { MessageUtils.formattedMessage(sender, "Please specify a name for the home") }
                    }
                    "delete" -> {
                        if (args.size > 1 && args[1].isNotEmpty()) {
                            val homeName = args[1]
                            if (homeList.remove(homeName) != null) {
                                MessageUtils.formattedMessage(sender, "Home '$homeName' was deleted")
                            } else { MessageUtils.formattedMessage(sender, "Home '$homeName' does not exist") }
                        } else { MessageUtils.formattedMessage(sender, "Please specify a name for the home to delete.") }
                    }
                    "list" -> {
                        val homes = homeList.keys.joinToString { ", " }
                        MessageUtils.formattedMessage(sender,"Homes: $homes")
                    }
                    "rename" -> {
                        if (args.size > 2 && args[1].isNotEmpty() && args[2].isNotEmpty()) {
                            val oldHomeName = args[1]
                            val newHomeName = args[2]
                            val homeLocation = homeList[oldHomeName]
                            if (homeLocation != null) {
                                homeList.remove(oldHomeName)
                                homeList[newHomeName] = homeLocation
                                MessageUtils.formattedMessage(sender, "Home '$oldHomeName' renamed to '$newHomeName'.")
                            } else {
                                MessageUtils.formattedMessage(sender, "Home '$oldHomeName' does not exist.")
                            }
                        } else { MessageUtils.formattedMessage(sender, "Please specify the current name and new name for your home.") }
                    }
                    else -> {
                        if (args.size > 1 && args[1].isNotEmpty()) {
                            val homeToTeleport = args[1]
                            val homeLocation = homeList[homeToTeleport]
                            if (homeLocation != null) {
                                sender.teleport(homeLocation)
                                MessageUtils.formattedMessage(sender, "Teleported you to '$homeToTeleport'")
                            } else { MessageUtils.formattedMessage(sender, "Home '$homeToTeleport' does not exist.") }
                        } else { MessageUtils.formattedMessage(sender, "Please specify a home to teleport to or whether you want to do something with home.") }
                    }
                }
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
            val commands = listOf("set", "delete", "list", "rename") + homeList.keys
                commands.filter { it.startsWith(args[0], ignoreCase = true) }.toMutableList()
        }
        if (args.size == 2 && (args[0].equals("delete", ignoreCase = true)) || (args[0].equals("rename", ignoreCase = true))) {
            return homeList.keys.filter{it.startsWith(args[1], ignoreCase = true)}.toMutableList()
        }
        return null
    }
}