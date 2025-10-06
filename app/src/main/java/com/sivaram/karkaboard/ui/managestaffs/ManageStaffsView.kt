package com.sivaram.karkaboard.ui.managestaffs

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.core.net.toUri
import com.sivaram.karkaboard.ui.auth.fake.FakeManageStaffRepo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageStaffsView(navController: NavController, context: Context, manageStaffsViewModel: ManageStaffsViewModel = hiltViewModel()){

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
                        text = "Manage Staffs",
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
        ){

            ManageStaffsViewContent(
                navController = navController,
                context = context,
                manageStaffsViewModel = manageStaffsViewModel
            )
            FloatingActionButton(
                modifier = Modifier
                    .padding(end = 20.dp)
                    .padding(bottom = 20.dp)
                    .size(60.dp)
                    .align(Alignment.BottomEnd),
                onClick = {
                    navController.navigate(NavConstants.ADD_STAFF)
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Filled.Add, "Small floating action button.")
            }
        }
    }

}

@Composable
fun ManageStaffsViewContent(navController: NavController, context: Context, manageStaffsViewModel: ManageStaffsViewModel){

    val coroutineScope = rememberCoroutineScope()
    val allStaffData by manageStaffsViewModel.allStaffData.observeAsState()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            manageStaffsViewModel.getAllStaff()
        }
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
            if(allStaffData?.isEmpty() == true){
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(250.dp)
                    )
                }
            }
            else {
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp),
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
//                content = {}
                ) {
                    Log.d("allStaffData", allStaffData.toString())
                    allStaffData?.forEach {staff ->

                        item{
                            var bgIcon by rememberSaveable { mutableStateOf(false) }
                            OutlinedCard(
                                modifier = Modifier
                                    .height(100.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.onSecondary,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                onClick = {
                                    navController.navigate(NavConstants.STAFF_PROFILE + "/${staff.uId}")
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(15.dp),
                                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedCard(
                                        modifier = Modifier
                                            .size(70.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        ),
                                        shape = CircleShape
                                    ) {

                                        if(staff.profileImgUrl.isEmpty()){
                                            Icon(
                                                modifier = Modifier.fillMaxSize(),
                                                painter = painterResource(R.drawable.ic_user_profile),
                                                contentDescription = "Profile Image"
                                            )
                                        }
                                        
                                        else{
                                            AsyncImage(
                                                model = staff.profileImgUrl.toUri(),
                                                contentDescription = "Profile Image",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop,
                                                error = painterResource(R.drawable.ic_user_profile),
                                                onError = {bgIcon = true
                                                          Log.d("load image", "error on loading image")
                                                          Log.d("load image", it.result.throwable.message.toString())},
                                                onSuccess = {bgIcon = false
                                                            Log.d("load image", "image loaded successfully")},
                                                colorFilter = if(bgIcon) ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer) else null
                                            )
                                        }
                                    }
                                    Column(
                                        modifier = Modifier.fillMaxHeight()
                                            .weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(
                                            15.dp,
                                            alignment = Alignment.CenterVertically
                                        )

                                    ) {
                                        Text(
                                            text = staff.name,
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = staff.companyMail,
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
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
fun ManageStaffsViewPreview(){
    val fakeManageStaffRepo = FakeManageStaffRepo()
    val fakeVm = ManageStaffsViewModel(fakeManageStaffRepo)
    KarkaBoardTheme {
        ManageStaffsViewContent(
            navController = rememberNavController(),
            context = LocalContext.current,
            manageStaffsViewModel = fakeVm
        )
    }
}