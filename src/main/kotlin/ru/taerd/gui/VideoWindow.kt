package ru.taerd.gui

import ru.smak.gui.graphics.convertation.CartesianScreenPlane
import ru.taerd.panels.VideoPanel
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.math.max

/**
 * Класс дополнительного окна,для создания видео по фреймам
 */
class VideoWindow : JFrame() {
    private val minSizeVidePanel = Dimension(400, 400)
    private val minSizePlaneScroll = Dimension(200, 380)

    private val minSizeAddFrameJButton = Dimension(150, 20)
    private val minSizeRemoveFrameJButton = Dimension(150, 20)

    private val minSizeStartJButton = Dimension(150, 20)
    private val minSizeStopJButton = Dimension(150, 20)

    private val minSizeTextField = Dimension(80,20)
    private val minSizeTextLabel = Dimension(220,20)


    //components
    public val videoPanel: VideoPanel
    private val addFrameButton = JButton("Добавить")
    private val startButton = JButton("Создать")
    private val removeFrameButton = JButton("Удалить")
    private val stopButton = JButton("Остановить")
    private val textField = JTextField("10")
    private val textLabel = JLabel("Время между переходами по кадрам")

    private val dlm = DefaultListModel<String>()
    private var list = JList(dlm)
    private var planeScroll = JScrollPane(list)

    private var timeBetweenFrames = 0

    //Можно будет удалить и использовать dlm
    private val frameList = mutableListOf<CartesianScreenPlane>()

    init {
        //сделать подписку на событие defaultCloseOperation которое будет блокировать потоки отрисовки изображения и закрывать окошко
        defaultCloseOperation.apply{
            isVisible=false
        }
        title = "Составление видео из кадров"
        minimumSize = Dimension(950, 700)
        videoPanel = VideoPanel()
        layout = GroupLayout(contentPane).apply {
            setVerticalGroup(
                    createSequentialGroup()
                            .addGap(4)
                            .addGroup(
                                    createParallelGroup()
                                            .addComponent(
                                                    videoPanel,
                                                    minSizeVidePanel.height,
                                                    minSizeVidePanel.height,
                                                    GroupLayout.DEFAULT_SIZE
                                            )
                                            .addGap(4)
                                            .addGroup(
                                                    createSequentialGroup()
                                                            .addComponent(
                                                                    planeScroll,
                                                                    minSizePlaneScroll.height,
                                                                    minSizePlaneScroll.height,
                                                                    GroupLayout.DEFAULT_SIZE
                                                            )
                                                            .addGap(4)
                                                            .addGroup(
                                                                    createParallelGroup()
                                                                            .addComponent(
                                                                                    addFrameButton,
                                                                                    minSizeAddFrameJButton.height,
                                                                                    minSizeAddFrameJButton.height,
                                                                                    GroupLayout.DEFAULT_SIZE
                                                                            )
                                                                            .addGap(4)
                                                                            .addComponent(
                                                                                    removeFrameButton,
                                                                                    minSizeRemoveFrameJButton.height,
                                                                                    minSizeRemoveFrameJButton.height,
                                                                                    GroupLayout.DEFAULT_SIZE
                                                                            )
                                                            )
                                                            .addGap(4)
                                                            .addGroup(
                                                                    createParallelGroup()
                                                                            .addComponent(
                                                                                    textLabel,
                                                                                    minSizeTextLabel.height,
                                                                                    minSizeTextLabel.height,
                                                                                    GroupLayout.DEFAULT_SIZE
                                                                            )
                                                                            .addGap(4)
                                                                            .addComponent(
                                                                                    textField,
                                                                                    minSizeTextField.height,
                                                                                    minSizeTextField.height,
                                                                                    GroupLayout.PREFERRED_SIZE
                                                                            )
                                                            )
                                                            .addGap(4)
                                                            .addGroup(
                                                                    createParallelGroup()
                                                                            .addComponent(
                                                                                    startButton,
                                                                                    minSizeStartJButton.height,
                                                                                    minSizeStartJButton.height,
                                                                                    GroupLayout.DEFAULT_SIZE
                                                                            )
                                                                            .addGap(4)
                                                                            .addComponent(
                                                                                    stopButton,
                                                                                    minSizeStopJButton.height,
                                                                                    minSizeStopJButton.height,
                                                                                    GroupLayout.DEFAULT_SIZE
                                                                            )
                                                            )
                                            )

                            )
                            .addGap(4)
            )
            setHorizontalGroup(
                    createSequentialGroup()
                            .addGap(4)
                            .addComponent(videoPanel, minSizeVidePanel.width, minSizeVidePanel.width, GroupLayout.DEFAULT_SIZE)
                            .addGap(4)
                            .addGroup(
                                    createParallelGroup()
                                            .addComponent(
                                                    planeScroll,
                                                    minSizePlaneScroll.width,
                                                    minSizePlaneScroll.width,
                                                    GroupLayout.DEFAULT_SIZE
                                            )
                                            .addGap(4)
                                            .addGroup(
                                                    createSequentialGroup()
                                                            .addComponent(
                                                                    addFrameButton,
                                                                    minSizeAddFrameJButton.width,
                                                                    minSizeAddFrameJButton.width,
                                                                    GroupLayout.DEFAULT_SIZE
                                                            )
                                                            .addGap(4)
                                                            .addComponent(
                                                                    removeFrameButton,
                                                                    minSizeRemoveFrameJButton.width,
                                                                    minSizeRemoveFrameJButton.width,
                                                                    GroupLayout.DEFAULT_SIZE
                                                            )
                                            )
                                            .addGap(4)
                                            .addGroup(
                                                    createSequentialGroup()
                                                            .addComponent(
                                                                    textLabel,
                                                                    minSizeTextLabel.width,
                                                                    minSizeTextLabel.width,
                                                                    GroupLayout.DEFAULT_SIZE
                                                            )
                                                            .addGap(4)
                                                            .addComponent(
                                                                    textField,
                                                                    minSizeTextField.width,
                                                                    minSizeTextField.width,
                                                                    GroupLayout.PREFERRED_SIZE
                                                            )
                                            )
                                            .addGap(4)
                                            .addGroup(
                                                    createSequentialGroup()
                                                            .addComponent(
                                                                    startButton,
                                                                    minSizeStartJButton.width,
                                                                    minSizeStartJButton.width,
                                                                    GroupLayout.DEFAULT_SIZE
                                                            )
                                                            .addGap(4)
                                                            .addComponent(
                                                                    stopButton,
                                                                    minSizeStopJButton.width,
                                                                    minSizeStopJButton.width,
                                                                    GroupLayout.DEFAULT_SIZE
                                                            )
                                            )
                            )
                            .addGap(4)
            )
        }
        pack()



        with(addFrameButton) {
            //Событие возникающее при нажатии кнопку добавления фрейма
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    frameList.add(videoPanel.plane.copy())
                    val arrPlane = videoPanel.plane.copy()
                    dlm.addElement("xMin: "+arrPlane.xMin.toString()+" xMax: "+arrPlane.xMax.toString()+" yMin: "+arrPlane.yMin.toString()+" yMax: "+arrPlane.yMax.toString())
                    val index: Int = dlm.size() - 1
                    list.selectedIndex = index
                    list.ensureIndexIsVisible(index)
                    list.selectedIndex = 0
                    list.isFocusable = false
                }
            })
        }
        //Событие возникающее при нажатии на кнопку удаления фрейма (Удаляется выделенный)
        with(removeFrameButton) {
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    val indexRemove = list.selectedIndex
                    if(indexRemove>=0){
                        dlm.remove(indexRemove)
                        frameList.removeAt(indexRemove)
                        //list.remove(indexRemove)
                    }
                }
            })
        }
        with(startButton) {
            //Событие возникающее при нажатии на создание видео
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)

                    //timeBetweenFrames - время перехода между контрольными фреймами в int (секунд)
                    timeBetweenFrames = getValidValue(textField.text)
