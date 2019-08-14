package com.ebrithilcode.bomberman

class Grid(width : Int, height : Int) {
    val fields = Array(width) { Array(height) {Field()}}
}