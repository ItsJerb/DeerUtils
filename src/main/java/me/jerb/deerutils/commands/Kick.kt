package me.jerb.deerutils.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Kick : CommandExecutor{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (sender.isOp) {
                if (args.isNotEmpty()) {
                    val targetPlayer = Bukkit.getPlayer(args[0])
                    if (targetPlayer != null) {
                        if (!targetPlayer.isOp)
                            targetPlayer.kick()
                    }
                }
            }
        }
        return true
    }
}