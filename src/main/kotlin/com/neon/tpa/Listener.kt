package com.neon.tpa

import com.neon.tpa.Data.tpaList
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

object Listener : Listener {
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        tpaList.forEach {
            if (it.first === e.player) {
                it.second.sendMessage("${it.first.name}이 나가 tp 요청이 자동으로 거부되었습니다")
                tpaList.remove(it.first to it.second)
            } else if (it.second === e.player) {
                it.first.sendMessage("${it.second.name}이 나가 tp 요청이 자동으로 거부되었습니다")
                tpaList.remove(it.first to it.second)
            }
        }
    }
    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        var contains = false
        tpaList.forEach {
            if (it.first === e.player || it.second === e.player) {
                contains = true
            }
        }
        if (e.hasChangedPosition() && contains) {

        }
    }
}