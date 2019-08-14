package com.ebrithilcode.bomberman

import java.io.InputStream
import javax.json.Json
import javax.json.JsonReader

class Input(val inputStream : InputStream) {
    private val keyMap = HashMap<String, Boolean>()
    private val subscriber : HashMap<String, MutableList<()->Unit>> = HashMap()

    private val jsonReader: ()->JsonReader = {Json.createReader(inputStream)}


    private fun keyStroke(name : String) {
        val subList = subscriber["${name}Pressed"]
        if (subList!=null) {
            for (sub in subList) sub()
        }
        keyMap[name] = true
    }

    private fun keyReleased(name : String) {
        val subList = subscriber["${name}Released"]
        if (subList!=null) {
            for (sub in subList) sub()
        }
        keyMap[name] = false
    }

    fun onKey(key : String, pressed : Boolean, callback : ()->Unit ) {
        val keyValue = key+(if (pressed) "Pressed" else "Released")
        subscriber.putIfAbsent(keyValue, mutableListOf())
        subscriber[keyValue]?.add(callback)
    }

    fun onKeyStroke(key : String, callback: () -> Unit) {
        onKey(key, true, callback)
    }

    fun onKeyRelease(key : String, callback: () -> Unit) {
        onKey(key, false, callback)
    }

    fun manageInput() {
        try {
            do {
                println("Managing input")
                val jsonInput = Json.createReader(inputStream).readObject()
                println("Received json input $jsonInput")
                if (jsonInput.containsKey("stroke")) keyStroke(jsonInput.getString("stroke"))
                if (jsonInput.containsKey("release")) keyReleased(jsonInput.getString("release"))
            } while (!jsonInput.isEmpty())
        } catch (exception : javax.json.JsonException) {
            exception.printStackTrace()
        }
    }
}