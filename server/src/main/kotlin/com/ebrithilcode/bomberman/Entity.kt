package com.ebrithilcode.bomberman

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector
import kotlin.math.roundToInt

open class Entity(val grid : Grid) {
    var position = PVector(0f,0f)
    var direction = PVector(1f, 0f)
    var speed = 0f
    var isDead = false

    open fun update(deltaTime : Float) {
        move()
    }

    open fun show(applet : PApplet) {
        applet.fill(255f,0f,0f)
        applet.ellipseMode(PConstants.CENTER)
        applet.ellipse((position.x * grid.gridSize).toFloat(), (position.y * grid.gridSize).toFloat(),
            grid.gridSize.toFloat(), grid.gridSize.toFloat())
    }

    fun roundPosition() : PVector {
        return PVector(position.x.roundToInt().toFloat(), position.y.roundToInt().toFloat())
    }

    fun getAbsolutePosition(): PVector = PVector((position.x*grid.gridSize).toFloat(), (position.y*grid.gridSize).toFloat())


    /**
     * Determines if this entity does not want [other] to enter its field.
     * @param other The entity that is trying to enter this ones field
     * @return True if other entity is not allowed to enter this entities field, false if not.
     */
    fun isMoveRejected(other : Entity) : Boolean {
        return true
    }


    /**
     * Moves this entity if the move is not rejected by [Field.isMoveRejected] and manages changing of fields doing so
     */
    private fun move() {
        if (speed > 0 && !grid.isMoveRejected(this)) {
            val currentField = roundPosition()
            position.add(PVector.mult(direction, speed))

            if (direction.x==0f) position.x = position.x.roundToInt().toFloat()
            if (direction.y==0f) position.y = position.y.roundToInt().toFloat()

            val newField = roundPosition()
            if (newField != currentField) {
                grid.fields[currentField.x.toInt()][currentField.y.toInt()].entitiesOnField.remove(this)
                grid.fields[newField.x.toInt()][newField.y.toInt()].entitiesOnField.add(this)

            }
            println("Moving with $speed in $direction")
        }

    }


    open fun slayThatBitch() {}




}