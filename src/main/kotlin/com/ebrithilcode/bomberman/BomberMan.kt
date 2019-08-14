package com.ebrithilcode.bomberman

import processing.core.PApplet

fun main() {
    PApplet.runSketch(arrayOf<String>("nothing"), BomberMan(8001))

}

class BomberMan(port : Int) : PApplet(){
    var switch = true
    val server = Server(port)
    val playerList = mutableListOf<Player>()

    override fun settings() {
        size(300,300)
    }

    override fun setup() {
        server.acceptClients(1) { theSocket ->

            Player(theSocket).let {
                playerList.add(it)
                it.input.onKeyStroke("left") {
                    println("What a keystroke")
                    coolEvent()
                }
            }

        }
        frameRate(60f)

    }

    private fun coolEvent() {
        switch = !switch
    }

    override fun draw() {
        if (switch) background(255) else background(255f,0f,0f)
        fill(0f,0f,255f)
        textAlign(CENTER, CENTER)
        text("Waiting for ${server.scheduledClients} clients", width/2f, height/2f)
        text("Job done? ${server.clientJob.isCompleted}", width/2f, height/2f+40)
        for (player in playerList) player.update()
    }

}