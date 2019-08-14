package com.ebrithilcode.bomberman

import java.io.InputStream
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonObjectBuilder


/**
 * A Server side Input that gets information about key events via [inputStream]. You can subscribe to key events
 * via [onKeyRelease] and [onKeyStroke]
 * The names of all possible "keys" are set in the companion object by adding keys and their names to keys
 * or keyCodes and their names to keyCodes.
 */
class Input(val inputStream : InputStream) {

    companion object {
        val usedKeys: JsonObject by lazy {
                val builder : JsonObjectBuilder = Json.createObjectBuilder()
                val keys = Json.createObjectBuilder()
                keys.add("w", "up")
                keys.add("a", "left")
                keys.add("d", "right")
                keys.add("s", "down")
                val keyCodes = Json.createObjectBuilder()
                builder.add("keys", keys)
                builder.add("keyCodes", keyCodes)
                builder.build()
        }
    }

    /**
     * Storing booleans whether or not the specified actionKey is pressed or not
     */
    private val keyMap = HashMap<String, Boolean>()

    /**
     * Storing Callbacks for a specified key(Stroke|Released) event. Subscribe to it via [onKeyStroke] or [onKeyRelease]
     */
    private val subscriber : HashMap<String, MutableList<()->Unit>> = HashMap()




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


    private fun onKey(key : String, pressed : Boolean, callback : ()->Unit ) {
        val keyValue = key+(if (pressed) "Pressed" else "Released")
        subscriber.putIfAbsent(keyValue, mutableListOf())
        subscriber[keyValue]?.add(callback)
    }

    /** Add a callback to be called when [key] is pressed
     * @param key The key to listen for with its server side name
     * @param callback The callback to be executed when the event happens
     */
    fun onKeyStroke(key : String, callback: () -> Unit) {
        onKey(key, true, callback)
    }

    /** Add a callback to be called when [key] is released
     * @param key The key to listen for with its server side name
     * @param callback The callback to be executed when the event happens
     */
    fun onKeyRelease(key : String, callback: () -> Unit) {
        onKey(key, false, callback)
    }

    /**
     * Tests if a JsonObject was send and calls [keyStroke] or [keyReleased] if so
     */
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