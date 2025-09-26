package se.umu.cs.phbo0006.parkLens.view.helper

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.model.signs.SignType
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ParkingBlue
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ButtonBlue
import se.umu.cs.phbo0006.parkLens.view.ui.theme.RestrictedParking
import se.umu.cs.phbo0006.parkLens.view.ui.theme.BackgroundColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.CardBackgroundColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.SurfaceVariantColor
import se.umu.cs.phbo0006.parkLens.R

/**
 * Displays the debug mode page containing navigation buttons and a scrollable list of block information cards.
 *
 * @param blockInfoList List of [BlockInfo] objects to display in card format
 * @param onBackClick Callback invoked when back button is clicked
 * @param onNextClick Callback invoked when next button is clicked
 * @param modifier Optional [Modifier] for layout customization
 */
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
        DebugNavigationBar(onBackClick, onNextClick)
        DebugBlockInfoList(blockInfoList)
    }
}

/**
 * Displays the navigation bar with Back and Next buttons.
 *
 * @param onBackClick Callback for Back button
 * @param onNextClick Callback for Next button
 */
@Composable
private fun DebugNavigationBar(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DebugNavButton(
            text = stringResource(R.string.back_button),
            onClick = onBackClick
        )
        DebugNavButton(
            text = stringResource(R.string.next_button),
            onClick = onNextClick
        )
    }
}

/**
 * Displays a navigation button with the given text and click handler.
 *
 * @param text Button label
 * @param onClick Callback for button click
 */
@Composable
private fun DebugNavButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonBlue,
            contentColor = TextColor
        )
    ) {
        Text(text)
    }
}

/**
 * Displays a scrollable list of block information cards.
 *
 * @param blockInfoList List of [BlockInfo] objects
 */
@Composable
private fun DebugBlockInfoList(blockInfoList: List<BlockInfo>) {
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

/**
 * Displays a card containing information about a specific parking block.
 *
 * @param blockInfo The [BlockInfo] object containing block data
 * @param blockNumber Current block number (starting from 1)
 * @param modifier Optional [Modifier] for layout adjustments (default: fills maximum width)
 */
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
            BlockInfoHeader(blockNumber)
            BlockInfoContent(blockInfo)
            BlockInfoRules(blockInfo)
        }
    }
}

/**
 * Displays the header for a block info card.
 *
 * @param blockNumber The block number to display
 */
@Composable
private fun BlockInfoHeader(blockNumber: Int) {
    Text(
        text = stringResource(R.string.block_title, blockNumber),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = ParkingBlue,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

/**
 * Displays the main content section of a block info card, including image and details.
 *
 * @param blockInfo The [BlockInfo] object
 */
@Composable
private fun BlockInfoContent(blockInfo: BlockInfo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BlockInfoImageSection(
            blockInfo,
            modifier = Modifier
                .weight(0.4f),
            imageModifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
        )
        BlockInfoDetailsSection(
            blockInfo,
            modifier = Modifier
                .weight(0.6f)
        )
    }
}

/**
 * Displays the image section of a block info card.
 *
 * @param blockInfo The [BlockInfo] object
 * @param modifier Modifier for the column
 * @param imageModifier Modifier for the image
 */
@Composable
private fun BlockInfoImageSection(blockInfo: BlockInfo, modifier: Modifier, imageModifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.image_title),
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
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = stringResource(R.string.no_image),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Displays the details section of a block info card.
 *
 * @param blockInfo The [BlockInfo] object
 * @param modifier Modifier for the column
 */
@Composable
private fun BlockInfoDetailsSection(blockInfo: BlockInfo, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        BlockInfoColorIndicator(blockInfo)
        BlockInfoExtractedText(blockInfo)
    }
}

/**
 * Displays a color indicator for the sign type.
 *
 * @param blockInfo The [BlockInfo] object
 */
@Composable
private fun BlockInfoColorIndicator(blockInfo: BlockInfo) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.sign_type_title),
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
}

/**
 * Displays the extracted text from a block info.
 *
 * @param blockInfo The [BlockInfo] object
 */
