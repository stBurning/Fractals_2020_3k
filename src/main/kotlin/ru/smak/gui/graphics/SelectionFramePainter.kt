package ru.smak.gui.graphics

import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle

class SelectionFramePainter(var g: Graphics?){

    init{
        g?.apply{
            setXORMode(Color.WHITE)
            drawRect(-2, -2, 1, 1)
            setPaintMode()
        }
    }

    var startPoint: Point? = null
        set(value) {
            field = value
        }

    var currentPoint: Point? = null
        set(value) {
            if (field != null) paint()
            field = value
            paint()
        }

    var isVisible = false
        set(value) {
            if (!value)
                paint()
            else {
                currentPoint = null
                startPoint = null
            }
            field = value
        }

    val selectionRect: Rectangle?
        get() {
            startPoint?.let {sp->
                currentPoint?.let {cp->
                    val r = Rectangle(sp)
                    r.add(cp)
                    return r
                }
            }
            return null
        }

    private fun paint(){
        if (isVisible){
            selectionRect?.let { r ->
                g?.apply{
                    setXORMode(Color.WHITE)
                    color = Color.BLACK
                    drawRect(r.x, r.y, r.width, r.height)
                    setPaintMode()
                }
            }
        }
    }
}