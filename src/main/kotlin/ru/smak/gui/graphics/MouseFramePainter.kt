package ru.smak.gui.graphics

import java.awt.Color
import java.awt.Graphics
import java.awt.Point

class MouseFramePainter(var g: Graphics){

    var startPoint: Point? = null
    var currentPoint: Point? = null
        set(value) {
            paint()
            field = value
            paint()
        }
    var isVisible = false
        set(value) {
            field = value
            if (value){
                currentPoint = null
                startPoint = null
            }
        }

    private fun paint(){
        if (isVisible){
            startPoint?.let{s ->
                currentPoint?.let {c ->
                    g.setXORMode(Color.WHITE)
                    g.color = Color.BLACK
                    g.drawRect(s.x, s.y, c.x-s.x, c.y-s.y)
                    g.setPaintMode()
                }
            }
        }
    }
}