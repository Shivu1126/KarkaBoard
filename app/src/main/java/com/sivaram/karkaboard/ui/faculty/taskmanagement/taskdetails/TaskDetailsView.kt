package com.sivaram.karkaboard.ui.faculty.taskmanagement.taskdetails

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.data.dto.enums.SubmissionStatus
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.ui.theme.overpassMonoRegular
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sivaram.karkaboard.data.dto.TaskSubmissionData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.faculty.taskmanagement.state.UiState
import com.sivaram.karkaboard.ui.theme.overpassMonoSemiBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsView(
    taskId: String,
    batchId: String,
    navController: NavController,
    context: Context,
    taskDetailsViewModel: TaskDetailsViewModel = hiltViewModel()
) {

    Log.d("TaskDetailsView", "taskId: $taskId, batchId: $batchId")
    
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
                        text = "Task Details",
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
            TaskDetailsContent(
                userData = userData,
                taskId = taskId,
                batchId = batchId,
                navController = navController,
                context = context,
                taskDetailsViewModel = taskDetailsViewModel
            )
        }
    }
}

@Composable
fun TaskDetailsContent(
    userData: UserData?,
    taskId: String,
    batchId: String,
    navController: NavController,
    context: Context,
    taskDetailsViewModel: TaskDetailsViewModel
){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val taskData by taskDetailsViewModel.taskData.observeAsState()
    val batchData by taskDetailsViewModel.batchData.observeAsState()

    LaunchedEffect(true) {
        taskDetailsViewModel.getTaskById(taskId)
        taskDetailsViewModel.getBatchDetailsById(batchId)
        taskDetailsViewModel.getStudentsByTaskStatus(taskId, SubmissionStatus.PENDING)
    }


    val studentsDataByStatus by taskDetailsViewModel.submissionByStatus.observeAsState()
    val uiState by taskDetailsViewModel.uiState.collectAsState()

    val tabItems = SubmissionStatus.entries.map { it.label }
    val pagerState = rememberPagerState {
        tabItems.size
    }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
//    LaunchedEffect(true) {
//        taskDetailsViewModel.getStudentsByTaskStatus(taskId, SubmissionStatus.PENDING)
//    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
        taskDetailsViewModel.getStudentsByTaskStatus(taskId, SubmissionStatus.valueOf(tabItems[selectedTabIndex].uppercase()))
        Log.d("selectedTabIndex", "index: $selectedTabIndex")
        Log.d("selectedTabIndex-pagerState", "StudentViewContent: $studentsDataByStatus")
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
                    .padding(vertical = 20.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = taskData?.title ?: "",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = batchData?.batchName ?: "",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = taskData?.description ?: "",
                        style = TextStyle(
                            textAlign = TextAlign.Justify,
                            textIndent = TextIndent(firstLine = 25.sp, restLine = 0.sp),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            fontFamily = overpassMonoRegular
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_interview_date),
                                contentDescription = "Assigned Date",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = UtilityFunctions.convertMillisToDateMonthFormat(taskData?.assignedDate
                                    ?: 0),
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_time),
                                contentDescription = "Assigned Date",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = UtilityFunctions.convertMillisToDateMonthFormat(taskData?.dueDate
                                    ?: 0),
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    OutlinedButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.onSecondary,
                            contentColor = MaterialTheme.colorScheme.secondary
                        ),
                        onClick = {
                            if(taskData?.attachmentUrl?.isEmpty() == true){
                                //open question
                            }
                            else{
                                try {
                                    val uri = "https://msnlabs.com/img/resume-sample.pdf".toUri()
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/pdf")
                                        flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(context, "No PDF reader found", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            text = "View Attachment",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoMedium
                            )
                        )
                    }
                }
                HorizontalDivider(
                    thickness = (0.5).dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
                                studentsDataByStatus?.forEachIndexed { index, studentsData ->
                                    item {
                                        val studentDetail = studentsData.userData
                                        val submissionData = studentsData.taskSubmissionData
                                        StudentCard(
                                            name = studentDetail.name,
                                            email = studentDetail.email,
                                            imageUrl = studentDetail.profileImgUrl,
                                            submissionData = submissionData,
                                            context = context

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

@Composable
fun StudentCard(
    name: String,
    email: String,
    imageUrl: String,
    submissionData: TaskSubmissionData,
    context: Context
){
    val submissionStatus: SubmissionStatus = SubmissionStatus.valueOf(submissionData.status.uppercase())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(imageUrl.isBlank()){
                Icon(
                    modifier = Modifier
                        .size(60.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    painter = painterResource(R.drawable.ic_user_profile),
                    contentDescription = "Profile Image",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            else {
                AsyncImage(
                    model = imageUrl.toUri(),
                    contentDescription = "Student Profile",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_user_profile)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                        fontFamily = overpassMonoSemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = email,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                        fontFamily = overpassMonoMedium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (submissionStatus == SubmissionStatus.PENDING) {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notify",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                else if(submissionStatus == SubmissionStatus.SUBMITTED || submissionStatus == SubmissionStatus.REASSIGNED){
                    Text(
                        text = UtilityFunctions.convertMillisToDateMonthFormat(0),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            fontFamily = overpassMonoRegular
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Card (
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        onClick = { /*TODO*/ },
                    ) {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            text = "View Docs",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentCardPreview(){
    KarkaBoardTheme {
        StudentCard(
            name = "John Doe",
            email = "william.m@mydomain.com",
            imageUrl = "",
            context = LocalContext.current,
            submissionData = TaskSubmissionData(),
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun TaskDetailsContentPreview(){
    KarkaBoardTheme {
        val taskDetailsViewModel = TaskDetailsViewModel(
            FakeDbRepo()
        )
        TaskDetailsContent(
            userData = UserData(),
            taskId = "",
            batchId = "",
            navController = rememberNavController(),
            context = LocalContext.current,
            taskDetailsViewModel = taskDetailsViewModel
        )
    }
}