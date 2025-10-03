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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults.DragHandle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
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
fun ApplicationPortalViewContent(
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
        if(userData!=null) {
            applicationPortalViewModel.getApplicationPortalData(userData.uId)
        }
    }
    Log.d("applicationPortalData", applicationPortalData.toString())

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    var bottomSheetParams by remember { mutableStateOf<BottomSheetParams?>(null) }

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
                }
                else{
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        applicationPortalData?.forEach {data ->
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
                                                    text = "Posted at ${UtilityFunctions.convertMillisToDate(
                                                        batchData?.createdAt ?: 0
                                                    )}",
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
                                                        text = batchData?.interviewLocation ?: "Unknown",
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
                                                    if(isApplied){
                                                        bottomSheetParams = batchData?.let { batchData
                                                            userData?.let { studentData ->
                                                                BottomSheetParams(
                                                                    batchData, studentData
                                                                )
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        applicationPortalViewModel.applyForTraining(
                                                            batchData?.docId ?: "",
                                                            userData?.uId ?: ""
                                                        )
                                                    }
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    containerColor = if(isApplied)MaterialTheme.colorScheme.onSecondary
                                                                        else MaterialTheme.colorScheme.primaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                ),
                                                border = BorderStroke(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            ) {
                                                when(state){
                                                    is ApplyState.Error -> {
                                                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                                        applicationPortalViewModel.resetApplyState(
                                                            batchData?.docId ?: ""
                                                        )
                                                    }
                                                    ApplyState.Idle -> {
                                                        Text(
                                                            textAlign = TextAlign.Center,
                                                            text = if(isApplied)"Status" else "Apply",
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
                                                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
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
    if(bottomSheetParams!=null){
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
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
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
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
}

@Composable
fun BottomSheetDesign(bottomSheetParams: BottomSheetParams, applicationPortalViewModel: ApplicationPortalViewModel){
    Log.d("bottomSheetParams", bottomSheetParams.toString())
    val batchData = bottomSheetParams.batchData
    val studentData = bottomSheetParams.studentData

//    val processState by applicationPortalViewModel.processState.observeAsState()
//    val appliedDate by applicationPortalViewModel.appliedDate.observeAsState()
    val applicationData by applicationPortalViewModel.applicationData.observeAsState()

    LaunchedEffect(bottomSheetParams){
        applicationPortalViewModel.getApplicationData(batchData.docId, studentData.uId)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    text = "Applied at ${UtilityFunctions.convertMillisToDate(
                        applicationData?.appliedAt ?: 0
                    )}",
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
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(0.dp)
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
                            Column(modifier = Modifier.padding(top = 7.dp),) {
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
                            Column(modifier = Modifier.padding(top = 7.dp),) {
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
                        {Column(modifier = Modifier.padding(top = 7.dp),) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = "Interview",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                    fontFamily = overpassMonoSemiBold
                                )
                            )
                        }},
                        {Column(modifier = Modifier.padding(top = 7.dp),) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = "Onboarding",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                    fontFamily = overpassMonoSemiBold
                                )
                            )
                        }}
                    ),
                    totalSteps = 4,
                    currentStep = applicationData?.processId?.toLong() ?: 0,
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
//            Column(modifier = Modifier.padding(top = 10.dp)
//                .padding(horizontal = 0.dp)) { StatusIndicator() }


//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ){
//                Text(
//                    modifier = Modifier.weight(1f),
//                    text = "InReview",
//                    style = TextStyle(
//                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
//                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
//                        fontFamily = overpassMonoSemiBold
//                    )
//                )
//                Text(
//                    modifier = Modifier.weight(1f),
//                    textAlign = TextAlign.Center,
//                    text = "Shortlisted",
//                    style = TextStyle(
//                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
//                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
//                        fontFamily = overpassMonoSemiBold
//                    )
//                )
//                Text(
//                    modifier = Modifier.weight(1f),
//                    textAlign = TextAlign.Center,
//                    text = "Interview",
//                    style = TextStyle(
//                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
//                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
//                        fontFamily = overpassMonoSemiBold
//                    )
//                )
//                Text(
//                    modifier = Modifier.weight(1f),
//                    textAlign = TextAlign.Center,
//                    text = "Result",
//                    style = TextStyle(
//                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
//                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
//                        fontFamily = overpassMonoSemiBold
//                    )
//                )
//            }
        }
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