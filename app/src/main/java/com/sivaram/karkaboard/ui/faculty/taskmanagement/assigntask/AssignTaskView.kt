package com.sivaram.karkaboard.ui.faculty.taskmanagement.assigntask

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import com.sivaram.karkaboard.data.dto.TaskData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.faculty.taskmanagement.state.AssignTaskState
import com.sivaram.karkaboard.ui.faculty.taskmanagement.state.ValidationState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.ui.theme.overpassMonoRegular
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignTaskView(
    navController: NavController,
    context: Context,
    assignTaskViewModel: AssignTaskViewModel = hiltViewModel()
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
                        text = "Assign Task",
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
            AssignTaskViewContent(
                navController = navController,
                userData = userData,
                context = context,
                assignTaskViewModel = assignTaskViewModel
            )
        }
    }
}

@Composable
fun AssignTaskViewContent(
    navController: NavController,
    userData: UserData?,
    context: Context,
    assignTaskViewModel: AssignTaskViewModel
) {

    var taskTitle by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    var questionList = rememberSaveable { mutableStateListOf<String>() }
    var questionContent by rememberSaveable { mutableStateOf("") }

    var tagList = rememberSaveable { mutableStateListOf<String>() }
    var tag by rememberSaveable { mutableStateOf("") }
    val tagListState = rememberLazyListState()

    var dueDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var showDueDatePicker by rememberSaveable { mutableStateOf(false) }

    var batchName by rememberSaveable { mutableStateOf("") }
    var selectedBatchId by rememberSaveable { mutableStateOf("") }

    val validationState by assignTaskViewModel.validationState.observeAsState()

    val batches by assignTaskViewModel.batchData.observeAsState()
    var expandBatchDropDown by rememberSaveable { mutableStateOf(false) }

    val assignTaskState by assignTaskViewModel.assignTaskState.collectAsState()
    var attachmentUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var attachmentName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(true) {
        assignTaskViewModel.getAvailableBatches()
    }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            val fileSize = context.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
                it.length
            } ?: 0L

            val fileSizeInMB = fileSize / (1024 * 1024)

            if (fileSizeInMB > 5) {
                Toast.makeText(context, "File size should be below 5 MB", Toast.LENGTH_SHORT).show()
            }
            else {
                attachmentUri = it
                attachmentName = UtilityFunctions.getFileName(context, it) ?: ""
            }
        }
        Log.d("resumeFileName", attachmentName)
        Log.d("resumeUri", attachmentUri.toString())
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
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 0.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Column(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Assign New Task",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 10.dp)
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Task Title",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            )
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
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(R.drawable.ic_task_title),
                                    contentDescription = "Task Icon",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                BasicTextField(
                                    value = taskTitle,
                                    onValueChange = {
                                        taskTitle = it
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
                                        if (taskTitle.isEmpty()) {
                                            Text(
                                                text = "Enter task title",
                                                style = TextStyle(
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Description",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            )
                        )
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
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
                                    .padding(10.dp),
                            ) {
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(R.drawable.ic_description),
                                    contentDescription = "Description Icon",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                BasicTextField(
                                    value = description,
                                    onValueChange = {
                                        description = it
                                    },
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp),
                                    singleLine = false,
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                        fontFamily = overpassMonoMedium
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (description.isEmpty()) {
                                            Text(
                                                text = "Enter the description",
                                                style = TextStyle(
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Questions",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            )
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            questionList.forEachIndexed { index, question ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = "${index + 1}. $question",
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
                                            questionList.removeAt(index)
                                        },
                                        modifier = Modifier.size(25.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_reject),
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            questionContent = questionList[index]
                                            questionList.removeAt(index)
                                        },
                                        modifier = Modifier.size(25.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_edit),
                                            contentDescription = "Edit",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedCard(
                                    modifier = Modifier
                                        .weight(1f)
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
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(25.dp),
                                            painter = painterResource(R.drawable.ic_question),
                                            contentDescription = "Question Icon",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        BasicTextField(
                                            value = questionContent,
                                            onValueChange = {
                                                questionContent = it
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
                                                if (questionContent.isEmpty()) {
                                                    Text(
                                                        text = "Enter question(s)",
                                                        style = TextStyle(
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                            fontFamily = overpassMonoMedium
                                                        )
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        )
                                    }
                                }
                                OutlinedCard(
                                    modifier = Modifier
                                        .size(50.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    onClick = {
                                        if (questionContent.trim().isEmpty()) {
                                            Toast.makeText(
                                                context,
                                                "Please enter question",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            questionList.add(questionContent.trim())
                                            questionContent = ""
                                        }
                                    },

                                    ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(30.dp),
                                            painter = painterResource(R.drawable.ic_plus),
                                            contentDescription = "Add Icon",
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = "OR",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoRegular
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Add Attachment",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            )
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedCard(
                                    modifier = Modifier
                                        .weight(1f)
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
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(25.dp),
                                            painter = painterResource(R.drawable.ic_attach),
                                            contentDescription = "Attach Icon",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            modifier = Modifier.padding(start = 10.dp),
                                            text = if (attachmentName.isEmpty()) "Upload a file (Max file: 5MB)" else attachmentName,
                                            style = TextStyle(
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                fontFamily = overpassMonoMedium
                                            )
                                        )
                                    }
                                }
                                OutlinedCard(
                                    modifier = Modifier
                                        .size(50.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    onClick = {
                                        documentLauncher.launch(
                                            arrayOf(
                                                "application/pdf", // PDF
                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
                                                "application/msword" // DOC (old Word format)
                                            )
                                        )
                                    },
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(30.dp),
                                            painter = painterResource(R.drawable.ic_add_file),
                                            contentDescription = "Add Icon",
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Tags",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            )
                        )
                        LazyRow(
                            state = tagListState,
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            items(tagList) { item ->
                                OutlinedCard(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .height(30.dp),
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
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .padding(horizontal = 5.dp)
                                                .padding(top = 4.dp),
                                            textAlign = TextAlign.Center,
                                            text = item,
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                fontFamily = overpassMonoMedium
                                            )
                                        )
                                        IconButton(
                                            modifier = Modifier.size(20.dp),
                                            onClick = {
                                                tagList.remove(item)
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_close),
                                                contentDescription = "Close"
                                            )
                                        }
                                    }
                                }
                            }
                        }
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
                                        painter = painterResource(R.drawable.ic_hashtag),
                                        contentDescription = "Tag Icon"
                                    )
                                    BasicTextField(
                                        value = tag,
                                        onValueChange = {
                                            tag = it
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
                                            if (tag.isEmpty()) {
                                                Text(
                                                    text = "Enter tags (e.g : java)",
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
                                IconButton(
                                    onClick = {
                                        if (tag.trim().isEmpty()) {
                                            Toast.makeText(
                                                context,
                                                "Please enter tag",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else if (
                                            tagList.any {
                                                it.equals(
                                                    tag.trim(),
                                                    ignoreCase = true
                                                )
                                            }) {
                                            Toast.makeText(
                                                context,
                                                "Tag already exists",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            tagList.add(tag.trim())
                                            tag = ""
                                        }
                                    },
                                    modifier = Modifier.size(25.dp),
                                ) {
                                    Icon(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_add),
                                        contentDescription = "Add"
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Due Date",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            )
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
                            onClick = {
                                showDueDatePicker = true
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(R.drawable.ic_calendar_due),
                                    contentDescription = "Date Icon"
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .fillMaxWidth(),
                                    text = dueDate?.let {
                                        UtilityFunctions.convertMillisToDate(it)
                                    } ?: "Pick the due date",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                                if (showDueDatePicker) {
                                    UtilityFunctions.DatePickerModal(
                                        onDateSelected = { dueDate = it },
                                        onDismiss = { showDueDatePicker = false }
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Batch",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            )
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
                            onClick = {
                                if (batches != null && batches?.isNotEmpty() == true)
                                    expandBatchDropDown = true
                                else
                                    Toast.makeText(
                                        context,
                                        "No batches available",
                                        Toast.LENGTH_SHORT
                                    ).show()
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_batch),
                                        contentDescription = "Batch Icon",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .fillMaxWidth(),
                                        text = if (batchName.trim().isEmpty()) "Pick the batch"
                                        else batchName,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                            fontFamily = overpassMonoBold
                                        )
                                    )
                                }
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(R.drawable.ic_down),
                                    contentDescription = "Down Icon",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Column {
                                    DropdownMenu(
                                        expanded = expandBatchDropDown,
                                        onDismissRequest = { expandBatchDropDown = false },
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        batches?.forEachIndexed { index, batch ->
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
                                                    batchName = batch.batchName
                                                    selectedBatchId = batch.docId
                                                    expandBatchDropDown = false
                                                },
                                                modifier = Modifier
                                                    .wrapContentWidth()
                                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                                colors = MenuDefaults.itemColors(
                                                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                )
                                            )
                                            if (batch != batches?.last()) {
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
                        }
                    }

                    OutlinedButton(
                        enabled = assignTaskState !is AssignTaskState.Loading,
                        onClick = {
                            val taskObj = TaskData(
                                title = taskTitle.trim(),
                                description = description.trim(),
                                dueDate = dueDate ?: 0,
                                tags = tagList,
                                batchId = selectedBatchId,
                                questions = questionList,
                                attachmentUrl = attachmentUri.toString()
                            )
                            assignTaskViewModel.validateInputs(taskObj)
                            when (val state = validationState) {
                                is ValidationState.Error ->
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT)
                                        .show()

                                ValidationState.Idle -> Unit

                                ValidationState.Success -> {
                                    taskObj.facultyId = userData?.uId ?: ""
                                    assignTaskViewModel.assignTask(taskObj)
                                }

                                else -> Unit
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 60.dp),
                        shape = RoundedCornerShape(12.dp),
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
                        when (val state = assignTaskState) {
                            is AssignTaskState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                assignTaskViewModel.resetAssignTaskState()
                            }

                            AssignTaskState.Idle -> {
                                Text(
                                    text = "Assign",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                            }

                            AssignTaskState.Loading -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(25.dp),
                                    strokeWidth = 4.dp
                                )
                            }

                            is AssignTaskState.Success -> {
                                Toast.makeText(
                                    context,
                                    "Task assigned successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                                assignTaskViewModel.resetAssignTaskState()
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
fun AssignTaskViewContentPreview() {
    val fakeVM = AssignTaskViewModel(
        databaseRepository = FakeDbRepo()
    )
    KarkaBoardTheme {
        AssignTaskViewContent(
            navController = rememberNavController(),
            userData = UserData(),
            context = LocalContext.current,
            assignTaskViewModel = fakeVM
        )
    }
}