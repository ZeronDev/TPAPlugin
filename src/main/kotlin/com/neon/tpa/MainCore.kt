package com.neon.tpa

import io.github.monun.kommand.kommand
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import com.neon.tpa.TpaKommand.tpaKommand

class MainCore : JavaPlugin() {
    companion object {
        lateinit var plugin: Plugin
    }

    override fun onEnable() {
        logger.info("[TPA] §a플러그인 활성화중")
        plugin = this

        kommand { tpaKommand(this) }

        server.pluginManager.registerEvents(Listener, this)

    }

    override fun onDisable() {
        logger.info("[TPA] §c플러그인 비활성화중")
    }
}