package com.sivaram.karkaboard.ui.student.task

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.data.dto.TaskData
import com.sivaram.karkaboard.data.dto.TaskSubmissionData
import com.sivaram.karkaboard.data.dto.TaskViewData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.dto.enums.SubmissionStatus
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.student.state.UiState
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
fun TaskView(
    navController: NavController,
    context: Context,
    taskViewModel: TaskViewModel = hiltViewModel()
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
                        text = "Your Tasks",
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
            TaskViewContent(
                userData = userData,
                context = context,
                navController = navController,
                taskViewModel = taskViewModel
            )
        }
    }
}

@Composable
fun TaskViewContent(
    userData: UserData?,
    context: Context,
    navController: NavController,
    taskViewModel: TaskViewModel
) {
    val tabItems = listOf("All") + SubmissionStatus.entries.map { it.label }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val studentData by taskViewModel.studentData.observeAsState()
    val taskViewData by taskViewModel.taskViewData.observeAsState()
    val uiState by taskViewModel.uiState.collectAsState()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState {
        tabItems.size
    }

    LaunchedEffect(userData) {
        taskViewModel.getStudentData(userData?.uId.toString())
    }
    LaunchedEffect(studentData) {
        if (userData != null && studentData != null) {
            taskViewModel.getTaskByBatch(
                userData.uId,
                studentData?.batchId.toString(),
                tabItems[selectedTabIndex]
            )
            Log.d("taskViewData", "inner block - ${taskViewData.toString()}")
        }
        Log.d("taskViewData", taskViewData.toString())
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
        if (userData != null && studentData != null) {
            taskViewModel.getTaskByBatch(
                userData.uId,
                studentData?.batchId.toString(),
                tabItems[selectedTabIndex]
            )
        }
        Log.d("selectedTabIndex", "TaskViewContent: $selectedTabIndex")
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
        Log.d("selectedTabIndex-pagerState", "TaskViewContent: $selectedTabIndex")
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
                    .fillMaxSize(),
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    tabItems.forEachIndexed { index, item ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = {
                                selectedTabIndex = index
                            },
                            text = {
                                Text(
                                    text = item.toString(),
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                        fontFamily = overpassMonoMedium
                                    ),
                                    color =
                                        if (index == selectedTabIndex) MaterialTheme.colorScheme.onSurface
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                ) { index ->
                    when (val state = uiState) {
                        UiState.Empty -> {
                            Log.d("UiState", "EMPTY")
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
                            Log.d("UiState", "ERROR")
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

                        UiState.Loading -> {
                            Log.d("UiState", "LOADING")
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
                            Log.d("UiState", "SUCCESS")
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .padding(vertical = 10.dp),
                                contentPadding = PaddingValues(
                                    bottom = 24.dp
                                ),
                                columns = GridCells.Fixed(1),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                taskViewData?.forEachIndexed { index, task ->
                                    Log.d("taskViewData", "TaskViewContent: $task")
                                    item {
                                        TaskViewCard(navController = navController, taskViewData = task)
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
fun TaskViewCard(navController: NavController, taskViewData: TaskViewData) {
    val taskData = taskViewData.taskData
    val taskSubmissionData = taskViewData.taskSubmissionData

    val statusEnum = SubmissionStatus.entries.firstOrNull(){ it.label == taskSubmissionData.status }

    val questionsCount = if(taskData.questions.isEmpty()) "1 Attachment"
                    else "${taskData.questions.size} Questions"
    val taskId = taskData.taskId
    val taskSubmissionId = taskSubmissionData.submissionId
    val facultyId = taskData.facultyId
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(vertical = 5.dp)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary,
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 10.dp,
            pressedElevation = 20.dp
        ),
        shape = RoundedCornerShape(20.dp),
        onClick = {
            navController.navigate(NavConstants.TASK_SUBMISSION + "/$taskId/$taskSubmissionId/$facultyId")
        }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = if (statusEnum == SubmissionStatus.COMPLETED) {
                                MaterialTheme.colorScheme.successContainer
                            } else if (statusEnum == SubmissionStatus.REASSIGNED) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.onSecondary
                            },
                            contentColor = if (statusEnum == SubmissionStatus.COMPLETED) {
                                MaterialTheme.colorScheme.success
                            } else if (statusEnum == SubmissionStatus.REASSIGNED) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter =
                                    if (statusEnum == SubmissionStatus.COMPLETED) painterResource(
                                        R.drawable.ic_completed
                                    )
                                    else if (statusEnum == SubmissionStatus.SUBMITTED) painterResource(
                                        R.drawable.ic_submitted
                                    )
                                    else if (statusEnum == SubmissionStatus.REASSIGNED) painterResource(
                                        R.drawable.ic_repeat
                                    )
                                    else painterResource(R.drawable.ic_time),
                                contentDescription = "Status",
                            )
                            Text(
                                text = taskSubmissionData.status,
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                )
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier.size(30.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(13.dp),
                    onClick = {
                        navController.navigate(NavConstants.TASK_SUBMISSION + "/$taskId/$taskSubmissionId/$facultyId")
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(R.drawable.ic_arrow_top_right),
                            contentDescription = "Detail Page Icon"
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(
                        2.dp,
                        alignment = Alignment.CenterVertically
                    ),
                ) {
                    Text(
                        text = taskData.title,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            fontFamily = overpassMonoBold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${UtilityFunctions.convertMillisToDateMonthFormat(taskData.assignedDate)} - " +
                                UtilityFunctions.convertMillisToDateMonthFormat(taskData.dueDate),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            fontFamily = overpassMonoMedium
                        ),
                    )
                }
                Text(
                    text = questionsCount,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                        fontFamily = overpassMonoSemiBold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskViewCardPreview() {
    KarkaBoardTheme {
        TaskViewCard(
            navController = rememberNavController(),
            taskViewData = TaskViewData(
                taskData = TaskData(),
                taskSubmissionData = TaskSubmissionData()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskViewContentPreview() {
    KarkaBoardTheme {

    }
}