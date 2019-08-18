package com.ebrithilcode.bomberman

import processing.core.PApplet
import processing.core.PVector

class Item(grid:Grid, pos : PVector, val onCollect : (Pawn)->Unit) : Entity(grid){

    companion object {

        private fun increaseSpeed(pawn : Pawn) {
            pawn.maxSpeed += 0.02f
        }
        private fun increaseMaxBombs(pawn: Pawn) {
            pawn.allowedBombCount++
        }
        private fun increaseBombRange(pawn: Pawn) {
            pawn.explosionRange++
        }
        private fun allowBombKnocking(pawn : Pawn) {
            pawn.knockingBombs=true
        }

        fun createSpeedItem(grid: Grid, pos : PVector) : Item {
            return Item(grid, pos, this::increaseSpeed)
        }
        fun createBombCountItem(grid: Grid, pos : PVector) : Item {
            return Item(grid, pos, this::increaseMaxBombs)
        }
        fun createBombRangeItem(grid: Grid, pos : PVector) : Item {
            return Item(grid, pos, this::increaseBombRange)
        }
        fun createGloveItem(grid: Grid, pos : PVector) : Item {
            return Item(grid, pos, this::allowBombKnocking)
        }
    }

    init {
        position = pos
    }

    override fun show(applet: PApplet) {
        applet.fill(255f,255f,0f)
        applet.ellipse(position.x, position.y, grid.gridSize.toFloat(), grid.gridSize.toFloat())
    }

    override fun isMoveRejected(other: Entity): Boolean {
        if (other is Pawn) {
            onCollect(other)
            isDead = true
        }
        return false
    }
}