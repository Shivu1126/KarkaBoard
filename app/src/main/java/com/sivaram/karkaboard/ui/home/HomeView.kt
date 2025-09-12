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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.appconstants.OtherConstants
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.local.RolePrefs
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeRepo
import com.sivaram.karkaboard.ui.auth.state.LogoutState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavController,
    context: Context,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val brush = UtilityFunctions.getGradient()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // profile pic + options
            }
        }
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
                                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
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
                HomeViewContent(
                    userData,
                    navController,
                    context,
                    homeViewModel
                )
//                HorizontalDivider()
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

    val logoutState by homeViewModel.logoutState.collectAsState()

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
                        if(role == OtherConstants.ADMIN) {
                            item {
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
                                            modifier = Modifier.size(50.dp),
                                            painter = painterResource(R.drawable.ic_manage_staff),
                                            contentDescription = "Manage Staffs"
                                        )
                                        Text(
                                            text = "Manage Staffs",
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            item {
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
                                    shape = RoundedCornerShape(25.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,

                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp),
                                            text = "All Batches (Students)",
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        item{
                            TextButton(
                                onClick = {
                                    homeViewModel.signOut(context)
                                }
                            ) {
                                when(val state = logoutState){
                                    is LogoutState.Error -> {
                                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                    }
                                    LogoutState.Idle -> {
                                        Text(
                                            text = "Logout",
                                            style = TextStyle(
                                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                                            )
                                        )
                                    }
                                    LogoutState.Loading -> {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier.size(25.dp),
                                            strokeWidth = 4.dp
                                        )
                                    }
                                    LogoutState.Success -> {
                                        navController.navigate(NavConstants.LOGIN){
                                            popUpTo(NavConstants.HOME){
                                                inclusive = true
                                            }
                                        }
                                        homeViewModel.resetLogoutState()
                                        homeViewModel.resetLocalData(context)
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    val fakeRepo = FakeRepo()
    val fakeVm = HomeViewModel(
        authRepository = fakeRepo
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