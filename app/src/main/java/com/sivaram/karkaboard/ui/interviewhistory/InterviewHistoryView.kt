package com.sivaram.karkaboard.ui.interviewhistory

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.interviewhistory.state.UiState
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.ui.theme.overpassMonoSemiBold
import com.sivaram.karkaboard.ui.theme.success
import com.sivaram.karkaboard.ui.theme.successContainer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewHistoryView(
    navController: NavController,
    context: Context,
    interviewHistoryViewModel: InterviewHistoryViewModel = hiltViewModel()
) {
    val brush = UtilityFunctions.getGradient()
    val coroutineScope = rememberCoroutineScope()
    BaseView(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(brush),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.secondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                title = {
                    Text(
                        modifier = Modifier.padding(start = 15.dp),
                        text = "Interview History",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 15.dp),
                        onClick = {
                            coroutineScope.launch {
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(R.drawable.ic_back_return),
                            contentDescription = "Back",
                        )
                    }
                },
            )
        }

    ) { innerPadding, userData ->
        Box(
            Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.secondaryContainer),
        ) {
            InterviewHistoryViewContent(
                userData = userData,
                navController = navController,
                context = context,
                interviewHistoryViewModel = interviewHistoryViewModel
            )
        }
    }
}

@Composable
fun InterviewHistoryViewContent(
    userData: UserData?,
    navController: NavController,
    context: Context,
    interviewHistoryViewModel: InterviewHistoryViewModel = hiltViewModel()
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val interviewHistoryData by interviewHistoryViewModel.interviewHistoryData.observeAsState()
    val uiState by interviewHistoryViewModel.uiState.collectAsState()

    LaunchedEffect(userData) {
        Log.d("UserData", "InterviewHistoryViewContent: $userData")
        if (userData != null) {
            interviewHistoryViewModel.getInterviewHistory(userData.uId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimaryContainer)

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    is UiState.Success -> {
                        interviewHistoryData?.forEach { data ->
                            HistoryContentCard(
                                applicationData = data.applicationData,
                                batchData = data.batchData
                            )
                        }
                    }

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Something went wrong",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                    fontFamily = overpassMonoBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Toast.makeText(
                                context,
                                "Something went wrong, Please check your internet connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    UiState.Empty -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = Modifier.size(250.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryContentCard(
    modifier: Modifier = Modifier,
    applicationData: ApplicationData,
    batchData: BatchData
) {
    var expand by rememberSaveable { mutableStateOf(false) }
    var status by rememberSaveable { mutableStateOf("In Review") }
    var statusContentColor by remember { mutableStateOf(Color.Black) }
    var statusContainerColor by remember { mutableStateOf(Color.Black) }
    var statusBorderColor by remember { mutableStateOf(Color.Black) }
    var statusContent by rememberSaveable { mutableStateOf("") }
    if (applicationData.processId.toInt() == 1) {
        statusContentColor = MaterialTheme.colorScheme.primary
        statusContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
        statusBorderColor = MaterialTheme.colorScheme.primary
        statusContent = "You're application currently in review"
        status = "In Review"
    } else if (applicationData.processId.toInt() == 2) {
        statusContentColor = MaterialTheme.colorScheme.primary
        statusContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
        statusBorderColor = MaterialTheme.colorScheme.primary
        statusContent =
            "Your application has been shortlisted And you're currently in interview process"
        status = "In Interview"
    } else if (applicationData.processId.toInt() == 3) {
        statusContentColor = MaterialTheme.colorScheme.success
        statusContainerColor = MaterialTheme.colorScheme.successContainer
        statusBorderColor = MaterialTheme.colorScheme.success
        statusContent = "You're onboarded for ${batchData.batchName}"
        status = "Selected"
    } else {
        statusContentColor = MaterialTheme.colorScheme.error
        statusContainerColor = MaterialTheme.colorScheme.errorContainer
        statusBorderColor = MaterialTheme.colorScheme.error
        statusContent = "Sorry, your application has been rejected."
        status = "Rejected"
    }
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = batchData.batchName,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            fontFamily = overpassMonoBold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = statusContainerColor,
                            contentColor = statusContentColor,
                        ),
                        border = BorderStroke(
                            1.dp,
                            statusBorderColor
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            text = status,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoMedium
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row() {
                        Text(
                            text = "Applied At ",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoMedium
                            )
                        )
                        Text(
                            text = UtilityFunctions.convertLongToDateTimeAmPm(
                                applicationData.appliedAt
                            ),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoMedium
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Icon(
                        modifier = Modifier
                            .size(25.dp)
                            .clickable { expand = !expand },
                        painter = if (expand) painterResource(R.drawable.ic_up)
                        else painterResource(R.drawable.ic_down),
                        contentDescription = "Down",
                        tint = MaterialTheme.colorScheme.primary
                    )

                }
            }
            if (expand) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_java),
                                contentDescription = "designation",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Designation",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                )
                            )
                        }
                        Text(
                            text = batchData.designation,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoSemiBold
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_date),
                                contentDescription = "posted at",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Posted At",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                )
                            )
                        }
                        Text(
                            text = UtilityFunctions.convertMillisToDate(
                                batchData.createdAt
                            ),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoSemiBold
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_interview_date),
                                contentDescription = "interview date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Interview Date",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                )
                            )
                        }
                        Text(
                            text = UtilityFunctions.convertMillisToDate(
                                batchData.interviewDate
                            ),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoSemiBold
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_location),
                                contentDescription = "location",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Interview Location",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                )
                            )
                        }
                        Text(
                            text = batchData.interviewLocation,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoSemiBold
                            )
                        )
                    }
                    if(applicationData.processId >= 3) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "Feedback given by interviewer",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = applicationData.feedback,
                                style = TextStyle(
                                    textAlign = TextAlign.Justify,
                                    textIndent = TextIndent(firstLine = 30.sp, restLine = 0.sp),
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                )
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "Ratings",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.padding(start = 30.dp),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                for (index in 1..5) {
                                    Icon(
                                        modifier = Modifier.size(30.dp),
                                        painter = painterResource(
                                            if (applicationData.performanceRating >= index)
                                                R.drawable.ic_star_fill
                                            else
                                                R.drawable.ic_star
                                        ),
                                        contentDescription = "Star",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = statusContainerColor,
                            contentColor = statusContentColor
                        ),
                        border = BorderStroke(1.dp, statusBorderColor),
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = statusContent,
                            modifier = Modifier.padding(10.dp),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoMedium
                            ),
                        )
                    }
                }
            }
        }

    }
}

@Composable
@Preview
fun HistoryContentCardPreview() {
    KarkaBoardTheme {
        HistoryContentCard(
            applicationData = ApplicationData(),
            batchData = BatchData()
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
fun InterviewHistoryViewPreview() {
    val interviewHistoryViewModel = InterviewHistoryViewModel(
        FakeDbRepo()
    )
    KarkaBoardTheme {
        InterviewHistoryViewContent(
            userData = UserData(),
            navController = rememberNavController(),
            context = LocalContext.current,
            interviewHistoryViewModel = interviewHistoryViewModel
        )
    }
}