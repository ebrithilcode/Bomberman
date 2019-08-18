package com.ebrithilcode.bomberman.client

import com.ebrithilcode.bomberman.common.Direction
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import processing.core.PVector
import java.util.Collections.rotate
import kotlin.math.min

class BombAnimation(val position : PVector, val directionalLength : IntArray) {
    companion object {
        lateinit var center : PImage
        lateinit var edges : Array<PImage>
        lateinit var ends : Array<PImage>
        fun setup(applet: PApplet, gridSize : Float) {
            val baseFile = "${System.getProperty("user.dir")}/src/main/resources/ExplosionTiles"
            center = applet.loadImage("$baseFile/BummZentrum.png")
            edges = Array(2) {applet.loadImage("$baseFile/StreckenTile${it+1}.png")}
            ends = Array(2) {applet.loadImage("$baseFile/Ende${it+1}.png")}
            center.resize(gridSize.toInt(), gridSize.toInt())
            edges.forEach { it.resize(gridSize.toInt(), gridSize.toInt()) }
            ends.forEach { it.resize(gridSize.toInt(), gridSize.toInt()) }
        }
    }

    fun render(applet: PApplet, gridSize : Float, timeStamp : Long) {
        applet.imageMode(PConstants.CENTER)

        applet.pushMatrix()
        applet.translate(position.x*gridSize, position.y*gridSize)
        applet.scale(
            min(1f,(timeStamp/100f))
        )

        val directions = arrayOf(Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH)
        applet.image(center, 0f, 0f)
        for ( (index, value) in directionalLength.withIndex()) {
            for (range in 1 until value) {

                val pos = PVector.mult(directions[index].vector, range.toFloat())
                applet.pushMatrix()
                applet.translate(pos.x * gridSize, pos.y * gridSize)
                applet.rotate(PConstants.HALF_PI*index)
                applet.image(edges[range%2], 0f, 0f)
                applet.popMatrix()
            }

            if (value>0) {
                val pos = PVector.mult(directions[index].vector, value.toFloat())
                applet.pushMatrix()
                applet.translate(pos.x * gridSize, pos.y * gridSize)
                applet.rotate(PConstants.HALF_PI * index)
                applet.image(ends[(index + 1) % 2], 0f, 0f)
                applet.popMatrix()
            }
        }

        applet.popMatrix()
    }

}