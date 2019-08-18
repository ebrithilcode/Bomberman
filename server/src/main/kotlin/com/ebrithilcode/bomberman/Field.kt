package com.ebrithilcode.bomberman

import processing.core.PVector
import java.util.HashMap
import kotlin.random.Random

class Field(val grid: Grid, val position: PVector) {

    companion object {
        val numToState : MutableMap<Byte, State> = HashMap()
        init {
            numToState[0] = State.FREE
            numToState[1] = State.BREAKABLE
            numToState[2] = State.SOLID
        }
        fun numToState(byte: Byte) : State? = numToState[byte]

        var dropRates : DoubleArray = DoubleArray(0)
        private val itemDrops = mutableListOf<(Grid, PVector)->Item>()
        fun addItemDrop(drop : (Grid, PVector) -> Item) {
            itemDrops.add(drop)
        }
        fun getNextDrop(grid: Grid, position: PVector) : Item {
            val rand = Random.nextDouble(1.0)
            var sum = 0.0
            for ((index, value) in dropRates.withIndex()) {
                sum += value
                if (sum > rand)
                   return itemDrops[index](grid, position)
            }
            throw IllegalStateException("Item spawning rates dont add up to 1: ${dropRates.asList()}")
        }
    }

    val entitiesOnField = mutableListOf<Entity>()

    var state = State.FREE
    set(value) {
        field = value
        byteState = when (value) {
            State.FREE -> 0
            State.BREAKABLE -> 1
            State.SOLID -> 2
        }
    }
    var byteState : Byte = 0
    private set(value) {
        field = value
    }

    fun isMoveRejected(entity : Entity) : Boolean {
        if (state!=State.FREE) return true
        for (fieldEntity in entitiesOnField) {
            if (fieldEntity.isMoveRejected(entity)) return true
        }
        return false

    }

    fun breakFree() {
        state = State.FREE
        //TODO("Implement item spawning")
        val newItem = getNextDrop(grid, position)
        grid.addEntity(newItem)

    }

    enum class State{

        FREE, BREAKABLE, SOLID
    }

}