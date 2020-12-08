package ru.smak.gui.graphics

import ru.smak.math.fractals.Mandelbrot
import java.awt.FileDialog
import java.io.EOFException
import java.io.FileInputStream
import java.io.ObjectInputStream
@Suppress("DEPRECATION")
class LoadData (fsd: FileDialog,fp: FractalPainter){
    init {
        fsd.show()
                //проверка какая кнопка нажата, аналогично с SaveData, суть можно посмотреть там.
        if(fsd.file!=null){
            //вызов функции для сохранения.
            val sd = load(fsd.file,0)
            if(sd!=null){
                open(fp, sd)
            }else{
                println("Не удалось открыть")
            }
            //дальше
        }
    }
    // туть весь код, как на паре и оно работает
    //TODO: нужно ли нам i ( если в 1 файле храниться информация только об одном рисунке, то i не нужен)
    fun load(name : String, i: Int):SaveData?{
        //TODO : переписать, без i
        val `is` = FileInputStream(name)
        val isw = ObjectInputStream(`is`)
        var cnt = 0
        while(true){
            try{
                val d = isw.readObject() as? SaveData
                if(cnt==i)
                    return d
            }catch (e: EOFException){
                break
            }
            cnt++
        }
        return null
    }
    fun open(fp: FractalPainter, sd:SaveData){
        fp.plane.let {
            it.xMin = sd.xMin
            it.xMax = sd.xMax
            it.yMin = sd.yMin
            it.yMax = sd.yMax
        }
        val fractal = Mandelbrot()
        fp.isInSet = fractal::isInSet
        fp.getColor = ::colorScheme5
    }

}