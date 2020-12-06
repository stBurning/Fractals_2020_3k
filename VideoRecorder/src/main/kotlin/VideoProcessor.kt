import io.humble.video.*
import io.humble.video.awt.MediaPictureConverter
import io.humble.video.awt.MediaPictureConverterFactory
import java.awt.AWTException
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.IOException
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue


/** (Consumer) Класс, получающий через очередь изображения и собирающий их в видео
 * @param queue BlockingQueue - очередь, осуществляющая связь с (Producers) */
class VideoProcessor(private val queue: LinkedBlockingQueue<BufferedImage>, private val width: Int, private val height: Int) : Runnable{
        /**
         * Records the screen
         */
        @Throws(AWTException::class, InterruptedException::class, IOException::class)
        private fun recordScreen(filename: String, formatname: String, duration: Int, snapsPerSecond: Int) {
            /**
             * Set up the AWT infrastructure to take screenshots of the desktop.
             */
            val robot = Robot()
            val toolkit = Toolkit.getDefaultToolkit()
            val screenbounds = Rectangle(toolkit.screenSize)
            val framerate = Rational.make(1, snapsPerSecond)
            /** First we create a muxer using the passed in filename and formatname if given.  */
            val muxer = Muxer.make(filename, null, formatname)

            /** Now, we need to decide what type of codec to use to encode video. Muxers
             * have limited sets of codecs they can use. We're going to pick the first one that
             * works, or if the user supplied a codec name, we're going to force-fit that
             * in instead.
             */
            val format = muxer.format
            val codec: Codec = Codec.findEncodingCodec(format.defaultVideoCodecId)
            /**
             * Now that we know what codec, we need to create an encoder
             */
            val encoder = Encoder.make(codec)
            /**
             * Video encoders need to know at a minimum:
             * width
             * height
             * pixel format
             * Some also need to know frame-rate (older codecs that had a fixed rate at which video files could
             * be written needed this). There are many other options you can set on an encoder, but we're
             * going to keep it simpler here.
             */
            encoder.width = screenbounds.width
            encoder.height = screenbounds.height
            // We are going to use 420P as the format because that's what most video formats these days use
            val pixelformat = PixelFormat.Type.PIX_FMT_YUV420P
            encoder.pixelFormat = pixelformat
            encoder.timeBase = framerate
            /** An annoynace of some formats is that they need global (rather than per-stream) headers,
             * and in that case you have to tell the encoder. And since Encoders are decoupled from
             * Muxers, there is no easy way to know this beyond
             */
            if (format.getFlag(ContainerFormat.Flag.GLOBAL_HEADER)) encoder.setFlag(Coder.Flag.FLAG_GLOBAL_HEADER, true)
            /** Open the encoder.  */
            encoder.open(null, null)
            /** Add this stream to the muxer.  */
            muxer.addNewStream(encoder)
            /** And open the muxer for business.  */
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
                pixelformat
            )
            picture.timeBase = framerate
            /** Now begin our main loop of taking screen snaps.
             * We're going to encode and then write out any resulting packets.  */
            val packet = MediaPacket.make()
            var i = 0
            while (i < duration / framerate.double) {
                /** Make the screen capture && convert image to TYPE_3BYTE_BGR  */

                val img = queue.take()
                val newImg = BufferedImage(screenbounds.width, screenbounds.height, BufferedImage.TYPE_INT_RGB )
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
            /** Encoders, like decoders, sometimes cache pictures so it can do the right key-frame optimizations.
             * So, they need to be flushed as well. As with the decoders, the convention is to pass in a null
             * input until the output is not complete.
             */
            do {
                encoder.encode(packet, null)
                if (packet.isComplete) muxer.write(packet, false)
            } while (packet.isComplete)
            /** Finally, let's clean up after ourselves.  */
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
        private fun convertToType(
            sourceImage: BufferedImage,
            targetType: Int
        ): BufferedImage {
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
        recordScreen("test.avi", "avi", 5, 30)
    }
}




