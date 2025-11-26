package com.sivaram.karkaboard.appnav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.ui.applicationportal.ApplicationPortalView
import com.sivaram.karkaboard.ui.auth.changepassword.ChangePasswordView
import com.sivaram.karkaboard.ui.auth.forgetpassword.ResetPasswordView
import com.sivaram.karkaboard.ui.home.HomeView
import com.sivaram.karkaboard.ui.auth.login.LoginView
import com.sivaram.karkaboard.ui.auth.register.RegisterView
import com.sivaram.karkaboard.ui.base.BaseView
import com.sivaram.karkaboard.ui.faculty.taskmanagement.TaskManagementView
import com.sivaram.karkaboard.ui.faculty.taskmanagement.assigntask.AssignTaskView
import com.sivaram.karkaboard.ui.faculty.taskmanagement.taskdetails.TaskDetailsView
import com.sivaram.karkaboard.ui.interviewhistory.InterviewHistoryView
import com.sivaram.karkaboard.ui.interviewmanagement.InterviewManagementView
import com.sivaram.karkaboard.ui.managebatches.ManageBatchesView
import com.sivaram.karkaboard.ui.managebatches.createnewbatch.CreateNewBatchView
import com.sivaram.karkaboard.ui.managestaffs.ManageStaffsView
import com.sivaram.karkaboard.ui.managestaffs.addstaff.AddStaffView
import com.sivaram.karkaboard.ui.managestaffs.staffprofile.StaffProfileView
import com.sivaram.karkaboard.ui.student.task.TaskView
import com.sivaram.karkaboard.ui.student.task.tasksubmission.TaskSubmissionView

@Composable
fun Navigation( navController: NavHostController, context: Context){
    NavHost(navController = navController, startDestination = NavConstants.DECIDER){
        composable(NavConstants.DECIDER){
            BaseView(
                topBar = null
            ) { innerPadding, userData ->
                NavigationDecider(navController, context)
            }
        }
        composable(NavConstants.HOME){
            HomeView(navController, context)
        }
        composable(NavConstants.LOGIN){
            BaseView(
                topBar = null
            ) { innerPadding, userData ->
                LoginView(navController, context)
            }
        }
        composable(NavConstants.REGISTER){
            BaseView(
                topBar = null,
            ) { innerPadding, userData ->
                RegisterView(navController, context)
            }
        }
        composable(NavConstants.RESET_PASSWORD){
            BaseView(
                topBar = null
            ) { innerPadding, userData ->
                ResetPasswordView(navController, context)
            }
        }
        composable(NavConstants.MANAGE_STAFFS) {
            ManageStaffsView(navController, context)
        }
        composable(NavConstants.ADD_STAFF) {
            AddStaffView(navController, context)
        }
        composable(NavConstants.STAFF_PROFILE+"/{staffId}",
            arguments = listOf(
                navArgument("staffId") { type = NavType.StringType }
            )
        ) {backStackEntry ->
            val staffId = backStackEntry.arguments?.getString("staffId")?:""
            StaffProfileView(staffId, navController, context)
        }
        composable(NavConstants.CREATE_NEW_BATCH) {
            CreateNewBatchView(navController, context)
        }
        composable(NavConstants.MANAGE_BATCHES) {
            ManageBatchesView(navController, context)
        }
        composable(NavConstants.APPLICATION_PORTAL) {
            ApplicationPortalView(navController, context)
        }
        composable(NavConstants.INTERVIEW_MANAGEMENT) {
            InterviewManagementView(navController, context)
        }
        composable(NavConstants.INTERVIEW_HISTORY) {
            InterviewHistoryView(navController, context)
        }
        composable(NavConstants.CHANGE_PASSWORD) {
            ChangePasswordView(navController, context)
        }
        composable(NavConstants.TASK_MANAGEMENT) {
            TaskManagementView(navController, context)
        }
        composable(NavConstants.ASSIGN_TASK) {
            AssignTaskView(navController, context)
        }
        composable(NavConstants.TASK) {
            TaskView(navController, context)
        }
        composable(
            route = NavConstants.TASK_SUBMISSION + "/{taskId}/{taskSubmissionId}/{facultyId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType },
                navArgument("taskSubmissionId") { type = NavType.StringType },
                navArgument("facultyId") { type = NavType.StringType }
            )
        ){backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?:""
            val taskSubmissionId = backStackEntry.arguments?.getString("taskSubmissionId")?:""
            val facultyId = backStackEntry.arguments?.getString("facultyId")?:""
            TaskSubmissionView(
                taskId, taskSubmissionId, facultyId, navController, context
            )
        }
        composable(
            route = NavConstants.TASK_DETAILS+"/{taskId}/{batchId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType },
                navArgument("batchId") { type = NavType.StringType }
            )
        ){ backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?:""
            val batchId = backStackEntry.arguments?.getString("batchId")?:""
            TaskDetailsView(taskId, batchId, navController, context)
        }
    }
}