@Composable
private fun BlockInfoExtractedText(blockInfo: BlockInfo) {
    Text(
        text = stringResource(R.string.extracted_text_title),
        style = MaterialTheme.typography.labelMedium,
        color = TextColor.copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 4.dp)
    )

    Text(
        text = blockInfo.text.ifBlank { stringResource(R.string.no_text_extracted) },
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

/**
 * Displays the parking rules section for a block info card.
 *
 * @param blockInfo The [BlockInfo] object
 */
@Composable
private fun BlockInfoRules(blockInfo: BlockInfo) {
    if (blockInfo.rules.isNotEmpty()) {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = TextColor.copy(alpha = 0.2f)
        )

        Text(
            text = stringResource(R.string.parking_rules_size, blockInfo.rules.size),
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

/**
 * Displays a card containing detailed information about an individual parking rule.
 *
 * @param rule The [ParkingRule] object with validation details
 * @param ruleNumber Current rule number (starting from 1)
 * @param modifier Optional [Modifier] for layout adjustments (default: fills maximum width)
 */
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
            ParkingRuleHeader(rule, ruleNumber)
            ParkingRuleDetails(rule)
        }
    }
}

/**
 * Displays the header for a parking rule card.
 *
 * @param rule The [ParkingRule] object
 * @param ruleNumber The rule number to display
 */
@Composable
private fun ParkingRuleHeader(rule: ParkingRule, ruleNumber: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.rule_count, ruleNumber),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = ParkingBlue
        )

        Spacer(modifier = Modifier.width(8.dp))

        ParkingRuleTypeBadge(rule.type.name)
    }
}

/**
 * Displays a badge for the parking rule type.
 *
 * @param typeName The name of the rule type
 */
@Composable
private fun ParkingRuleTypeBadge(typeName: String) {
    Surface(
        color = ButtonBlue,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = typeName,
            style = MaterialTheme.typography.labelSmall,
            color = TextColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Displays the details section for a parking rule card.
 *
 * @param rule The [ParkingRule] object
 */
@Composable
private fun ParkingRuleDetails(rule: ParkingRule) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ParkingRuleText(rule.text)
        ParkingRuleTimeRange(rule.startHour, rule.endHour)
        ParkingRuleSubType(rule.subType?.name)
    }
}

/**
 * Displays the rule text if available.
 *
 * @param text The rule text
 */
@Composable
private fun ParkingRuleText(text: String) {
    if (text.isNotBlank()) {
        Text(
            text = stringResource(R.string.text_title, text),
            style = MaterialTheme.typography.bodyMedium,
            color = TextColor
        )
    }
}

/**
 * Displays the time range for a parking rule if available.
 *
 * @param startHour Start hour (nullable)
 * @param endHour End hour (nullable)
 */
@Composable
private fun ParkingRuleTimeRange(startHour: Int?, endHour: Int?) {
    if (startHour != null || endHour != null) {
        Text(
            text = stringResource(R.string.time_range_extracted_text, startHour ?: "", endHour ?: ""),
            style = MaterialTheme.typography.bodyMedium,
            color = TextColor
        )
    }
}

/**
 * Displays the subtype for a parking rule if available.
 *
 * @param subTypeName The subtype name (nullable)
 */
@Composable
private fun ParkingRuleSubType(subTypeName: String?) {
    if (!subTypeName.isNullOrBlank()) {
        Text(
            text = stringResource(R.string.subtype_title, subTypeName),
            style = MaterialTheme.typography.bodyMedium,
            color = TextColor
        )
    }
}

/**
 * Converts a [SignType] enum value to its corresponding color representation.
 *
 * @param signType Enum of [SignType] to map to color
 * @return Color appropriate for the given sign type (handles UNKNOWN case by defaulting)
 */
@Composable
fun getSignTypeColor(signType: SignType): Color {
    return when (signType) {
        SignType.BLUE -> ParkingBlue
        SignType.YELLOW -> RestrictedParking
        SignType.UNKNOWN -> TextColor
    }
}