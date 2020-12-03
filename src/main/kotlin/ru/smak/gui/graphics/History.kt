package ru.smak.gui.graphics

class History() {
    class Coords(val xMin: Double, val xMax: Double, val yMin: Double, val yMax: Double)

    private val list = mutableListOf<Coords>()

    fun add(element: Coords) {
        list.add(element)
    }

    fun undo(): Coords? {
        if (list.isNotEmpty()) {
            val coords = list.last()
            list.remove(coords)
            return coords
        }
        return null
    }

    fun reset() {
        list.clear()
    }
}