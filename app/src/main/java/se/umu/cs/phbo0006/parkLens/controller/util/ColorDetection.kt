package se.umu.cs.phbo0006.parkLens.controller.util

import android.graphics.Bitmap
import android.util.Log
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.android.Utils
import se.umu.cs.phbo0006.parkLens.model.signs.SignType

/**
 * Detects the dominant color in a bitmap, representing a parking sign.
 * This function performs histogram equalization and then identifies the dominant colors (blue and yellow)
 * based on their pixel ratios. It's designed for identifying parking signs color.
 *
 * @param bitmap The bitmap to analyze.
 * @return The dominant SignType (SignType.BLUE, SignType.YELLOW, or SignType.UNKNOWN)
 *         based on the detected color ratios.
 */
fun detectBlockColor(bitmap: Bitmap): SignType {
    val (channels, mat, hsvMat) = histogramEqualization(bitmap)

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

/**
 * Performs histogram equalization on a given bitmap.
 * This function converts the bitmap to HSV color space, equalizes the value channel
 * (which represents brightness), and then merges the channels back into an HSV bitmap.
 *
 * @param bitmap The bitmap to perform histogram equalization on.
 * @return A Triple containing:
 *         - The list of Mat channels after splitting.
 *         - The original Mat bitmap after conversion.
 *         - The Mat bitmap after histogram equalization.
 */
private fun histogramEqualization(bitmap: Bitmap) : Triple<List<Mat>, Mat, Mat> {
    val mat = Mat()
    Utils.bitmapToMat(bitmap, mat)

    val hsvMat = Mat()
    Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)

    val channels = mutableListOf<Mat>()
    Core.split(hsvMat, channels)
    val valueChannel = channels[2]
    Imgproc.equalizeHist(valueChannel, valueChannel)
    Core.merge(channels, hsvMat)

    return Triple(channels, mat, hsvMat)
}


/**
 * Checks if a bitmap contains red text.
 * This function converts the bitmap to an HSV color space, identifies red hues,
 * and calculates the ratio of red pixels to the total number of pixels.
 *
 * @param bitmap The bitmap to analyze.
 * @return True if the bitmap contains a significant amount of red text (ratio > 0.01),
 *         false otherwise.
 */
fun checkForRedText(bitmap: Bitmap): Boolean{
    val bgrMat = Mat()
    Utils.bitmapToMat(bitmap, bgrMat)

    Imgproc.cvtColor(bgrMat, bgrMat, Imgproc.COLOR_RGB2BGR)

    val hsvMat = Mat()
    Imgproc.cvtColor(bgrMat, hsvMat, Imgproc.COLOR_BGR2HSV)

    val totalPixels = bitmap.width * bitmap.height

    val redLower1 = Scalar(0.0, 100.0, 100.0)
    val redUpper1 = Scalar(10.0, 255.0, 255.0)

    val redLower2 = Scalar(160.0, 100.0, 100.0)
    val redUpper2 = Scalar(179.0, 255.0, 255.0)


    val mask1 = Mat()
    val mask2 = Mat()
    Core.inRange(hsvMat, redLower1, redUpper1, mask1)
    Core.inRange(hsvMat, redLower2, redUpper2, mask2)


    val redMask = Mat()
    Core.add(mask1, mask2, redMask)
    val redPixels = Core.countNonZero(redMask)
    val redRatio = redPixels.toDouble() / totalPixels

    bgrMat.release()
    hsvMat.release()
    redMask.release()

    return when {
        redRatio > 0.01 -> true
        else -> false
    }
}