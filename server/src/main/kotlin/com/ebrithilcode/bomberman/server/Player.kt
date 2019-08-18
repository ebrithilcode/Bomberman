package com.ebrithilcode.bomberman.server

import com.ebrithilcode.bomberman.common.Direction
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
        val directions = arrayOf(Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH)
        val actions = List(4) {
            {
                println("Action called: $it")
                character.facing = directions[it]
                character.speed = character.maxSpeed
            }
        }
        val releaseActions = List(4) {
            {
                if (character.facing == directions[it]) character.speed = 0f
            }
        }
        input.onKeyStroke(names, actions)
        input.onKeyRelease(names, releaseActions)
        input.onKeyStroke("action", character::onAction)
    }




}