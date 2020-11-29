import java.awt.Graphics
import java.awt.image.BufferedImage

/**Класс, получающий изображения и преобразующий их в видео
 * @author Ustinov Konstantin **/

class ScreenRecorder(g: Graphics) {

    private val imageBuffer = ArrayList<BufferedImage>()

    public fun addImage(img: BufferedImage){
        imageBuffer.add(img)
    }

    public fun compile(){
        
    }

}