package com.ebrithilcode.bomberman.common.klaxon

import com.beust.klaxon.JsonObject
import com.ebrithilcode.bomberman.common.Direction
import com.ebrithilcode.bomberman.common.PlayerAction
import java.util.*

data class PlayerActionMessage(val actions : HashSet<PlayerAction>)

data class RenderMessage(val grid: ByteArray, val entities: Array<EntityData>, val animations: Array<AnimationData>) {

    //auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RenderMessage

        if (!grid.contentEquals(other.grid)) return false
        if (!entities.contentEquals(other.entities)) return false
        if (!animations.contentEquals(other.animations)) return false

        return true
    }

    //auto-generated
    override fun hashCode(): Int {
        var result = grid.contentHashCode()
        result = 31 * result + entities.contentHashCode()
        result = 31 * result + animations.contentHashCode()
        return result
    }

}

data class EntityData(val id: Long, val spriteId: Long, val posX: Float, val posY: Float, val facing: Direction, val velocity: Float)

data class AnimationData(val id: Long, val animationId: Long, val posX: Float, val posY: Float, var timeStamp: Long, val metaData : JsonObject)

data class ClientRegisterMessage(val name : String)

data class ServerConfirmationMessage(val success : Boolean)