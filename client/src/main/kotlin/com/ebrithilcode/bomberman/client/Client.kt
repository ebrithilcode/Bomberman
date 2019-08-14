package com.ebrithilcode.bomberman.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import processing.core.PApplet
import processing.data.JSONObject
import java.lang.Exception
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.Socket
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonReader
import kotlin.system.measureTimeMillis

fun main() {
    println("Client starting up")
    PApplet.runSketch(arrayOf<String>("Hey there"), Client("localhost", 8001))
}


/**
 * A client connects to a server at a specific ip and port. It shall send JSON key events to the server on each
 * keyStroke and keyRelease and render the input
 */
class Client(val serverIP : String, val serverPort : Int) : PApplet() {

    /**
     * Contains all link between not coded keys and the corresponding Strings that are being send to the server
     */
    val keyMap = HashMap<Char, String>()
    /**
     * Contains a link between coded keys and the corresponding Strings that are being send to the server
     */
    val keyCodeMap = HashMap<Int, String>()
    /**
     * Stores booleans whether or not the already for the server encoded keys were already pressed
     * to separate keyStrokes from keyPresses
     */
    val keysPressed = HashMap<String, Boolean>()

    /**
     * A TCP Socket connection to the server to send input Events
     */
    private var socket = Socket()

    /**
     * A JSONReader that will be connected to the sockets input stream on socket initialization. Used to
     * receive information about the servers encoded key configuration.
     */
    private val jsonReader : ()->JsonReader = {
        Json.createReader(socket.getInputStream())
    }

    /**
     * A UDP Socket connection to the server to receive rendering Information
     */
    private val renderingSocket = DatagramSocket()

    override fun settings() {
        size(300,300)
    }

    override fun setup() {
        var notConnected = true
        val socketAddress = InetSocketAddress(serverIP, serverPort)

        while (notConnected) {
            println("Trying to connect...")
            try {
                socket = Socket()
                socket.setSoTimeout(3000)
                socket.connect(socketAddress, 3000)
                notConnected = false
                println("Connected")
            } catch (ignore: Exception) {
                Thread.sleep(2000)
                ignore.printStackTrace()
                println("Connection refused. Trying again...")
            }


        }
        frameRate(60f)

        GlobalScope.launch {
            while (true)
                listenForKeysToRegister()
        }


    }

    override fun draw() {
        background(255)

    }


    /**
     * Checks if the received key was a real key stroke or just a system based repetition of an already pressed key.
     * If it was a true key Stroke, a JSON Object is being send to the server via TCP.
     */
    override fun keyPressed() {
        val encoded = parsedKey()

        if (encoded.isNotEmpty() && !keysPressed.getOrDefault(encoded, false)) {
            keysPressed[encoded] = true
            val builder = Json.createObjectBuilder()
            builder.add("stroke", encoded)
            sendJSON(builder.build())
        }

    }

    /**
     * Encodes the released key. If it is a server used input, sends it to the server via TCP.
     * Sets the keyPressed map value to false to allow a new key stroke on this key.
     */
    override fun keyReleased() {
        val encoded = parsedKey()

        if (encoded.isNotEmpty()) {
            keysPressed[encoded] = false

            val builder = Json.createObjectBuilder()
            builder.add("release", encoded)
            sendJSON(builder.build())
        }
    }

    private fun sendJSON(json : JsonObject) {
        Json.createWriter(socket.getOutputStream()).writeObject(json)
    }


    /**
     *  Parses the server used String by the given key and keyCode
     *  @param key The key to encode as a server used String. Defaults to the last pressed or released key
     *  @param keyCode The keyCode to encode as a server used String. Defaults to the last pressed or released keyCode.
     *  @return Returns a for the server encoded String representation of the key or keyCode. Returns an empty
     *  String if the neither [key] nor [keyCode] are being used as an Input signal by the server
     */
    private fun parsedKey(key:Char = this.key, keyCode : Int = this.keyCode) : String {
        return if (key.toInt() == CODED) {
            keyCodeMap.getOrDefault(keyCode, "")
        } else {
            keyMap.getOrDefault(key, "")
        }
    }

    /**
     * Listening on the TCP Port for a JSONObject telling to register new keys with special server actions
     */
    private fun listenForKeysToRegister() {
        try {
            do {

                val jsonInput = jsonReader().readObject()
                val keysJson = jsonInput.getJsonObject("keys")
                val keyCodesJson = jsonInput.getJsonObject("keyCodes")
                println("Object read: $jsonInput")
                for (entry in keysJson.entries) {
                    keyMap[entry.key[0]] = keysJson.getString(entry.key)
                }
                for (entry in keyCodesJson.entries) {
                    keyCodeMap[entry.key.toInt()] = keyCodesJson.getString(entry.key)
                }
            } while (!jsonInput.isEmpty())
        } catch (exception : javax.json.JsonException) {}
    }
}