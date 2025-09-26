package com.sivaram.karkaboard.ui.managebatches.createnewbatch

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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
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
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeManageBatchesRepo
import com.sivaram.karkaboard.ui.auth.state.ValidationState
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.managebatches.state.CreateBatchState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewBatchView(
    navController: NavController,
    context: Context,
    createNewBatchViewModel: CreateNewBatchViewModel = hiltViewModel()
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
                        text = "Create New Batch",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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

            CreateNewBatchViewContent(
                userData = userData,
                navController = navController,
                context = context,
                createNewBatchViewModel = createNewBatchViewModel
            )
        }
    }
}

@Composable
fun CreateNewBatchViewContent(
    userData: UserData?,
    navController: NavController,
    context: Context,
    createNewBatchViewModel: CreateNewBatchViewModel
) {

    val coroutineScope = rememberCoroutineScope()
    Log.d("CreateNewBatchViewContent", "CreateNewBatch ->$userData")

    var batchName by rememberSaveable { mutableStateOf("") }
    var designation by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    var batchStartDate by rememberSaveable { mutableLongStateOf(0) }
    var batchEndDate by rememberSaveable { mutableLongStateOf(0) }
    var startAndEndDate by rememberSaveable { mutableStateOf("") }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    var skillsList = rememberSaveable { mutableStateListOf<String>() }
    var skill by rememberSaveable { mutableStateOf("") }
    val skillListState = rememberLazyListState()

    var interviewLocation by rememberSaveable { mutableStateOf("") }
    var interviewDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var showInterviewDatePicker by rememberSaveable { mutableStateOf(false) }

    val createBatchState by createNewBatchViewModel.createBatchState.collectAsState()
    val validationState by createNewBatchViewModel.validationState.observeAsState()

    val allBatchesData by createNewBatchViewModel.allBatchesData.observeAsState(emptyList())

    LaunchedEffect(Unit){
        createNewBatchViewModel.getAllBatches()
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
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
            ) {
                Column(
                    modifier = Modifier.padding(top = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Create New Batch",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                    Text(
                        text = "Fill all fields and create new batch",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 50.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(R.drawable.ic_batch_name),
                                contentDescription = "Name Icon",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            BasicTextField(
                                value = batchName,
                                onValueChange = {
                                    batchName = it
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
                                    if (batchName.isEmpty()) {
                                        Text(
                                            text = "Enter batch name",
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
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(R.drawable.ic_java),
                                contentDescription = "Designation Icon",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            BasicTextField(
                                value = designation,
                                onValueChange = {
                                    designation = it
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
                                    if (designation.isEmpty()) {
                                        Text(
                                            text = "Enter the designation of batch",
                                            style = TextStyle(
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                fontFamily = overpassMonoMedium
                                            ),
                                            maxLines = 1,
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
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
                                            text = "Enter the description about this batch",
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
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable {
                                showDatePicker = true
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(R.drawable.ic_date),
                                contentDescription = "Date Icon",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp),
                                text = if (startAndEndDate.isEmpty()) "Pick a duration of batch"
                                else startAndEndDate,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                )
                            )
                            if (showDatePicker) {
                                DateRangePickerModal(
                                    context,
                                    onDateRangeSelected = {
                                        batchStartDate = it.first ?: 0
                                        batchEndDate = it.second ?: 0
                                        startAndEndDate =
                                            UtilityFunctions.convertMillisToDate(batchStartDate) + " - " + UtilityFunctions.convertMillisToDate(
                                                batchEndDate
                                            )
                                    },
                                    onDismiss = { showDatePicker = false }
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Skills Required :",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontFamily = overpassMonoMedium
                            )
                        )
                        LazyRow(
                            state = skillListState,
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            items(skillsList) { item ->
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
                                                skillsList.remove(item)

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
                                        painter = painterResource(R.drawable.ic_skill),
                                        contentDescription = "Password Icon"
                                    )
                                    BasicTextField(
                                        value = skill,
                                        onValueChange = {
                                            skill = it
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
                                            if (skill.isEmpty()) {
                                                Text(
                                                    text = "Enter skill and add",
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
                                        if (skill.trim().isEmpty()) {
                                            Toast.makeText(
                                                context,
                                                "Please enter skill",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else if (
                                            skillsList.any {
                                                it.equals(
                                                    skill.trim(),
                                                    ignoreCase = true
                                                )
                                            }) {
                                            Toast.makeText(
                                                context,
                                                "Skill already exists",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            skillsList.add(skill.trim())
                                            skill = ""
                                        }

                                        Log.d("skillsList", skillsList.toString())
                                    },
                                    modifier = Modifier.size(25.dp),
                                ) {
                                    Icon(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_add),
                                        contentDescription = "Toggle Password"
                                    )
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
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(R.drawable.ic_location),
                                contentDescription = "Location Icon",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            BasicTextField(
                                value = interviewLocation,
                                onValueChange = {
                                    interviewLocation = it
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
                                    if (interviewLocation.isEmpty()) {
                                        Text(
                                            text = "Enter the interview location",
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
                            showInterviewDatePicker = true
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
                                painter = painterResource(R.drawable.ic_date),
                                contentDescription = "bday Icon"
                            )
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                                text = interviewDate?.let {
                                    UtilityFunctions.convertMillisToDate(it)
                                }?:"Pick the interview date",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                    fontFamily = overpassMonoBold
                                )
                            )
                            if (showInterviewDatePicker) {
                                InterviewDatePickerModal(
                                    onDateSelected = { interviewDate = it },
                                    onDismiss = { showInterviewDatePicker = false }
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        enabled = createBatchState !is CreateBatchState.Loading,
                        onClick = {
                            val batch = BatchData(
                                batchName = batchName,
                                designation = designation,
                                description = description,
                                skills = skillsList,
                                startDate = batchStartDate,
                                endDate = batchEndDate,
                                createdBy = userData?.uId ?: "",
                                isOpen = true,
                                interviewLocation = interviewLocation,
                                interviewDate = interviewDate ?: 0,
                                appliedCount = 0,
                                selectedCount = 0,
                                rejectedCount = 0
                            )
                            createNewBatchViewModel.validateInputs(batch,allBatchesData)
                            when (val state = validationState) {
                                is ValidationState.Error ->
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT)
                                        .show()

                                is ValidationState.Success -> {
                                    createNewBatchViewModel.createNewBatch(batch)
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

                        when(val state = createBatchState) {
                            is CreateBatchState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                createNewBatchViewModel.resetCreateBatchState()
                            }
                            CreateBatchState.Idle -> {
                                Text(
                                    text = "Create",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                            }
                            CreateBatchState.Loading -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(25.dp),
                                    strokeWidth = 4.dp
                                )
                            }
                            is CreateBatchState.Success -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                                createNewBatchViewModel.resetCreateBatchState()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewDatePickerModal(onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    context: Context,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
                        onDateRangeSelected(
                            Pair(
                                dateRangePickerState.selectedStartDateMillis,
                                dateRangePickerState.selectedEndDateMillis
                            )
                        )
                        onDismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Please select date range",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                headlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                subheadContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                dayContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                weekdayContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedDayContentColor = MaterialTheme.colorScheme.secondaryContainer,
                dayInSelectionRangeContentColor = MaterialTheme.colorScheme.secondaryContainer,
                dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                    alpha = 0.3f
                ),
                dividerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),

                ),
            state = dateRangePickerState,
            title = {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Select date range",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                        fontFamily = overpassMonoBold
                    )
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(0.dp)
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun CreateNewBatchViewPreview() {
    val fakeBatchDb = FakeManageBatchesRepo()
    val fakeDb = FakeDbRepo()
    val fakeVm = CreateNewBatchViewModel(fakeBatchDb, fakeDb)
    KarkaBoardTheme {
        CreateNewBatchViewContent(
            userData = UserData(),
            navController = rememberNavController(),
            context = LocalContext.current,
            createNewBatchViewModel = fakeVm
        )
    }
}
