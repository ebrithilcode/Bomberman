package com.ebrithilcode.bomberman.client

import processing.core.PApplet

abstract class RenderingComponent(val id: Long, var posX : Float, var posY : Float) {

    infix fun idEquals (other : RenderingComponent) : Boolean = id == other.id

    abstract fun update(currentTime : Long, applet : PApplet)

}