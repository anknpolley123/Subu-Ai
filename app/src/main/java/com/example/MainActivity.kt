package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

// Data Class for Message Representation
data class ChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val timestamp: Long
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SubuTerminalChatScreen()
            }
        }
    }
}

@Composable
fun SubuTerminalChatScreen() {
    // 1. STATE MANAGEMENT
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Blinking Cursor Timer for Styling Feedback
    var showCursor by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            showCursor = !showCursor
        }
    }

    // Initialize terminal with initial status logs for visual immersion
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(ChatMessage(UUID.randomUUID().toString(), "SYS", "[SYS] BOOTING SECURE MAINFRAME MODULE...", System.currentTimeMillis()))
            delay(150)
            messages.add(ChatMessage(UUID.randomUUID().toString(), "SYS", "[SYS] INTRUSION DETECTION SYSTEMS ONLINE", System.currentTimeMillis()))
            delay(150)
            messages.add(ChatMessage(UUID.randomUUID().toString(), "SYS", ">> SUBU_AI SECURE COM_PORT OPENED. PROMPT READY.", System.currentTimeMillis()))
        }
    }

    // Scroll to latest index whenever a message is added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // 2. REAL-TIME AI MODEL INLINE INITIALIZATION
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = "AIzaSyBLQVnZYcNxNwzHwGYHbFGWBFs4e40Ekjs",
            generationConfig = generationConfig {
                temperature = 0.7f
            },
            systemInstruction = content {
                text("You are SUBU_AI, an elite, hyper-intelligent, and deeply protective AI companion. You must answer any question or request perfectly while maintaining a sleek, confident, terminal-operator tone.")
            }
        )
    }

    // Send Message routine
    fun sendMessage() {
        val prompt = inputText.trim()
        if (prompt.isEmpty()) return

        // Instantly add user message
        val userMsgId = UUID.randomUUID().toString()
        messages.add(ChatMessage(userMsgId, "USER@ROOT", prompt, System.currentTimeMillis()))
        
        // Clear input immediately to prevent double submissions
        inputText = ""

        // Launch async process for connection handshake, terminal simulation logs, and SDK request
        scope.launch {
            // Sequence of terminal status messages with 200ms sequential intervals
            val statusMessages = listOf(
                "[SYS] INITIATING SECURE HANDSHAKE...",
                "[SYS] DECRYPTING SECURE DATA LAYER...",
                "[SYS] BYPASSING KERNEL FIREWALLS...",
                "[SYS] UPLINK STABILIZED. DATA STREAM OPEN."
            )

            for (log in statusMessages) {
                delay(200)
                messages.add(ChatMessage(UUID.randomUUID().toString(), "SYS", log, System.currentTimeMillis()))
            }

            try {
                // Fetch Gemini AI response in background thread
                val response = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(prompt)
                }
                
                val aiReply = response.text ?: "[SYS_ERROR] NO VALUE TRANSMITTED"
                messages.add(ChatMessage(UUID.randomUUID().toString(), "SUBU_AI", aiReply, System.currentTimeMillis()))
            } catch (e: Exception) {
                messages.add(
                    ChatMessage(
                        UUID.randomUUID().toString(),
                        "SYS",
                        "[SYS_FAIL] CORES COLLAPSED: ${e.localizedMessage ?: "SSL_SHAKE_FAILED"}",
                        System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // 3. CYBERPUNK TERMINAL UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Row: display logo and secure links
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "• SUBU TERMINAL",
                    color = Color(0xFF00FF00),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[ SECURE_LINK ]",
                    color = Color(0xFF00FF00),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }

            // Subtitle status monitor line adding highly high-fidelity visual context
            Text(
                text = "PORT_STR: 256.A0F | LOGS: STABLE | LINK_MODE: ACTIVE",
                color = Color(0xFF00FF00).copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Terminal Border Separator
            Text(
                text = "=".repeat(48),
                color = Color(0xFF00FF00).copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp),
                maxLines = 1
            )

            // Chat display column
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    val textToDisplay = when (message.sender) {
                        "USER@ROOT" -> "> USER@ROOT: ${message.text}"
                        "SUBU_AI" -> ">> SUBU_AI: ${message.text}"
                        else -> message.text
                    }
                    val textColor = if (message.sender == "USER@ROOT") {
                        Color(0xFF00FF00)
                    } else if (message.sender == "SUBU_AI") {
                        Color(0xFF00FF00)
                    } else {
                        Color(0xFF00FF00).copy(alpha = 0.7f) // Status messages have minor dimness
                    }
                    Text(
                        text = textToDisplay,
                        color = textColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Input Terminal interface
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF00FF00))
                    .background(Color(0xFF000500))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "> ",
                    color = Color(0xFF00FF00),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "COMMAND PROMPT...",
                            color = Color(0xFF00FF00).copy(alpha = 0.3f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            textStyle = TextStyle(
                                color = Color(0xFF00FF00),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 15.sp
                            ),
                            cursorBrush = SolidColor(Color(0xFF00FF00)),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    sendMessage()
                                }
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_text_field")
                        )

                        if (showCursor) {
                            Text(
                                text = "█",
                                color = Color(0xFF00FF00),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 15.sp,
                                modifier = Modifier.offset(x = (-4).dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { sendMessage() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Command",
                        tint = Color(0xFF00FF00)
                    )
                }
            }
        }
    }
}
