package com.ebrithilcode.bomberman.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket

suspend fun DatagramSocket.asyncReceive(packet: DatagramPacket) = withContext(Dispatchers.IO) {
    receive(packet)
}

suspend fun DatagramSocket.asyncSend(packet : DatagramPacket) = withContext(Dispatchers.IO) {
    send(packet)
}

suspend fun DatagramPacket.getDataAsString() = String(data, offset, length, Charsets.UTF_8)