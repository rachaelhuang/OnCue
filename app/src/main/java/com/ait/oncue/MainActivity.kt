package com.ait.oncue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ait.oncue.data.OnCueRepository
import com.ait.oncue.ui.auth.LoginScreen
import com.ait.oncue.ui.auth.SignUpScreen
import com.ait.oncue.ui.feed.FeedScreen
import com.ait.oncue.ui.feed.FeedViewModel
import com.ait.oncue.ui.feed.PromptScreen
import com.ait.oncue.ui.feed.SubmitScreen
import com.ait.oncue.ui.feed.TakePhotoScreen
import com.ait.oncue.ui.feed.UploadImageScreen
import com.ait.oncue.ui.profile.ProfileScreen
import com.ait.oncue.ui.theme.OnCueTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnCueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OnCueApp()
                }
            }
        }
    }
}

@Composable
fun OnCueApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Auth
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onLoginSuccess = {
                    navController.navigate("prompt") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate("prompt") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // Main App
        composable("prompt") {
            PromptScreen(
                onNavigateToSubmit = { promptId, type ->
                    when (type) {
                        "WRITTEN" -> navController.navigate("submitWritten/$promptId")
                        "UPLOAD" -> navController.navigate("submitUpload/$promptId")
                        "SNAPSHOT" -> navController.navigate("submitSnapshot/$promptId")
                    }
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        // Written prompt
        composable("submitWritten/{promptId}") { backStackEntry ->
            val promptId = backStackEntry.arguments?.getString("promptId") ?: ""
            SubmitScreen(
                promptId = promptId,
                onNavigateToFeed = {
                    navController.navigate("feed/$promptId") {
                        popUpTo("prompt") { inclusive = false }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Upload prompt — opens gallery
        composable("submitUpload/{promptId}") { backStackEntry ->
            val promptId = backStackEntry.arguments?.getString("promptId") ?: ""
            val promptType = backStackEntry.arguments?.getString("promptType") ?: ""

            UploadImageScreen(
                promptId = promptId,
                promptType = promptType,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFeed = {
                    navController.navigate("feed/$promptId") {
                        popUpTo("prompt") { inclusive = false }
                    }
                }
            )
        }

        // Snapshot prompt — opens camera
        composable("submitSnapshot/{promptId}") { backStackEntry ->
            val promptId = backStackEntry.arguments?.getString("promptId") ?: ""
            val promptType = backStackEntry.arguments?.getString("promptType") ?: ""

            TakePhotoScreen(
                promptId = promptId,
                promptType = promptType,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFeed = {
                    navController.navigate("feed/$promptId") {
                        popUpTo("prompt") { inclusive = false }
                    }
                }
            )
        }

        composable("submit/{promptId}") { backStackEntry ->
            val promptId = backStackEntry.arguments?.getString("promptId") ?: ""
            SubmitScreen(
                promptId = promptId,
                onNavigateToFeed = {
                    navController.navigate("feed/$promptId") {
                        popUpTo("prompt") { inclusive = false }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Feed screen
        composable("feed/{promptId}") { backStackEntry ->
            val promptId = backStackEntry.arguments?.getString("promptId") ?: ""
            val viewModel = hiltViewModel<FeedViewModel>()
            FeedScreen(
                promptId = promptId,
                viewModel = viewModel,
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}