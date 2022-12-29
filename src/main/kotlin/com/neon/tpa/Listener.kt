package com.neon.tpa

import com.neon.tpa.Data.cancelTpa
import com.neon.tpa.Data.prefix
import com.neon.tpa.Data.tpaCoroutine
import com.neon.tpa.Data.tpaList
import com.neon.tpa.TpaKommand.cancelTp
import com.neon.tpa.TpaKommand.cancelTpForLeaving
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

object Listener : Listener {
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        tpaCoroutine.keys.forEach {
            if (it.first === e.player) {
                cancelTpForLeaving(it.first, it.second)
            } else if (it.second === e.player) {
                cancelTpForLeaving(it.second, it.first)
            }
        }
        while (tpaList.iterator().hasNext()) {
            val it = tpaList.iterator().next()
            if (it.first === e.player) {
                tpaList.remove(it)
                cancelTpa.remove(it)!!.cancel()
                it.second.sendMessage(prefix.append(text("${it.first.name}이/가 나가 순간이동 요청이 자동으로 취소 되었습니다")))
            } else if (it.second === e.player) {
                tpaList.remove(it)
                cancelTpa.remove(it)!!.cancel()
                it.first.sendMessage(prefix.append(text("${it.second.name}이/가 나가 순간이동 요청이 자동으로 거부되었습니다")))
            }
        }
    }
    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (e.hasChangedPosition()) {
            tpaCoroutine.keys.forEach {
                if (it.first === e.player) {
                    cancelTp(it.first, it.second)
                }
            }
        }
    }
}