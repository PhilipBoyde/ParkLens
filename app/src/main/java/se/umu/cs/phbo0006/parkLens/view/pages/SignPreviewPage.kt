package se.umu.cs.phbo0006.parkLens.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import se.umu.cs.phbo0006.parkLens.R
import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.model.signs.SignType
import se.umu.cs.phbo0006.parkLens.view.ui.theme.BackgroundColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ParkingBlue
import se.umu.cs.phbo0006.parkLens.view.ui.theme.RestrictedParking
import se.umu.cs.phbo0006.parkLens.view.ui.theme.RestrictedParkingBorder
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ButtonBlue
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule

/**
 * Composable that displays a preview of detected parking signs.
 *
 * @param blocks List of BlockInfo representing detected sign blocks.
 * @param onTakeNewPhoto Callback invoked when the user wants to retake a photo.
 * @param onContinue Callback invoked when the user wants to continue to the next step.
 */
@Composable
fun SignPreviewPage(
    blocks: List<BlockInfo>,
    onTakeNewPhoto: () -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .systemBarsPadding()
    ) {
        BackButton(
            onTakeNewPhoto,
            modifier = Modifier
                .align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp, bottom = 150.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SignList(
                blocks,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally),
            )
        }

        ContinueButton(
            onContinue,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

/**
 * Composable for the back button, allowing the user to retake a photo.
 *
 * @param onTakeNewPhoto Callback invoked when the button is clicked.
 * @param modifier Modifier for styling and positioning.
 */
@Composable
private fun BackButton(onTakeNewPhoto: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onTakeNewPhoto,
        modifier = modifier
            .padding(16.dp)
            .size(48.dp)
            .background(
                color = ButtonBlue,
                shape = CircleShape
            )
            .zIndex(10f)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.back_arrow),
            contentDescription = null,
            tint = TextColor,
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * Composable that displays a list of sign blocks.
 *
 * @param blocks List of BlockInfo to display.
 * @param modifier Modifier for styling and positioning.
 */
@Composable
private fun SignList(blocks: List<BlockInfo>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(blocks.size) { index ->
            SignBlock(blocks[index])
        }
    }
}

/**
 * Composable that displays a single sign block with its rules.
 *
 * @param block BlockInfo representing the sign block.
 * @param modifier Modifier for styling and positioning.
 */
@Composable
private fun SignBlock(block: BlockInfo, modifier: Modifier = Modifier) {
    val (backgroundColor, borderColor, textColor) = when (block.color) {
        SignType.BLUE -> Triple(ParkingBlue, Color.White, Color.White)
        SignType.YELLOW -> Triple(RestrictedParking, RestrictedParkingBorder, Color.Black)
        SignType.UNKNOWN -> Triple(Color.White, Color.Black, Color.Black)
    }

    Box(
        modifier = modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(7.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.wrapContentWidth()
        ) {
            block.rules.forEach { line ->
                SignRuleText(line, textColor)
            }
        }
    }
}

/**
 * Composable that displays a single rule line within a sign block.
 *
 * @param line ParkingRule representing the rule.
 * @param defaultTextColor Default color for the rule text.
 */
@Composable
private fun SignRuleText(line: ParkingRule, defaultTextColor: Color) {
    val fontSize = if (line.type == SymbolType.PARKING) {
        124.sp
    } else {
        43.sp
    }

    val currentTextColor = if (line.type == SymbolType.HOLIDAY) {
        RestrictedParkingBorder
    } else {
        defaultTextColor
    }

    Text(
        text = line.text,
        color = currentTextColor,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .wrapContentWidth()
            .padding(8.dp)
    )
}

/**
 * Composable for the continue button, allowing the user to proceed to the next step.
 *
 * @param onContinue Callback invoked when the button is clicked.
 * @param modifier Modifier for styling and positioning.
 */
@Composable
private fun ContinueButton(onContinue: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onContinue,
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = ButtonBlue,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.scan),
                contentDescription = null,
                tint = TextColor,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.continue_to_result),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = TextColor,
            textAlign = TextAlign.Center
        )
    }
}
