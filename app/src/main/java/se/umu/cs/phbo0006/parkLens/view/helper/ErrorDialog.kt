package se.umu.cs.phbo0006.parkLens.view.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.CardBackgroundColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ParkingBlue
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ErrorIconBackground
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ErrorIconColor
import se.umu.cs.phbo0006.parkLens.R


/**
 * Displays a modal error dialog indicating that no parking sign was found.
 *
 * @param onDismiss Lambda function to be called when the dialog is dismissed.
 */
@Composable
fun ErrorDialog(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ErrorIcon()

                Spacer(modifier = Modifier.height(20.dp))

                ErrorTitle()

                Spacer(modifier = Modifier.height(8.dp))

                ErrorDescription()

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ErrorDialogButton(
                        onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                    )
                }
            }
        }
    }
}

/**
 * Displays the error icon inside a circular background.
 */
@Composable
private fun ErrorIcon() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(
                color = ErrorIconBackground,
                shape = CircleShape
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.no_local_parking),
            contentDescription = "No sign found",
            tint = ErrorIconColor,
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * Displays the error dialog title.
 */
@Composable
private fun ErrorTitle() {
    Text(
        text = stringResource(R.string.no_sign_found_title),
        color = TextColor,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
    )
}

/**
 * Displays the error dialog description text.
 */
@Composable
private fun ErrorDescription() {
    Text(
        text = stringResource(R.string.no_sign_found_description),
        color = TextColor.copy(alpha = 0.7f),
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

/**
 * Displays the button for dismissing the error dialog.
 *
 * @param onDismiss Lambda function to be called when the button is clicked.
 * @param modifier Modifier to be applied to the button.
 */
@Composable
private fun ErrorDialogButton(onDismiss: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(
            containerColor = ParkingBlue,
            contentColor = TextColor
        ),
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Text(
            text = stringResource(R.string.ok_button),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
