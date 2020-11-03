package ru.smak.gui.components

import ru.smak.gui.graphics.Painter
import java.awt.Graphics
import javax.swing.JPanel

class GraphicsPanel : JPanel(){
    private val painters: MutableList<Painter> = mutableListOf()

    override fun paint(g: Graphics?) {
        super.paint(g)
        painters.forEach { it.paint(g) }
    }

    fun addPainter(p: Painter){
        if (!painters.contains(p))
            painters.add(p)
    }

    fun removePainter(p: Painter) {
        if (painters.contains(p))
            painters.remove(p)
    }

}