package com.example.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentApp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgentForgeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    viewModel: AgentForgeViewModel,
    onNavigateToWorkstation: () -> Unit
) {
    val allApps by viewModel.allApps.collectAsState()
    val inputPrompt by viewModel.inputPrompt.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    val templates = remember {
        listOf(
            TemplateItem("🧠 Todo List", "Build an elegant, offline-first task tracker with local database features", "Build a minimalist todo list app with a dark cyber slate theme and task status checkboxes."),
            TemplateItem("☁️ Weather Radar", "An aerospace weather analytics widget displaying forecasts", "Build a real-time weather widget with radar forecast, precipitation charts, and severe alerts."),
            TemplateItem("📊 Sci-Calculator", "Scientific calculator containing custom function evaluation", "Create a scientific math calculator with log operations, percentage computing, and memory registers."),
            TemplateItem("🧘 Zen Breathing", "Diaphragmatic loop breath guides and relaxation timer", "Build an immersive zen meditation breathing app with diaphragmatic timers, stats, and audio wave mockups."),
            TemplateItem("📈 Crypto Analytics", "Real-time pricing tracker for top cryptocurrency tokens", "Build an automated cryptocurrency coin analytics tracker showing price lists, market caps, and volatility charts.")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = "Glow Logo",
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AGENTFORGE",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = TextPrimary
                        )
                    }
                },
                actions = {
                    if (allApps.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAllApps() }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteSweep,
                                contentDescription = "Clear History",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Custom Title Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(ObsidianCard, ObsidianBackground),
                            radius = 400f
                        )
                    )
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Orchestrate 40+ AI Agents",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter a single prompt to automatically design, write, test, document, and deploy your complete software app model instantly.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Prompter field
            Text(
                text = "Enter App Blueprint Prompt",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputPrompt,
                onValueChange = { viewModel.updatePrompt(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "e.g., build an automatic plant water tracker with level sensors and a database...",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (inputPrompt.trim().isNotEmpty() && !isGenerating) {
                                viewModel.buildApp(inputPrompt)
                                onNavigateToWorkstation()
                            }
                        },
                        enabled = inputPrompt.trim().isNotEmpty() && !isGenerating
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Build App",
                            tint = if (inputPrompt.trim().isNotEmpty() && !isGenerating) NeonCyan else TextMuted
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onGo = {
                        if (inputPrompt.trim().isNotEmpty() && !isGenerating) {
                            viewModel.buildApp(inputPrompt)
                            onNavigateToWorkstation()
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = ObsidianBorder,
                    focusedContainerColor = ObsidianCard,
                    unfocusedContainerColor = ObsidianCard
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Templates Carousel
            Text(
                text = "Quick Starter Blueprints",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(templates) { template ->
                    Card(
                        modifier = Modifier
                            .width(260.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                if (!isGenerating) {
                                    viewModel.updatePrompt(template.prompt)
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = template.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = template.desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Past Apps / Historical Registry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historical App Registry (${allApps.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (allApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FolderZip,
                            contentDescription = "Empty",
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No apps compiled yet",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter a prompt above to compile your first app",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allApps) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ObsidianSurface)
                                .border(
                                    1.dp,
                                    if (app.status == "Orchestrating") NeonCyan else ObsidianBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.selectApp(app)
                                    onNavigateToWorkstation()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ObsidianCard)
                                        .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when {
                                            app.name.contains("Weather", true) -> Icons.Filled.Cloud
                                            app.name.contains("Calc", true) -> Icons.Filled.Calculate
                                            app.name.contains("Todo", true) || app.name.contains("Task", true) -> Icons.Filled.CheckBox
                                            app.name.contains("Serenity", true) || app.name.contains("Breath", true) -> Icons.Filled.Spa
                                            else -> Icons.Filled.SettingsInputHdmi
                                        },
                                        contentDescription = "App Icon",
                                        tint = if (app.status == "Orchestrating") NeonCyan else LaserBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = app.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Prompt: \"${app.prompt}\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (app.status == "Completed") Color(0x1539FF14) else Color(0x1500FFCC)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = app.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (app.status == "Completed") TechGreen else NeonCyan
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(onClick = { viewModel.deleteApp(app) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete App",
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

data class TemplateItem(val title: String, val desc: String, val prompt: String)
