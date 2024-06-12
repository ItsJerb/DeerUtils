package me.jerb.deerutils.commands

import me.jerb.deerutils.utils.MessageUtils
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Warp : CommandExecutor, TabCompleter {
    private val warpList = mutableMapOf<String, Location>()
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (args.isNotEmpty()) {
                val arg = args[0].lowercase()
                when (arg) {
                    "create" -> {
                        if (args.size > 1 && args[1].isNotEmpty()) {
                            val warpLocation = sender.location
                            val warpName = args[1]
                            warpList[warpName] = warpLocation
                            MessageUtils.formattedMessage(sender, "Warp '$warpName' created.")
                        } else {
                            MessageUtils.formattedMessage(sender, "Please specify a name for the warp.")
                        }
                    }
                    "remove" -> {
                        if (args.size > 1 && args[1].isNotEmpty()) {
                            val warpName = args[1]
                            if (warpList.remove(warpName) != null) {
                                MessageUtils.formattedMessage(sender, "Warp '$warpName' was removed.")
                            } else {
                                MessageUtils.formattedMessage(sender, "Warp '$warpName' does not exist.")
                            }
                        } else {
                            MessageUtils.formattedMessage(sender, "Please specify a name for the warp to remove.")
                        }
                    }
                    "list" -> {
                        val warps = warpList.keys.joinToString(", ")
                        MessageUtils.formattedMessage(sender, "Warps: $warps")
                    }
                    "rename" -> {
                        if (args.size > 2 && args[1].isNotEmpty() && args[2].isNotEmpty()) {
                            val oldWarpName = args[1]
                            val newWarpName = args[2]
                            val warpLocation = warpList[oldWarpName]
                            if (warpLocation != null) {
                                warpList.remove(oldWarpName)
                                warpList[newWarpName] = warpLocation
                                MessageUtils.formattedMessage(sender, "Warp '$oldWarpName' renamed to '$newWarpName'.")
                            } else {
                                MessageUtils.formattedMessage(sender, "Warp '$oldWarpName' does not exist.")
                            }
                        } else {
                            MessageUtils.formattedMessage(sender, "Please specify the current name and new name for the warp.")
                        }
                    }
                    else -> {
                        MessageUtils.formattedMessage(sender, "Unknown warp command.")
                    }
                }
            } else {
                MessageUtils.formattedMessage(sender, "Please specify a warp command.")
            }
        } else {
            MessageUtils.formattedMessage(sender, "This command can only be run by players.")
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
            return mutableListOf("create", "remove", "list", "rename").filter { it.startsWith(args[0], ignoreCase = true) }.toMutableList()
        }
        if (args.size == 2 && (args[0].equals("remove", ignoreCase = true) || args[0].equals("rename", ignoreCase = true))) {
            return warpList.keys.filter { it.startsWith(args[1], ignoreCase = true) }.toMutableList()
        }
        return null
    }
}