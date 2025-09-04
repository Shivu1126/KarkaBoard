package com.sivaram.karkaboard.ui.auth.login

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.data.local.ResetPasswordPref
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeRepo
import com.sivaram.karkaboard.ui.auth.register.OtpInput
import com.sivaram.karkaboard.ui.auth.state.AuthFlowState
import com.sivaram.karkaboard.ui.auth.state.LoginState
import com.sivaram.karkaboard.ui.auth.state.VerifyState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginView(navController: NavController, context: Context, loginViewModel: LoginViewModel = hiltViewModel()){

            LoginViewContent(
                navController,
                context,
                loginViewModel
            )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("AutoboxingStateCreation", "CoroutineCreationDuringComposition")
@Composable
fun LoginViewContent(
    navController: NavController, context: Context, loginViewModel: LoginViewModel
) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var loginButtonEnabled by rememberSaveable { mutableStateOf(true) }
//    val roleItemDate = listOf(
//        "I'm Student","@karkaadmin.com","@karkahr.com","@karkafaculty.com","@karkapanelist.com"
//    )
    var verificationId by rememberSaveable { mutableStateOf("") }

    val rolesData by loginViewModel.rolesList.observeAsState()
    LaunchedEffect(true) {
        Log.d("LoginViewContent", "LaunchedEffect triggered")
        loginViewModel.getRolesList()
    }
    val roleItemData = rolesData ?: emptyList()

    var mailEnd by rememberSaveable { mutableStateOf("") }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    var bottomSheetVisibility by rememberSaveable { mutableStateOf(false) }

// Update mailEnd when rolesData changes
    LaunchedEffect(roleItemData) {
        if (roleItemData.isNotEmpty()) {
            mailEnd = roleItemData[0].content
        }
    }
    var mailEndIndex by rememberSaveable { mutableIntStateOf(0) }
    var emailDropDown by rememberSaveable { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsState()

    var coroutineScope = rememberCoroutineScope()

    var mobile by rememberSaveable { mutableStateOf("") }
    var countryNo by rememberSaveable { mutableStateOf("") }
    var otpText by rememberSaveable { mutableStateOf("") }
    val verifyState by loginViewModel.verifyState.collectAsState()

    val authFlowState by loginViewModel.authFlowState.collectAsState()


    val otp by loginViewModel.otp.collectAsState()

    val brush = Brush.verticalGradient(
        listOf(MaterialTheme.colorScheme.inversePrimary, MaterialTheme.colorScheme.onPrimaryContainer),
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
                        30.dp,
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
                        text = "You'll receive an OTP on your registered mobile number ending in $countryNo "+
                                "${
                                    mobile.let {
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
                        onOtpChange = {index, value -> loginViewModel.updateOtp(index, value)},
                        onOtpComplete = {
                            otpText = it
                            Log.d("otpText", otpText)
                        }
                    )

                    OutlinedButton(
                        enabled = verifyState !is VerifyState.Loading,
                        onClick = {
                            loginViewModel.verifyCredential(verificationId, otpText)
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
                                Log.d("verify-error", state.message)
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                loginViewModel.resetVerifyState()
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
                                coroutineScope.launch {
                                    ResetPasswordPref.setResetInProgress(context, true)
                                }
                                navController.navigate(NavConstants.RESET_PASSWORD){
                                    popUpTo(0)
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
        ){
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
                .background(MaterialTheme.colorScheme.secondaryContainer),
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
                        text = "Login",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                    Text(
                        text = "Please sign in to continue",
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
                                                text = "Enter mail id",
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
                                modifier = Modifier.weight(5.3f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = mailEnd,
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        emailDropDown = !emailDropDown
                                    },
                                    modifier = Modifier.size(25.dp),
                                ) {
                                    Image(
                                        modifier = Modifier.size(25.dp),
                                        painter = painterResource(R.drawable.ic_arrow_drop_down),
                                        contentDescription = "Toggle mail dropdown"
                                    )
                                }
                                DropdownMenu(
                                    expanded = emailDropDown,
                                    onDismissRequest = { emailDropDown = false },
                                    shape = RoundedCornerShape( bottomStart = 12.dp, bottomEnd = 12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp),

                                ){
                                    roleItemData.forEach {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                                .fillMaxWidth()
                                                .clickable(
                                                    onClick = {
                                                        mailEnd = it.content
                                                        mailEndIndex = roleItemData.indexOf(it)
                                                        Log.d(
                                                            "MailChange",
                                                            "mailEndIndex -> $mailEndIndex"
                                                        )
                                                        if (mailEndIndex == 0) {
                                                            Toast.makeText(
                                                                context,
                                                                "Please enter full email id",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        emailDropDown = false
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = it.content,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                                fontFamily = overpassMonoBold
                                            )
                                        }
                                        if(roleItemData.last() != it) {
                                            HorizontalDivider(
                                                thickness = 1.dp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha=0.5f)
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
                                .padding(start = 10.dp, end = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,

                            ) {
                                Image(
                                    modifier = Modifier.size(30.dp),
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
                                                maxLines = 1,
                                                style = TextStyle(
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ){
                        TextButton(
                            enabled = authFlowState !is AuthFlowState.Loading,
                            onClick = {
                                if(email.trim().isEmpty()){
                                    Toast.makeText(context, "Please enter registered email id", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    loginViewModel.checkEnd(email.trim(), mailEndIndex, roleItemData){
                                            isNotAllowed, email ->
                                        if(isNotAllowed){
                                            Toast.makeText(context, "Please select proper role !!", Toast.LENGTH_SHORT).show()
                                        }
                                        else {
                                            loginViewModel.getMobileNoByMail(email){
                                                isMobileNoExist, mobileNumber, countryCode ->
                                                    Log.d("phoneBook", "mobileNoExist: $isMobileNoExist")
                                                    if(isMobileNoExist){
                                                        mobile = mobileNumber
                                                        countryNo = countryCode
                                                        loginViewModel.getOtp(countryNo+mobileNumber, context) {
                                                            Log.d("OTP_STATE", "Current state: $authFlowState")
                                                            coroutineScope.launch {
                                                                Log.d(
                                                                    "UI timeout",
                                                                    "LoginPageContent: $it"
                                                                )
                                                                loginViewModel.resetAuthFlowState()
                                                            }
                                                        }
//                                                        Toast.makeText(
//                                                            context,
//                                                            "OTP sent to $mobileNumber",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()

                                                    }
                                                    else{
                                                        Toast.makeText(
                                                            context,
                                                            "Please enter registered mail id",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                        }
                                    }
                                }
                            },
                        ) {
                            when (val state = authFlowState) {
                                is AuthFlowState.Error -> {
                                    coroutineScope.launch {
                                        Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT)
                                            .show()
                                        Log.e("OTP", state.message)
                                        loginViewModel.resetAuthFlowState()
                                    }
                                }

                                AuthFlowState.Idle -> {
                                    Text(
                                        text = "Forget Password ?",
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                            fontFamily = overpassMonoBold
                                        )
                                    )
                                }

                                AuthFlowState.Loading -> {
                                    Text(
                                        text = "Please wait..",
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                            fontFamily = overpassMonoBold
                                        )
                                    )
                                }

                                is AuthFlowState.MailAndPhoneNumVerified -> {
                                    Log.d("OtpUiState", "LoginPageContent: $state")
                                }
                                is AuthFlowState.OtpSent -> {
                                    Log.d("OtpUiState", "LoginPageContent: $state")
                                    verificationId = state.verificationId
                                    Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT)
                                        .show()
                                    bottomSheetVisibility = true
                                    loginViewModel.resetAuthFlowState()
                                }

                                is AuthFlowState.OtpTimeout -> {
                                    Log.d("UI timeout", "LoginPageContent: $state")
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                    loginViewModel.resetAuthFlowState()
                                }

                                is AuthFlowState.Success -> {
                                    Log.d("OTP-success", "LoginPageContent: $state")
                                }
                            }

                        }
                    }
                    OutlinedButton (
                        onClick = {
                            var emailId = email.trim()
                            if(email.trim().isEmpty() || password.trim().isEmpty()){
                                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                var isEnd = false
                                if (mailEndIndex != 0) {
                                    if(! emailId.endsWith(roleItemData[mailEndIndex].content)){
                                        emailId += roleItemData[mailEndIndex].content
                                    }
                                }
                                else{
                                    roleItemData.forEach {
                                        if (emailId.endsWith(it.content)) {
                                            isEnd = true
                                        }
                                    }
                                }
                                if(isEnd){
                                    Toast.makeText(context, "Please select proper role !!", Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    loginViewModel.login(emailId, password)
                                }
                            }
                        },
                        enabled = loginState !is LoginState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                            disabledContentColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.inversePrimary),
                    ){
                        when(val state = loginState){
                            is LoginState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                loginViewModel.resetLoginState()
                            }
                            LoginState.Idle -> {
                                Text(
                                    text = "Login",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                            }
                            LoginState.Loading -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(25.dp),
                                    strokeWidth = 4.dp
                                )
                            }
                            is LoginState.Success -> {
                                loginViewModel.resetLoginState()
                                navController.navigate(NavConstants.HOME) {
                                    popUpTo(0)
                                }
                            }
                        }

                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ){
                        Text(
                            text="Don’t have an account ?",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                fontFamily = overpassMonoMedium
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Register here",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                fontFamily = overpassMonoBold
                            ),
                            modifier = Modifier.clickable() {
                                navController.navigate(NavConstants.REGISTER)
                            }
                        )
                    }
                }
            }


        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    val fakeRepo = FakeRepo()
    val fakeDbRepo = FakeDbRepo()
    val fakeVM = LoginViewModel(
        fakeRepo,
        fakeDbRepo
    )
    KarkaBoardTheme {
        LoginView(
            navController = rememberNavController(),
            context = LocalContext.current,
            loginViewModel = fakeVM
        )
    }
}