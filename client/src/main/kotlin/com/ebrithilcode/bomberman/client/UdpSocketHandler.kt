package com.ebrithilcode.bomberman.client

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.ebrithilcode.bomberman.common.PlayerAction
import com.ebrithilcode.bomberman.common.asyncReceive
import com.ebrithilcode.bomberman.common.asyncSend
import com.ebrithilcode.bomberman.common.klaxon.PlayerActionMessage
import com.ebrithilcode.bomberman.common.klaxon.RenderMessage
import kotlinx.coroutines.*
import java.io.IOException
import java.net.DatagramSocket
import java.net.DatagramPacket
import java.util.concurrent.Executors

class UdpSocketHandler(val port : Int) {

    private val socket : DatagramSocket = DatagramSocket(port)
    private val coroutineScope : CoroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName("Socket-Couroutine"))

    private val receivePacket = DatagramPacket(ByteArray(RECEIVE_PACKET_SIZE), RECEIVE_PACKET_SIZE)
    private val sendPacket = DatagramPacket(ByteArray(SEND_PACKET_SIZE), SEND_PACKET_SIZE)

    companion object {
        val RECEIVE_PACKET_SIZE = 4096
        val SEND_PACKET_SIZE = 2048
    }

    fun start() {
        val job = coroutineScope.launch {
            val packet = receivePacket
            socket.asyncReceive(packet)
        }
    }

    fun sendAction(msg : PlayerActionMessage) {
        coroutineScope.launch {
            sendPacket.length = SEND_PACKET_SIZE
            sendPacket.data = Klaxon().toJsonString(msg).toByteArray(Charsets.UTF_8)
            println(String(sendPacket.data))
            //socket.asyncSend(sendPacket)
        }
    }

    fun beginInputHandlingLoop() : Job = coroutineScope.launch {
            while(isActive) {
                val packet = receivePacket;
                packet.length = packet.data.size
                socket.asyncReceive(packet)
                val msg = Klaxon().parse<RenderMessage>(String(packet.data)) ?: throw IOException() //TODO
            }
        }



}