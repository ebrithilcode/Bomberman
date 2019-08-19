package com.ebrithilcode.bomberman.client

import com.beust.klaxon.Klaxon
import com.ebrithilcode.bomberman.common.PlayerAction
import com.ebrithilcode.bomberman.common.asyncReceive
import com.ebrithilcode.bomberman.common.asyncSend
import com.ebrithilcode.bomberman.common.getDataAsString
import com.ebrithilcode.bomberman.common.klaxon.PlayerActionMessage
import com.ebrithilcode.bomberman.common.klaxon.RenderMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import processing.core.PApplet
import processing.core.PVector
import java.io.StringReader
import java.net.*
import java.util.*
import kotlin.collections.HashMap

object Client : PApplet() {

    private const val REMOTE_IP = "127.0.0.1"
    private const val REMOTE_REGISTRATION_PORT = 8001
    private var remoteConnectionPort = -1;

    const val GRID_SIZE = 50f

    var conf: KeyConfig = KeyConfig.DEFAULT_CONFIG

    private val currentPlayerActions: EnumSet<PlayerAction> = EnumSet.noneOf(PlayerAction::class.java)

    @Volatile
    private lateinit var grid: Grid
    @Volatile
    private var idToEntityMap: MutableMap<Long, MockEntity> = HashMap()
    @Volatile
    private var idToAnimationMap: MutableMap<Long, Animation> = HashMap()

    private var connectionCoroutineScope = CoroutineScope(Dispatchers.Default)

    private var socket = DatagramSocket()

    override fun settings() {
        size(800, 800)
    }

    val testAnimation = BombAnimation(PVector(3f,3f), intArrayOf(1,2,3,4))

    override fun setup() {
        frameRate(60f)

        runBlocking {
            registerAtServer()
        }
        launchServerReceive()
        launchServerSend()
        ellipseMode(CENTER)
        rectMode(CENTER)
    }

    override fun draw() {
        background(255)
        val currentTime = System.currentTimeMillis()
        for(rc : RenderingComponent in idToEntityMap.values) {
            pushMatrix()
            translate(rc.posX, rc.posY)
            rc.update(currentTime, this)
            popMatrix()
        }
        for(rc : RenderingComponent in idToAnimationMap.values) {
            pushMatrix()
            translate(rc.posX, rc.posY)
            rc.update(currentTime, this)
            popMatrix()
        }
    }

    fun handleMessage(msg: RenderMessage) {
        this.grid = Grid.fromData(msg.grid)
        idToEntityMap = msg.entities.asSequence() //entities need to be recreated every time since data can change
                .associateByTo(mutableMapOf(), { it.id }, { MockEntity(it) })
        idToAnimationMap = msg.animations.asSequence() //animations can and should be reused
                .associateByTo(mutableMapOf<Long, Animation>(), { it.id }, { idToAnimationMap[it.id] ?: Animation(it) })

    }

    private fun registerAtServer() : Job = connectionCoroutineScope.launch {
        val recvPacket = DatagramPacket(ByteArray(4096), 4096, InetSocketAddress(REMOTE_IP, REMOTE_REGISTRATION_PORT))
        val registrationSocket = DatagramSocket()
        registrationSocket.use {
            it.asyncReceive(recvPacket)
            val json = Klaxon().parseJsonObject(StringReader(recvPacket.getDataAsString()))
            if (json.boolean("success") == true) println("Successfully registered at server")
            remoteConnectionPort = json.int("port") ?: throw java.lang.IllegalStateException() //TODO: write message
        }
    }

    private fun launchServerReceive() : Job = connectionCoroutineScope.launch {
        val recvPacket = DatagramPacket(ByteArray(8192), 8192)
        socket.use {
            while (isActive) {
                recvPacket.length = recvPacket.data.size
                it.asyncReceive(recvPacket)
                val msg = Klaxon().parse<RenderMessage>(recvPacket.getDataAsString())
                        ?: throw IllegalStateException() //TODO write message
                handleMessage(msg)
            }
        }
        println("Stopped listening for incoming RenderMessages!")
    }

    private fun launchServerSend() : Job = connectionCoroutineScope.launch {
        socket.use {
            while(isActive) {
                val bytes = Klaxon().toJsonString(PlayerActionMessage(currentPlayerActions)).toByteArray(Charsets.UTF_8)
                val packet = DatagramPacket(bytes, bytes.size, InetSocketAddress(REMOTE_IP, remoteConnectionPort))
                it.asyncSend(packet)
            }
        }
        println("Stopped sending RenderMessages!")
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