package ru.smak.gui.graphics

/**
 * Класс журнала
 */
class History {
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