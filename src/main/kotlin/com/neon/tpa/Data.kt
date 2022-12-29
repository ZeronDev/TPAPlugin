package com.neon.tpa

import kotlinx.coroutines.Job
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import java.util.*

object Data {
    val prefix = text("[ ").append(text("N").decorate(TextDecoration.BOLD).color(TextColor.color(0x01FC7A)).
        append(text("E").decorate(TextDecoration.BOLD).color(TextColor.color(0x03FCBE))).
        append(text("O").decorate(TextDecoration.BOLD).color(TextColor.color(0x02FAEA))).
        append(text("N").decorate(TextDecoration.BOLD).color(TextColor.color(0x03DCFD))).
        append(text(" §f] ")))
    val tpaList: MutableList<Pair<Player, Player>> = mutableListOf()
    val tpaCoroutine: MutableMap<Pair<Player, Player>, Job> = mutableMapOf()
    val cancelTpa: MutableMap<Pair<Player, Player>, Job> = mutableMapOf()
    val tpaCountDown: MutableMap<UUID, Int> = mutableMapOf()
}