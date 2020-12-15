package ru.smak.gui

import ru.smak.gui.graphics.LoadData
import ru.smak.gui.graphics.SaveData
import ru.smak.gui.graphics.SaveFormat
import java.awt.event.KeyEvent
import javax.swing.*
import ru.smak.gui.graphics.SaveFractal
import java.util.*
import kotlin.collections.AbstractCollection

class Menu(mw1: MainWindow): JFrame() {
    var menuBar = JMenuBar()
    val mw: MainWindow = mw1
    val bgClr = ButtonGroup()
    val detail = JCheckBox("Детализация ")
    val f1 : JRadioButtonMenuItem = JRadioButtonMenuItem("Множество Мандельброта")
    val f2 = JRadioButtonMenuItem("Множество Мандельброта 2")
    val bgF = ButtonGroup()
    init{
        //заполняем меню
        fillMenuBar()
        jMenuBar = menuBar
    }


    private fun fillMenuBar(){
        //отдельные функции для создания вложенных пунктов меню
        menuBar.add(createFileMenu())
        menuBar.add(createEditMenu())
        //кнопка для записи видео
        val videoBtn = JButton("Записать видео ", ImageIcon("icons/video.png"))
        menuBar.add(videoBtn)
        videoBtn.addActionListener {
            mw.video.isVisible = true
        }
        menuBar.add(Box.createHorizontalGlue())//пробел
        //поле с галочкой для детализации

        detail.addActionListener {
            mw.updated = detail.isSelected
        }
        menuBar.add(detail)
        //кнопка отмена операции
        val undoBtn = JButton(ImageIcon("icons/undo15.png"))
        undoBtn.addActionListener {
            mw.onUndo()
        }
        //привязка к комбинации клавиш
        undoBtn.mnemonic = KeyEvent.VK_Z
        menuBar.add(undoBtn)

        //кнопка сброса
        val resetBtn = JButton(ImageIcon("icons/reset15.png"))
        resetBtn.addActionListener {
            mw.onReset()
        }
        resetBtn.mnemonic = KeyEvent.VK_R
        menuBar.add(resetBtn)
    }

    private fun createFileMenu() : JMenu {
        //Создание выпадающего меню
        val fileMenu = JMenu("Файл")
        //Пункт меню "Открыть"
        val open = JMenuItem("Открыть", ImageIcon("icons/addfile.png"))
        open.addActionListener {
            val sd = LoadData.loadData(mw.fp)
            if(sd!=null) {
                mw.fp.plane.let {
                    it.xMin = sd.xMin
                    it.xMax = sd.xMax
                    it.yMin = sd.yMin
                    it.yMax = sd.yMax
                }
                for (i in bgClr.elements){
                    if(i.actionCommand== sd.color){
                        i.doClick()
                        break
                    }
                }
                detail.isSelected = sd.detail
                if( bgF.selection.actionCommand=="1"){f1.doClick()}
                else(f2.doClick())
            }

        }
        // Пункт меню "Сохранить как..."
        val save = JMenu("Сохранить как...")
        //Подпункты "Сохранить как..."
        val format1 = JMenuItem("Собственный формат", ImageIcon("icons/imagefile.png"))
        format1.addActionListener {
            val sd = SaveData( mw.plane.xMin, mw.plane.xMax, mw.plane.yMin, mw.plane.yMax,bgClr.selection.actionCommand,detail.isSelected,bgF.selection.actionCommand.toInt() )
            val s = SaveFormat(sd)
            f1.doClick()

        }
        val format2 = JMenuItem("Изображение", ImageIcon("icons/image.png"))
        format2.addActionListener {
            SaveFractal.invoke(mw.plane,mw.fp.savedImage, mw.updated,bgClr.selection.actionCommand)
        }
        //Добавление подпунктов в пункты
        save.add(format1)
        save.add(format2)
        fileMenu.add(open)
        // Добавление разделителя
        fileMenu.addSeparator()
        fileMenu.add(save)
        return fileMenu
    }
    private fun createEditMenu() : JMenu {
        val editMenu = JMenu("Настройки")
        val clrSchemes = JMenu("Цветовая схема")
        val fractal = JMenu("Фрактал")

        // меню-переключатели для цветов
        val clr1 = JRadioButtonMenuItem("Цветовая схема 1")
        clr1.actionCommand = "colorScheme1"
        val clr2 = JRadioButtonMenuItem("Цветовая схема 2")
        clr2.actionCommand = "colorScheme2"
        val clr3 = JRadioButtonMenuItem("Цветовая схема 3")
        clr3.actionCommand = "colorScheme3"
        val clr4 = JRadioButtonMenuItem("Цветовая схема 4")
        clr4.actionCommand = "colorScheme4"
        val clr5 = JRadioButtonMenuItem("Цветовая схема 5")
        clr5.actionCommand = "colorScheme5"

        clr1.addActionListener { mw.changeColorScheme(1) }
        clr2.addActionListener { mw.changeColorScheme(2) }
        clr3.addActionListener { mw.changeColorScheme(3) }
        clr4.addActionListener { mw.changeColorScheme(4) }
        clr5.addActionListener { mw.changeColorScheme(5) }
        clr1.doClick()
        // организуем переключатели в логическую группу

        bgClr.add(clr1)
        bgClr.add(clr2)
        bgClr.add(clr3)
        bgClr.add(clr4)
        bgClr.add(clr5)
        clrSchemes.add(clr1)
        clrSchemes.add(clr2)
        clrSchemes.add(clr3)
        clrSchemes.add(clr4)
        clrSchemes.add(clr5)
        bgClr.selection.actionCommand

        //меню-переключатели для выбора фрактала
        //f1 = JRadioButtonMenuItem("Множество Мандельброта", ImageIcon("icons/save.png"))
        f1.addActionListener {
            mw.createMandelbrot()
        }
        f1.actionCommand="1"
        f1.doClick()

        f2.actionCommand="2"
        //f2.isEnabled = false
        f2.addActionListener {
            mw.createJulia()
        }

        // организуем переключатели в логическую группу

        bgF.add(f1)
        bgF.add(f2)
        fractal.add(f1)
        fractal.add(f2)
        bgF.selection.actionCommand

        // добавим все в меню
        editMenu.add(clrSchemes)
        editMenu.add(JSeparator()) //разделитель
        editMenu.add(fractal)
        return editMenu
    }


}

