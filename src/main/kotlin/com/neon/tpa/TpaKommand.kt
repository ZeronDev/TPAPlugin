package com.neon.tpa

import com.github.shynixn.mccoroutine.bukkit.launch
import com.neon.tpa.Data.prefix
import com.neon.tpa.Data.tpaCoroutine
import com.neon.tpa.Data.tpaList
import com.neon.tpa.MainCore.Companion.plugin
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.Player

class TpaKommand {
    fun TpaKommand(kommand: PluginKommand) {
        kommand.register("tpa") {
            requires { sender is Player }
            executes { sender.sendMessage("$prefix 요청할 플레이어를 선택해주세요") }

            then("player" to string().apply { suggests { suggest(Bukkit.getOnlinePlayers().map { it.name }) }}) {
                executes {
                    val tpaSender = player
                    val tpaName: String by it
                    var tpaGetter = Bukkit.getOfflinePlayer(tpaName)

                    if (tpaGetter.isOnline) {
                        tpaGetter = tpaGetter as Player
                        if (tpaGetter.world === tpaSender.world) {

                            val acceptText = text("§a[수락]")
                            acceptText.clickEvent(ClickEvent.runCommand("tpaccept"))
                            acceptText.hoverEvent(HoverEvent.showText(text("§a클릭하여 순간이동 요청을 수락합니다")))
                            val denyText = text("§c[거부]")
                            denyText.clickEvent(ClickEvent.runCommand("tpdeny"))
                            denyText.hoverEvent(HoverEvent.showText(text("§c클릭하여 순간이동 요청을 거부합니다")))
                            val cancelText = text("§c[취소]")
                            denyText.clickEvent(ClickEvent.runCommand("tpacancel"))
                            denyText.hoverEvent(HoverEvent.showText(text("§c클릭하여 순간이동 요청을 취소합니다")))

                            tpaSender.sendMessage(text("${tpaGetter.name}에게 순간이동 요청을 했습니다 ").append(cancelText))
                            tpaGetter.sendMessage(text("${tpaSender.name}에게서 순간이동 요청이 왔습니다. 2분 후 요청이 자동으로 거부됩니다")
                                .append(acceptText).append(text(" ").append(denyText)))
                        } else {
                            sender.sendMessage(text("$prefix 플레이어가 다른 월드에 있습니다"))
                        }
                    } else {
                        sender.sendMessage(text("$prefix 플레이어가 온라인이 아닙니다"))
                    }
                }
            }
        }
        kommand.register("tpaccept") {
            requires { sender is Player }
            executes {
                var tpaSender: Player? = null
                tpaList.forEach {
                    if (it.second === sender) {
                        tpaSender = it.first
                    }
                }

                tpaSender?.let {

                }
            }
        }
        kommand.register("tpdeny") {
            requires { sender is Player }
            executes {
                var tpaSender: Player? = null
                tpaList.forEach {
                    if (it.second === sender) {
                        tpaSender = it.first
                    }
                }

                tpaSender?.let {
                    it.sendMessage(text("$prefix ${sender.name}이 순간이동 요청을 거부하였습니다"))
                }
                
            }
        }
        kommand.register("tpacancel") {

        }
    }

    fun tp(p1: Player, p2: Player) {
        val job = plugin.launch {
            p1.sendActionBar(text("§a3초 후에 순간이동합니다"))
            p2.sendActionBar(text("§a3초 후에 순간이동합니다"))
            delay(1000L)
            p1.spawnParticle(Particle.END_ROD, p1.location, 50, 3.0, 3.0, 3.0)
            p2.spawnParticle(Particle.END_ROD, p2.location, 50, 3.0, 3.0, 3.0)
            p1.sendActionBar(text("§a2초 후에 순간이동합니다"))
            p2.sendActionBar(text("§a2초 후에 순간이동합니다"))
            p1.spawnParticle(Particle.END_ROD, p1.location, 60, 3.0, 3.0, 3.0)
            p2.spawnParticle(Particle.END_ROD, p2.location, 60, 3.0, 3.0, 3.0)
            delay(1000L)
            p1.sendActionBar(text("§a1초 후에 순간이동합니다"))
            p2.sendActionBar(text("§a1초 후에 순간이동합니다"))
            p1.spawnParticle(Particle.END_ROD, p1.location, 70, 3.0, 3.0, 3.0)
            p2.spawnParticle(Particle.END_ROD, p2.location, 70, 3.0, 3.0, 3.0)
            delay(1000L)
            p1.teleport(p2.location)
            p1.sendActionBar(text("§a순간이동 되었습니다"))
            p2.sendActionBar(text("§a순간이동 되었습니다"))
        }
        tpaCoroutine.put(p1 to p2, job)
    }

    fun cancelTp(p1: Player, p2: Player) {
        p1.sendActionBar(text("§c위치가 변경되어 순간이동이 취소되었습니다"))
        p2.sendActionBar(text("§c${p1.name}의 위치가 변경되어 순간이동이 취소되었습니다"))
    }
}