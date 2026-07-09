package com.example.ui.screen

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentApp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgentForgeViewModel
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmulatorScreen(
    viewModel: AgentForgeViewModel,
    onBack: () -> Unit
) {
    val currentApp by viewModel.currentApp.collectAsState()
    val emulatorUi by viewModel.emulatorUi.collectAsState()
    val activeTab by viewModel.emulatorActiveTab.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Local mutable state for custom interaction inside the running prototype!
    // For example, if it's a todo app, we keep track of dynamically added tasks.
    var customTaskList by remember {
        mutableStateOf(mutableListOf<String>())
    }
    var currentTaskInput by remember { mutableStateOf("") }

    // Sync task list on load
    LaunchedEffect(emulatorUi) {
        customTaskList.clear()
        if (emulatorUi != null) {
            try {
                val screens = emulatorUi!!.getJSONArray("screens")
                if (screens.length() > 0) {
                    val firstScreen = screens.getJSONObject(0)
                    val widgets = firstScreen.getJSONArray("widgets")
                    for (i in 0 until widgets.length()) {
                        val widget = widgets.getJSONObject(i)
                        if (widget.getString("type") == "list") {
                            val items = widget.getJSONArray("items")
                            val temp = mutableListOf<String>()
                            for (j in 0 until items.length()) {
                                temp.add(items.getString(j))
                            }
                            customTaskList = temp
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Live App Emulator",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "INTERACTIVE INSTANT PROTOTYPE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TechGreen
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Server Domain Bar
            if (currentApp != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TechGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = "Hosted Server",
                                tint = TechGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentApp!!.hostedUrl,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                        Row {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(currentApp!!.hostedUrl))
                                    Toast.makeText(context, "Hosted link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy Link",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Opening live deployment server node...", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.OpenInNew,
                                    contentDescription = "Open Browser",
                                    tint = TechGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simulated Mobile Phone Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(ObsidianCard)
                    .border(4.dp, ObsidianBorder, RoundedCornerShape(32.dp))
            ) {
                if (emulatorUi != null) {
                    val appTitle = emulatorUi!!.optString("title", "Running App")
                    val screens = emulatorUi!!.optJSONArray("screens") ?: JSONArray()

                    Column(modifier = Modifier.fillMaxSize()) {
                        // Phone Status Bar / Notch
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(ObsidianSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("9:41", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Wifi, "Wifi", tint = TextPrimary, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Filled.Battery5Bar, "Battery", tint = TextPrimary, modifier = Modifier.size(12.dp))
                                }
                            }
                        }

                        // App Container Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianSurface)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = appTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = TextPrimary
                            )
                        }

                        // Dynamic Screen Content Rendering!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(ObsidianBackground)
                        ) {
                            if (activeTab < screens.length()) {
                                val currentScreen = screens.getJSONObject(activeTab)
                                val widgets = currentScreen.optJSONArray("widgets") ?: JSONArray()

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    for (i in 0 until widgets.length()) {
                                        val widget = widgets.getJSONObject(i)
                                        val type = widget.getString("type")
                                        val title = widget.optString("title", "")
                                        val subtitle = widget.optString("subtitle", "")
                                        val action = widget.optString("action", "")

                                        item {
                                            when (type) {
                                                "text" -> {
                                                    Column(modifier = Modifier.fillMaxWidth()) {
                                                        Text(title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                                        if (subtitle.isNotEmpty()) {
                                                            Spacer(modifier = Modifier.height(2.dp))
                                                            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                                        }
                                                    }
                                                }
                                                "button" -> {
                                                    Button(
                                                        onClick = {
                                                            handleWidgetAction(action, context)
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = ButtonDefaults.buttonColors(containerColor = LaserBlue)
                                                    ) {
                                                        Text(title, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                "input" -> {
                                                    Column(modifier = Modifier.fillMaxWidth()) {
                                                        OutlinedTextField(
                                                            value = currentTaskInput,
                                                            onValueChange = { currentTaskInput = it },
                                                            modifier = Modifier.fillMaxWidth(),
                                                            placeholder = { Text(title, color = TextMuted) },
                                                            trailingIcon = {
                                                                IconButton(onClick = {
                                                                    if (currentTaskInput.isNotEmpty()) {
                                                                        customTaskList = (customTaskList + currentTaskInput).toMutableList()
                                                                        currentTaskInput = ""
                                                                        Toast.makeText(context, "Item added locally!", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }) {
                                                                    Icon(Icons.Filled.Add, "Add", tint = NeonCyan)
                                                                }
                                                            },
                                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                                            keyboardActions = KeyboardActions(onDone = {
                                                                if (currentTaskInput.isNotEmpty()) {
                                                                    customTaskList = (customTaskList + currentTaskInput).toMutableList()
                                                                    currentTaskInput = ""
                                                                    Toast.makeText(context, "Item added locally!", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }),
                                                            colors = OutlinedTextFieldDefaults.colors(
                                                                focusedTextColor = TextPrimary,
                                                                unfocusedTextColor = TextPrimary,
                                                                focusedBorderColor = NeonCyan,
                                                                unfocusedBorderColor = ObsidianBorder,
                                                                focusedContainerColor = ObsidianCard,
                                                                unfocusedContainerColor = ObsidianCard
                                                            )
                                                        )
                                                    }
                                                }
                                                "list" -> {
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                                        border = BorderStroke(1.dp, ObsidianBorder)
                                                    ) {
                                                        Column(modifier = Modifier.padding(12.dp)) {
                                                            if (title.isNotEmpty()) {
                                                                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                                                Spacer(modifier = Modifier.height(8.dp))
                                                            }
                                                            customTaskList.forEachIndexed { idx, item ->
                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .clickable {
                                                                            if (action == "toggle") {
                                                                                // Toggle task completed or delete!
                                                                                customTaskList = customTaskList.filterIndexed { index, _ -> index != idx }.toMutableList()
                                                                                Toast.makeText(context, "Task checked off successfully!", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                        }
                                                                        .padding(vertical = 8.dp),
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Icon(
                                                                        imageVector = if (action == "toggle") Icons.Outlined.CheckCircle else Icons.Filled.Circle,
                                                                        contentDescription = "Bullet",
                                                                        tint = NeonCyan,
                                                                        modifier = Modifier.size(16.dp)
                                                                    )
                                                                    Spacer(modifier = Modifier.width(8.dp))
                                                                    Text(item, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                                                                }
                                                                if (idx < customTaskList.size - 1) {
                                                                    HorizontalDivider(color = ObsidianBorder)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                "card" -> {
                                                    Card(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable { handleWidgetAction(action, context) },
                                                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                                        border = BorderStroke(1.dp, ObsidianBorder)
                                                    ) {
                                                        Column(modifier = Modifier.padding(14.dp)) {
                                                            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                                            if (subtitle.isNotEmpty()) {
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                                            }
                                                        }
                                                    }
                                                }
                                                "chart" -> {
                                                    val rawData = widget.getJSONArray("chartData")
                                                    val dataPoints = remember(rawData) {
                                                        val points = mutableListOf<Float>()
                                                        for (k in 0 until rawData.length()) {
                                                            points.add(rawData.getDouble(k).toFloat())
                                                        }
                                                        points
                                                    }

                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                                        border = BorderStroke(1.dp, ObsidianBorder)
                                                    ) {
                                                        Column(modifier = Modifier.padding(14.dp)) {
                                                            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                                            if (subtitle.isNotEmpty()) {
                                                                Spacer(modifier = Modifier.height(2.dp))
                                                                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                                            }
                                                            Spacer(modifier = Modifier.height(12.dp))
                                                            
                                                            // Beautiful Canvas Drawing representing the simulated chart!
                                                            Canvas(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .height(100.dp)
                                                            ) {
                                                                if (dataPoints.isNotEmpty()) {
                                                                    val width = size.width
                                                                    val height = size.height
                                                                    val maxVal = dataPoints.maxOrNull() ?: 1f
                                                                    val stepX = width / (dataPoints.size - 1)
                                                                    
                                                                    val path = Path()
                                                                    dataPoints.forEachIndexed { index, valPoint ->
                                                                        val x = index * stepX
                                                                        val y = height - (valPoint / maxVal * height * 0.8f)
                                                                        if (index == 0) {
                                                                            path.moveTo(x, y)
                                                                        } else {
                                                                            path.lineTo(x, y)
                                                                        }
                                                                        
                                                                        // Draw value circles
                                                                        drawCircle(
                                                                            color = Color(0xFF00FFCC),
                                                                            radius = 4.dp.toPx(),
                                                                            center = Offset(x, y)
                                                                        )
                                                                    }
                                                                    
                                                                    drawPath(
                                                                        path = path,
                                                                        color = Color(0xFF0099FF),
                                                                        style = Stroke(width = 2.dp.toPx())
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

                        // Bottom Navigation Tabs
                        if (screens.length() > 1) {
                            NavigationBar(
                                containerColor = ObsidianSurface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                for (index in 0 until screens.length()) {
                                    val screenObj = screens.getJSONObject(index)
                                    val tabName = screenObj.getString("tabName")
                                    val iconName = screenObj.getString("icon")

                                    NavigationBarItem(
                                        selected = activeTab == index,
                                        onClick = { viewModel.setEmulatorTab(index) },
                                        label = { Text(tabName, fontSize = 10.sp) },
                                        icon = {
                                            Icon(
                                                imageVector = when (iconName) {
                                                    "settings" -> Icons.Filled.Settings
                                                    "list" -> Icons.Filled.List
                                                    "chart" -> Icons.Filled.ShowChart
                                                    "info" -> Icons.Filled.Info
                                                    else -> Icons.Filled.Home
                                                },
                                                contentDescription = tabName
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = NeonCyan,
                                            unselectedIconColor = TextSecondary,
                                            selectedTextColor = NeonCyan,
                                            unselectedTextColor = TextSecondary,
                                            indicatorColor = ObsidianCard
                                        )
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No Emulator State", color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Complete the cognitive swarm build loop first.", color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

private fun handleWidgetAction(action: String, context: android.content.Context) {
    if (action.startsWith("toast:")) {
        val msg = action.substringAfter("toast:")
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    } else if (action == "toggle") {
        // Handled directly inside lists
    } else {
        Toast.makeText(context, "Command Dispatched: $action", Toast.LENGTH_SHORT).show()
    }
}
