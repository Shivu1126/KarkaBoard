package com.sivaram.karkaboard.ui.auth.changepassword

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.fake.FakeRepo
import com.sivaram.karkaboard.ui.auth.state.ChangePasswordState
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordView(
    navController: NavController,
    context: Context,
    changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()
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
                        text = "Interview Management",
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
            ChangePasswordViewContent(
                userData,
                changePasswordViewModel,
                context
            )
        }
    }
}

@Composable
fun ChangePasswordViewContent(
    userData: UserData?,
    changePasswordViewModel: ChangePasswordViewModel,
    context: Context
) {
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var showOldPassword by rememberSaveable { mutableStateOf(false) }
    var showNewPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }

    val changePasswordState by changePasswordViewModel.changePasswordState.collectAsState()

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
                    .padding(bottom = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(
                    40.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                Column(
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Text(
                        text = "Change Password",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                    Text(
                        text = "Enter your new password here",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                }

                Icon(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally),
                    painter = painterResource(R.drawable.reset),
                    contentDescription = "Change Password Icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "At least 8 characters with A-Z, a-z, 0-9 & special character.",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                        fontFamily = overpassMonoBold
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
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
                                Image(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(R.drawable.ic_password_lock),
                                    contentDescription = "Password Icon"
                                )
                                BasicTextField(
                                    value = oldPassword.trim(),
                                    onValueChange = {
                                        oldPassword = it.trim()
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
                                        if (oldPassword.isEmpty()) {
                                            Text(
                                                text = "Enter your old password",
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
                                    visualTransformation = if (showOldPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                )
                            }
                            IconButton(
                                onClick = {
                                    showOldPassword = !showOldPassword
                                },
                                modifier = Modifier.size(25.dp),
                            ) {
                                Image(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(
                                        if (showOldPassword) R.drawable.ic_password_show
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
                                    value = newPassword.trim(),
                                    onValueChange = {
                                        newPassword = it.trim()
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
                                        if (newPassword.isEmpty()) {
                                            Text(
                                                text = "Enter your new password",
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
                                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                )
                            }
                            IconButton(
                                onClick = {
                                    showNewPassword = !showNewPassword
                                },
                                modifier = Modifier.size(25.dp),
                            ) {
                                Image(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(
                                        if (showNewPassword) R.drawable.ic_password_show
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
                                    value = confirmPassword.trim(),
                                    onValueChange = {
                                        confirmPassword = it.trim()
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
                                        if (confirmPassword.isEmpty()) {
                                            Text(
                                                text = "Enter confirm password",
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
                                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                )
                            }
                            IconButton(
                                onClick = {
                                    showConfirmPassword = !showConfirmPassword
                                },
                                modifier = Modifier.size(25.dp),
                            ) {
                                Image(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(
                                        if (showConfirmPassword) R.drawable.ic_password_show
                                        else R.drawable.ic_password_hide
                                    ),
                                    contentDescription = "Toggle Password"
                                )
                            }
                        }
                    }
                    OutlinedButton(
                        enabled = changePasswordState !is ChangePasswordState.Loading,
                        onClick = {
                            if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){
                                Toast.makeText(context, "Please fill all field ", Toast.LENGTH_SHORT).show()
                            }
                            else if(newPassword != confirmPassword){
                                Toast.makeText(context, "Password not matched", Toast.LENGTH_SHORT).show()
                            }
                            else if(changePasswordViewModel.validatePassword(newPassword)){
                                Toast.makeText(context, "New password is not valid", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                changePasswordViewModel.changePassword(
                                    userData?.email.toString(),
                                    oldPassword,
                                    newPassword
                                )
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
                        when(val state = changePasswordState){
                            is ChangePasswordState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                changePasswordViewModel.resetChangePasswordState()
                            }
                            ChangePasswordState.Idle -> {
                                Text(
                                    text = "Change",
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    )
                                )
                            }
                            ChangePasswordState.Loading -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.size(25.dp),
                                    strokeWidth = 4.dp
                                )
                            }
                            is ChangePasswordState.Success -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                oldPassword = ""
                                newPassword = ""
                                confirmPassword = ""
                                changePasswordViewModel.resetChangePasswordState()
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
fun ChangePasswordViewContentPreview() {
    val changePasswordViewModel = ChangePasswordViewModel(
        FakeRepo()
    )
    KarkaBoardTheme {
        ChangePasswordViewContent(
            userData = UserData(),
            changePasswordViewModel = changePasswordViewModel,
            context = LocalContext.current
        )
    }
}