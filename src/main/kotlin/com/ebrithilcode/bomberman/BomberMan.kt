package com.ebrithilcode.bomberman

import processing.core.PApplet

fun main() {
    PApplet.runSketch(arrayOf("nothing"), BomberMan(8001))

}

class BomberMan(port : Int) : PApplet() {
    private val server = Server(port)
    private val playerList = mutableListOf<Player>()
    private val grid = Grid(10, 10, 30.0)

    override fun settings() {
        size(300,300)
    }

    override fun setup() {
        server.acceptClients(1) { theSocket ->
            Player(theSocket, grid).let {
                playerList.add(it)
            }
        }
        frameRate(60f)

    }



    override fun draw() {
        background(255)
        fill(0f,0f,255f)
        textAlign(CENTER, CENTER)
        text("Waiting for ${server.scheduledClients} clients", width/2f, height/2f)
        text("Job done? ${server.clientJob.isCompleted}", width/2f, height/2f+40)
        for (player in playerList) {
            player.character.show(this)
            player.update()
        }
    }

}