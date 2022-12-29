package com.neon.tpa

import com.github.shynixn.mccoroutine.bukkit.launch
import com.neon.tpa.Data.cancelTpa
import com.neon.tpa.Data.prefix
import com.neon.tpa.Data.tpaCoroutine
import com.neon.tpa.Data.tpaCountDown
import com.neon.tpa.Data.tpaList
import com.neon.tpa.MainCore.Companion.plugin
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player

@SuppressWarnings("deprecation")
object TpaKommand {
    fun tpaKommand(kommand: PluginKommand) {
        kommand.register("tpa") {
            requires { sender is Player }
            executes { sender.sendMessage(prefix.append(text("§c요청할 플레이어를 선택해주세요"))) }

            then("tpaName" to string().apply { suggests { suggest(Bukkit.getOnlinePlayers().map { it.name }) } }) {
                executes {
                    if (tpaCountDown.containsKey(player.uniqueId)) {
                        player.sendMessage(prefix.append(text("§c${tpaCountDown[player.uniqueId]}초 후에 다시 쓸 수 있습니다")))
                        return@executes
                    }
                    val tpaSender = player
                    val tpaName: String by it
                    var tpaGetter = Bukkit.getOfflinePlayer(tpaName)
                    if (tpaGetter === tpaSender) {
                        tpaSender.sendMessage(prefix.append(text("§c보내는 유저와 받는 유저가 동일합니다")))
                        return@executes
                    } else if (getGetter(tpaSender).contains(tpaGetter)) tpaSender.sendMessage(prefix.append(text("§c이미 ${tpaGetter.name}에게 순간이동 요청을 보냈습니다")))

                    if (tpaGetter.isOnline) {
                        tpaGetter = tpaGetter as Player
                        if (tpaGetter.world === tpaSender.world) {

                            val acceptText = GsonComponentSerializer.gson().deserialize("{\"text\":\"[수락]\",\"bold\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpaccept\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"클릭하여 순간이동 요청을 수락합니다\",\"bold\":true,\"color\":\"green\"}]}}")
                            val denyText = GsonComponentSerializer.gson().deserialize("{\"text\":\"[거부]\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpdeny\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"클릭하여 순간이동 요청을 거부합니다\",\"bold\":true,\"color\":\"red\"}]}}")
                            val cancelText = GsonComponentSerializer.gson().deserialize("{\"text\":\"[취소]\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpcancel\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"클릭하여 순간이동 요청을 취소합니다\",\"bold\":true,\"color\":\"red\"}]}}")

                            tpaSender.sendMessage(prefix.append(text("${tpaGetter.name}에게 순간이동 요청을 보냈습니다 ").append(cancelText)))
                            tpaGetter.sendMessage(prefix.append(text("${tpaSender.name}에게서 순간이동 요청이 왔습니다. 2분 후 요청이 자동으로 거부됩니다 ").append(acceptText).append(text(" ")).append(denyText)))

                            val tpacancel = plugin.launch {
                                delay(120_000)
                                tpaSender.sendMessage(prefix.append(text("${tpaGetter.name}가 순간이동 요청을 거부하였습니다")))
                                cancelTpa.remove(tpaSender to tpaGetter)
                                tpaList.remove(tpaSender to tpaGetter)
                            }

                            tpaList.add(tpaSender to tpaGetter)

                            cancelTpa[tpaSender to tpaGetter] = tpacancel

                            plugin.launch {
                                repeat(60) {
                                    tpaCountDown[player.uniqueId] = 60-it
                                    delay(1000)
                                }
                                tpaCountDown.remove(player.uniqueId)
                            }
                        } else {
                            sender.sendMessage(prefix.append(text("§c${tpaName}가 다른 월드에 있습니다")))
                        }
                    } else {
                        sender.sendMessage(prefix.append(text("§c${tpaName}가 온라인이 아닙니다")))
                    }
                }
            }
            kommand.register("tpaccept")
            {
                requires { sender is Player}

                executes {
                    if (!isContains(player)) {
                        player.sendMessage(prefix.append(text("§c순간이동 요청을 받은 적이 없습니다")))
                        return@executes
                    }
                    val tpaSender = getSender(player)
                    tpaSender.take(tpaSender.size-1).forEach { tpaSender ->
                        cancelTpa[tpaSender to player]?.cancel()
                        cancelTpa.remove(tpaSender to player)

                        tpaList.remove(tpaSender to player)

                        tpaSender.sendMessage(prefix.append(text("${sender.name}가 순간이동 요청을 거부하였습니다")))
                    }
                    val tpaTeleporter = tpaSender[tpaSender.size-1]
                    cancelTpa[tpaTeleporter to player]?.cancel()
                    cancelTpa.remove(tpaTeleporter to player)

                    tpaList.remove(tpaTeleporter to player)
                    tpaTeleporter.tp(player)
                }
            }
            kommand.register("tpdeny")
            {
                requires { sender is Player}
                executes {
                    if (!isContains(player)) {
                        player.sendMessage(prefix.append(text("§c순간이동 요청을 받은 적이 없습니다")))
                        return@executes
                    }
                    val tpaSender = getSender(player)
                    val list = mutableListOf<String>()
                    tpaSender.forEach { tpaSender ->
                        cancelTpa[tpaSender to player]?.cancel()
                        cancelTpa.remove(tpaSender to player)

                        tpaList.remove(tpaSender to player)

                        list.add(tpaSender.name)
                        tpaSender.sendMessage(prefix.append(text("${sender.name}가 순간이동 요청을 거부하였습니다")))
                    }
                    sender.sendMessage(prefix.append(text("${list.joinToString(", ")}의 순간이동 요청을 모두 거부하였습니다")))
                }
            }
            kommand.register("tpcancel")
            {
                requires { playerOrNull != null}
                executes {
                    if (!isContainssender(player)) {
                        player.sendMessage(prefix.append(text("§c순간이동 요청을 보낸 적이 없습니다")))
                        return@executes
                    }
                    val tpaGetter = getGetter(player)

                    val list = mutableListOf<String>()
                    tpaGetter.forEach { tpaGetter ->
                        cancelTpa[player to tpaGetter]?.cancel()
                        cancelTpa.remove(player to tpaGetter)

                        tpaList.remove(player to tpaGetter)

                        list.add(tpaGetter.name)
                        tpaGetter.sendMessage(prefix.append(text("${sender.name}가 순간이동 요청을 취소하였습니다")))
                    }
                    player.sendMessage(prefix.append(text("${list.joinToString(", ")}에게 보낸 순간이동 요청을 취소하였습니다")))

                }
            }
        }
    }

