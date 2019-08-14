package com.ebrithilcode.bomberman

class Field {

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

    enum class State{
        FREE, BREAKABLE, SOLID
    }

}