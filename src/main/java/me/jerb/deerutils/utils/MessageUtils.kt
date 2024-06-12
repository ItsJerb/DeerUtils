package me.jerb.deerutils.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object MessageUtils {
    fun formattedMessage(sender: CommandSender, message: String) {
        val formattedMessage =
            Component.text("Deer Utils").color(TextColor.fromHexString("#c3c1a8")).decorate(TextDecoration.BOLD)
                .append(Component.text(" → ").color(TextColor.fromHexString("#413827")).decoration(TextDecoration.BOLD, false))
                .append(Component.text(message).color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))
        sender.sendMessage(formattedMessage)
    }

    fun targetPM(targetPlayer: Player, sender: Player, message: String) {
        val targetPM =
            Component.text("[").color(TextColor.fromHexString("#8f8c79"))
                .append(Component.text("$sender")).color(NamedTextColor.WHITE)
                .append(Component.text(" → ")).color(TextColor.fromHexString("#8f8c79"))
                .append(Component.text("me")).color(NamedTextColor.WHITE)
                .append(Component.text("] ").color(TextColor.fromHexString("#8f8c79"))
                .append(Component.text(message).color(NamedTextColor.WHITE)))
        targetPlayer.sendMessage(targetPM)
    }

    fun senderPM(sender: Player, targetPlayer: Player, message: String) {
        val senderPM =
            Component.text("[").color(TextColor.fromHexString("#8f8c79"))
                .append(Component.text("me")).color(NamedTextColor.WHITE)
                .append(Component.text(" → ")).color(TextColor.fromHexString("#8f8c79"))
                .append(Component.text("$targetPlayer").color(NamedTextColor.WHITE)
                .append(Component.text("] ").color(TextColor.fromHexString("#8f8c79"))
                .append(Component.text(message).color(NamedTextColor.WHITE))))
        sender.sendMessage(senderPM)
    }
}