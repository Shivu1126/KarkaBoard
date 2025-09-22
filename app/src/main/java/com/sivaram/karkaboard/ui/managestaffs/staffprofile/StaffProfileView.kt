package com.sivaram.karkaboard.ui.managestaffs.staffprofile

import android.R.attr.onClick
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeManageStaffRepo
import com.sivaram.karkaboard.ui.managestaffs.state.RemoveStaffState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffProfileView(
    staffId: String,
    navController: NavController,
    context: Context,
    staffProfileViewModel: StaffProfileViewModel = hiltViewModel()
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
                        text = "Staff Profile",
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
        ) {
            StaffProfileViewContent(
                staffId = staffId,
                navController = navController,
                context = context,
                staffProfileViewModel = staffProfileViewModel
            )
        }
    }
}

@Composable
fun StaffProfileViewContent(
    staffId: String,
    navController: NavController,
    context: Context,
    staffProfileViewModel: StaffProfileViewModel
){

    val staffData by staffProfileViewModel.staffData.observeAsState()
    val staffRole by staffProfileViewModel.staffRole.observeAsState()

    LaunchedEffect(staffId) {
        staffProfileViewModel.getStaffProfileData(staffId)
    }

    LaunchedEffect(staffData?.roleId) {
        staffData?.roleId?.let {roleId ->
            staffProfileViewModel.getStaffRole(roleId)
        }
    }

    val removeState by staffProfileViewModel.removeStaffState.collectAsState()
    var bgIcon by rememberSaveable { mutableStateOf(false) }

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
                    .padding(horizontal = 20.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
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
                            if (staffData?.profileImgUrl?.isEmpty() == true) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(R.drawable.ic_user_profile),
                                    contentDescription = "Profile Image"
                                )
                            } else {
                                AsyncImage(
                                    model = staffData?.profileImgUrl?.toUri(),
                                    contentDescription = "Profile Image",
                                    error = painterResource(R.drawable.ic_user_profile),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(5.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    onError = { bgIcon = true},
                                    onSuccess = {bgIcon = false},
                                    colorFilter = if(bgIcon) ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer) else null
                                )
                            }
                        }
                        Text(
                            text = staffData?.name ?: "Unknown",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                            )
                        )
                        Text(
                            text = staffData?.companyMail ?: "",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            )
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp)
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Text(
                            text = "Email",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            )
                        )
                        Text(
                            text = staffData?.companyMail ?: "",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Text(
                            text = "Personal Email",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            )
                        )
                        Text(
                            text = staffData?.email ?: "",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            )
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Text(
                            text = "Mobile No.",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            )
                        )
                        Text(
                            text = if (staffData?.countryCode.isNullOrBlank() || staffData?.mobile.isNullOrBlank()) {
                                "Not register yet"
                            } else {
                                "${staffData?.countryCode}${staffData?.mobile}"
                            },
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            )
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Text(
                            text = "Role",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            )
                        )
                        Text(
                            text =  staffRole?:"Unknown",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            )
                        )
                    }
                    OutlinedButton(
                        enabled = removeState !is RemoveStaffState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.5f),
                        ),
                        onClick = {
                            staffProfileViewModel.removeStaff(staffId)
                        }
                    ) {
                        when(val state = removeState){
                            is RemoveStaffState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                staffProfileViewModel.resetRemoveStaffState()
                            }
                            RemoveStaffState.Idle -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Remove Staff",
                                        maxLines = 2,
                                        style = TextStyle(
                                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                    Icon(
                                        modifier = Modifier
                                            .size(20.dp),
                                        painter = painterResource(R.drawable.ic_delete),
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                            RemoveStaffState.Loading ->
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(25.dp),
                                    strokeWidth = 4.dp
                                )
                            is RemoveStaffState.Success -> {
                                navController.popBackStack()
                                staffProfileViewModel.resetRemoveStaffState()
                                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
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
fun StaffProfileViewPreview() {
    val fakeManageStaffRepo = FakeManageStaffRepo()
    val vm = StaffProfileViewModel(fakeManageStaffRepo)
    KarkaBoardTheme {
        StaffProfileViewContent(
            staffId = "",
            navController = rememberNavController(),
            context = LocalContext.current,
            staffProfileViewModel = vm
        )
    }
}