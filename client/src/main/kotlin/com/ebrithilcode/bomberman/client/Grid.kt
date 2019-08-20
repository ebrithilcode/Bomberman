package com.ebrithilcode.bomberman.client

import processing.core.PApplet
import java.nio.ByteBuffer

class Grid(val width : Int, val height: Int) {

    val gridSize = 50f

    companion object {
        fun fromData(arr : Array<Byte>): Grid {
            val buffer = ByteBuffer.wrap(arr.toByteArray())
            val grid = Grid(buffer.int, buffer.int)
            for (subArray in grid.fields) {
                for (index in subArray.indices) subArray[index] = buffer.get()
            }
            return grid
        }
    }

    //Encoding: 0: Free, 1: Breakable, 2: Solid
    val fields = Array(width) {ByteArray(height)}

    fun show(applet : PApplet) {
        for ((xPos, column) in fields.withIndex()) {
            for ((yPos, value) in column.withIndex()) {
                when (value) {
                    0.toByte() -> applet.fill(255)
                    1.toByte() -> applet.fill(0f,0f,255f)
                    2.toByte() -> applet.fill(255f,0f,0f)
                }
                applet.rect(xPos*gridSize, yPos*gridSize, gridSize, gridSize)
            }
        }
    }


}