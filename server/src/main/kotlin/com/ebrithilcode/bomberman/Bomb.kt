package com.ebrithilcode.bomberman

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector

class Bomb(grid: Grid, val lifeTime : Int, val placer : Pawn) : Entity(grid) {
    val startTime = System.currentTimeMillis()

    companion object {
        val defaultDirections = arrayOf(PVector(1f,0f), PVector(0f,1f),PVector(-1f,0f), PVector(0f,-1f))
    }

    override fun update(deltaTime:Float) {
        if (System.currentTimeMillis()-startTime > lifeTime) {
            explode()
        }
    }
    override fun show(applet: PApplet) {
        val time = System.currentTimeMillis()-startTime
        applet.colorMode(PConstants.HSB)
        applet.fill(330/360f*255, 255-((time/3)%255f), 255f)
        applet.ellipse((position.x*grid.gridSize).toFloat(), (position.y*grid.gridSize).toFloat(),
            grid.gridSize.toFloat(), grid.gridSize.toFloat()
        )
        applet.colorMode(PConstants.RGB)
    }

    private fun explode() : IntArray {
        //rays are storing how long the bomb could explode in each direction from east on clockwise
        val rays = IntArray(4)
        for ((index, dir) in defaultDirections.withIndex()) {

            //The field to check for entities to slay and for blocks that end this explosion
            val tracer = roundPosition()
            inner@for (dist in 1..placer.explosionRange) {
                tracer.add(dir)
                grid.getField(tracer).let {
                    if (it.state!=Field.State.FREE) {
                        rays[index] = dist
                        //If the field was breakable, smash it
                        if (it.state==Field.State.BREAKABLE) it.breakFree()
                    }


                    //-But the children? --Kill 'em all...
                    for (onField in it.entitiesOnField) {
                        onField.slayThatBitch()
                    }
                }

                //A ray already ended
                if (rays[index]>0) break@inner
            }
            if (rays[index]==0) rays[index] = placer.explosionRange
        }
        isDead = true
        return rays
    }

}