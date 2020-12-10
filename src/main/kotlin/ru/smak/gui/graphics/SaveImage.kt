package ru.smak.gui.graphics

import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.smak.gui.graphics.convertation.Converter
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

class SaveImage(val plane: CartesianScreenPlane, val img: BufferedImage, val detal: Boolean, val colorScheme: String) {
    init {
        val fileChooser = JFileChooser()
        with(fileChooser) {
            setDialogTitle("Сохранение файла...")
            val filter1 = FileNameExtensionFilter("jpg", "jpg")
            val filter2 = FileNameExtensionFilter("png", "png")
            addChoosableFileFilter(filter1)
            addChoosableFileFilter(filter2)
        }
        SaveImage(fileChooser)
    }

    fun SaveImage(fileChooser: JFileChooser) {
        //Созддаем изображение в буфере изображения оперативной памяти
        val bufferedImage = BufferedImage(img.getWidth(), img.getHeight() + (img.getHeight().toDouble() * 0.1).toInt(), BufferedImage.TYPE_INT_ARGB)
        val g: Graphics = bufferedImage.getGraphics()
        val xMin = BigDecimal(plane.xMin).setScale(2, RoundingMode.HALF_EVEN).toString()
        val xMax = BigDecimal(plane.xMax).setScale(2, RoundingMode.HALF_EVEN).toString()
        val yMin = BigDecimal(plane.yMin).setScale(2, RoundingMode.HALF_EVEN).toString()
        val yMax = BigDecimal(plane.yMax).setScale(2, RoundingMode.HALF_EVEN).toString()
        val fnt = Font("Cambria", Font.BOLD, 14)
        g.font = fnt
        val m = g.fontMetrics
        val hxMin = m.getStringBounds("xMin=" + xMin, g).height.toInt()
        val hyMin = m.getStringBounds("yMin=" + yMin, g).height.toInt()
        val detalization = if (detal) "Вкл." else "Выкл."
        g.setColor(Color.black)
        g.drawRect(0, img.getHeight(), img.getWidth() - 1, (img.getHeight().toDouble() * 0.09).toInt())
        g.drawString("xMin=" + xMin, (img.getWidth().toDouble() * 0.05).toInt(), img.getHeight() + (img.getHeight().toDouble() * 0.04).toInt())
        g.drawString("xMax=" + xMax, (img.getWidth().toDouble() * 0.05).toInt(), img.getHeight() + (img.getHeight().toDouble() * 0.04).toInt() + hxMin)
        g.drawString("yMin=" + yMin, (img.getWidth().toDouble() * 0.05).toInt()+100, img.getHeight() + (img.getHeight().toDouble() * 0.04).toInt())
        g.drawString("yMax=" + yMax, (img.getWidth().toDouble() * 0.05).toInt()+100, img.getHeight() + (img.getHeight().toDouble() * 0.04).toInt() + hyMin)
        g.drawString("Детализация: " + detalization, img.getWidth() - (img.getWidth().toDouble() * 0.05).toInt() - 200, img.getHeight() + (img.getHeight().toDouble() * 0.04).toInt())
        g.drawString("Цветовая схема: " + colorScheme, img.getWidth() - (img.getWidth().toDouble() * 0.05).toInt() - 200, img.getHeight() + (img.getHeight().toDouble() * 0.04).toInt() + hyMin)
        g.drawImage(img, 0, 0, null)
        g.dispose()
        // Сохраняем изображение из буфера в файл
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
        val result = fileChooser.showSaveDialog(fileChooser)
        if (result == JFileChooser.APPROVE_OPTION) {
            var str = fileChooser.getSelectedFile().absolutePath
            if (fileChooser.getSelectedFile().extension == "") {
                if (fileChooser.fileFilter.description != "All Files") str = str + "." + fileChooser.fileFilter.description else str += ".jpg"
            }
            val outputFile = File(str)
            ImageIO.write(bufferedImage, "PNG", outputFile)
            JOptionPane.showMessageDialog(fileChooser,
                    "Файл '" + str +
                            "' сохранен")
        }
    }
}