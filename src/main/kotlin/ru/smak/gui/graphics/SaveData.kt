package ru.smak.gui.graphics

import java.awt.FileDialog
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.logging.Filter

data class SaveData(
        var xMin: Double,
        var xMax: Double,
        var yMin: Double,
        var yMax: Double,
        var color: Int
):Serializable{
    override fun toString() = "$xMin,$xMax,$yMin,$yMax,$color"
}
//Класс, внутри которого осуществляется сохранение
@Suppress("DEPRECATION") // эта штука появилась, после написания show, я не знаю, что это , у меня лапки
class SaveFormat(fsd: FileDialog, sd: SaveData){

    init {
        // открытие диалогого окна
        fsd.show()
        // в if проверяем file, если оно не null, то оно содержит название файла и нажата кнопочка "сохранить"
        //если null, действие отменено, т.е. нажато "отмена"

                //TODO: надо сделать фильтр =(
        if(fsd.file!=null){
            save(fsd.file.toString(), sd)
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
