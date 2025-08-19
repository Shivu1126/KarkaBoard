package com.sivaram.karkaboard.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
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
import coil.compose.AsyncImage
import kotlin.contracts.contract

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterView(navController: NavController, context: Context){
    Scaffold(
        content={
            RegisterViewContent(
                navController,
                context
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterViewContent(navController: NavController, context: Context) {

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
        "+91","+1","+123","+41"
    )
    var countryCode by rememberSaveable { mutableStateOf(mobileNoCountryCode[0]) }
    var countryCodeDropDown by rememberSaveable { mutableStateOf(false) }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }

    var collegeName by rememberSaveable { mutableStateOf("") }
    val passedOutYearList = listOf("2020","2021","2022","2023","2024","2025","2026")
    var passedOutYear by rememberSaveable { mutableStateOf(passedOutYearList.last()) }
    var passedOutYearDropDown by rememberSaveable { mutableStateOf(false) }

    var resumeFileName by rememberSaveable { mutableStateOf("") }
    var resumeUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        uri: Uri? ->
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
    ) {uri: Uri? ->
        uri?.let{
            profileImgUri = uri
            profileImgName = UtilityFunctions.getFileName(context, it) ?: ""
        }
    }

    val brush = Brush.verticalGradient(
        listOf(MaterialTheme.colorScheme.inversePrimary, MaterialTheme.colorScheme.onPrimaryContainer),
        startY = 0.0f,
        endY = 600.0f
    )
    Column(
        modifier = Modifier.fillMaxSize()
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
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.fillMaxHeight()
                        .padding(top = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {

                    Box(
                        modifier = Modifier.size(120.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ){
                        OutlinedCard(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),

                        ) {
                            if(profileImgName.isEmpty()){
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(R.drawable.ic_user_profile),
                                    contentDescription = "Profile Image"
                                )
                            }
                            else{
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
                            modifier = Modifier.size(35.dp)
                                .align(Alignment.BottomEnd),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                            ){
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
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()
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
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 10.dp),
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                ),
                                decorationBox = { innerTextField ->
                                    if(name.isEmpty()){
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
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()
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
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 10.dp),
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                ),
                                decorationBox = { innerTextField ->
                                    if(email.isEmpty()){
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
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()
                                .padding(start = 10.dp, end = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Image(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(R.drawable.ic_password),
                                    contentDescription = "Password Icon"
                                )
                                BasicTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                    },
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 10.dp),
                                    singleLine = true,
                                    textStyle = TextStyle(
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
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()
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
                                ){
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
                                    modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp),

                                    ) {
                                    mobileNoCountryCode.forEach {
                                        Row(
                                            modifier = Modifier.padding(
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
                                modifier = Modifier.fillMaxSize()
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
                                    modifier = Modifier.width(1.dp)
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
                                    modifier = Modifier.fillMaxWidth()
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
                                    }
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
                            modifier = Modifier.fillMaxWidth()
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
                                modifier = Modifier.fillMaxSize()
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
                                        modifier = Modifier.padding(horizontal = 5.dp)
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
                                    shape = RoundedCornerShape( bottomStart = 12.dp, bottomEnd = 12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                                    modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp),

                                    ){
                                    genderList.forEach {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
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
                                        if(genderList.last() != it) {
                                            HorizontalDivider(
                                                thickness = 1.dp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha=0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth()
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
                                modifier = Modifier.fillMaxSize()
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
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = selectedDate?.let{
                                            UtilityFunctions.convertMillisToDate(it)
                                        }?:"Pick Your DOB",
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
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()
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
                                    modifier = Modifier.fillMaxWidth()
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

                                    modifier = Modifier.width(1.dp)
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
                                    shape = RoundedCornerShape( bottomStart = 12.dp, bottomEnd = 12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                                    modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp),

                                    ){
                                    passedOutYearList.forEach {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
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
                                        if(passedOutYearList.last() != it) {
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
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()
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
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 10.dp),
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                    fontFamily = overpassMonoMedium
                                ),
                                decorationBox = { innerTextField ->
                                    if(degree.isEmpty()){
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

                    val borderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                            .height(70.dp)
                            .padding(horizontal = 20.dp)
                            .drawBehind{
                                drawRoundRect(
                                    color = borderColor,
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
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
                            modifier = Modifier.fillMaxSize()
                                .padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ){
                                Text(
                                    text = if(resumeFileName.isEmpty()) "Upload Resume" else resumeFileName,
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                        fontFamily = overpassMonoBold
                                    ),
                                    maxLines =  1,
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

                    OutlinedButton (
                        onClick = { /*TODO*/

                            if(profileImgName.isEmpty()){
                                Toast.makeText(context, "Please upload a profile picture", Toast.LENGTH_SHORT).show()
                            }
                            else if(name.trim().isEmpty()){
                                Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                            }
                            else if(email.trim().isEmpty()){
                                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                            }
                            else if(password.trim().isEmpty() || password.length < 8){
                                Toast.makeText(context, "Please enter valid password", Toast.LENGTH_SHORT).show()
                            }
                            else if(mobileNo.trim().isEmpty()){
                                Toast.makeText(context, "Please enter your mobile number", Toast.LENGTH_SHORT).show()
                            }
                            else if(selectedDate == null){
                                Toast.makeText(context, "Please pick your date of birth", Toast.LENGTH_SHORT).show()
                            }
                            else if(collegeName.trim().isEmpty()){
                                Toast.makeText(context, "Please enter your college name", Toast.LENGTH_SHORT).show()
                            }
                            else if(degree.trim().isEmpty()){
                                Toast.makeText(context, "Please enter your degree", Toast.LENGTH_SHORT).show()
                            }

                            else if(resumeFileName.isEmpty()){
                                Toast.makeText(context, "Please upload a resume", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                //TODO: Open Bottom sheet for OTP verification
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
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
                        Text(
                            text = "Register",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                                fontFamily = overpassMonoBold
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ){
                        Text(
                            text="Already have an account ?",
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
                                navController.navigate(NavConstants.LOGIN){
                                    popUpTo(NavConstants.LOGIN){
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

@Preview(showBackground = true)
@Composable
fun RegisterViewPreview() {
    KarkaBoardTheme {
        RegisterView(navController = rememberNavController(), context = LocalContext.current)
    }
}