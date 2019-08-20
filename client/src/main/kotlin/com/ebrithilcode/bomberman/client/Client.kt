package com.ebrithilcode.bomberman.client

import com.beust.klaxon.Klaxon
import com.ebrithilcode.bomberman.common.PlayerAction
import com.ebrithilcode.bomberman.common.asyncReceive
import com.ebrithilcode.bomberman.common.asyncSend
import com.ebrithilcode.bomberman.common.getDataAsString
import com.ebrithilcode.bomberman.common.klaxon.ClientRegisterMessage
import com.ebrithilcode.bomberman.common.klaxon.PlayerActionMessage
import com.ebrithilcode.bomberman.common.klaxon.RenderMessage
import com.ebrithilcode.bomberman.common.klaxon.ServerConfirmationMessage
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jetbrains.annotations.TestOnly
import processing.core.PApplet
import java.net.*
import java.util.*
import javax.annotation.processing.SupportedAnnotationTypes
import kotlin.collections.HashMap

object Client : PApplet() {

    private const val REMOTE_IP = "127.0.0.1"
    private const val REMOTE_PORT = 8001
    private var remoteConnectionPort = -1;

    private val json = Json(JsonConfiguration.Stable)

    const val GRID_SIZE = 50f

    var conf: KeyConfig = KeyConfig.DEFAULT_CONFIG

    private val currentPlayerActions: EnumSet<PlayerAction> = EnumSet.noneOf(PlayerAction::class.java)

    @Volatile
    private lateinit var grid: Grid
    @Volatile
    private var idToEntityMap: MutableMap<Long, MockEntity> = HashMap()
    @Volatile
    private var idToAnimationMap: MutableMap<Long, Animation> = HashMap()

    private lateinit var job : Job


    override fun settings() {
        size(800, 800)
    }

    override fun setup() {
        println("Started setup...")
        frameRate(60f)
        job = launchClient("Philipp")
        Runtime.getRuntime().addShutdownHook(Thread {
            job.cancel()
        })
        println("Finished setup!")
    }

    override fun draw() {
        background(255)
        val currentTime = System.currentTimeMillis()

        pushMatrix()
        //translate(width/2f - grid.gridSize * grid.width/2, height/2f - grid.gridSize*grid.width/2)
        for(rc : RenderingComponent in idToEntityMap.values) {
            pushMatrix()
            translate(rc.posX * grid.gridSize, rc.posY * grid.gridSize)
            rc.update(currentTime, this)
            popMatrix()
        }
        for(rc : RenderingComponent in idToAnimationMap.values) {
            pushMatrix()
            translate(rc.posX *grid.gridSize, rc.posY * grid.gridSize)
            rc.update(currentTime, this)
            popMatrix()
        }
        popMatrix()
    }


    private fun launchClient(name : String) : Job = CoroutineScope(Dispatchers.Default).launch {
        val bytes = Klaxon().toJsonString(ClientRegisterMessage(name)).toByteArray(Charsets.UTF_8)
        val sendPacket = DatagramPacket(bytes, bytes.size, InetSocketAddress(REMOTE_IP, REMOTE_PORT))
        DatagramSocket().use {
            val recvPacket = DatagramPacket(ByteArray(4096), 4096)
            loop@ while (true) {
                println("Sending registration message to server...")
                it.asyncSend(sendPacket)
                println("Sent registration message!")
                try {
                    it.soTimeout = 3000
                    it.asyncReceive(recvPacket)
                } catch (ex: SocketTimeoutException) {
                    println("Server did not respond in time, retrying...")
                    continue@loop
                }
                val confirmationMsg = Klaxon().parse<ServerConfirmationMessage>(recvPacket.getDataAsString())
                        ?: throw IllegalStateException("Error parsing ServerConfirmationMessage:\n ${recvPacket.getDataAsString()}")
                if (!confirmationMsg.success) throw IllegalStateException("Server rejected registration!")
                break@loop
            }
            val recvJob = launch {
                startMessageReceiveLoop(it)
            }
            val sendJob = launch {
                startMessageSendLoop(it)
            }
            recvJob.join()
            sendJob.join()
        }
    }

    private suspend fun startMessageReceiveLoop(socket : DatagramSocket) = coroutineScope {
        println("Now listening for incoming RenderMessages...")
        val recvPacket = DatagramPacket(ByteArray(8192), 8192)
        while (isActive) {
            recvPacket.length = recvPacket.data.size
            try {
                socket.asyncReceive(recvPacket)
            }
            catch (ex : SocketTimeoutException) {
                println("Server timed out...")
                //TODO: if multiple timeouts happen then stop exceptionally
            }
            val msg = json.parse(RenderMessage.serializer(), recvPacket.getDataAsString())
            grid = Grid.fromData(msg.grid)
            idToEntityMap = msg.entities.asSequence() //entities need to be recreated every time since data can change
                    .associateByTo(mutableMapOf(), { it.id }, { MockEntity(it) })
            idToAnimationMap = msg.animations.asSequence() //animations can and should be reused
                    .associateByTo(mutableMapOf<Long, Animation>(), { it.id }, { idToAnimationMap[it.id] ?: Animation(it) })

        }
        println("Stopped listening for incoming RenderMessages!")
    }

    private suspend fun startMessageSendLoop(socket : DatagramSocket) = coroutineScope {
        println("Now sending PlayerActionMessages...")
        while(isActive) {
            val bytes = json.stringify(PlayerActionMessage.serializer(), PlayerActionMessage(currentPlayerActions.toHashSet())).toByteArray(Charsets.UTF_8)
            val packet = DatagramPacket(bytes, bytes.size, InetSocketAddress(REMOTE_IP, REMOTE_PORT))
            socket.asyncSend(packet)
        }
        println("Stopped sending PlayerActionMessages!")
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