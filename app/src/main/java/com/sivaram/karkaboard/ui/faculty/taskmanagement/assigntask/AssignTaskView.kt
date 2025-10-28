package com.sivaram.karkaboard.ui.faculty.taskmanagement.assigntask

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignTaskView(
    navController: NavController,
    context: Context,
    assignTaskViewModel: AssignTaskViewModel = hiltViewModel()
){

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
                        text = "All Batches",
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

        }
    }
}

@Composable
fun AssignTaskViewContent(
    userData: UserData?,
    context: Context,
    assignTaskViewModel: AssignTaskViewModel = hiltViewModel()
){
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

        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun AssignTaskViewContentPreview(){
    val fakeVM = AssignTaskViewModel()
    KarkaBoardTheme {
        AssignTaskViewContent(
            userData = UserData(),
            context = LocalContext.current,
            assignTaskViewModel = fakeVM
        )
    }
}