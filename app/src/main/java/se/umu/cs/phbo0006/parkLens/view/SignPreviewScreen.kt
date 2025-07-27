package se.umu.cs.phbo0006.parkLens.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.model.signs.SignType
import se.umu.cs.phbo0006.parkLens.view.ui.theme.BackgroundColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ParkingBlue
import se.umu.cs.phbo0006.parkLens.view.ui.theme.RestrictedParking
import se.umu.cs.phbo0006.parkLens.view.ui.theme.RestrictedParkingBorder


@Composable
fun ColorBlocksScreen(
    blocks: List<BlockInfo>,
    onTakeNewPhoto: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
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


        Row( //buttons
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onTakeNewPhoto,
                modifier = Modifier
                    .widthIn(max = 120.dp)
                    .height(40.dp)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkingBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "New Photo",
                    fontSize = 18.sp
                )
            }
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .widthIn(max = 120.dp)
                    .height(40.dp)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkingBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 18.sp
                )
            }
        }
    }
}
