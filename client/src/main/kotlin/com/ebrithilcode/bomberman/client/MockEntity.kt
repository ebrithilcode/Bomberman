package com.ebrithilcode.bomberman.client

import com.ebrithilcode.bomberman.common.Direction
import com.ebrithilcode.bomberman.common.klaxon.EntityData
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage

class MockEntity(val data : EntityData) : RenderingComponent(data.spriteId, data.posX, data.posY) {

    var lastUpdateTimeStamp : Long = 0L
    //lateinit var sprite : PImage //TODO

    override fun update(currentTime: Long, applet: PApplet) {

        val colors = arrayOf(applet.color(255f,0f,0f),
            applet.color(0f,255f,0f),applet.color(0f,255f,0f),applet.color(0f,255f,0f),
            applet.color(255f,0f,255f),
            applet.color(255f,255f,0f),applet.color(255f,255f,0f),applet.color(255f,255f,0f),applet.color(255f,255f,0f),
            applet.color(255f,255f,0f),applet.color(255f,255f,0f)
            )

        val deltaTime = currentTime - lastUpdateTimeStamp
        lastUpdateTimeStamp = currentTime
        when(data.facing) {
            Direction.NORTH -> posY -= (deltaTime/1000) * data.velocity
            Direction.EAST -> posX += (deltaTime/1000) * data.velocity
            Direction.SOUTH -> posY += (deltaTime/1000) * data.velocity
            Direction.WEST -> posX -= (deltaTime/1000) * data.velocity
        }
        //applet.image(Clie, posX, posY)
        println("Rendering entity at $posX/$posY")
        applet.fill(colors[id.toInt()])
        applet.ellipse(0f, 0f ,50f, 50f)
        applet.stroke(0)
        applet.strokeWeight(3f)
        //applet.line(0f, 0f, 25f, 25f)
        applet.fill(0f,0f,255f)
        applet.textSize(10f)
        applet.textAlign(PConstants.CENTER, PConstants.CENTER)
        applet.text(data.data, 0f, 0f)
    }

}