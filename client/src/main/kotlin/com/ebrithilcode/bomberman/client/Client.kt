package com.ebrithilcode.bomberman.client

import com.beust.klaxon.Json
import com.ebrithilcode.bomberman.common.PlayerAction
import com.ebrithilcode.bomberman.common.klaxon.PlayerActionMessage
import com.ebrithilcode.bomberman.common.klaxon.RenderMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import processing.core.PApplet
import processing.core.PConstants
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import kotlin.collections.HashMap

class Client(val serverIP : String, val serverPort : Int) : PApplet() {

    var conf: KeyConfig = KeyConfig.DEFAULT_CONFIG

    private val renderingSocket = DatagramSocket()

    private val currentPlayerActions: EnumSet<PlayerAction> = EnumSet.noneOf(PlayerAction::class.java)

    @Volatile
    private var grid: ByteArray = ByteArray(0)
    @Volatile
    private var idToEntityMap: MutableMap<Long, MockEntity> = HashMap()
    @Volatile
    private var idToAnimationMap: MutableMap<Long, Animation> = HashMap()

    override fun settings() {
        size(300, 300)
    }

    override fun setup() {
        frameRate(60f)
    }

    override fun draw() {
        background(255)
        val currentTime = System.currentTimeMillis()
        for(rc : RenderingComponent in idToEntityMap.values){
            rc.update(currentTime, this)
        }
        for(rc : RenderingComponent in idToAnimationMap.values){
            rc.update(currentTime, this)
        }
    }

    fun handleMessage(msg: RenderMessage) {
        this.grid = msg.grid
        idToEntityMap = msg.entities.asSequence() //entities need to be recreated every time since data can change
                .associateByTo(HashMap(), { it.id }, { MockEntity(it) })
        idToAnimationMap = msg.animations.asSequence() //animations can and should be reused
                .associateByTo(HashMap(), { it.id }, { idToAnimationMap[it.id] ?: Animation(it) })

    }


    override fun keyPressed() {
        if (key.toInt() != CODED) {
            currentPlayerActions.add(conf.getPlayerAction(key))
        } else {
            currentPlayerActions.add(conf.getPlayerAction(keyCode))
        }
    }

    override fun keyReleased() {
        if (key.toInt() != CODED) {
            currentPlayerActions.remove(conf.getPlayerAction(key))
        } else {
            currentPlayerActions.remove(conf.getPlayerAction(keyCode))
        }
    }

}