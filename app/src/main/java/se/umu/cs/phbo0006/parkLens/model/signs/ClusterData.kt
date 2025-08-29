package se.umu.cs.phbo0006.parkLens.model.signs

import android.graphics.Rect
import com.google.mlkit.vision.text.Text

/**
 * Represents data associated with a cluster of text blocks.
 *
 * This data class stores the lines of text within a cluster,
 * its bounding rectangle, and a combined text representation.
 *
 * @param text The list of Text.Line objects that constitute the cluster.
 * @param boundingBox The bounding rectangle for the entire cluster.
 * @param combinedText A combined string representation of the text within the cluster.
 */
data class ClusterData (
    val text: List<Text.Line?>,
    val boundingBox: Rect,
    val combinedText: String
)
