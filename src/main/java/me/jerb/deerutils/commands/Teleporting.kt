package me.jerb.deerutils.commands

import me.jerb.deerutils.utils.MessageUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.util.*

class Teleporting : CommandExecutor, TabCompleter {
    private val teleportRequests = mutableMapOf<UUID, UUID>()
    private val lastLocations = mutableMapOf<UUID, Location>()
    private val teleportToggles = mutableMapOf<UUID, Boolean>()
    private val warpList = mutableMapOf<String, Location>()

    init {
        loadWarps()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            MessageUtils.formattedMessage(sender, "This command can only be run by players.")
            return true
        }

        when (label.lowercase()) {
            "spawn" -> {
                val spawnLocation = sender.world.spawnLocation
                lastLocations[sender.uniqueId] = sender.location
                sender.teleport(spawnLocation)
                MessageUtils.formattedMessage(sender, "Teleported to spawn!")
            }

            "tpa" -> {
                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Usage: /tpa <player>")
                    return true
                }
                val target = Bukkit.getPlayer(args[0])
                if (target == null) {
                    MessageUtils.formattedMessage(sender, "Player not found.")
                    return true
                }
                if (teleportToggles.getOrDefault(target.uniqueId, true)) {
                    teleportRequests[target.uniqueId] = sender.uniqueId
                    MessageUtils.formattedMessage(sender, "Teleport request sent to ${target.name}.")
                    MessageUtils.formattedMessage(target, "${sender.name} has requested to teleport to you. Type /tpaccept to accept.")
                } else {
                    MessageUtils.formattedMessage(sender, "${target.name} has teleport requests disabled.")
                }
            }

            "tpaccept" -> {
                val requesterId = teleportRequests[sender.uniqueId]

                if (requesterId == null) {
                    MessageUtils.formattedMessage(sender, "You have no pending teleport requests.")
                    return true
                }

                val requester = Bukkit.getPlayer(requesterId)

                if (requester == null) {
                    MessageUtils.formattedMessage(sender, "Requester is no longer online.")
                    teleportRequests.remove(sender.uniqueId)
                    return true
                }

                lastLocations[requester.uniqueId] = requester.location
                requester.teleport(sender.location)
                MessageUtils.formattedMessage(requester, "Teleport request accepted. Teleporting to ${sender.name}.")
                MessageUtils.formattedMessage(sender, "${requester.name} has been teleported to you.")
                teleportRequests.remove(sender.uniqueId)
            }

            "tptoggle" -> {
                val currentState = teleportToggles.getOrDefault(sender.uniqueId, true)
                teleportToggles[sender.uniqueId] = !currentState
                val stateMessage = if (!currentState) "enabled" else "disabled"
                MessageUtils.formattedMessage(sender, "Teleport requests $stateMessage.")
            }

            "back" -> {
                val lastLocation = lastLocations[sender.uniqueId]
                if (lastLocation == null) {
                    MessageUtils.formattedMessage(sender, "No previous location found.")
                    return true
                }
                lastLocations[sender.uniqueId] = sender.location
                sender.teleport(lastLocation)
                MessageUtils.formattedMessage(sender, "Teleported back to your previous location.")
            }

