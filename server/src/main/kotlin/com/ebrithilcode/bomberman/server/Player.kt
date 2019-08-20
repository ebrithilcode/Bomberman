package com.ebrithilcode.bomberman.server

import com.ebrithilcode.bomberman.common.Direction
import com.ebrithilcode.bomberman.common.PlayerAction
import java.util.*

class Player(val name: String, val character: Pawn) {

    var currentActions : MutableSet<PlayerAction> = HashSet()

    fun onActionBegun(action: PlayerAction) {
        when (action) {
            PlayerAction.UP -> {
                character.facing = Direction.NORTH; character.speed = character.maxSpeed
            }
            PlayerAction.DOWN -> {
                character.facing = Direction.SOUTH; character.speed = character.maxSpeed
            }
            PlayerAction.LEFT -> {
                character.facing = Direction.WEST; character.speed = character.maxSpeed
            }
            PlayerAction.RIGHT -> {
                character.facing = Direction.EAST; character.speed = character.maxSpeed
            }
            PlayerAction.SWITCH -> character.placeBomb()
            PlayerAction.UNASSIGNED -> {}
        }
    }

    fun onActionStopped(action: PlayerAction) {
        when (action) {
            PlayerAction.UP -> {
                if (character.facing == Direction.NORTH) character.speed = 0f
            }
            PlayerAction.DOWN -> {
                if (character.facing == Direction.SOUTH) character.speed = 0f
            }
            PlayerAction.LEFT -> {
                if (character.facing == Direction.EAST) character.speed = 0f
            }
            PlayerAction.RIGHT -> {
                if (character.facing == Direction.WEST) character.speed = 0f
            }
            PlayerAction.SWITCH -> {
            }
            PlayerAction.UNASSIGNED -> throw IllegalArgumentException() //TODO write message
        }
    }

    fun onPlayerActionUpdate(newActions: MutableSet<PlayerAction>) {
        var actionsBegun = newActions - currentActions
        actionsBegun.forEach(this::onActionBegun)
        var actionsStopped = currentActions - newActions
        actionsStopped.forEach(this::onActionStopped)
        currentActions = newActions
    }


}