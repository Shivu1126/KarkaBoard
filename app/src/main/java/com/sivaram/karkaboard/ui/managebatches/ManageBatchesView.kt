package com.sivaram.karkaboard.ui.managebatches

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.appconstants.OtherConstants
import com.sivaram.karkaboard.data.local.RolePrefs
import com.sivaram.karkaboard.ui.auth.fake.FakeDbRepo
import com.sivaram.karkaboard.ui.auth.fake.FakeManageBatchesRepo
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.managebatches.state.EndBatchState
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import com.sivaram.karkaboard.ui.theme.overpassMonoBold
import com.sivaram.karkaboard.ui.theme.overpassMonoMedium
import com.sivaram.karkaboard.ui.theme.success
import com.sivaram.karkaboard.ui.theme.successContainer
import com.sivaram.karkaboard.utils.UtilityFunctions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBatchesView(
    navController: NavController,
    context: Context,
    manageBatchesViewModel: ManageBatchesViewModel = hiltViewModel()
) {

    val brush = UtilityFunctions.getGradient()
    val coroutineScope = rememberCoroutineScope()

    val role = RolePrefs.getRole(context).collectAsState(initial = "Unknown").value
    Log.d("role", role)

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

            ManageBatchesViewContent(
                navController = navController,
                context = context,
                manageBatchesViewModel = manageBatchesViewModel
            )
            if (role == OtherConstants.ADMIN) {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .padding(bottom = 20.dp)
                        .size(60.dp)
                        .align(Alignment.BottomEnd),
                    onClick = {
                        navController.navigate(NavConstants.CREATE_NEW_BATCH)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Filled.Add, "Add New Batches")
                }
            }
        }
    }
}

@Composable
fun ManageBatchesViewContent(
    navController: NavController,
    context: Context,
    manageBatchesViewModel: ManageBatchesViewModel
) {
    val role = RolePrefs.getRole(context).collectAsState(initial = "Unknown").value
    Log.d("role", role)
    val coroutineScope = rememberCoroutineScope()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val allBatchesData by manageBatchesViewModel.allBatchesData.observeAsState()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            manageBatchesViewModel.
            getAllBatches()
        }
    }

    val endBatchStates by manageBatchesViewModel.endBatchState.collectAsState()

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
            if (allBatchesData?.isEmpty() == true) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(250.dp)
                    )
                }
            } else {

                Log.d("allBatchesData", allBatchesData.toString())
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp),
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
//                content = {}
                ) {


                    allBatchesData?.forEach { batchData ->
                        item {

                            val endBatchState = endBatchStates[batchData.docId] ?: EndBatchState.Idle

                            OutlinedCard(
                                modifier = Modifier
                                    .height(150.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.onSecondary,
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
                                onClick = {

                                },
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(15.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Text(
                                                text = batchData.batchName,
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                ),
                                            )
                                            Text(
                                                text = UtilityFunctions.convertMillisToDate(
                                                    batchData.startDate
                                                ) + " - " + UtilityFunctions.convertMillisToDate(
                                                    batchData.endDate
                                                ),
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                ),
                                            )
                                        }

                                        if (batchData.isOpen) {
                                            if (role == OtherConstants.ADMIN) {
                                                OutlinedButton(
                                                    modifier = Modifier
                                                        .wrapContentWidth()
                                                        .padding(start = 15.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                                        disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                                            alpha = 0.5f
                                                        ),
                                                        disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(
                                                            alpha = 0.5f
                                                        )
                                                    ),
                                                    onClick = {
                                                        manageBatchesViewModel.closeBatchPortal(batchData.docId){ isClosed ->
                                                            if(isClosed){
                                                                Toast.makeText(context, "Batch closed successfully", Toast.LENGTH_SHORT).show()
                                                            }
                                                            else{
                                                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(
                                                        1.dp,
                                                        MaterialTheme.colorScheme.onErrorContainer
                                                    )
                                                ) {
                                                    Text(
                                                        textAlign = TextAlign.Center,
                                                        text = "Close Portal",
                                                        style = TextStyle(
                                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                            fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                                                            fontFamily = overpassMonoMedium
                                                        ),
                                                    )
                                                }
                                            }
                                            else {
                                                Text(
                                                    textAlign = TextAlign.Center,
                                                    text = "Interview",
                                                    style = TextStyle(
                                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                        fontFamily = overpassMonoMedium
                                                    ),
                                                    color = MaterialTheme.colorScheme.success
                                                )
                                            }
                                        }
                                        else if(!batchData.isEnd){
                                            if (role == OtherConstants.ADMIN) {
                                                OutlinedButton(
                                                    enabled = endBatchState !is EndBatchState.Loading,
                                                    modifier = Modifier
                                                        .wrapContentWidth()
                                                        .padding(start = 15.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                                            alpha = 0.5f
                                                        ),
                                                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                            alpha = 0.5f
                                                        )
                                                    ),
                                                    onClick = {
                                                        manageBatchesViewModel.endBatch(batchData.docId)
                                                    },
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(
                                                        1.dp,
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                ) {
                                                    when(endBatchState){
                                                        is EndBatchState.Error ->
                                                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                                        EndBatchState.Idle -> {
                                                            Text(
                                                                textAlign = TextAlign.Center,
                                                                text = "End Batch",
                                                                style = TextStyle(
                                                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                                    fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                                                                    fontFamily = overpassMonoMedium
                                                                ),
                                                            )
                                                        }
                                                        EndBatchState.Loading ->
                                                            CircularProgressIndicator(
                                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                                    alpha = 0.5f
                                                                ),
                                                                modifier = Modifier.size(
                                                                    20.dp
                                                                ),
                                                                strokeWidth = 4.dp
                                                            )
                                                        is EndBatchState.Success -> {
                                                            Toast.makeText(context, "Batch ended successfully", Toast.LENGTH_SHORT).show()
                                                            manageBatchesViewModel.resetEndBatchState(batchData.docId)
                                                        }
                                                    }

                                                }
                                            }
                                            else {
                                                Text(
                                                    textAlign = TextAlign.Center,
                                                    text = "Interning",
                                                    style = TextStyle(
                                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                        fontFamily = overpassMonoMedium
                                                    ),
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                        else {
                                            Text(
                                                textAlign = TextAlign.Center,
                                                text = "Batch Ended",
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                ),
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(5.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = batchData.appliedCount.toString(),
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                )
                                            )
                                            Text(
                                                text = "Applied",
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                )
                                            )
                                        }
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(5.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = batchData.selectedCount.toString(),
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                ),
                                                color = MaterialTheme.colorScheme.success
                                            )
                                            Text(
                                                text = "Selected",
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                ),
                                                color = MaterialTheme.colorScheme.success
                                            )
                                        }
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(5.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = batchData.rejectedCount.toString(),
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                ),
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            Text(
                                                text = "Rejected",
                                                style = TextStyle(
                                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                                    fontFamily = overpassMonoMedium
                                                ),
                                                color = MaterialTheme.colorScheme.onErrorContainer
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
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ManageBatchesViewPreview() {
    val fakeManageBatchesRepo = FakeManageBatchesRepo()
    val fakeDb = FakeDbRepo()
    val fakeVM = ManageBatchesViewModel(fakeManageBatchesRepo, fakeDb)
    KarkaBoardTheme {
        ManageBatchesViewContent(
            navController = rememberNavController(),
            context = LocalContext.current,
            manageBatchesViewModel = fakeVM
        )
    }
}