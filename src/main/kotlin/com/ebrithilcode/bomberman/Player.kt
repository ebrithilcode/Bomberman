package com.ebrithilcode.bomberman

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.Socket

class Player(socket : Socket){
    val id = socket.remoteSocketAddress
    val input = Input(socket.getInputStream())

    init {
        GlobalScope.launch {
            while (true)
                input.manageInput()
        }
    }

    fun update() {

    }

}