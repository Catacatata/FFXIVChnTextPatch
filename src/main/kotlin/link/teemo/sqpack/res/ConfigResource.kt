package com.shenou.fs.core.utils.res

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Properties

class ConfigResource {

    private var configFile: InputStreamReader? = null
    private var props: Properties? = null

    fun getProperty(property: String): String? {
        return this.props!!.getProperty(property)
    }

    @Throws(IOException::class)
    fun loadConfig() {
        this.configFile = InputStreamReader(FileInputStream("/Config.properties"), "UTF-8")
        this.props = Properties()
        this.props!!.load(this.configFile)
    }

    @Throws(IOException::class)
    fun loadConfig(fileName: String) {
        this.configFile = InputStreamReader(FileInputStream(fileName), "UTF-8")
        this.props = Properties()
        this.props!!.load(this.configFile)
    }

    fun setPropperty(name: String, value: String) {
        if (this.props == null) {
            this.props = Properties()
        }
        this.props!!.put(name, value)
    }
    
}
