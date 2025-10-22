package com.sivaram.karkaboard.ui.applicationportal

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults.DragHandle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sivaram.karkaboard.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.binayshaw7777.kotstep.model.LineDefault
import com.binayshaw7777.kotstep.model.LineType
import com.binayshaw7777.kotstep.model.StepDefaults
import com.binayshaw7777.kotstep.model.StepStyle
import com.binayshaw7777.kotstep.model.iconVerticalWithLabel
import com.binayshaw7777.kotstep.ui.vertical.VerticalStepper
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.BottomSheetParams
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.applicationportal.state.ApplyState
import com.sivaram.karkaboard.ui.auth.fake.FakeApplicationPortalRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.ui.theme.overpassMonoSemiBold
import com.sivaram.karkaboard.ui.theme.success
import com.sivaram.karkaboard.ui.theme.successContainer
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationPortalView(
    navController: NavController,
    context: Context,
    applicationPortalViewModel: ApplicationPortalViewModel = hiltViewModel()
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
                        text = "Application Portal",
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
            ApplicationPortalViewContent(
                userData = userData,
                navController = navController,
                context = context,
                applicationPortalViewModel = applicationPortalViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApplicationPortalViewContent(
    userData: UserData?,
    navController: NavController,
    context: Context,
    applicationPortalViewModel: ApplicationPortalViewModel
) {

    val applicationPortalData by applicationPortalViewModel.applicationPortalData.observeAsState()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val applyStates by applicationPortalViewModel.applyState.collectAsState()

    LaunchedEffect(userData) {
        Log.d("userData", userData.toString())
        if (userData != null) {
            applicationPortalViewModel.getApplicationPortalData(userData.uId)
        }
    }
    Log.d("applicationPortalData", applicationPortalData.toString())

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    var bottomSheetParams by remember { mutableStateOf<BottomSheetParams?>(null) }

    var detailsBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    var detailsBottomSheetParams by remember { mutableStateOf<BottomSheetParams?>(null) }
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
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                if (applicationPortalData?.isEmpty() == true) {
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
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        applicationPortalData?.forEach { data ->
                            val batchData = data.batchData
                            val isApplied = data.isApplied
                            val state = applyStates[batchData?.docId] ?: ApplyState.Idle
                            item {
                                OutlinedCard(
                                    modifier = Modifier
                                        .height(140.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.onSecondary,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    onClick = {

                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(15.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(5.dp)
                                            ) {
                                                Text(
                                                    text = batchData?.batchName ?: "Unknown",
                                                    style = TextStyle(
                                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                        fontFamily = overpassMonoBold
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = batchData?.designation ?: "Unknown",
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    style = TextStyle(
                                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                        fontFamily = overpassMonoMedium
                                                    ),
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.padding(start = 10.dp),
                                                verticalArrangement = Arrangement.spacedBy(5.dp)
                                            ) {
                                                Text(
                                                    text = "Posted at ${
                                                        UtilityFunctions.convertMillisToDate(
                                                            batchData?.createdAt ?: 0
                                                        )
                                                    }",
                                                    style = TextStyle(
                                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                        fontFamily = overpassMonoMedium
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Row(
                                                    modifier = Modifier.align(Alignment.End),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    Icon(
                                                        modifier = Modifier.size(15.dp),
                                                        painter = painterResource(R.drawable.ic_location),
                                                        contentDescription = "Location",
                                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                    Text(
                                                        textAlign = TextAlign.End,
                                                        text = batchData?.interviewLocation
                                                            ?: "Unknown",
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        style = TextStyle(
                                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                            fontFamily = overpassMonoBold
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        HorizontalDivider(
                                            thickness = 1.dp,
                                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(
                                                15.dp,
                                                Alignment.CenterHorizontally
                                            ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedButton(
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    detailsBottomSheetParams = batchData?.let {
                                                        batchData
                                                        userData?.let { studentData ->
                                                            BottomSheetParams(
                                                                batchData, studentData
                                                            )
                                                        }
                                                    }
                                                },
                                                border = BorderStroke(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                ),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    containerColor = MaterialTheme.colorScheme.onSecondary,
                                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                ),
                                            ) {
                                                Text(
                                                    textAlign = TextAlign.Center,
                                                    text = "Details",
                                                    style = TextStyle(
                                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                        fontFamily = overpassMonoBold
                                                    )
                                                )
                                                Icon(
                                                    modifier = Modifier.size(20.dp),
                                                    painter = painterResource(R.drawable.ic_arrow_right),
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    contentDescription = "Details"
                                                )
                                            }
                                            OutlinedButton(
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    if (isApplied) {
                                                        bottomSheetParams = batchData?.let {
                                                            batchData
                                                            userData?.let { studentData ->
                                                                BottomSheetParams(
                                                                    batchData, studentData
                                                                )
                                                            }
                                                        }
                                                    } else {
                                                        applicationPortalViewModel.applyForTraining(
                                                            batchData?.docId ?: "",
                                                            userData?.uId ?: ""
                                                        )
                                                    }
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    containerColor = if (isApplied) MaterialTheme.colorScheme.onSecondary
                                                    else MaterialTheme.colorScheme.primaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                ),
                                                border = BorderStroke(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            ) {
                                                when (state) {
                                                    is ApplyState.Error -> {
                                                        Toast.makeText(
                                                            context,
                                                            state.message,
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        applicationPortalViewModel.resetApplyState(
                                                            batchData?.docId ?: ""
                                                        )
                                                    }

                                                    ApplyState.Idle -> {
                                                        Text(
                                                            textAlign = TextAlign.Center,
                                                            text = if (isApplied) "Status" else "Apply",
                                                            style = TextStyle(
                                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                fontFamily = overpassMonoBold
                                                            )
                                                        )
                                                    }

                                                    ApplyState.Loading -> {
                                                        CircularProgressIndicator(
                                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                                            modifier = Modifier.size(25.dp),
                                                            strokeWidth = 4.dp
                                                        )
                                                    }

                                                    is ApplyState.Success -> {
                                                        if (state.isApplied) {
                                                            Toast.makeText(
                                                                context,
                                                                state.message,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                state.message,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        applicationPortalViewModel.resetApplyState(
                                                            batchData?.docId ?: ""
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (bottomSheetParams != null) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            sheetState = bottomSheetState,
            onDismissRequest = {
                bottomSheetParams = null
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            dragHandle = {
                DragHandle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(
                    0.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BottomSheetDesign(
                    bottomSheetParams = bottomSheetParams!!,
                    applicationPortalViewModel = applicationPortalViewModel
                )
            }
        }
    }
    if (detailsBottomSheetParams != null) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            sheetState = detailsBottomSheetState,
            onDismissRequest = {
                detailsBottomSheetParams = null
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            dragHandle = {
                DragHandle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(
                    0.dp,
                    alignment = Alignment.CenterVertically
                ),
            ) {
                DetailsBottomSheetDesign(
                    detailsBottomSheetParams!!
                )
            }
        }
    }
}

@Composable
private fun BottomSheetDesign(
    bottomSheetParams: BottomSheetParams,
    applicationPortalViewModel: ApplicationPortalViewModel
) {
    Log.d("bottomSheetParams", bottomSheetParams.toString())
    val batchData = bottomSheetParams.batchData
    val studentData = bottomSheetParams.studentData

//    val processState by applicationPortalViewModel.processState.observeAsState()
//    val appliedDate by applicationPortalViewModel.appliedDate.observeAsState()
    val applicationData by applicationPortalViewModel.applicationData.observeAsState()

    LaunchedEffect(bottomSheetParams) {
        applicationPortalViewModel.getApplicationData(batchData.docId, studentData.uId)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        applicationData?.processId?.toLong()?.let {

            val processId = it
            var statusContentColor by remember { mutableStateOf(Color.Transparent) }
            var statusContainerColor by remember { mutableStateOf(Color.Transparent) }
            var statusBorderColor by remember { mutableStateOf(Color.Transparent) }
            var statusContent by rememberSaveable { mutableStateOf("") }
            if (processId.toInt() == 1) {
                statusContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                statusContainerColor = MaterialTheme.colorScheme.onSecondary
                statusBorderColor = MaterialTheme.colorScheme.onSecondaryContainer
                statusContent = "You're application currently in review"
            } else if (processId.toInt() == 2) {
                statusContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                statusContainerColor = MaterialTheme.colorScheme.onSecondary
                statusBorderColor = MaterialTheme.colorScheme.onSecondaryContainer
                statusContent =
                    "Your application has been shortlisted And you're currently in interview process"
            } else if (processId.toInt() == 3) {
                statusContentColor = MaterialTheme.colorScheme.success
                statusContainerColor = MaterialTheme.colorScheme.successContainer
                statusBorderColor = MaterialTheme.colorScheme.success
                statusContent = "You're onboarded for ${batchData.batchName}"
            } else {
                statusContentColor = MaterialTheme.colorScheme.error
                statusContainerColor = MaterialTheme.colorScheme.errorContainer
                statusBorderColor = MaterialTheme.colorScheme.error
                statusContent = "Sorry, your application has been rejected."
            }
            Text(
                textAlign = TextAlign.Center,
                text = "Application Status",
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                    fontFamily = overpassMonoBold
                )
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = batchData.batchName,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = batchData.designation,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            fontFamily = overpassMonoMedium
                        ),
                    )
                }
                Column(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Applied at ${
                            UtilityFunctions.convertMillisToDate(
                                applicationData?.appliedAt ?: 0
                            )
                        }",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            fontFamily = overpassMonoMedium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            textAlign = TextAlign.End,
                            text = batchData.interviewLocation,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            )
                        )
                    }
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
            )
            Column(
            ) {
                Text(
                    text = "Your Application",
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                        fontFamily = overpassMonoSemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                VerticalStepper(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(horizontal = 5.dp),
                    style = iconVerticalWithLabel(
                        trailingLabels = listOf(
                            {
                                Column(modifier = Modifier.padding(top = 7.dp)) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = "Applied",
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                            fontFamily = overpassMonoSemiBold
                                        )
                                    )
                                }
                            },
                            {
                                Column(modifier = Modifier.padding(top = 7.dp)) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = "Shortlisted",
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                            fontFamily = overpassMonoSemiBold
                                        )
                                    )
                                }
                            },
                            {
                                Column(modifier = Modifier.padding(top = 7.dp)) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = "Interview",
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                            fontFamily = overpassMonoSemiBold
                                        )
                                    )
                                }
                            },
                            {
                                Column(modifier = Modifier.padding(top = 7.dp)) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = "Onboarding",
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                            fontFamily = overpassMonoSemiBold
                                        )
                                    )
                                }
                            }
                        ),
                        totalSteps = 4,
                        currentStep = if (processId > 3) {
                            processId - 5
                        } else if (processId.toInt() == 3) {
                            4
                        } else {
                            processId
                        },
                        icons = listOf(
                            ImageVector.vectorResource(R.drawable.ic_location),
                            ImageVector.vectorResource(R.drawable.ic_shortlist),
                            ImageVector.vectorResource(R.drawable.ic_interview),
                            ImageVector.vectorResource(R.drawable.ic_onboard)
                        ),
                        stepStyle = StepStyle(
                            colors = StepDefaults(
                                todoContainerColor = MaterialTheme.colorScheme.onSecondary,
                                todoContentColor = MaterialTheme.colorScheme.secondary,
                                todoLineColor = MaterialTheme.colorScheme.onSecondary,
                                currentContainerColor = MaterialTheme.colorScheme.primary,
                                currentContentColor = MaterialTheme.colorScheme.primary,
                                currentLineColor = MaterialTheme.colorScheme.primary,
                                doneContainerColor = MaterialTheme.colorScheme.primary,
                                doneContentColor = MaterialTheme.colorScheme.onSecondary,
                                doneLineColor = MaterialTheme.colorScheme.primary,
                                checkMarkColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            lineStyle = LineDefault(
                                lineSize = 40.dp,
                                lineThickness = 5.dp,
                                linePaddingStart = 5.dp,
                                linePaddingEnd = 5.dp,
                                linePaddingTop = 0.dp,
                                linePaddingBottom = 0.dp,
                                trackStrokeCap = StrokeCap.Round,
                                progressStrokeCap = StrokeCap.Round,
                                currentLineTrackType = LineType.SOLID,
                            ),
                            stepSize = 36.dp,
                            stepShape = CircleShape,
                            stepStroke = 2f,
                            iconSize = 24.dp,
                        ),
                    )
                )
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
                    modifier = Modifier.padding(15.dp),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                        fontFamily = overpassMonoSemiBold
                    ),
                )
            }
        }
    }
}

@Composable
private fun DetailsBottomSheetDesign(
    bottomSheetParams: BottomSheetParams
) {
    val batchData = bottomSheetParams.batchData
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically)
    ) {
        Text(
            text = "Details",
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                fontFamily = overpassMonoSemiBold
            )
        )
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.onSecondary,
//                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//            ),
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(
                    10.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_batch_name),
                        contentDescription = "Batch Name",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = batchData.batchName,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            fontFamily = overpassMonoSemiBold
                        )
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_java),
                        contentDescription = "Designation",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = batchData.designation,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                            fontFamily = overpassMonoSemiBold
                        )
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_date),
                        contentDescription = "Duration",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Duration " + UtilityFunctions.convertMillisToDate(batchData.startDate) + " to " +
                                UtilityFunctions.convertMillisToDate(batchData.endDate),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                            fontFamily = overpassMonoSemiBold
                        )
                    )
                }
            }
        }
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.onSecondary,
//                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//            ),
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(
                    10.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_time),
                        contentDescription = "Posted at",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Posted at " + UtilityFunctions.convertMillisToDate(
                            batchData.createdAt
                        ),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                            fontFamily = overpassMonoSemiBold
                        )
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_interview_date),
                        contentDescription = "Interview Date",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Interview Date " + UtilityFunctions.convertMillisToDate(batchData.interviewDate),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            fontFamily = overpassMonoMedium
                        )
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = batchData.interviewLocation,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                            fontFamily = overpassMonoSemiBold
                        )
                    )
                }

            }
        }
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.onSecondary,
//                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//            ),
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(
                    10.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                Column(
                    modifier = Modifier.padding(0.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        5.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(R.drawable.ic_description),
                            contentDescription = "Description",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Description",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                                fontFamily = overpassMonoSemiBold
                            )
                        )
                    }
                    Text(
                        text = batchData.description.trimIndent(),
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
                    modifier = Modifier.padding(0.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        5.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(R.drawable.ic_skill),
                            contentDescription = "Skill",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Skills Needed",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                                fontFamily = overpassMonoSemiBold
                            )
                        )
                    }
                    LazyRow(
                        modifier = Modifier
                            .padding(start = 30.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        items(batchData.skills) { skill ->
                            OutlinedCard(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(25.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 5.dp)
                                            .padding(top = 4.dp),
                                        textAlign = TextAlign.Center,
                                        text = skill,
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                            fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                                            fontFamily = overpassMonoMedium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsBottomSheetDesignPreview() {
    KarkaBoardTheme {
        DetailsBottomSheetDesign(
            bottomSheetParams = BottomSheetParams(
                BatchData(),
                UserData()
            )
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ApplicationPortalViewPreview() {
    val fakeDbRepo = FakeDbRepo()
    val fakeApplicationPortalRepo = FakeApplicationPortalRepo()
    val fakeVM = ApplicationPortalViewModel(fakeApplicationPortalRepo, fakeDbRepo)
    KarkaBoardTheme {
        ApplicationPortalViewContent(
            userData = UserData(),
            navController = rememberNavController(),
            context = LocalContext.current,
            applicationPortalViewModel = fakeVM
        )
    }
}