            "sethome" -> {
                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Usage: /sethome <name>")
                    return true
                }
                val homeName = args[0]
                val homes = getPlayerHomes(sender.uniqueId)
                if (homes.keys.size >= 5 && !homes.containsKey(homeName)) {
                    MessageUtils.formattedMessage(sender, "You can only set up to 5 homes.")
                    return true
                }
                homes[homeName] = sender.location
                savePlayerHomes(sender.uniqueId, homes)
                MessageUtils.formattedMessage(sender, "Home '$homeName' set.")
            }

            "delhome" -> {
                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Usage: /delhome <name>")
                    return true
                }
                val homeName = args[0]
                val homes = getPlayerHomes(sender.uniqueId)
                if (!homes.containsKey(homeName)) {
                    MessageUtils.formattedMessage(sender, "Invalid home name. Try again with a valid home name.")
                    return true
                }
                homes.remove(homeName)
                deletePlayerHomes(sender.uniqueId, homeName)
                MessageUtils.formattedMessage(sender, "Home '$homeName' deleted.")
            }

            "home" -> {
                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Usage: /home <name>")
                    return true
                }
                val homeName = args[0]
                val homes = getPlayerHomes(sender.uniqueId)
                val homeLocation = homes[homeName]
                if (homeLocation == null) {
                    MessageUtils.formattedMessage(sender, "Home '$homeName' not found.")
                    return true
                }
                lastLocations[sender.uniqueId] = sender.location
                sender.teleport(homeLocation)
                MessageUtils.formattedMessage(sender, "Teleported to home '$homeName'.")
            }

            "homes" -> {
                val homes = getPlayerHomes(sender.uniqueId)
                if (homes.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "You have no homes set.")
                    return true
                }
                val homeList = homes.keys.joinToString(", ")
                MessageUtils.formattedMessage(sender, "Your homes: $homeList")
            }

            "setwarp" -> {
                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Usage: /setwarp <name>")
                    return true
                }
                val warpName = args[0].lowercase()
                warpList[warpName] = sender.location
                saveWarps()
                MessageUtils.formattedMessage(sender, "Warp '$warpName' created.")
            }

            "delwarp" -> {
                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Usage: /delwarp <name>")
                    return true
                }
                val warpName = args[0].lowercase()
                if (warpList.remove(warpName) != null) {
                    deleteWarps(warpName)
                    warpList.remove(warpName)
                    MessageUtils.formattedMessage(sender, "Warp '$warpName' deleted.")
                } else {
                    MessageUtils.formattedMessage(sender, "Warp '$warpName' does not exist.")
                }
            }

            "renamewarp" -> {
                if (args.size < 2) {
                    MessageUtils.formattedMessage(sender, "Usage: /renamewarp <old name> <new name>")
                    return true
                }
                val oldWarpName = args[0].lowercase()
                val newWarpName = args[1].lowercase()
                val warpLocation = warpList[oldWarpName]
                if (warpLocation != null) {
                    warpList.remove(oldWarpName)
                    warpList[newWarpName] = warpLocation
                    saveWarps()
                    MessageUtils.formattedMessage(sender, "Warp '$oldWarpName' renamed to '$newWarpName'.")
                } else {
                    MessageUtils.formattedMessage(sender, "Warp '$oldWarpName' does not exist.")
                }
            }

            "warps" -> {
                val warps = warpList.keys.joinToString(", ")

                if (warpList.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "There aren't any warps yet!")
                    return true
                }
                MessageUtils.formattedMessage(sender, "Warps: $warps")
            }

            "warp" -> {
                if (args.isEmpty()) {
                    MessageUtils.formattedMessage(sender, "Usage: /warp <name>")
                    return true
                }
                val warpName = args[0].lowercase()
                val warpLocation = warpList[warpName]
                if (warpLocation != null) {
                    lastLocations[sender.uniqueId] = sender.location
                    sender.teleport(warpLocation)
                    MessageUtils.formattedMessage(sender, "Teleported to warp '$warpName'.")
                } else {
                    MessageUtils.formattedMessage(sender, "Warp '$warpName' not found.")
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
        if (sender !is Player) {
            return null
        }

        when (label.lowercase()) {
            "tpa" -> {
                if (args.size == 1) {
                    return Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
                }
            }
            "home", "delhome" -> {
                if (args.size == 1) {
                    val homes = getPlayerHomes(sender.uniqueId)
                    return homes.keys.filter { it.startsWith(args[0], ignoreCase = true) }.toMutableList()
                }
            }
            "setwarp", "delwarp", "warp", "renamewarp" -> {
                if (args.size == 1) {
                    return warpList.keys.filter { it.startsWith(args[0].lowercase(), ignoreCase = true) }.toMutableList()
                } else if (label.lowercase() == "renamewarp" && args.size == 2) {
                    return warpList.keys.filter { it.startsWith(args[1].lowercase(), ignoreCase = true) }.toMutableList()
                }
            }
        }
        return null
    }

    private fun getPlayerHomeFile(playerId: UUID): File {
        val pluginFolder = Bukkit.getPluginManager().getPlugin("DeerUtils")?.dataFolder ?: throw IOException("Plugin folder not found")
        val homesFolder = File(pluginFolder, "homes")
        if (!homesFolder.exists()) {
            homesFolder.mkdirs()
        }
        val file = File(homesFolder, "${playerId}.yml")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    private fun getPlayerHomes(playerId: UUID): MutableMap<String, Location> {
        val file = getPlayerHomeFile(playerId)
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        val homes = mutableMapOf<String, Location>()
        for (key in config.getKeys(false)) {
            config.getLocation(key)?.let { homes[key] = it }
        }
        return homes
    }

    private fun savePlayerHomes(playerId: UUID, homes: Map<String, Location>) {
        val file = getPlayerHomeFile(playerId)
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        homes.forEach { (name, location) ->
            config.set(name, location)
        }
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deletePlayerHomes(playerId: UUID, homeName: String) {
        val file = getPlayerHomeFile(playerId)
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        config.set(homeName, null)
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getWarpFile(): File {
        val plugin = Bukkit.getPluginManager().getPlugin("DeerUtils") ?: throw IOException("Plugin 'DeerUtils' not found")

        val pluginFolder = plugin.dataFolder
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs()
        }

        val file = File(pluginFolder, "warps.yml")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    private fun loadWarps() {
        val file = getWarpFile()
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        for (key in config.getKeys(false)) {
            warpList[key.lowercase()] = config.getLocation(key) ?: continue
        }
    }

    private fun saveWarps() {
        val file = getWarpFile()
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        for ((name, location) in warpList) {
            config.set(name, location)
        }
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deleteWarps(warpName: String) {
        val file = getWarpFile()
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        config.set(warpName, null)
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}