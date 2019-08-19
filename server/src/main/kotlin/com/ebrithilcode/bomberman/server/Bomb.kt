package com.ebrithilcode.bomberman.server

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.ebrithilcode.bomberman.common.Direction
import com.ebrithilcode.bomberman.common.klaxon.AnimationData
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector


class Bomb(grid: Grid, val lifeTime: Int, val placer: Pawn) : Entity(grid, 4) {
    val startTime = System.currentTimeMillis()

    companion object {
        val defaultDirections = arrayOf(Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH)
    }

    override fun update(deltaTime: Float) {
        //Called for bomb movement with gloves
        super.update(deltaTime)
        if (System.currentTimeMillis() - startTime > lifeTime) {
            explode()
        }
    }

    override fun show(applet: PApplet) {
        val time = System.currentTimeMillis() - startTime
        applet.colorMode(PConstants.HSB)
        applet.fill(330 / 360f * 255, 255 - ((time / 3) % 255f), 255f)
        applet.ellipse((position.x * grid.gridSize).toFloat(), (position.y * grid.gridSize).toFloat(),
                grid.gridSize.toFloat(), grid.gridSize.toFloat()
        )
        applet.colorMode(PConstants.RGB)
    }

    override fun isMoveRejected(other: Entity): Boolean {
        if (other is Pawn) {
            if (other.knockingBombs) {
                facing = other.facing
                speed = other.speed
            }
        }
        return true
    }

    private fun explode(): IntArray {
        //For the field the bomb is lying on, we just have to check for entities
        for (onField in grid.getField(roundPosition()).entitiesOnField) onField.kill()
        //rays are storing how long the bomb could explode in each direction from east on clockwise
        val rays = IntArray(4)
        for ((index, dir) in defaultDirections.withIndex()) {

            //The field to check for entities to slay and for blocks that end this explosion
            val tracer = roundPosition()
            inner@ for (dist in 1..placer.explosionRange) {
                tracer.add(dir.vector)
                grid.getField(tracer).let {
                    if (it.state != Field.State.FREE) {
                        rays[index] = dist
                        //If the field was breakable, smash it
                        if (it.state == Field.State.BREAKABLE) it.breakFree()
                    }


                    //-But the children? --Kill 'em all...
                    for (onField in it.entitiesOnField) {
                        onField.kill()
                    }
                }

                //A ray already ended
                if (rays[index] > 0) break@inner
            }
            if (rays[index] == 0) rays[index] = placer.explosionRange
        }
        isDead = true
        val pos = roundPosition()
        val animationData = AnimationData(0,0,pos.x, pos.y, System.currentTimeMillis(), JsonObject(
            mutableMapOf("dirMags" to JsonArray(rays.asList()))
        ))
        grid.animationList.add(ServerAnimation(animationData, 500L))

        return rays
    }

}