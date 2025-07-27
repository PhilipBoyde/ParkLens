package se.umu.cs.phbo0006.parkLens.controller

import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.text.Text
import se.umu.cs.phbo0006.parkLens.model.signs.BlockData
import se.umu.cs.phbo0006.parkLens.model.signs.ClusterData
import kotlin.math.*


/**
 * Combines multiple bounding boxes into a single encompassing rectangle
 */
private fun combineBoundingBoxes(
    indices: List<Int>,
    lines: List<List<Text.Line?>>,
    textRects: List<Rect>
): ClusterData {
    val selectedLines = indices.flatMap { lines[it] }

    val combinedText = selectedLines.joinToString("\n") { it?.text.toString() }
    val rects = indices.map { textRects[it] }

    val minLeft = rects.minOf { it.left }
    val minTop = rects.minOf { it.top }
    val maxRight = rects.maxOf { it.right }
    val maxBottom = rects.maxOf { it.bottom }

    val combinedRect = Rect(minLeft, minTop, maxRight, maxBottom)

    return ClusterData(
        text = selectedLines,
        boundingBox = combinedRect,
        combinedText = combinedText
    )
}

/**
 * Groups text bounding boxes into clusters and returns combined bounding boxes
 *
 * @param textRects List of bounding boxes for detected text
 * @return List of Rect where each Rect is either a single text box or combined bounding box of a cluster
 */
fun clusterTextBoxes(blockData:  List<BlockData>): List<ClusterData> {
    val textRects: List<Rect> = blockData.map { it.boundingBox }
    val lines: List<List<Text.Line?>> = blockData.map { it.line}

    val adaptiveThreshold = calculateAdaptiveThreshold(textRects)
    Log.i("ClusterDistance", adaptiveThreshold.toString())

    val clusters = mutableListOf<MutableList<Int>>()
    blockData.forEachIndexed { index, _ ->
        clusters.add(mutableListOf(index))
    }

    var merged = true
    while (merged) {
        merged = false

        for (i in clusters.indices) {
            if (clusters[i].isEmpty()) continue

            for (j in i + 1 until clusters.size) {
                if (clusters[j].isEmpty()) continue

                val distance = calculateClusterDistance(clusters[i], clusters[j], textRects)

                if (distance <= adaptiveThreshold) {
                    clusters[i].addAll(clusters[j])
                    clusters[j].clear()
                    merged = true
                    break
                }
            }

            if (merged) break
        }
    }

    return clusters.filter { it.isNotEmpty() }.map { cluster ->
        combineBoundingBoxes(cluster, lines, textRects)
    }
}

/**
 * Calculates an adaptive threshold for grouping sign text blocks based on their size and density.
 * This function aims to intelligently group nearby text rectangles into consistent sizes,
 * avoiding overly-aggressive merging while still ensuring text chunks are grouped correctly.
 *
 * @param textRects A list of Rect objects, each representing the bounding box of a signs text block.
 * @return A float representing the adaptive threshold for merging text rectangles.
 */
private fun calculateAdaptiveThreshold(textRects: List<Rect>): Float {
    val avgWidth = textRects.map { it.width() }.average().toFloat()
    val avgHeight = textRects.map { it.height() }.average().toFloat()
    val avgDimension = (avgWidth + avgHeight) / 2

    val baseThreshold = avgDimension * 1.2f

    val imageBounds = calculateImageBounds(textRects)
    val textDensity = textRects.size.toFloat() / (imageBounds.width() * imageBounds.height())

    Log.i("textDensity", textDensity.toString())


    val densityFactor = when {
        textDensity > 0.0001f -> 0.6f     // Higher density
        else -> 0.8f                      // Lower density
    }

    Log.i("densityFactor", densityFactor.toString())

    return (baseThreshold * densityFactor).coerceAtLeast(20f).coerceAtMost(200f)
}

/**
 * Calculates the minimum distance between two clusters of sign text blocks.
 * This function compares the distance between all pairs of rectangles from the two clusters.
 * It returns the smallest distance found.
 *
 * @param cluster1 A list of indices representing the sign text blocks in the first cluster.
 * @param cluster2 A list of indices representing the sign text blocks in the second cluster.
 * @param textRects A list of Rect objects, where each Rect represents a sign text block.
 * @return The minimum distance between any two rectangles, one from each cluster.
 */
private fun calculateClusterDistance(
    cluster1: List<Int>,
    cluster2: List<Int>,
    textRects: List<Rect>
): Float {
    var minDistance = Float.MAX_VALUE

    cluster1.forEach { idx1 ->
        cluster2.forEach { idx2 ->
            val distance = calculateRectDistance(textRects[idx1], textRects[idx2])
            minDistance = min(minDistance, distance)
        }
    }

    return minDistance
}

/**
 * Calculates distance between two rectangles using their centers
 * Weights vertical distance less than horizontal for better sign grouping
 */
private fun calculateRectDistance(rect1: Rect, rect2: Rect): Float {
    val centerX1 = rect1.centerX().toFloat()
    val centerY1 = rect1.centerY().toFloat()
    val centerX2 = rect2.centerX().toFloat()
    val centerY2 = rect2.centerY().toFloat()

    val horizontalDistance = abs(centerX1 - centerX2)
    val verticalDistance = abs(centerY1 - centerY2)

    // Weight vertical distance less (0.7x) since sign text is often stacked vertically
    return sqrt(horizontalDistance.pow(2) + (verticalDistance * 0.7f).pow(2))
}

/**
 * Calculates the bounding rectangle that contains all text boxes
 */
private fun calculateImageBounds(textRects: List<Rect>): Rect {
    val minX = textRects.minOf { it.left }
    val maxX = textRects.maxOf { it.right }
    val minY = textRects.minOf { it.top }
    val maxY = textRects.maxOf { it.bottom }

    return Rect(minX, minY, maxX, maxY)
}