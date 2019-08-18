package com.ebrithilcode.bomberman.server

import processing.core.PApplet
import processing.core.PVector

class Item(grid: Grid, pos: PVector, val itemID: String, spriteID: Long, val onCollect: (Pawn) -> Unit) : Entity(grid, spriteID) {


    init {
        position = pos
    }

    override fun show(applet: PApplet) {
        applet.fill(255f, 255f, 0f)
        applet.ellipse(position.x * grid.gridSize, position.y * grid.gridSize, grid.gridSize, grid.gridSize)
        applet.textSize(10f)
        applet.fill(0f, 0f, 255f)
        applet.text(itemID, position.x * grid.gridSize, position.y * grid.gridSize)

    }

    override fun isMoveRejected(other: Entity): Boolean {
        if (other is Pawn) {
            onCollect(other)
            isDead = true
        }
        return false
    }
}

fun increaseSpeed(pawn: Pawn) {
    pawn.maxSpeed += 0.02f
}

fun increaseMaxBombs(pawn: Pawn) {
    pawn.allowedBombCount++
}

fun increaseBombRange(pawn: Pawn) {
    pawn.explosionRange++
}

fun allowBombKnocking(pawn: Pawn) {
    pawn.knockingBombs = true
}

fun createSpeedItem(grid: Grid, pos: PVector): Item {
    return Item(grid, pos, "Speed", 5L, ::increaseSpeed)
}

fun createBombCountItem(grid: Grid, pos: PVector): Item {
    return Item(grid, pos, "MaxBombCount", 7L, ::increaseMaxBombs)
}

fun createBombRangeItem(grid: Grid, pos: PVector): Item {
    return Item(grid, pos, "Range", 6L, ::increaseBombRange)
}

fun createGloveItem(grid: Grid, pos: PVector): Item {
    return Item(grid, pos, "Glove", 8L, ::allowBombKnocking)
}