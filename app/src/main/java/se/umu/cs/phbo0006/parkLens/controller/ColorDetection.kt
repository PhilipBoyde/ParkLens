package se.umu.cs.phbo0006.parkLens.controller

import android.graphics.Bitmap
import android.util.Log
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import se.umu.cs.phbo0006.parkLens.model.signs.SignType

fun detectBlockColor(bitmap: Bitmap): SignType {
    val mat = Mat()
    org.opencv.android.Utils.bitmapToMat(bitmap, mat)

    val hsvMat = Mat()
    Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)

    // Histogram equalization
    val channels = mutableListOf<Mat>()
    Core.split(hsvMat, channels)
    val valueChannel = channels[2]
    Imgproc.equalizeHist(valueChannel, valueChannel)
    Core.merge(channels, hsvMat)


    val blueLower = Scalar(100.0, 50.0, 50.0)
    val blueUpper = Scalar(140.0, 255.0, 255.0)
    val yellowLower = Scalar(20.0, 50.0, 50.0)
    val yellowUpper = Scalar(40.0, 255.0, 255.0)


    val blueMask = Mat()
    val yellowMask = Mat()
    Core.inRange(hsvMat, blueLower, blueUpper, blueMask)
    Core.inRange(hsvMat, yellowLower, yellowUpper, yellowMask)


    val bluePixels = Core.countNonZero(blueMask)
    val yellowPixels = Core.countNonZero(yellowMask)
    val totalPixels = bitmap.width * bitmap.height

    mat.release()
    hsvMat.release()
    blueMask.release()
    yellowMask.release()
    channels.forEach { it.release() }

    val blueRatio = bluePixels.toDouble() / totalPixels
    val yellowRatio = yellowPixels.toDouble() / totalPixels
    Log.i("ColorDetection", "Blue ratio: $blueRatio, Yellow ratio: $yellowRatio")

    return when {
        blueRatio > 0.2 -> SignType.BLUE
        yellowRatio > 0.2 -> SignType.YELLOW
        else -> SignType.UNKNOWN
    }
}