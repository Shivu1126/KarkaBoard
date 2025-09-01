package com.sivaram.karkaboard.ui.auth.forgetpassword

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.ui.auth.fake.FakeRepo
import com.sivaram.karkaboard.ui.auth.register.RegisterViewModel
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ResetPasswordView(navController: NavController, context: Context, resetPasswordViewModel: ResetPasswordViewModel = hiltViewModel()){
    Scaffold(
        content = {
            ResetPasswordViewContent(
                navController,
                context,
                resetPasswordViewModel
            )
        }
    )
}

@Composable
fun ResetPasswordViewContent(navController: NavController, context: Context, resetPasswordViewModel: ResetPasswordViewModel){

    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var showNewPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }

    val brush = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.inversePrimary,
            MaterialTheme.colorScheme.onPrimaryContainer
        ),
        startY = 0.0f,
        endY = 200.0f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    ) {
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

        }
        Box(
            modifier = Modifier
                .weight(4.7f)
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
                verticalArrangement = Arrangement.spacedBy(40.dp, alignment = Alignment.CenterVertically)
                ) {
                Column(
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Text(
                        text = "Reset Password",
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
                    contentDescription = "Forgot Password Icon",
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
                                    value = newPassword,
                                    onValueChange = {
                                        newPassword = it
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
                                    value = confirmPassword,
                                    onValueChange = {
                                        confirmPassword = it
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
                        onClick = {
                            if(resetPasswordViewModel.validatePassword(newPassword)){
                                Toast.makeText(context, "Password is not valid", Toast.LENGTH_SHORT).show()
                            }
                            else if(newPassword == confirmPassword){
                                resetPasswordViewModel.resetPassword(newPassword, context){
                                    if(it){
                                        Toast.makeText(context, "Password changed successfully! Please login again", Toast.LENGTH_SHORT).show()
                                        resetPasswordViewModel.signOut(context)
                                        navController.navigate(NavConstants.LOGIN){
                                            popUpTo(0)
                                        }
                                    }
                                    else{
                                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            else{
                                Toast.makeText(context, "Password not matched", Toast.LENGTH_SHORT).show()
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
                        Text(
                            text = "Change",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                fontFamily = overpassMonoBold
                            )
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
fun ResetPasswordViewPreview(){
    val fakeRepo = FakeRepo()
    val fakeVm = ResetPasswordViewModel(fakeRepo)
    KarkaBoardTheme {
        ResetPasswordView(
            navController = rememberNavController(),
            context = LocalContext.current,
            fakeVm
        )
    }
}