package com.ebrithilcode.bomberman.server

import com.ebrithilcode.bomberman.common.Direction
import com.ebrithilcode.bomberman.common.PlayerAction
import com.ebrithilcode.bomberman.server.Grid
import com.ebrithilcode.bomberman.server.Input
import com.ebrithilcode.bomberman.server.Pawn
import processing.core.PVector
import java.lang.IllegalArgumentException
import java.net.Socket
import java.util.*

class Player(socket : Socket, grid : Grid, playerNum : Int){

    var currentActions = EnumSet.noneOf(PlayerAction::class.java)

    val id = socket.remoteSocketAddress
    private val input = Input(socket.getInputStream())
    val character : Pawn = Pawn(grid, playerNum.toLong())


    fun onActionBegun(action : PlayerAction) {
        when(action) {
            PlayerAction.UP -> { character.facing = Direction.NORTH; character.speed = character.maxSpeed }
            PlayerAction.DOWN -> { character.facing = Direction.SOUTH; character.speed = character.maxSpeed }
            PlayerAction.LEFT -> { character.facing = Direction.WEST; character.speed = character.maxSpeed }
            PlayerAction.RIGHT -> { character.facing = Direction.EAST; character.speed = character.maxSpeed }
            PlayerAction.SWITCH -> character.placeBomb()
            PlayerAction.UNASSIGNED -> throw IllegalArgumentException() //TODO write message
        }
    }

    fun onActionStopped(action : PlayerAction) {
        when(action) {
            PlayerAction.UP -> { if (character.facing == Direction.NORTH) character.speed = 0f }
            PlayerAction.DOWN -> { if (character.facing == Direction.SOUTH) character.speed = 0f }
            PlayerAction.LEFT -> { if (character.facing == Direction.EAST) character.speed = 0f }
            PlayerAction.RIGHT -> { if (character.facing == Direction.WEST) character.speed = 0f }
            PlayerAction.SWITCH -> {}
            PlayerAction.UNASSIGNED -> throw IllegalArgumentException() //TODO write message
        }
    }

    fun onPlayerActionUpdate(newActions : EnumSet<PlayerAction>) {
        var actionsBegun = newActions - currentActions
        actionsBegun.forEach(this::onActionBegun)
        var actionsStopped = currentActions - newActions
        actionsStopped.forEach(this::onActionStopped)
        currentActions = newActions
    }




}