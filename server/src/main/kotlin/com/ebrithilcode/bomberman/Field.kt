package com.ebrithilcode.bomberman

import java.util.HashMap

class Field {

    companion object {
        val numToState : MutableMap<Byte, State> = HashMap()
        init {
            numToState[0] = State.FREE
            numToState[1] = State.BREAKABLE
            numToState[2] = State.SOLID
        }
        fun numToState(byte: Byte) : State? = numToState[byte]
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
    }

    enum class State{

        FREE, BREAKABLE, SOLID
    }

}