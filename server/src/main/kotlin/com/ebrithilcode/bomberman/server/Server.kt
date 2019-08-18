package com.ebrithilcode.bomberman.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger


class Server(port:Int) {
    private val serverSocket = ServerSocket(port)
    var scheduledClients = AtomicInteger(0)
    lateinit var clientJob : Job
    private val clientList : MutableList<Socket> = mutableListOf()


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

                    }
                }
            }
        }
        println("Coroutine launched, waiting for $scheduledClients clients")
    }


}