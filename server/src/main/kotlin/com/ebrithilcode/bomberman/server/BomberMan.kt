package com.ebrithilcode.bomberman.server

import processing.core.PApplet
import processing.core.PVector

fun main() {
    PApplet.runSketch(arrayOf("nothing"), BomberMan(8001))

}

class BomberMan(port : Int) : PApplet() {
    private val server = Server(port)
    private val playerList = mutableListOf<Player>()

    init {
        println("Working directory ${System.getProperty("user.dir")}")
    }

    private val level = Level.fromFile("${System.getProperty("user.dir")}/src/main/resources/TestLevel.data")



    private val grid = level.grid

    override fun settings() {
        size(800,800)
    }

    override fun setup() {
        server.acceptClients(1) { theSocket ->
            Player(theSocket, grid).let {
                it.character.position = level.positionMap["pos${playerList.size}"] ?: PVector(0f,0f)
                grid.entityList.add(it.character)
                playerList.add(it)
            }
        }
        frameRate(60f)

    }



    override fun draw() {
        background(255)
        fill(0f,0f,255f)
        textAlign(CENTER, CENTER)

        if (server.scheduledClients.get()>0) {
            text("Waiting for ${server.scheduledClients} clients", width / 2f, height / 2f)
            text("Job done? ${server.clientJob.isCompleted}", width / 2f, height / 2f + 40)
        }

        pushMatrix()
        translate((width/2f-grid.width/2f*grid.gridSize).toFloat(), (height/2f-grid.height/2f*grid.gridSize).toFloat())
        grid.show(this)
        grid.update(1f/frameRate)
        popMatrix()
    }

}