package se.umu.cs.phbo0006.parkLens.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import se.umu.cs.phbo0006.parkLens.model.Rules
import se.umu.cs.phbo0006.parkLens.R

import se.umu.cs.phbo0006.parkLens.view.ui.theme.BackgroundColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ParkingBlue
import se.umu.cs.phbo0006.parkLens.view.ui.theme.RestrictedParking
import se.umu.cs.phbo0006.parkLens.view.ui.theme.RestrictedParkingBorder

@Composable
fun ParkingRulePage(
    rules: Rules,
    onBack: () -> Unit = {},
    onNotifyTimeUp: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .systemBarsPadding()
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
                .background(
                    color = ParkingBlue.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .zIndex(10f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Back",
                tint = TextColor,
                modifier = Modifier.size(32.dp)
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = if (rules.allowedToPark) 120.dp else 0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Parking Status
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (rules.allowedToPark || rules.freeParking != null)
                        ParkingBlue
                    else
                        RestrictedParkingBorder
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = if (rules.allowedToPark || rules.freeParking != null)
                            painterResource(id = R.drawable.local_parking)
                        else
                            painterResource(id = R.drawable.no_local_parking),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (rules.allowedToPark || rules.freeParking != null)

                            if (rules.freeParking == true){
                                stringResource(R.string.free_Parking)
                            } else if (rules.freeParking == false){
                                stringResource(R.string.Parking_24h, rules.currentTime)
                            }else {
                                stringResource(R.string.parking_allowed)
                            }
                        else
                            stringResource(R.string.parking_not_allowed),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }


            // Time Range Status
            if (rules.allowedToPark && rules.timeRangeRule != null && rules.timeRangeRule.timeRange != null ){
                val timeRangeRule =  rules.timeRangeRule
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = RestrictedParking
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.timer),
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(56.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.allowed_parking_time, timeRangeRule.timeRange),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }


            // Paid Parking
            if (rules.allowedToPark && rules.paymentRule != null && rules.paymentRule.paidParkingWholeDay != null) {
                val paymentRule =  rules.paymentRule
                val paidParkingWholeDay = paymentRule.paidParkingWholeDay
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = RestrictedParkingBorder
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = if (paidParkingWholeDay) {
                                painterResource(id = R.drawable.monetization)
                            } else {
                                painterResource(id = R.drawable.time_based_parking)
                            },
                            contentDescription = null,
                            tint = TextColor,
                            modifier = Modifier.size(56.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (paidParkingWholeDay)
                                stringResource(R.string.paid_parking_whole_day)
                            else
                                if (paymentRule.endParkTime != null){
                                    stringResource(R.string.paid_parking_not_whole_day, paymentRule.endParkTime)
                                } else {
                                    throw IllegalArgumentException ("Payment rule is not allowed to be null")
                                },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextColor ,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Notify Button
        /*
        if (rules.allowedToPark) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNotifyTimeUp,
                    modifier = Modifier
                        .size(80.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkingBlue.copy(alpha = 0.5f),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.notification),
                        contentDescription = "Set Notification",
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.reminder),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextColor,
                    textAlign = TextAlign.Center
                )
            }
        }

         */
    }
}