    fun Player.tp(to: Player) {
        val player = this@tp
        val job = plugin.launch {
            player.sendActionBar(text("§a§l3초 후에 순간이동합니다"))
            to.sendActionBar(text("§a§l3초 후에 순간이동합니다"))
            delay(1000L)
            player.spawnParticle(Particle.END_ROD, player.location, 50, 2.0, 2.0, 2.0)
            to.spawnParticle(Particle.END_ROD, to.location, 50, 3.0, 3.0, 3.0)
            player.sendActionBar(text("§a§l2초 후에 순간이동합니다"))
            to.sendActionBar(text("§a§l2초 후에 순간이동합니다"))
            player.spawnParticle(Particle.END_ROD, player.location, 60, 3.0, 3.0, 3.0)
            to.spawnParticle(Particle.END_ROD, to.location, 60, 3.0, 3.0, 3.0)
            delay(1000L)
            player.sendActionBar(text("§a§l1초 후에 순간이동합니다"))
            to.sendActionBar(text("§a§l1초 후에 순간이동합니다"))
            player.spawnParticle(Particle.END_ROD, player.location, 70, 2.0, 2.0, 2.0)
            to.spawnParticle(Particle.END_ROD, to.location, 70, 3.0, 3.0, 3.0)
            delay(1000L)
            player.spawnParticle(Particle.ASH, player.location, 60, 2.0, 2.0, 2.0)
            player.teleport(to.location)
            player.sendActionBar(text("§a§l순간이동 되었습니다"))
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f)
            to.playSound(to, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f)
            to.sendActionBar(text("§a§l${player.name}이/가 순간이동 되었습니다"))
            tpaCoroutine.remove(player to to)

        }
        tpaCoroutine.put(player to to, job)
    }

    fun isContains(player: Player) : Boolean {
        var contains = false
        for (pair in tpaList) {
            if (pair.second === player) {
                contains = true
            }
        }
        return contains
    }
    fun isContainssender(player: Player) : Boolean {
        var contains = false
        for (pair in tpaList) {
            if (pair.first === player) {
                contains = true
            }
        }
        return contains
    }

    fun getSender(player: Player) : MutableList<Player> {
        val sender: MutableList<Player> = mutableListOf()
        for (pair in tpaList) {
            if (pair.second === player) {
                sender.add(pair.first)
                break
            }
        }
        return sender
    }

    fun getGetter(player: Player) : MutableList<Player> {
        val getter: MutableList<Player> = mutableListOf()
        for (pair in tpaList) {
            if (pair.first === player) {
                getter.add(pair.second)
            }
        }
        return getter
    }


    fun cancelTp(p1: Player, p2: Player) {
        p1.sendActionBar(text("§c위치가 변경되어 순간이동이 취소되었습니다"))
        p2.sendActionBar(text("§c${p1.name}의 위치가 변경되어 순간이동이 취소되었습니다"))
        tpaCoroutine.remove(p1 to p2)!!.cancel()
    }
    fun cancelTpForLeaving(p1: Player, p2: Player) {
        p2.sendActionBar(text("§c${p1.name}가 나가 순간이동이 취소되었습니다"))
        tpaCoroutine.remove(p1 to p2)!!.cancel()
    }
}