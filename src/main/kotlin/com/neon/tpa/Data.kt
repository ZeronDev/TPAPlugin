package com.neon.tpa

import kotlinx.coroutines.Job
import org.bukkit.entity.Player

object Data {
    const val prefix: String = "[ §aNEON ]"
    val tpaList: MutableList<Pair<Player, Player>> = mutableListOf()
    val tpaCoroutine: MutableMap<Pair<Player, Player>, Job> = mutableMapOf()
}