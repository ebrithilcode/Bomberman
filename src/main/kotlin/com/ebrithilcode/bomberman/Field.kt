package com.ebrithilcode.bomberman

class Field {

    var state = State.FREE

    enum class State{
        SOLID, BREAKABLE, FREE
    }
}