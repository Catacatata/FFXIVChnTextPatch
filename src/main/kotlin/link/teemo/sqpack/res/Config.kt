package com.shenou.fs.core.utils.res

import java.io.IOException

object Config {

    //reload map
    private var resourceMap: HashMap<String, String>? = null

    @JvmStatic
    fun setConfigResource() {
        val defaultConfigResource = ConfigResource()
        try {
            defaultConfigResource.loadConfig()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Resource.remove("DefaultConfig")
        Resource.put("DefaultConfig", defaultConfigResource)
    }

    @JvmStatic
    fun setConfigResource(fileName: String) {
        if (resourceMap == null) resourceMap = HashMap()
        resourceMap!!.put("DefaultConfig", fileName)
        val defaultConfigResource = ConfigResource()
        try {
            defaultConfigResource.loadConfig(fileName)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Resource.remove("DefaultConfig")
        Resource.put("DefaultConfig", defaultConfigResource)
    }

    @JvmStatic
    fun setConfigResource(configName: String, fileName: String) {
        if (resourceMap == null) resourceMap = HashMap()
        resourceMap!!.put(configName, fileName)
        val defaultConfigResource = ConfigResource()
        try {
            defaultConfigResource.loadConfig(fileName)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Resource.remove(configName)
        Resource.put(configName, defaultConfigResource)
    }
    @JvmStatic
    fun getConfigResource(): ConfigResource? {
        return Resource.get("DefaultConfig") as ConfigResource
    }
    @JvmStatic
    fun getConfigResource(configName: String): ConfigResource? {
        return Resource.get(configName) as ConfigResource
    }

    @JvmStatic
    fun getProperty(property: String): String? {
        val defaultConfigResource = getConfigResource()
        return defaultConfigResource?.getProperty(property)
    }

    @JvmStatic
    fun getProperty(configName: String, property: String): String? {
        val defaultConfigResource = getConfigResource(configName)
        return defaultConfigResource?.getProperty(property)
    }

    @JvmStatic
    fun reloadConfig() {
        try {
            if (resourceMap != null) {
                val resMapIter = resourceMap!!.entries.iterator()
                while (resMapIter.hasNext()) {
                    val entry = resMapIter.next()
                    val configName = entry.key
                    Resource.remove(configName)
                    val defaultConfigResource = ConfigResource()
                    try {
                        defaultConfigResource.loadConfig(entry.value)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    Resource.put(configName, defaultConfigResource)
                }
            } else {
                getConfigResource()?.loadConfig()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}