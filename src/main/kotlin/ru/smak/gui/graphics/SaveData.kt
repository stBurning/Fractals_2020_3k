package ru.smak.gui.graphics

import java.awt.FileDialog
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.*
import java.util.logging.Filter
import javax.imageio.ImageIO
import javax.swing.AbstractButton
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

data class SaveData(
        var xMin: Double,
        var xMax: Double,
        var yMin: Double,
        var yMax: Double,
        var color: String,
        var detail: Boolean,
        var method: Int
):Serializable{
    override fun toString() = "$xMin,$xMax,$yMin,$yMax,$color,$detail, $method"
}
class SaveFormat( sd: SaveData){

    init {
        val fileChooser = JFileChooser()
        with(fileChooser) {
            dialogTitle = "Сохранение файла..."
            val filter1 = FileNameExtensionFilter("dat", "dat")
            addChoosableFileFilter(filter1)
        }
        fileChooser.fileSelectionMode = JFileChooser.OPEN_DIALOG
        val result = fileChooser.showSaveDialog(fileChooser)
        if (result == JFileChooser.APPROVE_OPTION) {
            var str = fileChooser.selectedFile.absolutePath
            if (fileChooser.selectedFile.extension == "") {
                if (fileChooser.fileFilter.description != "All Files")
                    str = str + "." + fileChooser.fileFilter.description
                else str += ".dat"
            }
            save(str,sd)
            JOptionPane.showMessageDialog(fileChooser,
                    "Файл '" + str +
                            "' сохранен")
        }

    }

    /**
     * @param name - название файла, который будет сохранятся
     * @param sd - данные, которые будут сохранятся
     */
    fun save(name: String, sd: SaveData){
        // открытие файла
        val os = FileOutputStream(name)
        val osw = ObjectOutputStream(os)
        //запись в файл
        osw.writeObject(sd)
                //закрытие файла
        osw.close()
    }


}
