package com.example.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgentForgeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkstationScreen(
    viewModel: AgentForgeViewModel,
    onNavigateToWorkspace: () -> Unit,
    onNavigateToEmulator: () -> Unit,
    onBack: () -> Unit
) {
    val currentApp by viewModel.currentApp.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val activeAgent by viewModel.activeAgent.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val terminalLogs by viewModel.terminalLogs.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val terminalListState = rememberLazyListState()

    // Auto-scroll the terminal to the bottom as new logs stream in
    LaunchedEffect(terminalLogs.size) {
        if (terminalLogs.isNotEmpty()) {
            coroutineScope.launch {
                terminalListState.animateScrollToItem(terminalLogs.size - 1)
            }
        }
    }

    // High-tech pulse animation for active agent
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val activeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "activeAlpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentApp?.name ?: "Orchestration Deck",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = if (isGenerating) "AUTOMATED MULTI-AGENT SWARM IN PROGRESS" else "COGNITIVE CYCLE IDLE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = if (isGenerating) NeonCyan else TechGreen
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Navigation shortcuts once app is completed
                    if (!isGenerating && currentApp != null) {
                        Button(
                            onClick = onNavigateToWorkspace,
                            colors = ButtonDefaults.buttonColors(containerColor = ObsidianCard),
                            modifier = Modifier
                                .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Code,
                                contentDescription = "View Code",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Code", fontSize = 12.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onNavigateToEmulator,
                            colors = ButtonDefaults.buttonColors(containerColor = ObsidianCard),
                            modifier = Modifier
                                .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Run App",
                                tint = TechGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run App", fontSize = 12.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Live progress monitor
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isGenerating) "Orchestrating 40+ Agents" else "Deployment Status",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonCyan,
                        trackColor = ObsidianBorder
                    )

                    if (isGenerating && activeAgent != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(activeAgent!!.dept.colorHex))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACTIVE: ${activeAgent!!.displayName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(activeAgent!!.dept.colorHex)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The Command Grid showing status of all 8 major departments!
            Text(
                text = "SWARM COGNITIVE ENGINE MAP",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            DepartmentGrid(
                activeAgent = activeAgent,
                isGenerating = isGenerating,
                activeAlpha = activeAlpha
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live Monospace Terminal Screen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE SYSTEM COMPILATION LOGS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextSecondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isGenerating) NeonCyan else TechGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGenerating) "STREAMING" else "IDLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGenerating) NeonCyan else TechGreen
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TerminalDark)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                LazyColumn(
                    state = terminalListState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(terminalLogs) { log ->
                        Column(modifier = Modifier.padding(vertical = 3.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                val timestampText = remember(log.timestamp) {
                                    SimpleDateFormat("HH:mm:ss", Locale.ROOT).format(Date(log.timestamp))
                                }
                                Text(
                                    text = "[$timestampText] ",
                                    color = TextMuted,
                                    style = MonospaceTerminalStyle
                                )
                                Text(
                                    text = "[${log.agentName}] ",
                                    color = when (log.deptDisplayName) {
                                        "Core Management" -> Color(0xFF00FFCC)
                                        "Data & Analytics" -> Color(0xFF88CCFF)
                                        "Machine Learning" -> Color(0xFFFF99FF)
                                        "Software Engineering" -> Color(0xFFFFFF99)
                                        "Quality Assurance" -> Color(0xFFFF9999)
                                        "DevOps & Infra" -> Color(0xFF99FF99)
                                        "Technical Docs" -> Color(0xFFE0E0E0)
                                        "User Relations" -> Color(0xFFFFCC80)
                                        else -> TextPrimary
                                    },
                                    style = MonospaceTerminalStyle,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            when (log.severity) {
                                                "SUCCESS" -> Color(0x1539FF14)
                                                "WARN" -> Color(0x15FFA500)
                                                "ERROR" -> Color(0x15FF5252)
                                                else -> Color(0x1500FFCC)
                                            }
                                        )
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = log.severity,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = when (log.severity) {
                                            "SUCCESS" -> TechGreen
                                            "WARN" -> Color(0xFFFFA500)
                                            "ERROR" -> Color(0xFFFF5252)
                                            else -> NeonCyan
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = log.msg,
                                color = TextPrimary,
                                style = MonospaceTerminalStyle,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DepartmentGrid(
    activeAgent: AgentType?,
    isGenerating: Boolean,
    activeAlpha: Float
) {
    val depts = remember { Department.values() }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Render departments grouped in rows of 2 (Grid of 4x2)
        depts.toList().chunked(2).forEach { rowDepts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowDepts.forEach { dept ->
                    val isActiveDept = activeAgent?.dept == dept
                    val borderGlow = if (isGenerating && isActiveDept) {
                        Color(dept.colorHex).copy(alpha = activeAlpha)
                    } else {
                        ObsidianBorder
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianSurface)
                            .border(1.dp, borderGlow, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isActiveDept) Color(dept.colorHex).copy(alpha = 0.15f) else ObsidianCard
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(dept.icon, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = dept.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActiveDept) Color(dept.colorHex) else TextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (isActiveDept) "Orchestrating..." else "Idle",
                                    fontSize = 9.sp,
                                    color = if (isActiveDept) TechGreen else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
