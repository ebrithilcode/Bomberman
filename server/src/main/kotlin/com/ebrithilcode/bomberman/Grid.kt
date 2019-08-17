package com.ebrithilcode.bomberman

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class Grid(val width : Int, val height : Int, val gridSize : Double) {

    var entityList = mutableListOf<Entity>()

    /**
     * Entities are allowed to take a turn when they are at least [cornerThreshold] close to a corner
     */
    private val cornerThreshold = 0.25

    val fields = Array(width) { Array(height) {Field()}}


    /**
     * Request to make an entity move from it's position with it's velocity. If the move is not valid, nothing happens.
     * @param entity The entity to be moved
     */
    fun isMoveRejected(entity : Entity) : Boolean {

        val from = intArrayOf(entity.position.x.roundToInt(), entity.position.y.roundToInt())


        /*TODO: Reconsider if there is a solution that is more elegant. What [to] should be is the next round field
            the player will reach if he continues walking in [velocity] direction*/
        val newPosition = PVector.add(entity.position, PVector.mult(entity.direction, entity.speed))
        val to = if (entity.direction.x.compareTo(0)!=0) {
            if (entity.direction.x.compareTo(0) > 0) {
                intArrayOf(ceil(newPosition.x).toInt(), newPosition.y.roundToInt())
            } else {
                intArrayOf(floor(newPosition.x).toInt(), newPosition.y.roundToInt())
            }
        } else {
            if (entity.direction.y.compareTo(0) > 0) {
                intArrayOf(newPosition.x.roundToInt(), ceil(newPosition.y).toInt())
            } else {
                intArrayOf(newPosition.x.roundToInt(), floor(newPosition.y).toInt())
            }
        }


        //If the distance from the round field to the true position along the axis that is not currently being used
        //is bigger than the threshold, the move is rejected
        if (entity.direction.x.compareTo(0) != 0) {
            if (abs(entity.position.y.roundToInt()-entity.position.y) > cornerThreshold) return true
        } else {
            if (abs(entity.position.x.roundToInt()-entity.position.x) > cornerThreshold) return true
        }

        //Move is only rejected if to and from are not equal to help move out stuck between two bombs
        return (!to.contentEquals(from) && fields[to[0]][to[1]].isMoveRejected(entity))

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

    fun update(deltaTime : Float) {
        for (entity in entityList) {
            entity.update(deltaTime)
        }
        entityList.removeIf(Entity::isDead)


    }
    //TODO: Remove that, should be client side
    fun show(applet : PApplet) {
        applet.rectMode(PConstants.CENTER)
        val colors = intArrayOf(applet.color(255), applet.color(0,0,255), applet.color(255,0,0))
        for (x in fields.indices) {
            for (y in fields[x].indices) {
                applet.fill(colors[fields[x][y].byteState.toInt()])
                applet.stroke(0)
                val position = PVector((x*gridSize).toFloat(), (y*gridSize).toFloat())
                if (fields[x][y].entitiesOnField.size>0) applet.fill(255f,0f,255f)
                applet.rect(position.x, position.y, gridSize.toFloat(), gridSize.toFloat())
            }
        }
        for (entity in entityList) entity.show(applet)
    }

    fun getField(pos : PVector): Field {
        return fields[pos.x.toInt()][pos.y.toInt()]
    }


}