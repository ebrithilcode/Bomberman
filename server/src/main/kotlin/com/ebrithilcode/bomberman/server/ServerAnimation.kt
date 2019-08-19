package com.ebrithilcode.bomberman.server

import com.ebrithilcode.bomberman.common.klaxon.AnimationData

class ServerAnimation(val animationData: AnimationData, val lifeTime : Long) {
    val lifeStart = System.currentTimeMillis()


    fun isDead() : Boolean = System.currentTimeMillis()-lifeStart > lifeTime
}