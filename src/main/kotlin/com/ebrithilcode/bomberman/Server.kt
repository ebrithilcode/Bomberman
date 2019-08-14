package com.ebrithilcode.bomberman

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.StringReader
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonObjectBuilder
import javax.json.JsonReader


class Server(port:Int) {
    val serverSocket = ServerSocket(port)
    var scheduledClients = AtomicInteger(0)
    lateinit var clientJob : Job
    private val clientList : MutableList<Socket> = mutableListOf()

    private val keySet = buildDefaultKeySetJSON()

    fun acceptClients(amount : Int, onAccept : (Socket)->Unit = {}) {
        scheduledClients.addAndGet(amount)
        if (!::clientJob.isInitialized || !clientJob.isActive) {
            clientJob = GlobalScope.launch {
                while (scheduledClients.get() > 0) {
                    val newClient = serverSocket.accept()
                    if (newClient != null) {
                        scheduledClients.decrementAndGet()
                        onAccept(newClient)
                        clientList.add(newClient)
                        newClient.getOutputStream().let {
                            Json.createWriter(it).writeObject(keySet)
                        }

                    }
                }
            }
        }
        println("Coroutine launched, waiting for $scheduledClients clients")
    }

    fun buildDefaultKeySetJSON() : JsonObject {
        val builder : JsonObjectBuilder = Json.createObjectBuilder()
        val keys = Json.createObjectBuilder()
        keys.add("w", "up")
        keys.add("a", "left")
        keys.add("d", "right")
        keys.add("s", "down")
        val keyCodes = Json.createObjectBuilder()
        builder.add("keys", keys)
        builder.add("keyCodes", keyCodes)
        val build = builder.build()
        println("Encoding String: "+build)
        val reader = Json.createReader(StringReader(String(build.toString().toByteArray())))
        println("Reader reads: "+reader.readObject().toString())
        return build
    }

}