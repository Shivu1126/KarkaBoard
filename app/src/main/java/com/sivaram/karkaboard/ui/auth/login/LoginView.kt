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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
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
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeRepo
import com.sivaram.karkaboard.ui.auth.state.LoginState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginView(navController: NavController, context: Context, loginViewModel: LoginViewModel = hiltViewModel()){



    Scaffold(
        content = {
            LoginViewContent(
                navController,
                context,
                loginViewModel
            )
        }
    )
}

@SuppressLint("AutoboxingStateCreation")
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

    val rolesData by loginViewModel.rolesList.observeAsState()
    LaunchedEffect(true) {
        Log.d("LoginViewContent", "LaunchedEffect triggered")
        loginViewModel.getRolesList()
    }
    val roleItemData = rolesData ?: emptyList()

    var mailEnd by rememberSaveable { mutableStateOf("") }

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
                                                        Log.d("MailChange", "mailEndIndex -> $mailEndIndex")
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
                            onClick = {

                            },
                        ) {
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