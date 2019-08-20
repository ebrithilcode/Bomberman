package com.ebrithilcode.bomberman.common.klaxon

import com.ebrithilcode.bomberman.common.Direction
import com.ebrithilcode.bomberman.common.PlayerAction
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.util.*

@Serializable
data class PlayerActionMessage(val actions : HashSet<PlayerAction>)

@Serializable
data class RenderMessage(val grid: Array<Byte>, val entities: Array<EntityData>, val animations: Array<AnimationData>) {

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

@Serializable
data class EntityData(val id: Long, val spriteId: Long, val posX: Float, val posY: Float, val facing: Direction, val velocity: Float, val data : String = "")


@Serializable
data class AnimationData(val id: Long, val animationId: Long, val posX: Float, val posY: Float, var timeStamp: Long, val metaData : Array<Int>)


@Serializable
data class ClientRegisterMessage(val name : String)


@Serializable
data class ServerConfirmationMessage(val success : Boolean)