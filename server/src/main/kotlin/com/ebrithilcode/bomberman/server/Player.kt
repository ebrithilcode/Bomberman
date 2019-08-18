package com.ebrithilcode.bomberman.server

import com.ebrithilcode.bomberman.server.Grid
import com.ebrithilcode.bomberman.server.Input
import com.ebrithilcode.bomberman.server.Pawn
import processing.core.PVector
import java.net.Socket

class Player(socket : Socket, grid : Grid, playerNum : Int){



    val id = socket.remoteSocketAddress
    private val input = Input(socket.getInputStream())
    val character : Pawn = Pawn(grid, playerNum.toLong())


    init {
        val names = listOf("right", "down", "left", "up")
        val directions = arrayOf(PVector(1f,0f), PVector(0f,1f), PVector(-1f,0f), PVector(0f, -1f))
        val actions = List(4) {
            {
                println("Action called: $it")
                character.direction = directions[it]
                character.speed = character.maxSpeed
            }
        }
        val releaseActions = List(4) {
            {
                if (character.direction == directions[it]) character.speed = 0f
            }
        }
        input.onKeyStroke(names, actions)
        input.onKeyRelease(names, releaseActions)
        input.onKeyStroke("action", character::onAction)
    }




}