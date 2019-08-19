package com.ebrithilcode.bomberman.client

import com.ebrithilcode.bomberman.common.klaxon.AnimationData
import processing.core.PApplet
import processing.core.PVector

class Animation(val data : AnimationData) : RenderingComponent(data.id, data.posX, data.posY) {

    val bombAnimation = BombAnimation(PVector(data.posX, data.posY),
        data.metaData.array<Int>("dirMags")?.toIntArray() ?: intArrayOf(0,0,0,0)
    )

    override fun update(currentTime: Long, applet : PApplet) {
        bombAnimation.render(applet, Client.GRID_SIZE, currentTime)
    }

}