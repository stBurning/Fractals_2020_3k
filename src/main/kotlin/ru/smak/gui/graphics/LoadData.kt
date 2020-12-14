package ru.smak.gui.graphics

import ru.smak.math.fractals.Mandelbrot
import java.awt.FileDialog
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*
import javax.imageio.ImageIO
import javax.swing.AbstractButton
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

@Suppress("DEPRECATION")
object LoadData{
    fun loadData(fp: FractalPainter):SaveData?{
        val fileChooser = JFileChooser()
        with(fileChooser) {
            dialogTitle = "Открытие файла..."
            val filter1 = FileNameExtensionFilter("dat", "dat")
            addChoosableFileFilter(filter1)
        }

        fileChooser.fileSelectionMode = JFileChooser.OPEN_DIALOG
        val result = fileChooser.showOpenDialog(fileChooser)
        if (result == JFileChooser.APPROVE_OPTION) {

            var name = fileChooser.selectedFile.absolutePath

            if (fileChooser.selectedFile.extension == "") {
                println("Error")
            }
            val l = load(name,0)
            if(l!=null) {
                return l
               // open(fp, l)
               // JOptionPane.showMessageDialog(fileChooser,
               //         "Файл открыт")
            }else {
                JOptionPane.showMessageDialog(fileChooser,
                        "Произошла ошибка")
                return null
            }

        }else{
            return null
        }
    }

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
    fun open(fp: FractalPainter, sd:SaveData, list: Enumeration<AbstractButton>){

        fp.plane.let {
            it.xMin = sd.xMin
            it.xMax = sd.xMax
            it.yMin = sd.yMin
            it.yMax = sd.yMax
        }

        for (i in list){
            if(i.name == sd.color){
                i.doClick()
                break
            }
        }

    }

}

