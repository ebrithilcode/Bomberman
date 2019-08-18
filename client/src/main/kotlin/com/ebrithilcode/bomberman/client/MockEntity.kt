package com.ebrithilcode.bomberman.client

import com.ebrithilcode.bomberman.common.Direction
import com.ebrithilcode.bomberman.common.klaxon.EntityData
import processing.core.PApplet
import processing.core.PImage

class MockEntity(val data : EntityData) : RenderingComponent(data.id, data.posX, data.posY) {

    var lastUpdateTimeStamp : Long = 0L
    lateinit var sprite : PImage //TODO

    override fun update(currentTime: Long, applet: PApplet) {
        val deltaTime = currentTime - lastUpdateTimeStamp
        lastUpdateTimeStamp = currentTime
        when(data.facing) {
            Direction.NORTH -> posY -= (deltaTime/1000) * data.velocity
            Direction.EAST -> posX += (deltaTime/1000) * data.velocity
            Direction.SOUTH -> posY += (deltaTime/1000) * data.velocity
            Direction.WEST -> posX -= (deltaTime/1000) * data.velocity
        }
        applet.image(sprite, posX, posY)
    }

}