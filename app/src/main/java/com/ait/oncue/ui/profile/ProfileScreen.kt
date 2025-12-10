package com.ait.oncue.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ait.oncue.data.OnCueUser
import com.ait.oncue.data.Post
import com.ait.oncue.ui.theme.OnCueGradient
import com.ait.oncue.ui.theme.OnCueGradientColors
import com.ait.oncue.ui.theme.OnCueTextGray
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val user = viewModel.user
    val posts = viewModel.userPosts
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Activity, 1 = Prompts

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
        viewModel.loadUserHistory()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onNavigateBack,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnCueGradientColors.first(),
                        unselectedIconColor = OnCueTextGray,
                        indicatorColor = Color.Transparent
                    )
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnCueGradientColors.first(),
                        selectedTextColor = OnCueGradientColors.first(),
                        unselectedIconColor = OnCueTextGray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
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
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Good to see you, ${user?.username ?: "User"}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date()),
                            color = OnCueTextGray,
                            fontSize = 14.sp
                        )
                    }

                    // Profile Picture
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Color(0xFFFF7A3D), Color(0xFFFF4B91))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.username?.firstOrNull()?.uppercase() ?: "?",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = OnCueGradientColors.first()
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Activity",
                                color = if (selectedTab == 0) Color.White else OnCueTextGray
                            )
                        }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Prompts",
                                color = if (selectedTab == 1) Color.White else OnCueTextGray
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Content
                when (selectedTab) {
                    0 -> ActivityTab(user = user)
                    1 -> PromptsTab(posts = posts)
                }
            }
        }
    }
}

@Composable
fun ActivityTab(user: OnCueUser?) {
    Column {
        // Streak Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current Streak
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${user?.currentStreak ?: 0}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "day streak",
                        color = OnCueTextGray,
                        fontSize = 14.sp
                    )
                }
            }

            // Best Streak
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "5",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "best streak",
                        color = OnCueTextGray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Calendar
        CalendarView()

        Spacer(modifier = Modifier.height(24.dp))

        // Friends added
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "12 friends added to OnCue",
                color = Color.White,
                fontSize = 16.sp
            )

            TextButton(onClick = { /* TODO: See all */ }) {
                Text(
                    text = "See all →",
                    color = OnCueGradientColors.first()
                )
            }
        }
    }
}

@Composable
fun CalendarView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "December 2025",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "6/30 days",
                    color = OnCueTextGray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar grid (simplified)
            val activeDays = listOf(1, 2, 3, 5, 7) // Days with posts
            val today = 8

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(30) { day ->
                    val dayNumber = day + 1
                    val isActive = dayNumber in activeDays
                    val isToday = dayNumber == today

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isToday -> OnCueGradientColors.first()
                                    isActive -> OnCueGradientColors.first().copy(alpha = 0.3f)
                                    else -> Color(0xFF3A3A3A)
                                }
                            )
                            .then(
                                if (isToday) Modifier.border(
                                    2.dp,
                                    OnCueGradientColors.first(),
                                    CircleShape
                                ) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNumber.toString(),
                            color = if (isActive || isToday) Color.White else OnCueTextGray,
                            fontSize = 14.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PromptsTab(posts: List<Post>) {
    var showPinned by remember { mutableStateOf(true) }

    Column {
        // Filter buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = showPinned,
                onClick = { showPinned = true },
                label = { Text("🏅 Pinned") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF3A3A3A),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF2A2A2A),
                    labelColor = OnCueTextGray
                )
            )

            FilterChip(
                selected = !showPinned,
                onClick = { showPinned = false },
                label = { Text("All responses") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF3A3A3A),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF2A2A2A),
                    labelColor = OnCueTextGray
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Posts Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts.take(6)) { post ->
                PromptHistoryCard(post = post)
            }
        }
    }
}

@Composable
fun PromptHistoryCard(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A3A3A)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Pin icon if pinned
            Icon(
                Icons.Default.Star,
                contentDescription = "Pinned",
                tint = OnCueGradientColors.first(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )

            // Post preview
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.textContent?.take(60) ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 3
                )

                Column {
                    Text(
                        text = "✨ Top",
                        color = OnCueGradientColors.first(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = SimpleDateFormat("MMM d", Locale.getDefault())
                            .format(post.timestamp),
                        color = OnCueTextGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}