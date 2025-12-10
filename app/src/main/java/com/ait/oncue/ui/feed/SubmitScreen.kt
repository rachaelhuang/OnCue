package com.ait.oncue.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ait.oncue.SubmitViewModel
import com.ait.oncue.ui.theme.OnCueGradient
import com.ait.oncue.ui.theme.OnCueGradientColors
import com.ait.oncue.ui.theme.OnCueTextGray
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitScreen(
    promptId: String,
    viewModel: SubmitViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel(),
    onNavigateToFeed: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val prompt = feedViewModel.prompt
    val maxChars = 280
    val charsLeft = maxChars - textInput.length

    // Show loading/success states
    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            delay(1500) // Show "You're in!" message
            onNavigateToFeed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF1A1A1A))
        ) {
            if (viewModel.loading) {
                LoadingState()
            } else if (viewModel.success) {
                SuccessState()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Characters left
                    Text(
                        text = "$charsLeft characters left",
                        color = OnCueTextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Badge
                    Text(
                        text = "CREATIVE PROMPT",
                        color = OnCueTextGray,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prompt
                    Text(
                        text = prompt?.text ?: "What's something you'd be famous for?",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Text Input
                    TextField(
                        value = textInput,
                        onValueChange = { if (it.length <= maxChars) textInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(
                                width = 1.dp,
                                color = OnCueTextGray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        placeholder = {
                            Text(
                                text = "Type your answer...",
                                color = OnCueTextGray.copy(alpha = 0.5f)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,

                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,

                            cursorColor = Color.White,

                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Helper text
                    Text(
                        text = "Be creative, honest, or funny — whatever feels right to you.",
                        color = OnCueTextGray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Submit Button
                    Button(
                        onClick = {
                            viewModel.submit(
                                promptId = promptId,
                                promptType = prompt?.type ?: "WRITTEN",
                                text = textInput,
                                uri = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = textInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (textInput.isNotBlank()) {
                                        Modifier.background(
                                            brush = OnCueGradient,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    } else {
                                        Modifier.background(
                                            color = Color(0xFF3A3A3A),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                )
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Submit 🚀",
                                color = if (textInput.isNotBlank()) Color.White
                                else OnCueTextGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = OnCueGradientColors.first()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Submitting...",
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
fun SuccessState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Checkmark circle
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    OnCueGradientColors.first().copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .border(
                    width = 2.dp,
                    brush = OnCueGradient,
                    shape = androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                fontSize = 40.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "You're in!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Loading what your friends posted...",
            color = OnCueTextGray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Animated dots
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            OnCueTextGray,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
    }
}