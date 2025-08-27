package se.umu.cs.phbo0006.parkLens.view.helper

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.model.signs.SignType
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule

// Custom color palette
val TextColor = Color(0xFFE5EAF0)
val ParkingBlue = Color(0xFF0067A4)
val ButtonBlue = ParkingBlue.copy(alpha = 0.8f)
val RestrictedParking = Color(0xFFFFD908)
val RestrictedParkingBorder = Color(0xFFF14B53)
val BackgroundColor = Color(0xFF161618)
val CardBackgroundColor = Color(0xFF2A2A2E)
val SurfaceVariantColor = Color(0xFF3A3A3E)

@Composable
fun DebugModePage(
    blockInfoList: List<BlockInfo>,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .systemBarsPadding()
    ) {
        // Top navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            Button(
                onClick = onBackClick,
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBlue,
                    contentColor = TextColor
                )
            ) {
                Text("Back")
            }

            // Next button
            Button(
                onClick = onNextClick,
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBlue,
                    contentColor = TextColor
                )
            ) {
                Text("Next")
            }
        }

        // Scrollable content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(blockInfoList.size) { index ->
                BlockInfoCard(
                    blockInfo = blockInfoList[index],
                    blockNumber = index + 1
                )
            }
        }
    }
}

@Composable
fun BlockInfoCard(
    blockInfo: BlockInfo,
    blockNumber: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Text(
                text = "Block #$blockNumber",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ParkingBlue,
                modifier = Modifier.padding(bottom = 12.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left side
                Column(
                    modifier = Modifier.weight(0.4f)
                ) {
                    Text(
                        text = "Image: ",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                SurfaceVariantColor,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (blockInfo.croppedImage != null) {
                            Image(
                                bitmap = blockInfo.croppedImage,
                                contentDescription = "Cropped sign image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "No image",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextColor.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Right side
                Column(
                    modifier = Modifier.weight(0.6f)
                ) {
                    // Color indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Sign Type: ",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextColor.copy(alpha = 0.7f)
                        )

                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    getSignTypeColor(blockInfo.color),
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    TextColor.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = blockInfo.color.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = TextColor
                        )
                    }

                    // Extracted text
                    Text(
                        text = "Extracted Text:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = blockInfo.text.ifBlank { "No text extracted" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextColor,
                        modifier = Modifier
                            .background(
                                SurfaceVariantColor,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp)
                    )
                }
            }

            // Parking rules
            if (blockInfo.rules.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = TextColor.copy(alpha = 0.2f)
                )

                Text(
                    text = "Parking Rules (${blockInfo.rules.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ParkingBlue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    blockInfo.rules.forEachIndexed { index, rule ->
                        ParkingRuleItem(
                            rule = rule,
                            ruleNumber = index + 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingRuleItem(
    rule: ParkingRule,
    ruleNumber: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariantColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Rule header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Rule #$ruleNumber",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = ParkingBlue
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Symbol type
                Surface(
                    color = ButtonBlue,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = rule.type.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Rule details
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Rule text
                if (rule.text.isNotBlank()) {
                    Text(
                        text = "Text: ${rule.text}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextColor
                    )
                }

                // Time range
                if (rule.startHour != -1 || rule.endHour != -1) {
                    Text(
                        text = "startHour: ${rule.startHour} | endHour: ${rule.endHour}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextColor
                    )
                }

                // Subtype
                rule.subType?.let { subType ->
                    Text(
                        text = "Subtype: ${subType.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextColor
                    )
                }
            }
        }
    }
}

@Composable
fun getSignTypeColor(signType: SignType): Color {
    return when (signType) {
        SignType.BLUE -> ParkingBlue
        SignType.YELLOW -> RestrictedParking
        SignType.UNKNOWN -> TextColor
    }
}