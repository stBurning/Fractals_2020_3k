package ru.smak.gui

import java.awt.event.KeyEvent
import javax.swing.*
import ru.smak.gui.graphics.SaveFractal
class Menu(mw1: MainWindow): JFrame() {
    var menuBar = JMenuBar()
    val mw: MainWindow = mw1
    val bgClr = ButtonGroup();
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
        //val vidoBtn = JButton(ImageIcon("src/video.png"), "Записать видео")
        val videoBtn = JButton("Записать видео ", ImageIcon("icons/video.png"))
        //videoBtn.border = BorderFactory.createEtchedBorder()
        menuBar.add(videoBtn)
        menuBar.add(Box.createHorizontalGlue())//пробел
        //поле с галочкой для детализации
        val detail = JCheckBox("Детализация ")
        detail.addActionListener{
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
        undoBtn.addActionListener{
            //создать объект нужного класса и обратиться через него к нужной функции
        }
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

        }
        // Пункт меню "Сохранить как..."
        val save = JMenu("Сохранить как...")
        //Подпункты "Сохранить как..."
        val format1 = JMenuItem("Собственный формат", ImageIcon("icons/imagefile.png"))
        format1.addActionListener {

        }
        val format2 = JMenuItem("Изображение", ImageIcon("icons/image.png"))
        format2.addActionListener {
            SaveFractal.invoke(mw.plane,mw.fp.savedImage, mw.updated,bgClr.selection.actionCommand )
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
        val clr1 = JRadioButtonMenuItem("Цветовая схема 1", ImageIcon("icons/save.png"))
        clr1.setActionCommand("colorScheme1")
        val clr2 = JRadioButtonMenuItem("Цветовая схема 2", ImageIcon("icons/save.png"))
        clr2.setActionCommand("colorScheme2")
        val clr3 = JRadioButtonMenuItem("Цветовая схема 3", ImageIcon("icons/save.png"))
        clr3.setActionCommand("colorScheme3")
        val clr4 = JRadioButtonMenuItem("Цветовая схема 4", ImageIcon("icons/save.png"))
        clr4.setActionCommand("colorScheme4")

        clr1.addActionListener { mw.changeColorScheme(1) }
        clr2.addActionListener {
            mw.changeColorScheme(2)
        }
        clr3.addActionListener { mw.changeColorScheme(3) }
        clr4.addActionListener { mw.changeColorScheme(4) }
        clr1.doClick()
        // организуем переключатели в логическую группу

        bgClr.add(clr1)
        bgClr.add(clr2)
        bgClr.add(clr3)
        bgClr.add(clr4)
        clrSchemes.add(clr1)
        clrSchemes.add(clr2)
        clrSchemes.add(clr3)
        clrSchemes.add(clr4)
        bgClr.selection.actionCommand

        //меню-переключатели для выбора фрактала
        val f1 = JRadioButtonMenuItem("Множество Мандельброта", ImageIcon("icons/save.png"))
        f1.addActionListener {
            mw.createMandelbrot()
        }
        f1.doClick()
        val f2 = JRadioButtonMenuItem("Множество Жюлиа", ImageIcon("icons/save.png"))
        f2.addActionListener {
            mw.createJulia()
        }
        // организуем переключатели в логическую группу
        val bgF = ButtonGroup();
        bgF.add(f1)
        bgF.add(f2)
        fractal.add(f1)
        fractal.add(f2)

        // добавим все в меню
        editMenu.add(clrSchemes)
        editMenu.add(JSeparator()) //разделитель
        editMenu.add(fractal)
        return editMenu;
    }


}

