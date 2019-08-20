package com.ebrithilcode.bomberman.server

import com.beust.klaxon.Klaxon
import com.ebrithilcode.bomberman.common.asyncReceive
import com.ebrithilcode.bomberman.common.asyncSend
import com.ebrithilcode.bomberman.common.getDataAsString
import com.ebrithilcode.bomberman.common.klaxon.ClientRegisterMessage
import com.ebrithilcode.bomberman.common.klaxon.PlayerActionMessage
import com.ebrithilcode.bomberman.common.klaxon.ServerConfirmationMessage
import kotlinx.coroutines.*
import processing.core.PApplet
import processing.core.PImage
import java.net.*
import java.util.concurrent.atomic.AtomicLong


object Server : PApplet() {

    private const val PORT = 8001
    private const val AVAILABLE_PLAYER_SPRITES = 4


    private val clientList: MutableList<Socket> = mutableListOf()

    private val addrToPlayerMap: MutableMap<SocketAddress, Player> = HashMap(4)

    //private val playerConnectionSocket = DatagramSocket(CONNECTION_PORT)

    private val level = Level.fromFile("${System.getProperty("user.dir")}/src/main/resources/TestLevel.data")
    //private val level = Level.fromFile("/home/rsttst/Coding/Projects/IntelliJ/Bomberman/server/src/main/resources/com.ebrithilcode.bomberman.server/TestLevel.data")
    private val grid = level.grid

    private val idToSpriteMap: MutableMap<Long, PImage> = HashMap(/*TODO: size arg*/)
    private val spriteIdCounter = AtomicLong(0)

    lateinit var job : Job

    override fun settings() {
        size(800, 800)
    }

    override fun setup() {
        println("Started setup...")
        frameRate(60f)
        job = launchServer(1)
        Runtime.getRuntime().addShutdownHook(Thread {
            job.cancel()
        })
        println("Finished setup!")
    }
    override fun draw() {
        background(255)
        fill(0f, 0f, 255f)
        textAlign(CENTER, CENTER)

        pushMatrix()
        translate((width / 2f - grid.width / 2f * grid.gridSize), (height / 2f - grid.height / 2f * grid.gridSize))
        grid.show(this)
        grid.update(1f / frameRate)
        popMatrix()
        textSize(24f)
        text("FrameRate: $frameRate", 20f, 20f)
    }

    private fun loadSpriteAndGetIdForPlayer(playerNum: Int): Long = loadSprite("player${playerNum % AVAILABLE_PLAYER_SPRITES}.png").first

    fun loadSprite(path: String): Pair<Long, PImage> {
        val img = loadImage(path) ?: throw IllegalArgumentException() //TODO: write message
        if (img.width == -1) throw IllegalArgumentException() //TODO: write message
        val result = Pair(spriteIdCounter.getAndIncrement(), img)
        idToSpriteMap += result
        return result
    }

    fun getSprite(id: Long): PImage = idToSpriteMap[id] ?: throw IllegalArgumentException() //TODO: write message

    private fun launchServer(numPlayers: Int): Job = CoroutineScope(Dispatchers.IO).launch {
        var playersLeft = numPlayers
        val recvPacket = DatagramPacket(ByteArray(4096), 4096) //receive packet can be reused
        val socket = DatagramSocket(PORT)
        socket.use {
            while (playersLeft > 0) {
                //wait for client to register
                recvPacket.length = recvPacket.data.size //reuse recvPacket
                it.asyncReceive(recvPacket)
                if (addrToPlayerMap.containsKey(recvPacket.socketAddress)) {
                    println("Client at ${recvPacket.socketAddress} already registered with name ${addrToPlayerMap[recvPacket.socketAddress]!!.name}!")
                    continue
                }
                val msg = Klaxon().parse<ClientRegisterMessage>(recvPacket.getDataAsString())
                        ?: throw IllegalStateException("Error while parsing ClientRegisterMessage:\n ${recvPacket.getDataAsString()}")
                val player = Player(msg.name, Pawn(grid, 0))
                println("Player $player at ${recvPacket.socketAddress} connected!")
                addrToPlayerMap[recvPacket.socketAddress] = player
                //send confirmation with connection port
                println("Sending confirmation message...")
                val confirmationBytes = Klaxon().toJsonString(ServerConfirmationMessage(true)).toByteArray(Charsets.UTF_8)
                val sendPacket = DatagramPacket(confirmationBytes, confirmationBytes.size, recvPacket.socketAddress)
                it.asyncSend(sendPacket)
                println("Sent confirmation message!")
                playersLeft--
            }
            val recvJob = launch {
                startMessageReceiveLoop(socket)
            }
            val sendJob = launch {
                startMessageSendLoop(socket)
            }
            recvJob.join()
            sendJob.join()
        }
    }

    private suspend fun startMessageReceiveLoop(socket : DatagramSocket) = coroutineScope {
        println("Now listening for incoming PlayerActionMessages...")
        val recvPacket = DatagramPacket(ByteArray(8192), 8192)
        while (isActive) {
            recvPacket.length = recvPacket.data.size
            socket.asyncReceive(recvPacket)
            val player = addrToPlayerMap[recvPacket.socketAddress]
                    ?: throw IllegalStateException("Received packet from ${recvPacket.socketAddress} who is not a registered Player")
            val msg = Klaxon().parse<PlayerActionMessage>(recvPacket.getDataAsString())
                    ?: throw IllegalStateException("Error while parsing PlayerActionMessage:\n ${recvPacket.getDataAsString()}")
            player.onPlayerActionUpdate(msg.actions)
        }
        println("Stopped listening for incoming PlayerActionMessages!")
    }

    private suspend fun startMessageSendLoop(socket : DatagramSocket) = coroutineScope {
        println("Now sending RenderMessages to all registered Players...")
        while(isActive) {
            val bytes = Klaxon().toJsonString(grid.encodeToRenderMessage()).toByteArray(Charsets.UTF_8)
            for(addr in addrToPlayerMap.keys) {
                val packet = DatagramPacket(bytes, bytes.size, addr)
                socket.asyncSend(packet)
            }
        }
        println("Stopped sending RenderMessages!")
    }


}