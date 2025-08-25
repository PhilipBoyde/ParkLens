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
        // Back button
        IconButton(
            onClick = onTakeNewPhoto,
            modifier = Modifier
                .align(Alignment.TopStart)
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


        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp, bottom = 150.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Signs
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(blocks.size) { index ->
                    val block = blocks[index]

                    val (backgroundColor, borderColor, textColor) = when (block.color) {
                        SignType.BLUE -> Triple(ParkingBlue, Color.White, Color.White)
                        SignType.YELLOW -> Triple(RestrictedParking, RestrictedParkingBorder, Color.Black)
                        SignType.UNKNOWN -> Triple(Color.White, Color.Black, Color.Black)
                    }

                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .border(7.dp, borderColor, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            block.rules.forEach { line ->
                                val fontSize = if (line.type == SymbolType.PARKING){
                                    124.sp
                                } else {
                                    43.sp
                                }

                                val currentTextColor = if (line.type == SymbolType.HOLIDAY) {
                                    RestrictedParkingBorder
                                } else {
                                    textColor
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
                        }
                    }
                }
            }
        }

        // Continue button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
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
}
