package com.ebrithilcode.bomberman

class Pawn(grid : Grid) : Entity(grid) {

    val bombList = mutableListOf<Bomb>()
    val allowedBombCount = 5
    val explosionRange = 1
    val maxSpeed = 0.04f
    val knockingBombs = false


    fun onAction() {
        placeBomb()
    }

    fun placeBomb() {
        if (bombList.size<allowedBombCount) {
            println("Adding a bomb")
            val bomb = Bomb(grid, 3000, this)
            bomb.position = roundPosition()
            grid.entityList.add(bomb)
        }
    }

    override fun slayThatBitch() {
        isDead = true
    }
}