package com.ebrithilcode.bomberman.server

class Pawn(grid: Grid, spriteID: Long) : Entity(grid, spriteID) {

    val bombList = mutableListOf<Bomb>()
    var allowedBombCount = 5
    var explosionRange = 1
    var maxSpeed = 0.02f
    var knockingBombs = false

    fun placeBomb() {
        if (bombList.size < allowedBombCount) {
            val field = grid.getField(roundPosition())
            if (field.entitiesOnField.filterIsInstance<Bomb>().isEmpty()) {
                println("Adding a bomb")
                val bomb = Bomb(grid, 3000, this)
                bomb.position = roundPosition()
                grid.addEntity(bomb)
            }
        }
    }

    override fun kill() {
        isDead = true
    }

}