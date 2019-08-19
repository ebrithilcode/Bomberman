package com.ebrithilcode.bomberman.server

import com.beust.klaxon.Klaxon
import com.ebrithilcode.bomberman.common.asyncReceive
import com.ebrithilcode.bomberman.common.asyncSend
import com.ebrithilcode.bomberman.common.getDataAsString
import com.ebrithilcode.bomberman.common.klaxon.PlayerActionMessage
import com.ebrithilcode.bomberman.common.klaxon.RenderMessage
import kotlinx.coroutines.*
import processing.core.PApplet
import processing.core.PImage
import java.io.StringReader
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.atomic.AtomicLong


object Server : PApplet() {

    private const val REGISTRATION_PORT: Int = 8001
    private const val CONNECTION_PORT: Int = 8002
    private const val AVAILABLE_PLAYER_SPRITES: Int = 4

    lateinit var clientJob: Job
    private val clientList: MutableList<Socket> = mutableListOf()

    private val addrToPlayerMap: MutableMap<SocketAddress, Player> = HashMap(4)

    private val coScope = CoroutineScope(Dispatchers.Default)

    private val playerConnectionSocket = DatagramSocket(CONNECTION_PORT)

    private val level = Level.fromFile("${System.getProperty("user.dir")}/src/main/resources/TestLevel.data")
    //private val level = Level.fromFile("/home/rsttst/Coding/Projects/IntelliJ/Bomberman/server/src/main/resources/com.ebrithilcode.bomberman.server/TestLevel.data")
    private val grid = level.grid

    private val idToSpriteMap: MutableMap<Long, PImage> = HashMap(/*TODO: size arg*/)
    private val spriteIdCounter = AtomicLong(0)

    override fun settings() {
        size(800, 800)
    }

    override fun setup() {
        println("Started setup")
        frameRate(60f)
        clientJob = launchPlayerRegistration(1)
        println("Finished setup")
        clientJob.invokeOnCompletion {
            for ((index, player) in addrToPlayerMap.values.withIndex()) {
                player.character.position = level.positionMap.get("pos$index") ?: throw IllegalStateException("The position $index is not declared in the level data ${level.positionMap}")
                grid.addEntity(player.character)
            }
            launchPlayerConnectionSend()
            launchPlayerConnectionReceive()
        }
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

    private fun launchPlayerRegistration(numPlayers: Int): Job = coScope.launch {
        var playersLeft = numPlayers
        val recvPacket = DatagramPacket(ByteArray(4096), 4096) //receive packet can be reused
        val registrationSocket = DatagramSocket(REGISTRATION_PORT)
        registrationSocket.use {
            while (playersLeft > 0) {
                //receive and register
                recvPacket.length = recvPacket.data.size
                it.asyncReceive(recvPacket)
                println(recvPacket.getDataAsString())
                val regJson = Klaxon().parseJsonObject(StringReader(recvPacket.getDataAsString()))
                val playerNum = addrToPlayerMap.size
                addrToPlayerMap[recvPacket.socketAddress] = Player(regJson.string("name")
                        ?: "ERROR_NAME", Pawn(grid, /*loadSpriteAndGetIdForPlayer(playerNum)*/0L))
                println("Player ${addrToPlayerMap[recvPacket.socketAddress]} connected!")
                //send confirmation with connection port
                val confJsonBytes = """{
                        "success" : true,
                        "port" : $CONNECTION_PORT
                }""".toByteArray(Charsets.UTF_8)
                val sendPacket = DatagramPacket(confJsonBytes, confJsonBytes.size, recvPacket.socketAddress)
                it.asyncSend(sendPacket)
                println("Confirmed player!")
                playersLeft--
            }
        }
    }

    private fun launchPlayerConnectionReceive() : Job = coScope.launch {
        val recvPacket = DatagramPacket(ByteArray(8192), 8192)
        playerConnectionSocket.use {
            while (isActive) {
                recvPacket.length = recvPacket.data.size
                it.asyncReceive(recvPacket)
                val asString = recvPacket.getDataAsString()
                println("Received string: $asString")
                val msg = Klaxon().parse<PlayerActionMessage>(asString)
                        ?: throw IllegalStateException() //TODO write message
                addrToPlayerMap.playerByAddress(recvPacket.socketAddress)?.onPlayerActionUpdate(msg.actions)
                        ?: throw IllegalStateException("Can't find Socket address ${recvPacket.socketAddress} " +
                                "in the map $addrToPlayerMap") //TODO write message
            }
        }
        println("Stopped listening for incoming PlayerActionMessages!")
    }

    private fun launchPlayerConnectionSend() : Job = coScope.launch {
        playerConnectionSocket.use {
            while(isActive) {
                val bytes = Klaxon().toJsonString(grid.encodeToRenderMessage()).toByteArray(Charsets.UTF_8)
                for(addr in addrToPlayerMap.keys) {
                    val packet = DatagramPacket(bytes, bytes.size, addr)
                    playerConnectionSocket.asyncSend(packet)
                }
            }
        }
        println("Stopped sending RenderMessages!")
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

    private fun MutableMap<SocketAddress, Player>.playerByAddress(adr: SocketAddress) : Player? {
        for (address in this.keys) {
            if (address.toString().split(":")[0]==adr.toString().split(":")[0])
                //Dirty workaround to get the update addresses during updating process TODO: RECONSIDER SOCKETADDRESS
                if (adr!=address) {
                    put(adr, get(address) ?: throw IllegalStateException("Address is weird"))
                    remove(address)
                }
                return get(adr)
        }
        return null
    }


}