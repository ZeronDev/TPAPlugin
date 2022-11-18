package com.neon.tpa

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class MainCore : JavaPlugin() {
    companion object {
        lateinit var plugin: Plugin
    }

    override fun onEnable() {
        logger.info("§a플러그인 활성화중")
        plugin = this

    }

    override fun onDisable() {
        logger.info("§c플러그인 비활성화중")
    }
}