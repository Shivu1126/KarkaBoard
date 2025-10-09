package com.sivaram.karkaboard.ui.interviewmanagement

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults.DragHandle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.interviewmanagement.state.AcceptState
import com.sivaram.karkaboard.ui.interviewmanagement.state.ApplicationState
import com.sivaram.karkaboard.ui.interviewmanagement.state.DeclineState
import com.sivaram.karkaboard.ui.interviewmanagement.state.UiState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoRegular
import com.sivaram.karkaboard.ui.theme.overpassMonoSemiBold
import com.sivaram.karkaboard.ui.theme.success
import com.sivaram.karkaboard.ui.theme.successContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewManagementView(
    navController: NavController,
    context: Context,
    interviewManagementViewModel: InterviewManagementViewModel = hiltViewModel()
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
                        text = "Interview Management",
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
            InterviewManagementViewContent(
                navController,
                context,
                interviewManagementViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewManagementViewContent(
    navController: NavController,
    context: Context,
    interviewManagementViewModel: InterviewManagementViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val allBatchesData by interviewManagementViewModel.allBatchesData.observeAsState()
    val appliedStudentData by interviewManagementViewModel.appliedStudentData.observeAsState()

    val filterList = listOf("All", "In Review", "In Interview", "Selected", "Rejected")
    var filterId by rememberSaveable { mutableIntStateOf(0) }

    var expandBatchDropDown by rememberSaveable { mutableStateOf(false) }
    var expandFilterDropDown by rememberSaveable { mutableStateOf(false) }

    var selectedBatchId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedBatch = remember(allBatchesData, selectedBatchId) {
        allBatchesData?.find { it.docId == selectedBatchId }
    }

    val acceptStates by interviewManagementViewModel.acceptState.collectAsState()
    val declineStates by interviewManagementViewModel.declineState.collectAsState()
    val applicationStates by interviewManagementViewModel.applicationState.collectAsState()
    val uiState by interviewManagementViewModel.uiState.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    var bottomSheetParam by remember { mutableStateOf<UserData?>(null) }

    LaunchedEffect(Unit) {
        interviewManagementViewModel.getAllBatches()
    }
    Log.d("allBatchesData", allBatchesData.toString())
    LaunchedEffect(allBatchesData, selectedBatchId) {
        when {
            selectedBatchId != null -> {
                interviewManagementViewModel.getAppliedStudentDetail(selectedBatchId!!, filterId)
            }

            !allBatchesData.isNullOrEmpty() -> {
                allBatchesData?.firstOrNull()?.let { batch ->
                    selectedBatchId = batch.docId
                    interviewManagementViewModel.getAppliedStudentDetail(batch.docId, filterId)
                }
            }
        }
    }
    Log.d("appliedStudentData", appliedStudentData.toString())
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
            if (allBatchesData?.isEmpty() == true) {
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    expandBatchDropDown = !expandBatchDropDown
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = selectedBatch?.batchName ?: "Unknown",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                    fontFamily = overpassMonoBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(R.drawable.ic_arrow_drop_down),
                                contentDescription = "DropDown",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        DropdownMenu(
                            expanded = expandBatchDropDown,
                            onDismissRequest = { expandBatchDropDown = false },
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            allBatchesData?.forEachIndexed { index, batch ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = batch.batchName,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                fontFamily = overpassMonoBold
                                            )
                                        )
                                    },
                                    onClick = {
                                        selectedBatchId = batch.docId
                                        interviewManagementViewModel.getAppliedStudentDetail(
                                            batch.docId, filterId
                                        )
                                        Log.d("selectedBatch", selectedBatch.toString())
                                        Log.d(
                                            "appliedStudentData",
                                            appliedStudentData.toString()
                                        )
                                        expandBatchDropDown = false
                                    },
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    colors = MenuDefaults.itemColors(
                                        textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                )
                                if (batch != allBatchesData?.last()) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.5f
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp),
                                        thickness = 1.dp
                                    )
                                }
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 0.dp)
                                .weight(0.5f)
                        )
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    expandFilterDropDown = !expandFilterDropDown
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                text = filterList[filterId],
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                    fontFamily = overpassMonoBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .size(20.dp),
                                painter = painterResource(R.drawable.ic_filter),
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            DropdownMenu(
                                expanded = expandFilterDropDown,
                                onDismissRequest = { expandFilterDropDown = false },
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                filterList.forEachIndexed { index, filterName ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = filterName,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                    fontFamily = overpassMonoBold
                                                )
                                            )
                                        },
                                        onClick = {
                                            filterId = index
                                            interviewManagementViewModel.getAppliedStudentDetail(
                                                selectedBatchId!!, filterId
                                            )
                                            expandFilterDropDown = false
                                        },
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        colors = MenuDefaults.itemColors(
                                            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        )
                                    )
                                    if (filterName != filterList.last()) {
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                alpha = 0.5f
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp),
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    when(uiState){
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
                        UiState.Error -> {
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
                                Toast.makeText(context, "Something went wrong, Please check your internet connection", Toast.LENGTH_SHORT).show()
                            }
                        }
                        UiState.Loading -> {
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
                        UiState.Success -> {
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .padding(horizontal = 0.dp)
                                    .padding(top = 20.dp),
                                columns = GridCells.Fixed(1),
                                verticalArrangement = Arrangement.spacedBy(25.dp),
                            ) {
                                appliedStudentData?.forEachIndexed { index, student ->
                                    item {
//                                    StudentCardContent(appliedStudentData = student)
                                        var studentData = student.studentData
                                        var applicationData = student.applicationData
                                        var studentUserData = student.userData
                                        val acceptState =
                                            acceptStates[applicationData.docId] ?: AcceptState.Idle
                                        val declineState = declineStates[applicationData.docId]
                                            ?: DeclineState.Idle
                                        val applicationState =
                                            applicationStates[applicationData.docId]
                                                ?: ApplicationState.Idle
                                        var bgIcon by rememberSaveable { mutableStateOf(false) }
                                        OutlinedCard(
                                            modifier = Modifier
                                                .wrapContentHeight()
                                                .fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.onSecondary,
                                                contentColor = MaterialTheme.colorScheme.secondary
                                            ),
                                            onClick = {
                                                bottomSheetParam = studentUserData
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            border = BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(vertical = 10.dp),
                                                verticalArrangement = Arrangement.spacedBy(
                                                    10.dp,
                                                    alignment = Alignment.CenterVertically
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 10.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        modifier = Modifier.weight(2f),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            15.dp
                                                        )
                                                    ) {
                                                        OutlinedCard(
                                                            modifier = Modifier
                                                                .size(70.dp),
                                                            colors = CardDefaults.cardColors(
                                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                            ),
                                                            border = BorderStroke(
                                                                1.dp,
                                                                MaterialTheme.colorScheme.onPrimaryContainer
                                                            ),
                                                            elevation = CardDefaults.cardElevation(
                                                                defaultElevation = 10.dp
                                                            ),
                                                            shape = CircleShape
                                                        ) {

                                                            if (studentUserData.profileImgUrl.isEmpty()) {
                                                                Icon(
                                                                    modifier = Modifier.fillMaxSize(),
                                                                    painter = painterResource(R.drawable.ic_user_profile),
                                                                    contentDescription = "Profile Image"
                                                                )
                                                            } else {
                                                                AsyncImage(
                                                                    model = studentUserData.profileImgUrl.toUri(),
                                                                    contentDescription = "Profile Image",
                                                                    modifier = Modifier
                                                                        .fillMaxSize()
                                                                        .clip(CircleShape),
                                                                    contentScale = ContentScale.Crop,
                                                                    error = painterResource(R.drawable.ic_user_profile),
                                                                    onError = {
                                                                        bgIcon = true
                                                                        Log.d("load image", "error on loading image")
                                                                        Log.d("load image", it.result.throwable.message.toString())
                                                                    },
                                                                    onSuccess = {
                                                                        bgIcon = false
                                                                        Log.d("load image", "image loaded successfully")
                                                                    },
                                                                    colorFilter = if (bgIcon) ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer) else null
                                                                )
                                                            }
                                                        }
                                                        Column(
                                                            modifier = Modifier
                                                                .weight(1f),
                                                            verticalArrangement = Arrangement.spacedBy(
                                                                8.dp,
                                                                alignment = Alignment.CenterVertically
                                                            )
                                                        ) {
                                                            Text(
                                                                text = studentUserData.name,
                                                                style = TextStyle(
                                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                                    fontFamily = overpassMonoBold
                                                                ),
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Text(
                                                                text = studentUserData.email,
                                                                style = TextStyle(
                                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                                    fontFamily = overpassMonoBold
                                                                ),
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    }
                                                    Row(
                                                        modifier = Modifier.weight(1f),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = if (applicationData.processId != 2) Arrangement.End
                                                        else Arrangement.spacedBy(5.dp)
                                                    ) {
                                                        if (applicationData.processId == 1) {
                                                            Column(
                                                                modifier = Modifier.weight(1f),
                                                                verticalArrangement = Arrangement.spacedBy(
                                                                    0.dp
                                                                ),
                                                                horizontalAlignment = Alignment.End
                                                            ) {
                                                                OutlinedButton(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    enabled = acceptState !is AcceptState.Loading,
                                                                    onClick = {
                                                                        interviewManagementViewModel.shortlistForInterview(
                                                                            applicationData.docId,
                                                                            applicationData.processId
                                                                        )
                                                                    },
                                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                                                            alpha = 0.5f
                                                                        ),
                                                                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                                            alpha = 0.5f
                                                                        )
                                                                    ),
                                                                    border = BorderStroke(
                                                                        1.dp,
                                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                                    )
                                                                ) {
                                                                    when (acceptState) {
                                                                        is AcceptState.Error -> {
                                                                            Toast.makeText(
                                                                                context,
                                                                                "Something went wrong",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                            interviewManagementViewModel.resetAcceptState(
                                                                                applicationData.docId
                                                                            )
                                                                        }

                                                                        AcceptState.Idle ->
                                                                            Text(
                                                                                textAlign = TextAlign.Center,
                                                                                text = "Accept",
                                                                                style = TextStyle(
                                                                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                                    fontFamily = overpassMonoBold
                                                                                ),
                                                                                maxLines = 1,
                                                                                overflow = TextOverflow.Ellipsis
                                                                            )

                                                                        AcceptState.Loading ->
                                                                            CircularProgressIndicator(
                                                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                                                    alpha = 0.5f
                                                                                ),
                                                                                modifier = Modifier.size(
                                                                                    20.dp
                                                                                ),
                                                                                strokeWidth = 4.dp
                                                                            )

                                                                        is AcceptState.Success -> {
                                                                            Toast.makeText(
                                                                                context,
                                                                                "${studentUserData.name} Selected for interview",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                            interviewManagementViewModel.resetAcceptState(
                                                                                applicationData.docId
                                                                            )
                                                                        }
                                                                    }

                                                                }
                                                                OutlinedButton(
                                                                    enabled = declineState !is DeclineState.Loading,
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    onClick = {
                                                                        interviewManagementViewModel.declineCandidateForInterview(
                                                                            applicationData
                                                                        )
                                                                    },
                                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                                        containerColor = MaterialTheme.colorScheme.onSecondary,
                                                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                                        disabledContainerColor = MaterialTheme.colorScheme.onSecondary.copy(
                                                                            alpha = 0.5f
                                                                        ),
                                                                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                                            alpha = 0.5f
                                                                        )
                                                                    ),
                                                                    border = BorderStroke(
                                                                        1.dp,
                                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                                    )
                                                                ) {

                                                                    when (declineState) {
                                                                        is DeclineState.Error -> {
                                                                            Toast.makeText(
                                                                                context,
                                                                                "Something went wrong",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                            interviewManagementViewModel.resetDeclineState(
                                                                                applicationData.docId
                                                                            )
                                                                        }

                                                                        DeclineState.Idle ->
                                                                            Text(
                                                                                textAlign = TextAlign.Center,
                                                                                text = "Decline",
                                                                                style = TextStyle(
                                                                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                                    fontFamily = overpassMonoBold
                                                                                ),
                                                                            )

                                                                        DeclineState.Loading ->
                                                                            CircularProgressIndicator(
                                                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                                                    alpha = 0.5f
                                                                                ),
                                                                                modifier = Modifier.size(
                                                                                    20.dp
                                                                                ),
                                                                                strokeWidth = 4.dp
                                                                            )

                                                                        is DeclineState.Success -> {
                                                                            Toast.makeText(
                                                                                context,
                                                                                "${studentUserData.name} Not shortlisted for interview",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                            interviewManagementViewModel.resetDeclineState(
                                                                                applicationData.docId
                                                                            )
                                                                        }
                                                                    }

                                                                    Text(
                                                                        textAlign = TextAlign.Center,
                                                                        text = "Decline",
                                                                        style = TextStyle(
                                                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                            fontFamily = overpassMonoBold
                                                                        ),
                                                                        maxLines = 1,
                                                                        overflow = TextOverflow.Ellipsis
                                                                    )
                                                                }
                                                            }
                                                        } else if (applicationData.processId == 2) {
                                                            Row(
                                                                modifier = Modifier.weight(1f),
                                                                horizontalArrangement = when (applicationState) {
                                                                    is ApplicationState.Error -> Arrangement.End
                                                                    ApplicationState.Idle -> Arrangement.End
                                                                    ApplicationState.Loading -> Arrangement.Center
                                                                    is ApplicationState.Success -> Arrangement.End
                                                                },
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                when (applicationState) {
                                                                    is ApplicationState.Error -> {
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Something went wrong",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                        interviewManagementViewModel.resetApplicationState(
                                                                            applicationData.docId
                                                                        )
                                                                    }

                                                                    ApplicationState.Idle -> {
                                                                        IconButton(
                                                                            onClick = {
                                                                                interviewManagementViewModel.rejectedFromInterview(
                                                                                    applicationData
                                                                                )
                                                                            }
                                                                        ) {
                                                                            Icon(
                                                                                modifier = Modifier.size(
                                                                                    33.dp
                                                                                ),
                                                                                painter = painterResource(
                                                                                    R.drawable.ic_reject
                                                                                ),
                                                                                contentDescription = "Reject",
                                                                                tint = MaterialTheme.colorScheme.error
                                                                            )
                                                                        }
                                                                        IconButton(
                                                                            onClick = {
                                                                                interviewManagementViewModel.selectedForTraining(
                                                                                    applicationData,
                                                                                    studentData.docId
                                                                                )
                                                                            }
                                                                        ) {
                                                                            Icon(
                                                                                modifier = Modifier.size(
                                                                                    30.dp
                                                                                ),
                                                                                painter = painterResource(
                                                                                    R.drawable.ic_select
                                                                                ),
                                                                                contentDescription = "Select",
                                                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                                            )
                                                                        }
                                                                    }

                                                                    ApplicationState.Loading -> {
                                                                        CircularProgressIndicator(
                                                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                                                alpha = 0.5f
                                                                            ),
                                                                            modifier = Modifier.size(
                                                                                30.dp
                                                                            ),
                                                                            strokeWidth = 4.dp
                                                                        )
                                                                    }

                                                                    is ApplicationState.Success -> {
                                                                        if (applicationState.isSelected) {
                                                                            Toast.makeText(
                                                                                context,
                                                                                "${studentUserData.name} Selected for training",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                        } else {
                                                                            Toast.makeText(
                                                                                context,
                                                                                "${studentUserData.name} Not Selected for training",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                        }
                                                                        interviewManagementViewModel.resetApplicationState(
                                                                            applicationData.docId
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        } else if (applicationData.processId == 3) {
                                                            OutlinedCard(
                                                                modifier = Modifier.weight(1f),
                                                                colors = CardDefaults.cardColors(
                                                                    containerColor = MaterialTheme.colorScheme.successContainer,
                                                                    contentColor = MaterialTheme.colorScheme.success
                                                                ),
                                                                border = BorderStroke(
                                                                    1.dp,
                                                                    MaterialTheme.colorScheme.success
                                                                )

                                                            ) {
                                                                Text(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(15.dp),
                                                                    textAlign = TextAlign.Center,
                                                                    text = "Selected",
                                                                    style = TextStyle(
                                                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                        fontFamily = overpassMonoBold
                                                                    ),
                                                                    color = MaterialTheme.colorScheme.success
                                                                )
                                                            }
                                                        } else {
                                                            OutlinedCard(
                                                                modifier = Modifier.weight(1f),
                                                                colors = CardDefaults.cardColors(
                                                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                                                ),
                                                                border = BorderStroke(
                                                                    1.dp,
                                                                    MaterialTheme.colorScheme.onErrorContainer
                                                                )
                                                            ) {
                                                                Text(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(15.dp),
                                                                    textAlign = TextAlign.Center,
                                                                    text = "Rejected",
                                                                    style = TextStyle(
                                                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                        fontFamily = overpassMonoRegular
                                                                    ),
                                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                HorizontalDivider(
                                                    thickness = 1.dp,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                        alpha = 0.3f
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 15.dp)
                                                )
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 15.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Status :",
                                                        style = TextStyle(
                                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                            fontFamily = overpassMonoSemiBold
                                                        ),
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                    Text(
                                                        text = if (applicationData.processId == 1) "Candidate Waiting For Interview"
                                                        else if (applicationData.processId == 2) "Candidate On Interview Process"
                                                        else if (applicationData.processId == 3) "Candidate Selected For Training"
                                                        else {
                                                            if (applicationData.processId == 6)
                                                                "Candidate Not Shortlisted"
                                                            else
                                                                "Candidate Rejected at Interview Process"
                                                        },
                                                        style = TextStyle(
                                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                            fontFamily = overpassMonoRegular
                                                        ),
                                                        color =
                                                            if (applicationData.processId == 1 || applicationData.processId == 2) MaterialTheme.colorScheme.onPrimaryContainer
                                                            else if (applicationData.processId == 3) MaterialTheme.colorScheme.success
                                                            else MaterialTheme.colorScheme.error
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
    bottomSheetParam?.let { userData ->
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                bottomSheetParam = null
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
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(
                    0.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BottomSheetDesign(
                    userData,
                    context
                )
            }
        }
    }
}

@SuppressLint("UseKtx")
@Composable
fun BottomSheetDesign(
    userData: UserData,
    context: Context
){

    var bgIcon by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ){

            Text(
                textDecoration = TextDecoration.Underline,
                text = "Candidate Details",
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                    fontFamily = overpassMonoBold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .size(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                shape = CircleShape
            ) {
                if (userData.profileImgUrl.isEmpty()) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.ic_user_profile),
                        contentDescription = "Profile Image"
                    )
                } else {
                    AsyncImage(
                        model = userData.profileImgUrl.toUri(),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.ic_user_profile),
                        onError = {
                            bgIcon = true
                            Log.d("load image", "error on loading image")
                            Log.d("load image", it.result.throwable.message.toString())
                        },
                        onSuccess = {
                            bgIcon = false
                            Log.d("load image", "image loaded successfully")
                        },
                        colorFilter = if (bgIcon) ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer) else null
                    )
                }
            }
            Text(
                text = userData.name,
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    fontFamily = overpassMonoBold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:") // only email apps should handle this
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(userData.email))
                    }
                    try{
                        context.startActivity(intent)
                    }catch (e: ActivityNotFoundException){
                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                    }catch (e: Exception){
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }

                },
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.5f
                    ),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp, alignment = Alignment.CenterHorizontally)
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(R.drawable.ic_mail),
                        contentDescription = "Mail"
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(
                            5.dp,
                            alignment = Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "E-Mail Id",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,

                        )
                        Text(
                            text = userData.email,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = "tel:${userData.countryCode + userData.mobile}".toUri()
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.successContainer,
                    contentColor = MaterialTheme.colorScheme.success
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.success
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 10.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp, alignment = Alignment.CenterHorizontally)
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(R.drawable.ic_phone),
                        contentDescription = "Call",
                        tint = MaterialTheme.colorScheme.success
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(
                            5.dp,
                            alignment = Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Mobile Number",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,

                            )
                        Text(
                            text = userData.countryCode+userData.mobile,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            color = MaterialTheme.colorScheme.success,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        val borderColor =
            MaterialTheme.colorScheme.onPrimaryContainer
        OutlinedButton(
            onClick = {
                try {
                    val uri = Uri.parse("https://msnlabs.com/img/resume-sample.pdf")
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No PDF reader found", Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .padding(horizontal = 90.dp)
                .drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(
                                    10f,
                                    7f
                                ), 0f
                            )
                        ),
                        cornerRadius = CornerRadius(20.dp.toPx())
                    )
                },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            border = BorderStroke(
                0.dp,
                Color.Transparent
            ),
            shape = RoundedCornerShape(20.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                Text(
                    text = "View Resume",
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                        fontFamily = overpassMonoBold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_document),
                    contentDescription = "Resume",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetDesignPreview() {
    KarkaBoardTheme {
        BottomSheetDesign(
            UserData(
                name = "Sivaram",
                email = "shiv@gmail.com",
                profileImgUrl = "",
                mobile = "9876543210",
                countryCode = "+91",
                resumeUrl = "https://msnlabs.com/img/resume-sample.pdf"
            ),
            LocalContext.current
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun InterviewManagementViewPreview() {
    val fakeVm = InterviewManagementViewModel(FakeDbRepo())
    KarkaBoardTheme {
        InterviewManagementViewContent(
            navController = NavController(LocalContext.current),
            context = LocalContext.current,
            interviewManagementViewModel = fakeVm
        )
    }
}