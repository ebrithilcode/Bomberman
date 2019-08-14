package com.ebrithilcode.bomberman

import processing.core.PVector
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class Grid(val width : Int, val height : Int, val gridSize : Double) {

    /**
     * Entities are allowed to take a turn when they are at least [cornerThreshold] close to a corner
     */
    private val cornerThreshold = 0.1

    private val fields = Array(width) { Array(height) {Field()}}


    /**
     * Request to make an entity move from it's position with it's velocity. If the move is not valid, nothing happens.
     * @param entity The entity to be moved
     */
    fun requestMove(entity : Entity) {

        val from = intArrayOf(entity.position.x.roundToInt(), entity.position.y.roundToInt())


        /*TODO: Reconsider if there is a solution that is more elegant. What [to] should be is the next round field
            the player will reach if he continues walking in [velocity] direction*/
        val to = if (entity.direction.x.compareTo(0)!=0) {
            if (entity.direction.x.compareTo(0) > 0) {
                intArrayOf(ceil(entity.position.x).toInt(), entity.position.y.roundToInt())
            } else {
                intArrayOf(floor(entity.position.x).toInt(), entity.position.y.roundToInt())
            }
        } else {
            if (entity.direction.y.compareTo(0) > 0) {
                intArrayOf(entity.position.x.roundToInt(), ceil(entity.position.y).toInt())
            } else {
                intArrayOf(entity.position.x.roundToInt(), floor(entity.position.y).toInt())
            }
        }

        //Move is only rejected if to and from are not equal to help move out stuck between two bombs
        if (fields[to[0]][to[1]].state != Field.State.FREE && !to.contentEquals(from)) return

        //If the distance from the round field to the true position along the axis that is not currently being used
        //is bigger than the threshold, the move is rejected
        if (entity.direction.x.compareTo(0) != 0) {
            if (abs(entity.position.y.roundToInt()-entity.position.y) > cornerThreshold) return
        } else {
            if (abs(entity.position.x.roundToInt()-entity.position.x) > cornerThreshold) return
        }

        entity.position.add(PVector.mult(entity.direction, entity.speed))
    }

    fun encodeToBytes() : ByteArray {
        val buffer = ByteBuffer.allocate(8+width*height)
        buffer.putInt(width).putInt(height)
        for (column in fields) {
            for (field in column) {
                buffer.put(field.byteState)
            }
        }
        return buffer.array()
    }

}