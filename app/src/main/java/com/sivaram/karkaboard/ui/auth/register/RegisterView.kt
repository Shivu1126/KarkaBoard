package com.sivaram.karkaboard.ui.auth.register

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.utils.UtilityFunctions
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import coil.compose.AsyncImage
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.sivaram.karkaboard.data.dto.StudentsData
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeRepo
import com.sivaram.karkaboard.ui.auth.state.AuthFlowState
import com.sivaram.karkaboard.ui.auth.state.ValidationState
import com.sivaram.karkaboard.ui.auth.state.VerifyState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterView(
    navController: NavController,
    context: Context,
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
            RegisterViewContent(
                navController,
                context,
                registerViewModel
            )

}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterViewContent(
    navController: NavController,
    context: Context,
    registerViewModel: RegisterViewModel
) {

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var mobileNo by rememberSaveable { mutableStateOf("") }

    var genderDropDown by rememberSaveable { mutableStateOf(false) }
    val genderList = listOf("Female", "Male", "Others")
    var gender by rememberSaveable { mutableStateOf(genderList[0]) }

    var degree by rememberSaveable { mutableStateOf("") }

    val mobileNoCountryCode = listOf(
        "+91", "+1", "+123", "+41"
    )
    var countryCode by rememberSaveable { mutableStateOf(mobileNoCountryCode[0]) }
    var countryCodeDropDown by rememberSaveable { mutableStateOf(false) }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }

    var collegeName by rememberSaveable { mutableStateOf("") }
    val passedOutYearList = listOf("2020", "2021", "2022", "2023", "2024", "2025", "2026")
    var passedOutYear by rememberSaveable { mutableStateOf(passedOutYearList.last()) }
    var passedOutYearDropDown by rememberSaveable { mutableStateOf(false) }

    var resumeFileName by rememberSaveable { mutableStateOf("") }
    var resumeUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            resumeUri = uri
            resumeFileName = UtilityFunctions.getFileName(context, it) ?: ""
        }
        Log.d("resumeFileName", resumeFileName)
        Log.d("resumeUri", resumeUri.toString())
    }

    var profileImgName by rememberSaveable { mutableStateOf("") }
    var profileImgUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            profileImgUri = uri
            profileImgName = UtilityFunctions.getFileName(context, it) ?: ""
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    var bottomSheetVisibility by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var otpText by rememberSaveable { mutableStateOf("") }

    var verificationId by rememberSaveable { mutableStateOf("") }
    val authFlowState by registerViewModel.authFlowState.collectAsState()
    val verifyState by registerViewModel.verifyState.collectAsState()

    val validationState by registerViewModel.validationState.observeAsState()

    val otp by registerViewModel.otp.collectAsState()

    val brush = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.inversePrimary,
            MaterialTheme.colorScheme.onPrimaryContainer
        ),
        startY = 0.0f,
        endY = 600.0f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    ) {

        if (bottomSheetVisibility) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxSize(),
                sheetState = bottomSheetState,
                onDismissRequest = { bottomSheetVisibility = !bottomSheetVisibility },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                dragHandle = {
                    BottomSheetDefaults.DragHandle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        20.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        modifier = Modifier.size(70.dp),
                        painter = painterResource(R.drawable.ic_otp),
                        contentDescription = "OTP Icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "You'll receive an OTP on your registered mobile number ending in $countryCode " +
                                "${
                                    mobileNo.let {
                                        "*".repeat(7) + it.substring(it.length - 3)
                                    }
                                }.",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                    OtpInput(
                        otpValues = otp,
                        onOtpChange = {index, value -> registerViewModel.updateOtp(index, value)},
                        onOtpComplete = {
                            otpText = it
                            Log.d("otpText", otpText)
                        }
                    )

                    ResendOtpTimer(
                        onResend = {
                            Toast.makeText(context, "OTP Resent", Toast.LENGTH_SHORT).show()
                        }
                    )

                    OutlinedButton(
                        enabled = verifyState !is VerifyState.Loading,
                        onClick = {
                            registerViewModel.verifyPhoneCredential(
                                otpText,
                                verificationId,
                                countryCode + mobileNo,
                                email,
                                password
                            )
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

                        when (val state = verifyState) {
                            is VerifyState.Error -> {
//                                coroutineScope.launch {
                                Log.d("verify-error", state.message)
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                registerViewModel.resetVerifyState()
//                                }
                            }

                            VerifyState.Idle -> {
                                Text(
                                    text = "Verify OTP",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                            }

                            VerifyState.Loading -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(25.dp),
                                    strokeWidth = 4.dp
                                )
                            }

                            is VerifyState.Success -> {
                                Log.d("verify-success", state.message)
                                val student = StudentsData(
                                    uId = state.userUid.toString(),
                                    profileImgUrl = profileImgUri.toString(),
                                    name = name.trim(),
                                    email = email.trim(),
                                    mobile = mobileNo.trim(),
                                    countryCode = countryCode.trim(),
                                    gender = gender.trim(),
                                    dob = selectedDate?.let {
                                        UtilityFunctions.convertMillisToDate(it)
                                    } ?: "NA",
                                    collegeName = collegeName.trim(),
                                    degree = degree.trim(),
                                    passingYear = passedOutYear.toInt(),
                                    resumeUrl = resumeUri.toString(),
                                )

                                registerViewModel.registerStudentDetails(student) {
                                    registerViewModel.resetVerifyState()
                                    registerViewModel.resetAuthFlowState()
                                    if (it) {
                                        Toast.makeText(
                                            context,
                                            "Register Successfully Completed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate(NavConstants.HOME) {
                                            popUpTo(0)
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Register Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier,
                painter = painterResource(R.drawable.karka_label_logo),
                contentDescription = "Logo"
            )
        }

        Box(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),

                ) {
                Column(
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Text(
                        text = "Register",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                    Text(
                        text = "Please register to login",
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
                        .padding(top = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedCard(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onPrimaryContainer
                            ),

                            ) {
                            if (profileImgName.isEmpty()) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(R.drawable.ic_user_profile),
                                    contentDescription = "Profile Image"
                                )
                            } else {
                                AsyncImage(
                                    model = profileImgUri,
                                    contentDescription = "Selected Image",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(5.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier
                                .size(35.dp)
                                .align(Alignment.BottomEnd),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                        ) {
                            IconButton(
                                onClick = {
                                    imageLauncher.launch(arrayOf("image/*"))
                                },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_camera),
                                    contentDescription = "Camera icon"
                                )
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
                            Image(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(R.drawable.ic_name),
                                contentDescription = "Name Icon"
                            )
                            BasicTextField(
                                value = name,
                                onValueChange = {
                                    name = it
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
                                    if (name.isEmpty()) {
                                        Text(
                                            text = "Enter your full name",
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
                            Image(
                                modifier = Modifier.size(25.dp),
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
                                    .padding(start = 10.dp),
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
                                            text = "Enter your mail",
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
                                .padding(start = 10.dp, end = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Image(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(R.drawable.ic_password_lock),
                                    contentDescription = "Password Icon"
                                )
                                BasicTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
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
                                        if (password.isEmpty()) {
                                            Text(
                                                text = "Enter your password",
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
                                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                )
                            }
                            IconButton(
                                onClick = {
                                    showPassword = !showPassword
                                },
                                modifier = Modifier.size(25.dp),
                            ) {
                                Image(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(
                                        if (showPassword) R.drawable.ic_password_show
                                        else R.drawable.ic_password_hide
                                    ),
                                    contentDescription = "Toggle Password"
                                )
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

                            Row(
                                modifier = Modifier.weight(1.8f),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(R.drawable.ic_phone),
                                    contentDescription = "Phone Icon"
                                )
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = countryCode,
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                            fontFamily = overpassMonoBold
                                        )
                                    )
                                }
                                DropdownMenu(
                                    expanded = countryCodeDropDown,
                                    onDismissRequest = { countryCodeDropDown = false },
                                    shape = RoundedCornerShape(
                                        bottomStart = 12.dp,
                                        bottomEnd = 12.dp
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp),

                                    ) {
                                    mobileNoCountryCode.forEach {
                                        Row(
                                            modifier = Modifier
                                                .padding(
                                                    horizontal = 10.dp,
                                                    vertical = 5.dp
                                                )
                                                .fillMaxWidth()
                                                .clickable(
                                                    onClick = {
                                                        countryCode = it
                                                        countryCodeDropDown = false
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                                fontFamily = overpassMonoBold
                                            )
                                        }
                                        if (mobileNoCountryCode.last() != it) {
                                            HorizontalDivider(
                                                thickness = 1.dp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(8.2f),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                IconButton(
                                    onClick = {
                                        countryCodeDropDown = !countryCodeDropDown
                                    },
                                    modifier = Modifier.size(25.dp),
                                ) {
                                    Image(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_arrow_drop_down),
                                        contentDescription = "Toggle mobile dropdown"
                                    )
                                }
                                VerticalDivider(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(30.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                BasicTextField(
                                    value = mobileNo,
                                    onValueChange = {
                                        mobileNo = it
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
                                        if (mobileNo.isEmpty()) {
                                            Text(
                                                text = "Enter your mobile number",
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
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone
                                    )
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .weight(1f),
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
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Image(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_gender),
                                        contentDescription = "Gender Icon"
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 5.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        text = gender,
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                            fontFamily = overpassMonoBold
                                        )
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        genderDropDown = !genderDropDown
                                    },
                                    modifier = Modifier.size(25.dp),
                                ) {
                                    Image(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_arrow_drop_down),
                                        contentDescription = "Toggle gender dropdown"
                                    )
                                }
                                DropdownMenu(
                                    expanded = genderDropDown,
                                    onDismissRequest = { genderDropDown = false },
                                    shape = RoundedCornerShape(
                                        bottomStart = 12.dp,
                                        bottomEnd = 12.dp
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp),

                                    ) {
                                    genderList.forEach {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                                .fillMaxWidth()
                                                .clickable(
                                                    onClick = {
                                                        gender = it
                                                        genderDropDown = false
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                                fontFamily = overpassMonoBold
                                            )
                                        }
                                        if (genderList.last() != it) {
                                            HorizontalDivider(
                                                thickness = 1.dp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .weight(2f),
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
                                    .padding(start = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Image(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_birthday),
                                        contentDescription = "bday Icon"
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = selectedDate?.let {
                                            UtilityFunctions.convertMillisToDate(it)
                                        } ?: "Pick Your DOB",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                            fontFamily = overpassMonoBold
                                        )
                                    )
                                    if (showDatePicker) {
                                        DatePickerModal(
                                            onDateSelected = { selectedDate = it },
                                            onDismiss = { showDatePicker = false }
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        showDatePicker = true
                                    }
                                ) {
                                    Image(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_date),
                                        contentDescription = "Date picker icon"
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(4f)
                            ) {
                                Image(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(R.drawable.ic_college),
                                    contentDescription = "college Logo"
                                )
                                BasicTextField(
                                    value = collegeName,
                                    onValueChange = {
                                        collegeName = it
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
                                        if (collegeName.isEmpty()) {
                                            Text(
                                                text = "Enter your college name",
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
                                VerticalDivider(

                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(30.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = passedOutYear,
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        passedOutYearDropDown = !passedOutYearDropDown
                                    },
                                    modifier = Modifier.size(25.dp),
                                ) {
                                    Image(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_arrow_drop_down),
                                        contentDescription = "Toggle year dropdown"
                                    )
                                }
                                DropdownMenu(
                                    expanded = passedOutYearDropDown,
                                    onDismissRequest = { passedOutYearDropDown = false },
                                    shape = RoundedCornerShape(
                                        bottomStart = 12.dp,
                                        bottomEnd = 12.dp
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp),

                                    ) {
                                    passedOutYearList.forEach {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                                .fillMaxWidth()
                                                .clickable(
                                                    onClick = {
                                                        passedOutYear = it
                                                        passedOutYearDropDown = false
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                                fontFamily = overpassMonoBold
                                            )
                                        }
                                        if (passedOutYearList.last() != it) {
                                            HorizontalDivider(
                                                thickness = 1.dp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                        }
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
                                painter = painterResource(R.drawable.ic_degree),
                                contentDescription = "Name Icon"
                            )
                            BasicTextField(
                                value = degree,
                                onValueChange = {
                                    degree = it
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
                                    if (degree.isEmpty()) {
                                        Text(
                                            text = "Enter your degree",
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

                    Text(
                        text = "Upload Your Resume/Cv from Device",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )

                    val borderColor =
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(horizontal = 20.dp)
                            .drawBehind {
                                drawRoundRect(
                                    color = borderColor,
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(
                                                10f,
                                                10f
                                            ), 0f
                                        )
                                    ),
                                    cornerRadius = CornerRadius(16.dp.toPx())
                                )
                            }
                            .clickable(
                                onClick = {
                                    documentLauncher.launch(
                                        arrayOf(
                                            "application/pdf", // PDF
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
                                            "application/msword" // DOC (old Word format)
                                        )
                                    )
                                }
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(0.dp, Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                10.dp,
                                Alignment.CenterVertically
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (resumeFileName.isEmpty()) "Upload Resume" else resumeFileName,
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Image(
                                    modifier = Modifier.size(22.dp),
                                    painter = painterResource(R.drawable.ic_upload),
                                    contentDescription = "Upload Icon"
                                )
                            }
                            Text(
                                text = "(.doc,.docx,.pdf Only with below 3MB.)",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                    fontFamily = overpassMonoMedium
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        enabled = authFlowState !is AuthFlowState.Loading,
                        onClick = {
                            val student = StudentsData(
                                uId = "",
                                profileImgUrl = profileImgUri.toString(),
                                name = name.trim(),
                                email = email.trim(),
                                mobile = mobileNo.trim(),
                                countryCode = countryCode,
                                gender = gender.trim(),
                                dob = selectedDate?.let {
                                    UtilityFunctions.convertMillisToDate(it)
                                } ?: "NA",
                                collegeName = collegeName.trim(),
                                degree = degree.trim(),
                                passingYear = passedOutYear.toInt(),
                                resumeUrl = resumeUri.toString(),
                            )
                            registerViewModel.validateInputs(student, password)
                            when (val state = validationState) {
                                is ValidationState.Error -> {
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT)
                                        .show()
                                }

                                ValidationState.Success -> {
                                    registerViewModel.checkIfUserExists(
                                        email,
                                        countryCode + mobileNo
                                    ) { isExist, message ->
                                        if (isExist) {
                                            Log.d("UserCheck", "User already exists")
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                                .show()
                                            registerViewModel.resetAuthFlowState()
                                        } else {
                                            Log.d("UserCheck", "User not exists")
                                            registerViewModel.getOtp(
                                                countryCode + mobileNo, context
                                            ) {
                                                Log.d("OTP_STATE", "Current state: $authFlowState")
                                                coroutineScope.launch {
                                                    Log.d(
                                                        "UI timeout",
                                                        "RegisterPageContent: $it"
                                                    )
                                                    registerViewModel.resetAuthFlowState()
                                                }

                                            }
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

                        when (val state = authFlowState) {
                            is AuthFlowState.Error -> {
                                coroutineScope.launch {
                                    Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT)
                                        .show()
                                    Log.e("OTP", state.message)
                                    registerViewModel.resetAuthFlowState()
                                }
                            }

                            AuthFlowState.Idle -> {
                                Text(
                                    text = "Register",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )

                                )
                            }

                            AuthFlowState.Loading -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(25.dp),
                                    strokeWidth = 4.dp
                                )
                            }

                            is AuthFlowState.MailAndPhoneNumVerified -> TODO()
                            is AuthFlowState.OtpSent -> {
                                Log.d("OtpUiState", "RegisterPageContent: $state")
                                verificationId = state.verificationId
                                Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT)
                                    .show()
                                bottomSheetVisibility = true
                                registerViewModel.resetAuthFlowState()
                            }

                            is AuthFlowState.OtpTimeout -> {
                                Log.d("UI timeout", "RegisterPageContent: $state")
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                registerViewModel.resetAuthFlowState()
                            }

                            is AuthFlowState.Success -> {
                                Log.d("OTP-success", "RegisterPageContent: $state")
                            }
                        }

                    }


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Text(
                            text = "Already have an account ?",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                fontFamily = overpassMonoMedium
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sign In",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate(NavConstants.LOGIN) {
                                    popUpTo(NavConstants.LOGIN) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            }


        }
    }
}

@Composable
fun OtpInput(
    otpValues: List<String>,
    onOtpChange: (Int, String) -> Unit,
    onOtpComplete: (String) -> Unit = {}
) {
    val otpLength = otpValues.size
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        otpValues.forEachIndexed { index, value ->
            val requester = focusRequesters[index]

            BasicTextField(
                value = value,
                onValueChange = { raw ->
                    val old = otpValues[index]
                    val digitsOnly = raw.filter { it.isDigit() }

                    when {
                        // Handle paste or multi-char input into a single box
                        digitsOnly.length > 1 -> {
                            val room = otpLength - index
                            val chunk = digitsOnly.take(room)
                            chunk.forEachIndexed { i, ch ->
//                                otpValues.se
                                onOtpChange(index + i, ch.toString())
//                                registerViewModel.updateOtp(index + i, ch.toString())
//                                otpValues[index + i] = ch.toString()
                            }

                            val nextIndex = (index + chunk.length).coerceAtMost(otpLength - 1)
                            val nextEmpty =
                                (nextIndex until otpLength).firstOrNull { otpValues[it].isEmpty() }
                                    ?: (0 until otpLength).firstOrNull { otpValues[it].isEmpty() }

                            if (otpValues.all { it.isNotEmpty() }) {
                                onOtpComplete(otpValues.joinToString(""))
                                focusManager.clearFocus()
                            } else {
                                nextEmpty?.let { focusRequesters[it].requestFocus() }
                            }
                        }

                        digitsOnly.length == 1 && digitsOnly != old -> {
                            onOtpChange(index, digitsOnly)
//                            otpValues[index] = digitsOnly

                            val nextEmpty =
                                (index + 1 until otpLength).firstOrNull { otpValues[it].isEmpty() }
                            if (nextEmpty != null) {
                                focusRequesters[nextEmpty].requestFocus()
                            } else {
                                // no empties left -> complete
                                if (otpValues.all { it.isNotEmpty() }) {
                                    onOtpComplete(otpValues.joinToString(""))
                                }
                            }
                        }

                        // User deleted inside this box (from filled -> empty)
                        digitsOnly.isEmpty() && old.isNotEmpty() -> {
//                            otpValues[index] = ""
                            onOtpChange(index, "")
                            if (index > 0) {
                                focusRequesters[index - 1].requestFocus()
                            }
                        }

                        // Else ignore (non-digit or same value)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = if (index == otpLength - 1) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        val nextEmpty =
                            (index + 1 until otpLength).firstOrNull { otpValues[it].isEmpty() }
                        nextEmpty?.let { focusRequesters[it].requestFocus() }
                    },
                    onDone = {
                        if (otpValues.all { it.isNotEmpty() }) {
                            onOtpComplete(otpValues.joinToString(""))
                        }
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier
                    .width(40.dp)
                    .height(50.dp)
                    .focusRequester(requester)
                    // Hardware keyboard backspace on an already-empty box
                    .onPreviewKeyEvent { e ->
                        if (e.type == KeyEventType.KeyDown && e.key == Key.Backspace) {
                            if (otpValues[index].isEmpty() && index > 0) {
//                                otpValues[index - 1] = ""
                                onOtpChange(index - 1, "")
                                focusRequesters[index - 1].requestFocus()
                                true
                            } else false
                        } else false
                    },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                singleLine = true,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                    fontFamily = overpassMonoBold
                ),
                decorationBox = { innerTextField ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            innerTextField()
                        }
                    }
                }
            )
        }
    }

    // Auto focus the first (or first empty) box on launch
    LaunchedEffect(otpValues.joinToString("")) {
        if (otpValues.all { it.isNotEmpty() }) {
            // Do not move focus if OTP fully filled, keep it on last box
            focusRequesters[otpLength - 1].requestFocus()
        } else {
            val firstEmpty = otpValues.indexOfFirst { it.isEmpty() }
            if (firstEmpty >= 0) {
                focusRequesters[firstEmpty].requestFocus()
            }
        }
    }
}

@Composable
fun ResendOtpTimer(
    viewModel: OtpTimerViewModel = viewModel(),
    onResend: () -> Unit
) {
    val timeLeft by viewModel.timeLeft
    val isRunning by viewModel.isRunning

    if (isRunning) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Resend OTP in ${timeLeft}s",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                    fontFamily = overpassMonoBold
                )
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Don’t receive the OTP ?",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                    fontFamily = overpassMonoBold
                )
            )

            TextButton(
                onClick = {
                    viewModel.resetTimer()
                    onResend()
                },
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    modifier = Modifier.padding(0.dp),
                    text = "Resend OTP",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                        fontFamily = overpassMonoBold
                    )
                )
            }
        }
    }

    // Start timer automatically once screen loads
    LaunchedEffect(Unit) {
        if (!isRunning) viewModel.startTimer()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun RegisterViewPreview() {
    val fakeRepo = FakeRepo()
    val fakeDbRepo = FakeDbRepo()
    val fakeVM = RegisterViewModel(fakeRepo, fakeDbRepo)
    KarkaBoardTheme {
        RegisterView(
            navController = rememberNavController(),
            context = LocalContext.current,
            registerViewModel = fakeVM
        )
    }
}