package com.ebrithilcode.bomberman

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import processing.core.PVector
import java.io.InputStream
import java.net.Socket

class Player(socket : Socket, grid : Grid){
    val id = socket.remoteSocketAddress
    val input = Input(socket.getInputStream())
    val character : Entity = Entity(grid)
    val directions = arrayOf(PVector(1f,0f), PVector(0f,1f), PVector(-1f,0f), PVector(0f, -1f))

    init {
        val names = listOf("right", "down", "left", "up")
        val actions = List<()->Unit>(4) {
            {
                character.direction = directions[it]
            }
        }
        val releaseActions = List(4) {
            {
                if (character.direction.equals(directions[it])) character.direction = PVector(0f,0f)
            }
        }
        input.onKeyStroke(names, actions)
        input.onKeyRelease(names, releaseActions)
    }

    fun update() {

    }

}