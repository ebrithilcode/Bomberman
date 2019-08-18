package com.ebrithilcode.bomberman.common

import processing.core.PVector

enum class Direction(val vector : PVector) {
    NORTH(PVector(0f,-1f)), EAST(PVector(1f,0f)), SOUTH(PVector(0f,1f)), WEST(PVector(-1f,0f))
}