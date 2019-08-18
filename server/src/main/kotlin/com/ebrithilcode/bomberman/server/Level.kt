package com.ebrithilcode.bomberman.server

import processing.core.PVector
import processing.data.FloatList
import processing.data.JSONArray
import processing.data.JSONObject
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths

class Level(val grid: Grid) {
    companion object {

        fun fromFile(file : String) : Level {
            return decode(Files.readAllBytes(Paths.get(file)))
        }

        fun decode(arr : ByteArray) : Level {
            val buffer = ByteBuffer.wrap(arr)
            val level = Level(Grid(buffer.int, buffer.int, 50.0))
            for (subArray in level.grid.fields) {
                for (field in subArray) field.state = Field.numToState(buffer.get()) ?: Field.State.FREE
            }
            val posBytes = ByteArray(buffer.int)
            buffer.get(posBytes)
            val posObj = JSONObject.parse(String(posBytes))
            for (entry in posObj.keyIterator()) {
                if (entry is String) {
                    val vec = PVector(posObj.getJSONArray(entry).getFloat(0), posObj.getJSONArray(entry).getFloat(1))
                    level.positionMap[entry] = vec
                }
            }
            return level
        }
    }

    val positionMap = HashMap<String, PVector>()


    fun encode() : ByteArray {
        val obj = JSONObject()
        for (entry in positionMap.entries) {
            obj.setJSONArray(entry.key, JSONArray(FloatList(entry.value.x, entry.value.y)))
        }
        val mapBytes = grid.encodeToBytes()
        val positionBytes = obj.toString().toByteArray()
        return ByteBuffer.allocate(mapBytes.size+positionBytes.size+4).put(mapBytes).putInt(positionBytes.size)
            .put(positionBytes).array()
    }

    fun saveToFile(file : String) {
        Files.write(Paths.get(file), encode())
    }


}