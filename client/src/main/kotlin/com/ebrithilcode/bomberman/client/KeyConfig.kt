package com.ebrithilcode.bomberman.client

import com.ebrithilcode.bomberman.common.PlayerAction
import processing.core.PConstants

class KeyConfig(private val keyToActionMap : Map<Char, PlayerAction>, private val keyCodeToActionMap : Map<Int, PlayerAction>) {

    companion object {
        val DEFAULT_CONFIG by lazy {KeyConfig(mapOf('w' to PlayerAction.UP, 's' to PlayerAction.DOWN, 'a' to PlayerAction.LEFT, 'd' to PlayerAction.RIGHT, ' ' to PlayerAction.SWITCH), mapOf())}
    }

    fun getPlayerAction(key : Char) : PlayerAction = keyToActionMap[key] ?: PlayerAction.UNASSIGNED

    fun getPlayerAction(keyCode : Int) : PlayerAction = keyCodeToActionMap[keyCode] ?: PlayerAction.UNASSIGNED

}