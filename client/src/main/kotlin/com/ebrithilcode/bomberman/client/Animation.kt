package com.ebrithilcode.bomberman.client

import com.ebrithilcode.bomberman.common.klaxon.AnimationData
import kotlinx.coroutines.flow.asFlow
import processing.core.PApplet
import processing.core.PImage

class Animation(val data : AnimationData) : RenderingComponent(data.id, data.posX, data.posY) {

    //val duration = data.delayPerImage.sum()

    override fun update(currentTime: Long, applet : PApplet) {
//        val animationTime = (currentTime - startTime) % duration
//        var sum = 0
//        for((index, time) in delayPerImage.withIndex()) {
//            sum += time
//            if(sum >= animationTime) {
//                applet.image(images[index], posX, posY)
//                return
//            }
//        }
    }

}