package com.ebrithilcode.bomberman.gridBuilder

import com.ebrithilcode.bomberman.Field
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector
import kotlin.math.floor
import com.ebrithilcode.bomberman.Grid
import com.ebrithilcode.bomberman.Level

fun main() {
    PApplet.runSketch(arrayOf("empty"), GridBuilder(10, 10))
}

class GridBuilder(width:Int, height:Int) : PApplet() {

    var selectedField : TextField? = null

    val grid = Grid(width, height, 50.0)
    var floorType = 0

    val level = Level(grid)

    val gridOffset = PVector(0f,0f)
    val colors = intArrayOf(color(255), color(0,0,255), color(255,0,0))

    var selectionStart : PVector? = null
    override fun settings() {
        size(640, 640)
    }

    override fun setup() {
        gridOffset.x = (width/2f - grid.width/2*grid.gridSize).toFloat()
        gridOffset.y = (height/2f - grid.height/2*grid.gridSize).toFloat()
        super.setup()
    }


    override fun draw() {


        background(255)
        for (x in grid.fields.indices) {
            for (y in grid.fields[x].indices) {
                fill(colors[grid.fields[x][y].byteState.toInt()])
                stroke(0)
                val position = PVector.add(gridOffset, PVector((x*grid.gridSize).toFloat(), (y*grid.gridSize).toFloat()))
                rect(position.x, position.y, grid.gridSize.toFloat(), grid.gridSize.toFloat())
            }
        }
        if (selectionStart!=null) {
            val range = getSelectionRanges()
            println("Range is ${range[0]}/${range[1]}")
            for (x in range[0]) {
                for (y in range[1]) {
                    fill(colors[floorType])
                    stroke(0)
                    val position = PVector.add(gridOffset, PVector((x*grid.gridSize).toFloat(), (y*grid.gridSize).toFloat()))
                    rect(position.x, position.y, grid.gridSize.toFloat(), grid.gridSize.toFloat())
                }
            }
        }
        fill(0f,255f,0f)

        textAlign(CENTER,CENTER)
        textSize(20f)
        for (entry in level.positionMap.entries) {
            text(entry.key, ((entry.value.x+0.5)*grid.gridSize+gridOffset.x).toFloat(),((entry.value.y+0.5)*grid.gridSize+gridOffset.y).toFloat())
        }

        selectedField?.let {
            text(it.text, ((it.position.x+0.5) * grid.gridSize + gridOffset.x).toFloat(), ((it.position.y+0.5)*grid.gridSize+gridOffset.y).toFloat())
        }
        if (selectedField==null) {
            fill(colors[floorType])
            ellipseMode(CENTER)
            ellipse(mouseX.toFloat(), mouseY.toFloat(),20f,20f)
        }
    }

    override fun keyPressed() {

        if (selectedField!=null) {
            manageSelectedKey()
        } else {
            if (key == 'd') {
                floorType++
            } else if (key == 'a') {
                floorType--
            } else if (key=='s') {
                level.saveToFile("TestLevel.data")
            }
            floorType %= 3
        }
    }

    private fun manageSelectedKey() {
        selectedField?.let {
            val isKeycodeReturn = (keyCode == PConstants.RETURN.toInt() || keyCode == PConstants.ENTER.toInt())
            if (key == PConstants.BACKSPACE) {
                it.text =
                    it.text.substring(0, max(0, it.text.length - 1))
            } else if (key==PConstants.RETURN||key==PConstants.ENTER) {
                if (it.text.length>0)
                level.positionMap.put(it.text, it.position)
                selectedField = null
            } else {
                it.text += key
            }
        }
    }

    override fun mousePressed() {
        if (mouseButton==LEFT) {
            selectionStart = PVector(mouseX.toFloat(), mouseY.toFloat())
        } else {
            if (selectedField==null) {

                selectedField = TextField(
                    position = PVector(
                        floor((mouseX - gridOffset.x) / grid.gridSize).toFloat(),
                        floor((mouseY - gridOffset.y) / grid.gridSize).toFloat()
                    )
                )
                val entry = level.positionMap.filterValues {
                    it==selectedField!!.position
                }.keys
                for (str in entry) {
                    selectedField?.text = str
                    level.positionMap.remove(str)
                }

            }
        }
    }

    override fun mouseReleased() {
        if (mouseButton==LEFT) {
            if (selectionStart != null) {

                val slicingRange = getSelectionRanges()
                for (x in slicingRange[0]) {
                    for (y in slicingRange[1]) {
                        grid.fields[x][y].state = Field.numToState(floorType.toByte()) ?: Field.State.FREE
                    }
                }
            }

            selectionStart = null
        }
    }

    private fun getSelectionRanges() : Array<IntRange> {
        if (selectionStart==null) return arrayOf()
        val saveStart :PVector = PVector.sub(selectionStart!!, gridOffset)
        val mousePos = PVector(mouseX.toFloat(), mouseY.toFloat()).sub(gridOffset)

        val minX = min(saveStart.x, mousePos.x)
        val maxX = max(saveStart.x, mousePos.x)
        val xSlice: IntRange = floor(minX/grid.gridSize).toInt() .. floor(x = maxX/grid.gridSize).toInt()
        val minY = min(saveStart.y, mousePos.y)
        val maxY = max(saveStart.y, mousePos.y)
        val ySlice = floor(minY/grid.gridSize).toInt() .. floor(x = maxY/grid.gridSize).toInt()
        return arrayOf(xSlice, ySlice)

    }
}

data class TextField(var text:String="", var position:PVector)


