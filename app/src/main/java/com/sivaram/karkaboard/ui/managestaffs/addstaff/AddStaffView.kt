package com.sivaram.karkaboard.ui.managestaffs.addstaff

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeManageStaffRepo
import com.sivaram.karkaboard.ui.auth.state.ValidationState
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffView(
    navController: NavController,
    context: Context,
    addStaffViewModel: AddStaffViewModel = hiltViewModel()
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
                        text = "Add Staff",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = MaterialTheme.typography.titleLarge.fontWeight
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

            AddStaffViewContent(
                navController = navController,
                context = context,
                addStaffViewModel = addStaffViewModel
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AddStaffViewContent(navController: NavController, context: Context, addStaffViewModel: AddStaffViewModel) {

    var email by rememberSaveable { mutableStateOf("") }

    val rolesData by addStaffViewModel.rolesList.observeAsState()
    LaunchedEffect(true) {
        Log.d("AddStaffViewContent", "LaunchedEffect triggered")
        addStaffViewModel.getRolesList()
    }
    val roleItemData = rolesData ?: emptyList()
    var selectedIndex by rememberSaveable { mutableIntStateOf(1)}

    val addStaffState by addStaffViewModel.addStaffState.collectAsState()
    var staffName by rememberSaveable { mutableStateOf("") }

    val validationState by addStaffViewModel.validationState.observeAsState()

    val coroutineScope = rememberCoroutineScope()
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
                    .padding(horizontal = 20.dp),
            ) {
                Column(
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Text(
                        text = "Add Staff",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                    Text(
                        text = "Please add new staff",
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
                            Image(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(R.drawable.ic_name),
                                contentDescription = "Name Icon"
                            )
                            BasicTextField(
                                value = staffName,
                                onValueChange = {
                                    staffName = it
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
                                    if (staffName.isEmpty()) {
                                        Text(
                                            text = "Enter staff's name",
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(4.7f)
                            ) {
                                Image(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(R.drawable.ic_mail),
                                    contentDescription = "Mail Logo"
                                )
                                BasicTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                    },
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp),
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                        fontFamily = overpassMonoMedium
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (email.isEmpty()) {
                                            Text(
                                                text = "Enter staff's personal mail id",
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = TextStyle(
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

                    Text(
                        text = "Select Role :",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            fontFamily = overpassMonoMedium
                        )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ){
                        roleItemData.forEach { roleData ->
                            if(roleData!=roleItemData[0]) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = (roleData == roleItemData[selectedIndex]),
                                            onClick = {
                                                selectedIndex = roleItemData.indexOf(roleData)
                                                Log.d(
                                                    "selectedDomain",
                                                    roleItemData[selectedIndex].content
                                                )
                                            },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically

                                ) {
                                    RadioButton(
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            unselectedColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        selected = (roleData == roleItemData[selectedIndex]),
                                        onClick = null
                                    )
                                    Text(
                                        text = roleData.role,
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                            fontFamily = overpassMonoMedium
                                        ),
                                        modifier = Modifier.padding(start = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                    OutlinedButton(
                        enabled = addStaffState !is AddStaffState.Loading,
                        onClick = {
                            addStaffViewModel.validateInputs(staffName, email)
                            when(val state = validationState){
                                is ValidationState.Error -> {
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }

                                ValidationState.Success -> {

                                    addStaffViewModel.checkStaffExist(email,roleItemData[selectedIndex]){
                                        staffEmail, isValid ->
                                            if(isValid){
                                                val userData = UserData(
                                                    name = staffName,
                                                    email = staffEmail,
                                                    isDisable = false
                                                )
                                                addStaffViewModel.createStaff(userData,email, roleItemData[selectedIndex],"12345678", context)
                                            }
                                            else{
                                                Toast.makeText(context, "Please enter valid email, Maybe Its already taken", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }
                                else -> Unit
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 50.dp),
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

                        when(val state = addStaffState){
                            is AddStaffState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                addStaffViewModel.resetAddStaffState()
                            }
                            AddStaffState.Idle -> {
                                Text(
                                    text = "Create",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineMedium.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                            }
                            AddStaffState.Loading -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(25.dp),
                                    strokeWidth = 4.dp
                                )
                            }
                            is AddStaffState.Success -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                addStaffViewModel.resetAddStaffState()
                                coroutineScope.launch {
                                    navController.popBackStack()
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
fun AddStaffViewPreview() {
    val fakeManageStaffRepo = FakeManageStaffRepo()
    val fakeDbRepo = FakeDbRepo()
    val fakeVm = AddStaffViewModel(fakeManageStaffRepo, fakeDbRepo)
    KarkaBoardTheme {
        AddStaffViewContent(
            navController = rememberNavController(),
            context = LocalContext.current,
            addStaffViewModel = fakeVm
        )
    }
}