//                    val snapsList = mutableListOf<CartesianScreenPlane>()

                    //frameList.size -1 теряет последний элемент
                    for (i in 0 until frameList.size - 1) {
                        println(frameList[i])

//                        val xMinDt = frameList[i].xMin - frameList[i + 1].xMin
//                        val xMaxDt = frameList[i].xMax - frameList[i + 1].xMax
//                        val yMinDt = frameList[i].yMin - frameList[i + 1].yMin
//                        val yMaxDt = frameList[i].yMax - frameList[i + 1].yMax

                        val accel = max(
                                (frameList[i].xMax - frameList[i].xMin) / (frameList[i + 1].xMax - frameList[i + 1].xMin),
                                (frameList[i].yMax - frameList[i].yMin) / (frameList[i + 1].yMax - frameList[i + 1].yMin)
                        )

//                        val snapsPerRound = (10 * accel).roundToInt()

//                        for (j in 0..snapsPerRound) {
//
//                        }

                    }
                }
            })
        }
        with (stopButton){
            //Событие возникающее при нажатии на кнопку Остановить
            addMouseListener(object : MouseAdapter(){
                override fun mouseClicked(e:MouseEvent?){
                    super.mouseClicked(e)
                    //Блокировка и удаление потоков отрисовки фреймов
                }
            })
        }
    }

    /**
     * Функция проверки валидности строки на число
     * @param inputStr - строка, в которой записано число
     * @return result - число от 1 до 30 
     */
    fun getValidValue(inputStr: String):Int{
        //println(inputStr)
        try{
            val result = Integer.parseInt(inputStr)
            //println(result)
            if(result > 0 && result < 30) return result
            else {
                textField.text="value set 10"
                return 10
            }
        }
        catch (e :Exception){
            println(e.message+ " -incorrect value")
            textField.text=10.toString()
            return 10
        }
    }
}