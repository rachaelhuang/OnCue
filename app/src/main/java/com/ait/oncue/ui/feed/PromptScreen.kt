package com.ait.oncue.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ait.oncue.data.DailyPrompt
import com.ait.oncue.ui.theme.OnCueGradient
import com.ait.oncue.ui.theme.OnCueGradientColors
import com.ait.oncue.ui.theme.OnCueTextGray
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PromptScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onNavigateToSubmit: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val prompt = viewModel.prompt

    LaunchedEffect(Unit) {
        viewModel.loadPromptForToday()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
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
                .background(Color(0xFF1A1A1A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween // Spread items vertically
            ) {
                // ---------- TOP ----------
                Column {
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

                // ---------- MIDDLE ----------
                if (prompt != null) {
                    PromptCard(
                        prompt = prompt,
                        onSubmitClick = { onNavigateToSubmit(prompt.id) }
                    )
                } else {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                // ---------- BOTTOM ----------
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Roll Again Button
                    OutlinedButton(
                        onClick = { /* TODO: roll again */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "🎲 Roll again (2 left)",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Let's Go Button
                    Button(
                        onClick = { if (prompt != null) onNavigateToSubmit(prompt.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
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
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "29 friends are waiting to see what you create",
                        color = OnCueTextGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ---------- PROMPT CARD ----------
val DarkGold = Color(0xFFC9A86A)
@Composable
fun PromptCard(
    prompt: DailyPrompt,
    onSubmitClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D0D0D)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Badge
            Text(
                text = "✏️ WRITTEN PROMPT",
                color = DarkGold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Prompt Text
            Text(
                text = prompt.text,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Big or small, whatever comes to mind",
                color = OnCueTextGray,
                fontSize = 14.sp
            )
        }
    }
}

// ---------- NAV BAR ----------
@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF1A1A1A),
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OnCueGradientColors.first(),
                selectedTextColor = OnCueGradientColors.first(),
                unselectedIconColor = OnCueTextGray,
                unselectedTextColor = OnCueTextGray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedTab == 1,
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OnCueGradientColors.first(),
                selectedTextColor = OnCueGradientColors.first(),
                unselectedIconColor = OnCueTextGray,
                unselectedTextColor = OnCueTextGray,
                indicatorColor = Color.Transparent
            )
        )
    }
}