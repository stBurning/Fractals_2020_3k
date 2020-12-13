import io.humble.video.*
import io.humble.video.awt.MediaPictureConverter
import io.humble.video.awt.MediaPictureConverterFactory
import java.awt.AWTException
import java.awt.image.BufferedImage
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue


/** (Consumer) Класс, получающий через очередь изображения и собирающий их в видео
 * @param queue BlockingQueue - очередь, осуществляющая связь с (Producers)
 * @param width ширина изображений
 * @param height высота изображений*/
class VideoProcessor(
    private val queue: LinkedBlockingQueue<BufferedImage>,
    private val width: Int,
    private val height: Int,
    private val duration: Int
) : Runnable {
    /**
     * Функция создания видео в потоке
     * @param fileName название файла вместе с форматом
     * @param formatName формат видео (avi, mp4, mpeg, ...)
     * @param fps количество кадров в секундв
     */
    @Throws(AWTException::class, InterruptedException::class, IOException::class)
    private fun createVideo(fileName: String, formatName: String, duration: Int, fps: Int) {

        /**Представление фреймрейта в виде рациональной дроби*/
        val framerate = Rational.make(1, fps)

        /** Контейнер для видео-файла */
        val muxer = Muxer.make(fileName, null, formatName)

        /** Now, we need to decide what type of codec to use to encode video. Muxers
         * have limited sets of codecs they can use. We're going to pick the first one that
         * works, or if the user supplied a codec name, we're going to force-fit that
         * in instead.
         */
        val format = muxer.format
        val codec: Codec = Codec.findEncodingCodec(format.defaultVideoCodecId)

        /**Создаем энкодер*/
        val encoder = Encoder.make(codec)
        /**Устанавливаем параметры энкодера*/
        // width, height - ширина и высота входных изображений и видео на выходе*/
        encoder.width = 1600
        encoder.height = 900
        //PixelFormat -
        val pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P
        encoder.pixelFormat = pixelFormat
        encoder.timeBase = framerate
        /** An annoynace of some formats is that they need global (rather than per-stream) headers,
         * and in that case you have to tell the encoder. And since Encoders are decoupled from
         * Muxers, there is no easy way to know this beyond
         */
        if (format.getFlag(ContainerFormat.Flag.GLOBAL_HEADER)) encoder.setFlag(Coder.Flag.FLAG_GLOBAL_HEADER, true)
        /** Открываем энкодер.  */
        encoder.open(null, null)
        /** Добавляем энкодер в поток контейнера.  */
        muxer.addNewStream(encoder)
        /** Открываем контейнер.  */
        muxer.open(null, null)
        /** Next, we need to make sure we have the right MediaPicture format objects
         * to encode data with. Java (and most on-screen graphics programs) use some
         * variant of Red-Green-Blue image encoding (a.k.a. RGB or BGR). Most video
         * codecs use some variant of YCrCb formatting. So we're going to have to
         * convert. To do that, we'll introduce a MediaPictureConverter object later. object.
         */
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
            //Получаем изображение из очереди если оно доступно, иначе ждем
            val img = queue.take()
            val newImg = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            newImg.graphics.drawImage(img, 0, 0, null)
            val image = convertToType(newImg, BufferedImage.TYPE_3BYTE_BGR)
            println("[Encoder] Изображение $i добавлено")
            /** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities.  */
            if (converter == null) converter = MediaPictureConverterFactory.createConverter(image, picture)
            converter!!.toPicture(picture, image, i.toLong())
            do {
                encoder.encode(packet, picture)
                if (packet.isComplete) muxer.write(packet, false)
            } while (packet.isComplete)
            i++
        }
        do {
            encoder.encode(packet, null)
            if (packet.isComplete) muxer.write(packet, false)
        } while (packet.isComplete)
        /** Закрываем контейнер.*/
        muxer.close()
        println("Done!")
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

    override fun run() {
        createVideo("test1.avi", "avi", duration, 30)
    }
}