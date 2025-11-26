package com.sivaram.karkaboard.ui.faculty.taskmanagement

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.interviewmanagement.state.UiState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.ui.theme.overpassMonoRegular
import com.sivaram.karkaboard.ui.theme.overpassMonoSemiBold
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagementView(
    navController: NavController,
    context: Context,
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel()
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
                        text = "Task Management",
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
            TaskManagementViewContent(
                userData = userData,
                navController = navController,
                context = context,
                taskManagementViewModel = taskManagementViewModel
            )
            FloatingActionButton(
                modifier = Modifier
                    .padding(end = 20.dp)
                    .padding(bottom = 20.dp)
                    .size(60.dp)
                    .align(Alignment.BottomEnd),
                onClick = {
                    navController.navigate(NavConstants.ASSIGN_TASK)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "Add New Task")
            }
        }
    }
}

@Composable
fun TaskManagementViewContent(
    userData: UserData?,
    navController: NavController,
    context: Context,
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel()
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val batchesData by taskManagementViewModel.batchData.observeAsState()
    val assignedTasksData by taskManagementViewModel.taskData.observeAsState()

    var expandBatchDropDown by rememberSaveable { mutableStateOf(false) }
    
    val uiState by taskManagementViewModel.uiState.collectAsState()

    var selectedBatchId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedBatch = remember(batchesData, selectedBatchId) {
        batchesData?.find { it.docId == selectedBatchId }
    }

    LaunchedEffect(Unit) {
        taskManagementViewModel.getAvailableBatches()
    }

    LaunchedEffect(batchesData, selectedBatchId) {
        when {
            selectedBatchId != null -> {
                Log.d("selectedBatchId - ", selectedBatchId.toString())
                taskManagementViewModel.getAssignedTasksByFaculty(
                    selectedBatchId!!,
                    userData?.uId ?: ""
                )
                Log.d("assignedTasksData", assignedTasksData.toString())
            }

            !batchesData.isNullOrEmpty() -> {
                batchesData?.firstOrNull()?.let { batch ->
                    selectedBatchId = batch.docId
                    Log.d("selectedBatchId", selectedBatchId.toString())
                    taskManagementViewModel.getAssignedTasksByFaculty(
                        batch.docId,
                        userData?.uId ?: ""
                    )
                    Log.d("assignedTasksData", assignedTasksData.toString())
                }
            }

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
            if (batchesData?.isEmpty() == true) {
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
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    expandBatchDropDown = !expandBatchDropDown
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
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
                            DropdownMenu(
                                expanded = expandBatchDropDown,
                                onDismissRequest = { expandBatchDropDown = false },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                batchesData?.forEachIndexed { index, batch ->
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
//                                            taskManagementViewModel.getAssignedTasksByFaculty(batch.docId, userData?.uId ?: "")
//                                            Log.d("assignedTasksData", assignedTasksData.toString())
                                            expandBatchDropDown = false
                                        },
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        colors = MenuDefaults.itemColors(
                                            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        )
                                    )
                                    if (batch != batchesData?.last()) {
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
                                Toast.makeText(
                                    context,
                                    "Something went wrong, Please check your internet connection",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                                assignedTasksData?.forEachIndexed { index, task ->
                                    item{
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.onSecondary,
                                                contentColor = MaterialTheme.colorScheme.secondary
                                            ),
                                            shape = RoundedCornerShape(20.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                                            onClick = {
                                                navController.navigate(NavConstants.TASK_DETAILS + "/${task.taskId}/${task.batchId}")
                                            }
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        modifier = Modifier.weight(1f),
                                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier.size(20.dp),
                                                            painter = painterResource(R.drawable.ic_task_title),
                                                            contentDescription = "Task Title",
                                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                        Text(
                                                            text = task.title,
                                                            style = TextStyle(
                                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                                fontFamily = overpassMonoBold
                                                            ),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                    }
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = UtilityFunctions.convertMillisToDateMonthFormat(task.assignedDate),
                                                            style = TextStyle(
                                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                fontFamily = overpassMonoSemiBold
                                                            ),
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = task.description.trimIndent(),
                                                    style = TextStyle(
                                                        textAlign = TextAlign.Justify,
                                                        textIndent = TextIndent(firstLine = 25.sp, restLine = 0.sp),
                                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                        fontFamily = overpassMonoRegular
                                                    )
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ){
                                                    Card(
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = MaterialTheme.colorScheme.onSecondary,
                                                            contentColor = MaterialTheme.colorScheme.secondary
                                                        ),
                                                        shape = RoundedCornerShape(5.dp),
                                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
                                                    ){
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                        ){
                                                            Icon(
                                                                modifier = Modifier.size(20.dp),
                                                                painter = painterResource(R.drawable.ic_time),
                                                                contentDescription = "Due date",
                                                            )
                                                            Text(
                                                                text = UtilityFunctions.convertMillisToDateMonthFormat( task.dueDate ),
                                                                style = TextStyle(
                                                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                    fontFamily = overpassMonoMedium
                                                                )
                                                            )
                                                        }
                                                    }
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ){
                                                        Icon(
                                                            modifier = Modifier.size(20.dp),
                                                            painter = painterResource(R.drawable.ic_tick),
                                                            contentDescription = "Submission Icon",
                                                        )
                                                        Text(
                                                            text = "${task.totalSubmission}",
                                                            style = TextStyle(
                                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                                fontFamily = overpassMonoSemiBold
                                                            )
                                                        )
                                                    }
                                                }
                                                LazyRow(
                                                    horizontalArrangement = Arrangement.spacedBy(
                                                        10.dp,
                                                        Alignment.CenterHorizontally
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    items(task.tags) { tag ->
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
                                                                    text = tag,
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
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard() {
    val tagList = listOf("Java", "Html")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(vertical = 5.dp)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary,
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_task_title),
                        contentDescription = "Task Title",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Task Title",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            fontFamily = overpassMonoBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "12 Oct 2025",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            fontFamily = overpassMonoSemiBold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                style = TextStyle(
                    textAlign = TextAlign.Justify,
                    textIndent = TextIndent(firstLine = 25.sp, restLine = 0.sp),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    fontFamily = overpassMonoRegular
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
                ){
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(R.drawable.ic_time),
                            contentDescription = "Due date",
                        )
                        Text(
                            text = "25 Oct 2025",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoMedium
                            )
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_tick),
                        contentDescription = "Submission Icon",
                    )
                    Text(
                        text = "12",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            fontFamily = overpassMonoSemiBold
                        )
                    )
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(tagList) { tag ->
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
                                text = tag,
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

@Preview(showBackground = true)
@Composable
fun TaskCardPreview() {
    KarkaBoardTheme {
        TaskCard()
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun TaskManagementViewPreview() {
    val fakeDb = FakeDbRepo()
    val fakeVM = TaskManagementViewModel(
        fakeDb
    )
    KarkaBoardTheme {
        TaskManagementViewContent(
            userData = UserData(),
            navController = rememberNavController(),
            context = LocalContext.current,
            taskManagementViewModel = fakeVM
        )
    }
}