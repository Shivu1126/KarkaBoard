package com.sivaram.karkaboard.ui.interviewmanagement

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.AppliedStudentData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.StudentsData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.interviewmanagement.state.SelectState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme

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

    var batchIndex by rememberSaveable { mutableIntStateOf(0) }
    var filterIndex by rememberSaveable { mutableIntStateOf(0) }

    var expandBatchDropDown by rememberSaveable { mutableStateOf(false) }
    var expandFilterDropDown by rememberSaveable { mutableStateOf(false) }
    var selectedBatch by remember { mutableStateOf(allBatchesData?.firstOrNull() ?: BatchData()) }

    val selectStates by interviewManagementViewModel.selectState.collectAsState()

    LaunchedEffect(Unit) {
        interviewManagementViewModel.getAllBatches()
    }
    Log.d("allBatchesData", allBatchesData.toString())
    LaunchedEffect(allBatchesData) {
        allBatchesData?.firstOrNull()?.let { batch ->
            interviewManagementViewModel.getAppliedStudentDetail(batch.docId)
            selectedBatch = batch
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
                                text = selectedBatch.batchName,
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
                                        selectedBatch = batch
                                        interviewManagementViewModel.getAppliedStudentDetail(
                                            selectedBatch.docId
                                        )
                                        Log.d("selectedBatch", selectedBatch.toString())
                                        Log.d("appliedStudentData", appliedStudentData.toString())
                                        batchIndex = index
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
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                text = "All ",
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
                        }
                    }
                    if (appliedStudentData?.isEmpty() == true) {
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
                        Log.d("appliedStudentData", appliedStudentData.toString())
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
                                    var studentData = student.studentsData
                                    var applicationData = student.applicationData
                                    var studentUserData = student.userData
                                    val state = selectStates[applicationData.docId] ?: SelectState.Idle
                                    OutlinedCard(
                                        modifier = Modifier
                                            .wrapContentHeight()
                                            .fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.onSecondary,
                                            contentColor = MaterialTheme.colorScheme.secondary
                                        ),
                                        onClick = {

                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
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
                                                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                                                ) {
                                                    OutlinedCard(
                                                        modifier = Modifier
                                                            .size(70.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                        ),
                                                        shape = CircleShape
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier.fillMaxSize(),
                                                            painter = painterResource(R.drawable.ic_user_profile),
                                                            contentDescription = "Profile Image"
                                                        )
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
                                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                                                            ),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = studentUserData.email,
                                                            style = TextStyle(
                                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
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
                                                        OutlinedButton(
                                                            modifier = Modifier.weight(1f),
                                                            onClick = {
                                                                interviewManagementViewModel.moveToNextProcess(
                                                                    applicationData.docId,
                                                                    applicationData.processId
                                                                )
                                                            },
                                                            colors = ButtonDefaults.outlinedButtonColors(
                                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            ),
                                                            border = BorderStroke(
                                                                1.dp,
                                                                MaterialTheme.colorScheme.onPrimaryContainer
                                                            )
                                                        ) {
                                                            when(state) {
                                                                is SelectState.Error -> {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Something went wrong",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                    interviewManagementViewModel.resetSelectState(
                                                                        applicationData.docId
                                                                    )
                                                                }
                                                                SelectState.Idle ->
                                                                    Text(
                                                                        modifier = Modifier.weight(1f),
                                                                        textAlign = TextAlign.Center,
                                                                        text = "Select",
                                                                        style = TextStyle(
                                                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                            fontFamily = overpassMonoBold
                                                                        ),
                                                                        maxLines = 1,
                                                                        overflow = TextOverflow.Ellipsis
                                                                    )
                                                                SelectState.Loading ->
                                                                    CircularProgressIndicator(
                                                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                                                        modifier = Modifier.size(25.dp),
                                                                        strokeWidth = 4.dp
                                                                    )
                                                                is SelectState.Success -> {
                                                                    Toast.makeText(context, "${studentUserData.name} Selected for interview", Toast.LENGTH_SHORT).show()
                                                                    interviewManagementViewModel.resetSelectState(
                                                                        applicationData.docId
                                                                    )
                                                                }
                                                            }

                                                        }
                                                    } else if (applicationData.processId == 2) {
                                                        IconButton(
                                                            onClick = {

                                                            }
                                                        ) {
                                                            Icon(
                                                                modifier = Modifier.size(33.dp),
                                                                painter = painterResource(R.drawable.ic_reject),
                                                                contentDescription = "Delete",
                                                                tint = MaterialTheme.colorScheme.error
                                                            )
                                                        }
                                                        IconButton(
                                                            onClick = {

                                                            }
                                                        ) {
                                                            Icon(
                                                                modifier = Modifier.size(30.dp),
                                                                painter = painterResource(R.drawable.ic_select),
                                                                contentDescription = "Edit",
                                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            HorizontalDivider(
                                                thickness = 1.dp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
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
                                                    text = "Note :",
                                                    style = TextStyle(
                                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                                                    ),
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                                Text(
                                                    text = if (applicationData.processId == 1) "Click 'Select' for move candidate to interview"
                                                    else if (applicationData.processId == 2) "Click buttons based on candidate interview performance"
                                                    else {
                                                        if (studentData.isSelected)
                                                            "Candidate selected for Training"
                                                        else
                                                            "Candidate not selected for Training"
                                                    },
                                                    style = TextStyle(
                                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                                                    ),
                                                    color = MaterialTheme.colorScheme.primaryContainer
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

@Composable
fun StudentCardContent(appliedStudentData: AppliedStudentData) {

}

@Preview
@Composable
fun StudentCardContentPreview() {
    KarkaBoardTheme {
        StudentCardContent(
            AppliedStudentData(
                applicationData = ApplicationData(),
                studentsData = StudentsData(),
                userData = UserData()
            )
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