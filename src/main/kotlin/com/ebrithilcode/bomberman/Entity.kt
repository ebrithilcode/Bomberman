package com.ebrithilcode.bomberman

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector

class Entity(val grid : Grid) {
    var position = PVector(0f,0f)
    var direction = PVector(0f, 0f)
    var speed = 0.5f

    fun update() {
        grid.requestMove(this)
    }

    fun show(applet : PApplet) {
        applet.fill(255f,0f,0f)
        applet.ellipseMode(PConstants.RADIUS)
        applet.ellipse((position.x * grid.gridSize).toFloat(), (position.y * grid.gridSize).toFloat(),
            grid.gridSize.toFloat(), grid.gridSize.toFloat())
    }


}