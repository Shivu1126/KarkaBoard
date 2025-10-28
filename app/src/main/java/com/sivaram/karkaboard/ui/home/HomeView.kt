package com.sivaram.karkaboard.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.ui.base.BaseView
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.appconstants.OtherConstants
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.local.RolePrefs
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeRepo
import com.sivaram.karkaboard.ui.auth.state.LogoutState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.utils.UtilityFunctions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavController,
    context: Context,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userInfo by homeViewModel.userData.collectAsState()
    val logoutState by homeViewModel.logoutState.collectAsState()

    val brush = UtilityFunctions.getGradient()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                // profile pic + options
                Log.d("userInfo", userInfo.toString())
                userInfo?.let {
                    DrawerContent(it, context, homeViewModel, navController, logoutState )
                }
            }
        },
        gesturesEnabled = true,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
    ) {
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
                            text = "DashBoard",
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
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(R.drawable.ic_menu),
                                contentDescription = "Menu",
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
                homeViewModel.setUserData(userData)
                HomeViewContent(
                    userData,
                    navController,
                    context,
                    homeViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("AutoboxingStateCreation", "CoroutineCreationDuringComposition")
@Composable
fun HomeViewContent(
    userData: UserData?,
    navController: NavController,
    context: Context,
    homeViewModel: HomeViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val role = RolePrefs.getRole(context).collectAsState(initial = "Unknown").value
    Log.d("role", role)
    Log.d("userData", "home->$userData")

    val studentData by homeViewModel.studentData.observeAsState()

    LaunchedEffect(userData) {
        if(role == OtherConstants.STUDENT){
            homeViewModel.getStudentData(userData?.uId.toString())
            Log.d("studentData", "home -> ${studentData.toString()}")
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
            Column(
                modifier = Modifier.padding(top = 30.dp),
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
                    columns = GridCells.Fixed(2),
                    content = {
                        if (role == OtherConstants.ADMIN) {     //admin only
                            item {  //Manage staffs
                                OutlinedCard(
                                    modifier = Modifier
                                        .aspectRatio(1f), // makes width == height,
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    border = BorderStroke(
                                        3.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 10.dp
                                    ),
                                    shape = RoundedCornerShape(25.dp),
                                    onClick = {
                                        navController.navigate(NavConstants.MANAGE_STAFFS)
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(40.dp),
                                            painter = painterResource(R.drawable.ic_manage_staff),
                                            contentDescription = "Manage Staffs"
                                        )
                                        Text(
                                            text = "Manage Staffs",
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                fontFamily = overpassMonoBold
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                        }
                        if(role!=OtherConstants.STUDENT){   //all except students
                            item {//all batch
                                OutlinedCard(
                                    modifier = Modifier
                                        .aspectRatio(1f), // makes width == height,
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    border = BorderStroke(
                                        3.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 10.dp
                                    ),
                                    shape = RoundedCornerShape(25.dp),
                                    onClick = {
                                        navController.navigate(NavConstants.MANAGE_BATCHES)
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(40.dp),
                                            painter = painterResource(R.drawable.ic_create_batch),
                                            contentDescription = "Batch"
                                        )
                                        Text(
                                            text = "All Batch",
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                fontFamily = overpassMonoBold
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        if(role==OtherConstants.STUDENT ){
                            if(studentData?.isSelected!=true) {
                                item {//application portal
                                    OutlinedCard(
                                        modifier = Modifier
                                            .aspectRatio(1f),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        border = BorderStroke(
                                            3.dp,
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 10.dp
                                        ),
                                        shape = RoundedCornerShape(25.dp),
                                        onClick = {
                                            navController.navigate(NavConstants.APPLICATION_PORTAL)
                                        }
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,

                                            ) {
                                            Icon(
                                                modifier = Modifier.size(40.dp),
                                                painter = painterResource(R.drawable.ic_portal),
                                                contentDescription = "Portal"
                                            )
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(5.dp),
                                                text = "Application Portal",
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                    fontFamily = overpassMonoBold
                                                ),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                            else{
                                item {//application portal
                                    OutlinedCard(
                                        modifier = Modifier
                                            .aspectRatio(1f),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        border = BorderStroke(
                                            3.dp,
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 10.dp
                                        ),
                                        shape = RoundedCornerShape(25.dp),
                                        onClick = {

                                        }
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally,

                                            ) {
                                            Icon(
                                                modifier = Modifier.size(40.dp),
                                                painter = painterResource(R.drawable.ic_tasks),
                                                contentDescription = "View Task"
                                            )
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(5.dp),
                                                text = "Tasks And Assignments",
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                    fontFamily = overpassMonoBold
                                                ),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if(role!=OtherConstants.STUDENT && role!=OtherConstants.ADMIN){
                            item {//interview process
                                OutlinedCard(
                                    modifier = Modifier
                                        .aspectRatio(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    border = BorderStroke(
                                        3.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 10.dp
                                    ),
                                    shape = RoundedCornerShape(25.dp),
                                    onClick = {
                                        navController.navigate(NavConstants.INTERVIEW_MANAGEMENT)
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,

                                        ) {
                                        Icon(
                                            modifier = Modifier.size(40.dp),
                                            painter = painterResource(R.drawable.ic_interview_management),
                                            contentDescription = "Interview_management"
                                        )
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp),
                                            text = "Interview Management",
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                fontFamily = overpassMonoBold
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        if(role==OtherConstants.FACULTY){
                            item {//interview process
                                OutlinedCard(
                                    modifier = Modifier
                                        .aspectRatio(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    border = BorderStroke(
                                        3.dp,
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 10.dp
                                    ),
                                    shape = RoundedCornerShape(25.dp),
                                    onClick = {

                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,

                                        ) {
                                        Icon(
                                            modifier = Modifier.size(40.dp),
                                            painter = painterResource(R.drawable.ic_task_management),
                                            contentDescription = "Task_management"
                                        )
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp),
                                            text = "Task Management",
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                fontFamily = overpassMonoBold
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DrawerContent(
    userData: UserData,
    context: Context,
    homeViewModel: HomeViewModel,
    navController: NavController,
    logoutState: LogoutState
) {
    val role = RolePrefs.getRole(context).collectAsState(initial = "Unknown").value
    var bgIcon by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedCard(
                modifier = Modifier
                    .size(70.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                shape = CircleShape
            ) {

                if (userData.profileImgUrl.isEmpty()) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.ic_user_profile),
                        contentDescription = "Profile Image"
                    )
                } else {
                    AsyncImage(
                        model = userData.profileImgUrl.toUri(),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.ic_user_profile),
                        onError = {
                            bgIcon = true
                            Log.d("load image", "error on loading image")
                            Log.d("load image", it.result.throwable.message.toString())
                        },
                        onSuccess = {
                            bgIcon = false
                            Log.d("load image", "image loaded successfully")
                        },
                        colorFilter = if (bgIcon) ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer) else null
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    5.dp,
                    alignment = Alignment.CenterVertically
                )

            ) {
                Text(
                    text = userData.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                        fontFamily = overpassMonoBold
                    )
                )
                Text(
                    text = userData.email,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                        fontFamily = overpassMonoMedium
                    )
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .weight(1f) // take remaining space
                .verticalScroll(rememberScrollState())
        ) {

            if(role == OtherConstants.STUDENT){
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "Interview History",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                fontFamily = overpassMonoBold
                            )
                        )
                    },
                    icon = {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(R.drawable.ic_history),
                            contentDescription = "interview history"
                        )
                    },
                    onClick = {
                        navController.navigate(NavConstants.INTERVIEW_HISTORY)
                    },
                    selected = false
                )
            }

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Edit Profile",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                },
                icon = {
                    Icon(
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "edit profile"
                    )
                },
                onClick = {

                },
                selected = false
            )

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Change Password",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            fontFamily = overpassMonoBold
                        )
                    )
                },
                icon = {
                    Icon(
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(R.drawable.ic_change_password),
                        contentDescription = "change password"
                    )
                },
                onClick = {
                    navController.navigate(NavConstants.CHANGE_PASSWORD)
                },
                selected = false
            )
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = "Logout",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.error,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                        fontFamily = overpassMonoBold
                    )
                )
            },
            icon = {
                Icon(
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = "Logout"
                )
            },
            onClick = {
                homeViewModel.signOut()
            },
            selected = false,
        )
        when (val state = logoutState) {
            is LogoutState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT)
                    .show()
            }
            LogoutState.Success -> {
                navController.navigate(NavConstants.LOGIN) {
                    popUpTo(NavConstants.HOME) {
                        inclusive = true
                    }
                }
                homeViewModel.resetLogoutState()
                homeViewModel.resetLocalData(context)
            }
            LogoutState.Idle -> {}
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    val fakeRepo = FakeRepo()
    val fakeDbRepo = FakeDbRepo()
    val fakeVm = HomeViewModel(
        authRepository = fakeRepo,
        databaseRepository = fakeDbRepo
    )
    KarkaBoardTheme {
        HomeViewContent(
            navController = rememberNavController(),
            context = LocalContext.current,
            homeViewModel = fakeVm,
            userData = UserData()
        )
    }
}