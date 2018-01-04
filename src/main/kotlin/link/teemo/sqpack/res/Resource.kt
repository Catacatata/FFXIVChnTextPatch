package com.shenou.fs.core.utils.res

import java.util.HashMap


object Resource {

    private var objectHashMap: HashMap<String, Any>? = null

    fun put(objectName: String, `object`: Any) {
        if (objectHashMap == null) {
            objectHashMap = HashMap()
        }
        objectHashMap!!.remove(objectName);
        objectHashMap!!.put(objectName, `object`)
    }

    fun get(objectName: String): Any? {
        if (objectHashMap == null)
            return null
        else
            return objectHashMap!![objectName]
    }

    fun remove(objectName: String): Any? {
        if (objectHashMap == null) {
            objectHashMap = HashMap()
        }
        return objectHashMap!!.remove(objectName)
    }

}
