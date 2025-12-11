package com.ait.oncue.ui.feed

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ait.oncue.data.DailyPrompt
import com.ait.oncue.ui.components.OnCueBottomNavigationBar
import com.ait.oncue.ui.theme.GrayGradient
import com.ait.oncue.ui.theme.OnCueBackground
import com.ait.oncue.ui.theme.OnCueGradient
import com.ait.oncue.ui.theme.OnCueGradientColors
import com.ait.oncue.ui.theme.OnCueLightGray
import com.ait.oncue.ui.theme.OnCueTextGray
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PromptScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onNavigateToSubmit: (String, String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val prompt = viewModel.prompt

    LaunchedEffect(Unit) {
        viewModel.loadPromptForToday()
    }

    // Debug logging
    LaunchedEffect(prompt) {
        Log.d("PromptScreen", "Current prompt: id=${prompt?.id}, type=${prompt?.type}")
    }

    Scaffold(
        bottomBar = {
            OnCueBottomNavigationBar(
                selectedTab = 0,
                onHomeClick = { },
                onProfileClick = onNavigateToProfile
            )
        }

    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(OnCueBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            ) {
                // Header
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Today's prompts",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                        color = OnCueTextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Prompt card
                if (prompt != null) {
                    PromptCard(prompt = prompt)
                } else {
                    CircularProgressIndicator(color = Color.White)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Buttons outside the card
                if (prompt != null) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        if (viewModel.rollsLeft > 0) {
                            OutlinedButton(
                                onClick = { viewModel.rollPrompt() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "🎲 Roll again (${viewModel.rollsLeft} left)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 16.sp,
                                    )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Button(
                            onClick = {
                                onNavigateToSubmit(prompt.id, prompt.type)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OnCueGradient, RoundedCornerShape(12.dp))
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Let's go",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 16.sp,
                                )
                            }
                        }
                        // Padding above bottom nav bar
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PromptCard(
    prompt: DailyPrompt
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(GrayGradient, shape = RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.075f), // faint white
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            val badgeText = when (prompt.type) {
                "WRITTEN" -> "✏️ WRITTEN PROMPT"
                "UPLOAD" -> "📸 UPLOAD PROMPT"
                "SNAPSHOT" -> "🤳 SNAPSHOT PROMPT"
                else -> "✨ PROMPT"
            }

            Text(
                text = badgeText,
                color = OnCueTextGray,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = prompt.text,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = prompt.subtext.ifEmpty { "Whatever feels right to you" },
                color = OnCueTextGray,
                fontSize = 14.sp
            )
        }
    }
}