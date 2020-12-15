import io.humble.video.*
import io.humble.video.awt.MediaPictureConverter
import io.humble.video.awt.MediaPictureConverterFactory
import java.awt.AWTException
import java.awt.image.BufferedImage
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue
import kotlin.jvm.Throws


/** (Consumer) Класс, получающий через очередь изображения и собирающий их в видео
 * @param queues списпок ConcurrentLinkedQueue - очередей, осуществляющих связь с (Producers)
 * @param width ширина изображений
 * @param height высота изображений
 * @param duration длительность выидео
 * @param fps количество кадров в секунду*/
class VideoProcessor(
    private val queues: MutableList<LinkedBlockingQueue<BufferedImage>>,
    private val width: Int,
    private val height: Int,
    private val duration: Int,
    private val fps: Int
) : Runnable {

    private var disable = true

    private val progressListeners: MutableList<(Double) -> Unit> = mutableListOf()

    private val finishListeners: MutableList<() -> Unit> = mutableListOf()

    fun addProgressListener(l: (Double) -> Unit){
        progressListeners.add(l)
    }
    fun addFinishListener(l: () -> Unit){
        finishListeners.add(l)
    }

    fun removeProgressListener(l: (Double) -> Unit){
        progressListeners.remove(l)
    }
    fun removeFinishListener(l: () -> Unit){
        finishListeners.remove(l)
    }



    /**
     * Функция создания видео в потоке
     * @param fileName название файла вместе с форматом
     * @param formatName формат видео (avi, mp4, mpeg, ...)
     * @param duration длительность видео
     * @param fps количество кадров в секундв
     */
    @Throws(AWTException::class, InterruptedException::class, IOException::class)
    private fun createVideo(fileName: String, formatName: String, duration: Int, fps: Int) {

        /**Представление фреймрейта в виде рациональной дроби*/
        val framerate = Rational.make(1, fps)

        /** Контейнер для видео-файла */
        val muxer = Muxer.make(fileName, null, formatName)

        /**
         * Выбираем кодек, подходящий к формату фидео
         */
        val format = muxer.format
        val codec: Codec = Codec.findEncodingCodec(format.defaultVideoCodecId)

        /**Создаем энкодер*/
        val encoder = Encoder.make(codec)
        /**Устанавливаем параметры энкодера*/
        // width, height - ширина и высота входных изображений и видео на выходе*/
        encoder.width = width
        encoder.height = height
        //PixelFormat -
        val pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P
        encoder.pixelFormat = pixelFormat
        encoder.timeBase = framerate
        /** Устанавливаем флаги*/
        if (format.getFlag(ContainerFormat.Flag.GLOBAL_HEADER)) encoder.setFlag(Coder.Flag.FLAG_GLOBAL_HEADER, true)
        /** Открываем энкодер.  */
        encoder.open(null, null)
        /** Добавляем энкодер в поток контейнера.  */
        muxer.addNewStream(encoder)
        /** Открываем контейнер.  */
        muxer.open(null, null)
        /** Конвертер, для преобразования в к нужному формату изображения*/
        var converter: MediaPictureConverter? = null
        val picture = MediaPicture.make(
            encoder.width,
            encoder.height,
            pixelFormat
        )
        picture.timeBase = framerate
        /** Основной цикл. Кодируем изображения и добавляем в пакет*/
        val packet = MediaPacket.make()
        var i = 0
        while (i < duration / framerate.double) {
            if (disable) {
                muxer.close()
                println("Поток Видео завершен")
                return
            }
            //Получаем изображение из очереди если оно доступно, иначе ждем
            queues.forEach { queue ->
                val img = queue.take()
                val newImg = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
                newImg.graphics.drawImage(img, 0, 0, null)
                /** Конвертируем изображение*/
                val image = convertToType(newImg, BufferedImage.TYPE_3BYTE_BGR)
                println("[Encoder] Изображение $i добавлено")
                if (converter == null) converter = MediaPictureConverterFactory.createConverter(image, picture)
                converter!!.toPicture(picture, image, i.toLong())
                do {
                    encoder.encode(packet, picture)
                    if (packet.isComplete) muxer.write(packet, false)
                } while (packet.isComplete)
                i++

            }
            progressListeners.forEach { pl ->
                pl.invoke((i.toDouble()/(duration * fps)))
            }

        }
        do {
            encoder.encode(packet, null)
            if (packet.isComplete) muxer.write(packet, false)
        } while (packet.isComplete)
        /** Закрываем контейнер.*/
        muxer.close()
        finishListeners.forEach { fl ->
            fl.invoke()
        }
        println("Создании видео завершено!")
    }

    /**
     * Convert a [BufferedImage] of any type, to [BufferedImage] of a
     * specified type. If the source image is the same type as the target type,
     * then original image is returned, otherwise new image of the correct type is
     * created and the content of the source image is copied into the new image.
     *
     * @param sourceImage the image to be converted
     * @param targetType  the desired BufferedImage type
     * @return a BufferedImage of the specifed target type.
     * @see BufferedImage
     */
    private fun convertToType(sourceImage: BufferedImage, targetType: Int): BufferedImage {
        val image: BufferedImage

        // if the source image is already the target type, return the source image
        if (sourceImage.type == targetType) image = sourceImage else {
            image = BufferedImage(
                sourceImage.width,
                sourceImage.height, targetType
            )
            image.graphics.drawImage(sourceImage, 0, 0, null)
        }
        return image
    }

    fun disable(){
        disable = true
        println("Потребитель остановлен")
    }

    override fun run() {
        disable = false
        println("Потребитель запущен")
        createVideo("fractal.avi", "avi", duration, fps)
    }
}