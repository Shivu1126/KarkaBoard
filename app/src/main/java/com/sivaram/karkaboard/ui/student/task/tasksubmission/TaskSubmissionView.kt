package com.sivaram.karkaboard.ui.student.task.tasksubmission

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.data.dto.TaskSubmissionData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.dto.enums.SubmissionStatus
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.faculty.taskmanagement.state.AssignTaskState
import com.sivaram.karkaboard.ui.student.state.SubmitTaskState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.ui.theme.overpassMonoRegular
import com.sivaram.karkaboard.ui.theme.overpassMonoSemiBold
import com.sivaram.karkaboard.ui.theme.success
import com.sivaram.karkaboard.ui.theme.successContainer
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSubmissionView(
    taskId: String,
    taskSubmissionId: String,
    facultyId: String,
    navController: NavController,
    context: Context,
    taskSubmissionViewModel: TaskSubmissionViewModel = hiltViewModel()
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
                        text = "Task",
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
            TaskSubmissionViewContent(
                taskId = taskId,
                taskSubmissionId = taskSubmissionId,
                facultyId = facultyId,
                userData = userData,
                context = context,
                taskSubmissionViewModel = taskSubmissionViewModel
            )
        }
    }
}

@Composable
fun TaskSubmissionViewContent(
    taskId: String,
    taskSubmissionId: String,
    facultyId: String,
    userData: UserData?,
    context: Context,
    taskSubmissionViewModel: TaskSubmissionViewModel
) {
    val taskData by taskSubmissionViewModel.taskData.observeAsState()
    val taskSubmissionData by taskSubmissionViewModel.taskSubmissionData.observeAsState()
    val facultyData by taskSubmissionViewModel.facultyData.observeAsState()

    var statusEnum by rememberSaveable { mutableStateOf(SubmissionStatus.PENDING) }

    LaunchedEffect(Unit) {
        taskSubmissionViewModel.getTaskById(taskId)
        taskSubmissionViewModel.getTaskSubmissionById(taskSubmissionId)
        taskSubmissionViewModel.getFacultyById(facultyId)
    }

    LaunchedEffect(taskSubmissionData) {
        Log.d("taskSubmissionData", "$taskSubmissionData")
        statusEnum =
            SubmissionStatus.entries.firstOrNull() { it.label == taskSubmissionData?.status }
                ?: SubmissionStatus.PENDING

    }

    val questions = taskData?.questions ?: emptyList()

    var submissionLink by rememberSaveable { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current

    var uriList = rememberSaveable { mutableStateListOf<String>() }
    val fileNameList = rememberSaveable { mutableStateListOf<String>() }
    val multipleDocsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                uri.let {
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                    uriList.add(uri.toString())
                    fileNameList.add(UtilityFunctions.getFileName(context, uri) ?: "")
                }
            }
        }
    }

    val submitTaskState by taskSubmissionViewModel.submitTaskState.collectAsState()

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
                    .padding(vertical = 20.dp)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 10.dp,
                    ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(
                                    2.dp,
                                    alignment = Alignment.CenterVertically
                                ),
                            ) {
                                Text(
                                    text = taskData?.title ?: "",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                        fontFamily = overpassMonoBold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = UtilityFunctions.convertMillisToDateMonthFormat(
                                            taskData?.dueDate ?: 0
                                        ),
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                            fontFamily = overpassMonoSemiBold
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        painter = painterResource(R.drawable.ic_time),
                                        contentDescription = "Assign date",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
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
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 5.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        painter = if (statusEnum == SubmissionStatus.COMPLETED) painterResource(
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
                                        text = taskSubmissionData?.status ?: "",
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                            fontFamily = overpassMonoMedium
                                        )
                                    )
                                }
                            }
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
                        if (questions.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = "Questions :",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                        fontFamily = overpassMonoBold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                questions.forEach { question ->
                                    Row(

                                    ) {
                                        Text(
                                            text = "\u2022",
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                fontFamily = overpassMonoBold
                                            ),
                                            modifier = Modifier.padding(end = 8.dp),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = question,
                                            style = TextStyle(
                                                textAlign = TextAlign.Justify,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                fontFamily = overpassMonoRegular
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                        if (taskData?.attachmentUrl?.isNotEmpty() == true) {
                            OutlinedButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.onSecondary,
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
                                onClick = {

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
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            thickness = (0.5).dp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "Assigned By :",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = facultyData?.name ?: "",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                    fontFamily = overpassMonoSemiBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                if (statusEnum == SubmissionStatus.PENDING || statusEnum == SubmissionStatus.REASSIGNED) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "Link :",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                    fontFamily = overpassMonoBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
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
                                        .padding(start = 10.dp, end = 15.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,

                                        ) {
                                        Icon(
                                            modifier = Modifier.size(25.dp),
                                            painter = painterResource(R.drawable.ic_link),
                                            contentDescription = "Tag Icon"
                                        )
                                        BasicTextField(
                                            value = submissionLink,
                                            onValueChange = {
                                                submissionLink = it
                                            },
                                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 10.dp),
                                            singleLine = true,
                                            textStyle = TextStyle(
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                fontFamily = overpassMonoMedium
                                            ),
                                            decorationBox = { innerTextField ->
                                                if (submissionLink.isEmpty()) {
                                                    Text(
                                                        text = "Type or Paste a link here",
                                                        style = TextStyle(
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                            fontFamily = overpassMonoMedium
                                                        )
                                                    )
                                                }
                                                innerTextField()
                                            },
                                        )
                                    }
                                    Row() {
                                        if (submissionLink.isNotEmpty()) {
                                            IconButton(
                                                onClick = {
                                                    submissionLink = ""
                                                },
                                                modifier = Modifier.size(25.dp),
                                            ) {
                                                Icon(
                                                    modifier = Modifier.size(25.dp),
                                                    painter = painterResource(R.drawable.ic_reject),
                                                    contentDescription = "Paste"
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                val annotatedString = clipboardManager.getText()
                                                if (annotatedString == null) {
                                                    Toast.makeText(
                                                        context,
                                                        "No text found",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                annotatedString?.let { asText ->
                                                    val pastedLink = asText.text
                                                    val isValid =
                                                        Patterns.WEB_URL.matcher(pastedLink)
                                                            .matches()
                                                    if (isValid)
                                                        submissionLink = pastedLink
                                                    else
                                                        Toast.makeText(
                                                            context,
                                                            "Invalid link",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                }
                                            },
                                            modifier = Modifier.size(25.dp),
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(25.dp),
                                                painter = painterResource(R.drawable.ic_paste_fill),
                                                contentDescription = "Paste"
                                            )
                                        }

                                    }
                                }
                            }
                        }
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = "or",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoRegular
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "Upload file(s)",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                    fontFamily = overpassMonoBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            fileNameList.forEachIndexed { index, fileName ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = "${index + 1}. $fileName",
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    IconButton(
                                        onClick = {
                                            fileNameList.removeAt(index)
                                            uriList.removeAt(index)
                                        },
                                        modifier = Modifier.size(25.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_reject),
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                            OutlinedButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.onSecondary,
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
                                onClick = {
                                    multipleDocsLauncher.launch(arrayOf("*/*"))
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = "Add file",
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                            fontFamily = overpassMonoMedium
                                        )
                                    )
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        painter = painterResource(R.drawable.ic_add_file),
                                        contentDescription = "add file"
                                    )
                                }
                            }
                        }
                    }
                    OutlinedButton(
                        enabled = submitTaskState !is SubmitTaskState.Loading,
                        onClick = {
                            if (submissionLink.trim().isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Please enter or paste a link",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val taskSub = TaskSubmissionData(
                                    submissionId = taskSubmissionId,
                                    taskId = taskId,
                                    studentId = userData?.uId ?: "",
                                    gitLink = submissionLink,
                                    fileUrls = uriList,
                                    fileNames = fileNameList,
                                    status = SubmissionStatus.SUBMITTED.label
                                )
                                taskSubmissionViewModel.updateSubmissionData(taskSub)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 60.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.5f
                            ),
                            disabledContentColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                alpha = 0.5f
                            ),
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.inversePrimary),
                    ) {
                        when (val state = submitTaskState) {
                            is SubmitTaskState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                taskSubmissionViewModel.resetSubmitTaskState()
                            }

                            SubmitTaskState.Idle -> {
                                Text(
                                    text = "Submit",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                            }

                            SubmitTaskState.Loading -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(25.dp),
                                    strokeWidth = 4.dp
                                )
                            }

                            is SubmitTaskState.Success -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                taskSubmissionViewModel.resetSubmitTaskState()
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Task Submission :",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            fontFamily = overpassMonoBold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Submitted Link",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = buildAnnotatedString {
                                withLink(
                                    LinkAnnotation.Url(
                                        taskSubmissionData?.gitLink ?: "",
                                        TextLinkStyles(
                                            style = SpanStyle(
                                                color = Color.Blue,
                                                textDecoration = TextDecoration.Underline
                                            )
                                        )
                                    )
                                ) {
                                    append(taskSubmissionData?.gitLink ?: "")
                                }
                            },
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (taskSubmissionData?.fileNames?.isNotEmpty() == true) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "File(s)",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            taskSubmissionData?.fileNames?.forEachIndexed { index, fileName ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = "${index + 1}. $fileName",
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                            fontFamily = overpassMonoMedium
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    OutlinedCard(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.onSecondary,
                                            contentColor = MaterialTheme.colorScheme.secondary
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp,
                                                    vertical = 5.dp
                                                ),
                                                text = "View",
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                    fontFamily = overpassMonoMedium,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun TaskSubmissionViewPreview() {
    val fakeVM = FakeDbRepo()
    val taskSubmissionVM = TaskSubmissionViewModel(fakeVM)
    KarkaBoardTheme {
        TaskSubmissionViewContent(
            taskId = "",
            taskSubmissionId = "",
            facultyId = "",
            userData = UserData(),
            context = LocalContext.current,
            taskSubmissionViewModel = taskSubmissionVM
        )
    }
}