package se.umu.cs.phbo0006.parkLens.controller.util

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

fun preprocessImage(bitmap: Bitmap): Mat {
    val image = Mat()
    Utils.bitmapToMat(bitmap, image)

    val hsvImage = Mat()
    Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV)

    Imgproc.GaussianBlur(hsvImage, hsvImage, Size(5.0, 5.0), 0.0)

    // Histogram equalization on V channel
    val channels = mutableListOf<Mat>()
    Core.split(hsvImage, channels)
    Imgproc.equalizeHist(channels[2], channels[2]) // Equalize brightness
    Core.merge(channels, hsvImage)

    return hsvImage
}