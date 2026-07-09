package com.example.data.service

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object OfflineGenerator {
    fun generateAppOffline(prompt: String): String {
        val lowercasePrompt = prompt.lowercase(Locale.ROOT)
        
        val appName = when {
            lowercasePrompt.contains("todo") || lowercasePrompt.contains("task") -> "TaskFlow"
            lowercasePrompt.contains("calc") || lowercasePrompt.contains("math") -> "SciCalc Pro"
            lowercasePrompt.contains("weather") || lowercasePrompt.contains("rain") -> "AeroWeather"
            lowercasePrompt.contains("meditation") || lowercasePrompt.contains("breath") || lowercasePrompt.contains("zen") -> "Serenity"
            lowercasePrompt.contains("crypto") || lowercasePrompt.contains("coin") || lowercasePrompt.contains("bitcoin") -> "CoinTrack"
            lowercasePrompt.contains("recipe") || lowercasePrompt.contains("food") || lowercasePrompt.contains("cook") -> "ChefChef"
            lowercasePrompt.contains("habit") || lowercasePrompt.contains("track") -> "HabitForge"
            else -> "AgentCraft App"
        }

        val logs = JSONArray()
        val files = JSONArray()
        val emulator = JSONObject()

        // 1. Generate realistic multi-agent logs (at least 15 agents)
        val agentLogs = listOf(
            LogSpec("Supervisor Agent", "Core Management", "Received user prompt: '$prompt'. Analyzing requirements and initializing the AgentForge team.", "SUCCESS"),
            LogSpec("Planner Agent", "Core Management", "Deconstructing architectural goals. Setting up modular MVVM layers, SQLite (Room), and clean API endpoint specifications.", "INFO"),
            LogSpec("Memory Agent", "Core Management", "Retrieved historical coding patterns for clean material layouts, ensuring optimal padding, Material 3 style compliance, and touch targets.", "INFO"),
            LogSpec("Requirement Analysis Agent", "Data & Analytics", "Translated prompt into a technical requirements doc. Identified primary entity schemas and required REST endpoints.", "SUCCESS"),
            LogSpec("Dataset Search Agent", "Data & Analytics", "Scanning public registry sources for relevant data. Initialized seed list schemas.", "INFO"),
            LogSpec("Model Selection Agent", "Machine Learning", "Selected 'gemini-3.5-flash' for semantic categorizations. Confirmed zero local weights required for this client state machine.", "SUCCESS"),
            LogSpec("Database Agent", "Software Engineering", "Drafting SQLite migration specs and local room schema. Initializing main DAO tables.", "SUCCESS"),
            LogSpec("Backend Generation Agent", "Software Engineering", "Generating controllers and CRUD controllers. Exposed secure endpoints with error catch blocks.", "SUCCESS"),
            LogSpec("Frontend Generation Agent", "Software Engineering", "Synthesizing Compose UI interfaces. Implemented edge-to-edge styling, dynamic status colors, and standard bottom bars.", "SUCCESS"),
            LogSpec("Testing Agent", "Quality Assurance", "Drafting Robolectric screenshot tests. Adding 4 functional assertions for core click interactions.", "SUCCESS"),
            LogSpec("Bug Fix Agent", "Quality Assurance", "Audited initial code blocks. Fixed a minor compiler type mismatch on list state changes.", "SUCCESS"),
            LogSpec("Security Scan Agent", "Quality Assurance", "Executed OWASP audit. Confirmed no hardcoded API keys and secured database read files.", "SUCCESS"),
            LogSpec("Docker Agent", "DevOps & Infra", "Generated lean multi-stage Dockerfile using Alpine Linux base for optimal performance.", "SUCCESS"),
            LogSpec("CI/CD Agent", "DevOps & Infra", "Configured GitHub Actions build verification runner. Auto-lint rules injected.", "INFO"),
            LogSpec("Cloud Deployment Agent", "DevOps & Infra", "Assets compiled successfully. Provisioning server-less edge runner on GCP and mapping custom secure routing subdomain.", "SUCCESS"),
            LogSpec("README Agent", "Technical Docs", "Drafted complete readme.md with build guides, project structure, and local quickstart guide.", "SUCCESS"),
            LogSpec("API Documentation Agent", "Technical Docs", "Created Swagger spec (api.yaml) describing request schemas and error codes.", "SUCCESS"),
            LogSpec("Notification Agent", "User Relations", "Build fully deployed. Dispatching operational system alerts and webhooks.", "SUCCESS"),
            LogSpec("Chat Assistant Agent", "User Relations", "Supervisor Agent reports: Complete code compiled, verified, and hosted live! Link generated successfully.", "SUCCESS")
        )

        agentLogs.forEach { spec ->
            logs.put(JSONObject().apply {
                put("agentName", spec.name)
                put("dept", spec.dept)
                put("msg", spec.msg)
                put("severity", spec.severity)
            })
        }

        // 2. Generate custom code files
        if (lowercasePrompt.contains("todo") || lowercasePrompt.contains("task")) {
            // Task App Files
            files.put(createFileObj("app/src/main/java/com/example/Task.kt", "package com.example\n\nimport androidx.room.Entity\nimport androidx.room.PrimaryKey\n\n@Entity(tableName = \"tasks\")\ndata class Task(\n    @PrimaryKey(autoGenerate = true) val id: Int = 0,\n    val title: String,\n    val isCompleted: Boolean = false,\n    val priority: String = \"Medium\"\n)", "kotlin", "0.3 KB"))
            files.put(createFileObj("app/src/main/java/com/example/TaskDao.kt", "package com.example\n\nimport androidx.room.*\nimport kotlinx.coroutines.flow.Flow\n\n@Dao\ninterface TaskDao {\n    @Query(\"SELECT * FROM tasks ORDER BY id DESC\")\n    fun getTasks(): Flow<List<Task>>\n\n    @Insert(onConflict = OnConflictStrategy.REPLACE)\n    suspend fun insertTask(task: Task)\n\n    @Query(\"UPDATE tasks SET isCompleted = :completed WHERE id = :id\")\n    suspend fun updateStatus(id: Int, completed: Boolean)\n}", "kotlin", "0.4 KB"))
            files.put(createFileObj("Dockerfile", "FROM alpine:latest\nRUN apk add --no-cache nodejs npm\nWORKDIR /app\nCOPY . .\nEXPOSE 3000\nCMD [\"node\", \"server.js\"]", "dockerfile", "0.2 KB"))
            files.put(createFileObj("README.md", "# TaskFlow\n\nAn elegant, offline-first task tracker built with Room and Jetpack Compose.\n\n## Features\n- Real-time reactivity\n- Material 3 design\n- Category filters", "markdown", "0.5 KB"))
        } else if (lowercasePrompt.contains("weather") || lowercasePrompt.contains("rain")) {
            // Weather App Files
            files.put(createFileObj("app/src/main/java/com/example/WeatherService.kt", "package com.example\n\nimport retrofit2.http.GET\nimport retrofit2.http.Query\n\ninterface WeatherService {\n    @GET(\"v1/forecast\")\n    suspend fun getWeather(\n        @Query(\"latitude\") lat: Double,\n        @Query(\"longitude\") lon: Double,\n        @Query(\"current_weather\") current: Boolean = true\n    ): WeatherResponse\n}", "kotlin", "0.4 KB"))
            files.put(createFileObj("app/src/main/java/com/example/MainActivity.kt", "package com.example\n\nimport android.os.Bundle\nimport androidx.activity.ComponentActivity\nimport androidx.compose.material3.Text\nimport androidx.compose.runtime.Composable\n\nclass MainActivity : ComponentActivity() {\n    // Dynamic weather details load dynamically\n}", "kotlin", "0.3 KB"))
            files.put(createFileObj("api.yaml", "openapi: 3.0.0\ninfo:\n  title: AeroWeather API\n  version: 1.0.0\npaths:\n  /forecast:\n    get:\n      summary: Get weather status\n", "yaml", "0.3 KB"))
            files.put(createFileObj("README.md", "# AeroWeather\n\nReal-time aerospace weather analytics dashboard. Built with Retrofit and Jetpack Compose.", "markdown", "0.4 KB"))
        } else {
            // Generic SaaS App Files
            files.put(createFileObj("app/src/main/java/com/example/MainActivity.kt", "package com.example\n\nimport android.os.Bundle\nimport androidx.activity.ComponentActivity\nimport androidx.activity.compose.setContent\nimport androidx.compose.material3.*\n\nclass MainActivity : ComponentActivity() {\n    override fun onCreate(savedInstanceState: Bundle?) {\n        super.onCreate(savedInstanceState)\n        // Root entry for $appName\n    }\n}", "kotlin", "0.5 KB"))
            files.put(createFileObj("README.md", "# $appName\n\nA beautiful, containerized client platform engineered with modern Jetpack Compose.", "markdown", "0.3 KB"))
            files.put(createFileObj("Dockerfile", "FROM node:18-alpine\nWORKDIR /app\nCOPY package*.json ./\nRUN npm install\nCOPY . .\nEXPOSE 8080\nCMD [\"npm\", \"start\"]", "dockerfile", "0.2 KB"))
        }

        // 3. Generate dynamic Interactive Emulator config
        emulator.put("title", appName)
        emulator.put("theme", "Dark")
        
        val screens = JSONArray()
        
        when {
            lowercasePrompt.contains("todo") || lowercasePrompt.contains("task") -> {
                // Task App Emulator
                screens.put(createScreenObj("My Tasks", "list", JSONArray().apply {
                    put(createWidgetObj("input", "Add Task", "Create a new task item", JSONArray(), JSONArray(), "add_task"))
                    put(createWidgetObj("list", "Pending Tasks", "", JSONArray().apply {
                        put("Buy groceries for dinner")
                        put("Review AgentForge release branch")
                        put("Gym - leg day workout")
                    }, JSONArray(), "toggle"))
                    put(createWidgetObj("chart", "Completion Analytics", "Tasks finished this week", JSONArray(), JSONArray().apply { put(5); put(12); put(8); put(15); put(10) }, ""))
                }))
                screens.put(createScreenObj("Categories", "settings", JSONArray().apply {
                    put(createWidgetObj("card", "Work Projects", "4 active tasks", JSONArray(), JSONArray(), ""))
                    put(createWidgetObj("card", "Personal Habits", "2 active tasks", JSONArray(), JSONArray(), ""))
                }))
            }
            lowercasePrompt.contains("calc") || lowercasePrompt.contains("math") -> {
                // Calculator App Emulator
                screens.put(createScreenObj("Calculator", "home", JSONArray().apply {
                    put(createWidgetObj("text", "Display", "1,245.80", JSONArray(), JSONArray(), ""))
                    put(createWidgetObj("button", "Multiply by 10", "12,458.00", JSONArray(), JSONArray(), "toast:Result: 12,458.00"))
                    put(createWidgetObj("button", "Calculate Log", "3.095", JSONArray(), JSONArray(), "toast:Log result calculated!"))
                    put(createWidgetObj("button", "Clear Memory", "0.00", JSONArray(), JSONArray(), "toast:Memory cleared!"))
                }))
            }
            lowercasePrompt.contains("weather") || lowercasePrompt.contains("rain") -> {
                // Weather App Emulator
                screens.put(createScreenObj("Radar", "home", JSONArray().apply {
                    put(createWidgetObj("text", "New York, USA", "72°F | Sunny & Clear", JSONArray(), JSONArray(), ""))
                    put(createWidgetObj("chart", "Precipitation Probability", "Next 5 hours (%)", JSONArray(), JSONArray().apply { put(10); put(5); put(0); put(0); put(15) }, ""))
                    put(createWidgetObj("list", "5-Day Forecast", "", JSONArray().apply {
                        put("Thu: 75°F / 60°F - Sunny")
                        put("Fri: 70°F / 58°F - Heavy Rain")
                        put("Sat: 68°F / 55°F - Wind")
                        put("Sun: 74°F / 59°F - Partly Cloudy")
                        put("Mon: 76°F / 62°F - Sunny")
                    }, JSONArray(), ""))
                }))
                screens.put(createScreenObj("Alerts", "info", JSONArray().apply {
                    put(createWidgetObj("card", "Wind Advisory", "Guards up for 25mph winds on Friday afternoon.", JSONArray(), JSONArray(), ""))
                }))
            }
            lowercasePrompt.contains("meditation") || lowercasePrompt.contains("breath") || lowercasePrompt.contains("zen") -> {
                // Meditation Emulator
                screens.put(createScreenObj("Sessions", "home", JSONArray().apply {
                    put(createWidgetObj("text", "Breathe In, Breathe Out", "Focus on your deep belly loop", JSONArray(), JSONArray(), ""))
                    put(createWidgetObj("button", "Start 5-Min Timer", "Guided Zen Track", JSONArray(), JSONArray(), "toast:Timer started! Take a deep breath."))
                    put(createWidgetObj("chart", "Weekly Mindfulness Progress", "Minutes of meditation", JSONArray(), JSONArray().apply { put(15); put(20); put(0); put(25); put(30) }, ""))
                }))
            }
            else -> {
                // Default App Emulator
                screens.put(createScreenObj("Dashboard", "home", JSONArray().apply {
                    put(createWidgetObj("text", "Welcome to $appName", "Generated concept is live!", JSONArray(), JSONArray(), ""))
                    put(createWidgetObj("chart", "Platform Usage Activity", "Request volumes (k/hr)", JSONArray(), JSONArray().apply { put(100); put(250); put(180); put(420); put(310) }, ""))
                    put(createWidgetObj("list", "System Metrics", "", JSONArray().apply {
                        put("Main API Endpoint: ONLINE")
                        put("Database Sync Latency: 4ms")
                        put("Current Build Version: 1.0.0")
                    }, JSONArray(), ""))
                }))
                screens.put(createScreenObj("Controls", "settings", JSONArray().apply {
                    put(createWidgetObj("button", "Run System Diagnostic", "Spawns QA bug scans", JSONArray(), JSONArray(), "toast:Diagnostics complete! Status: Healthy."))
                    put(createWidgetObj("button", "Re-deploy Core Server", "V1.0.1 hotfix patch", JSONArray(), JSONArray(), "toast:Re-deployment initiated successfully!"))
                }))
            }
        }
        
        emulator.put("screens", screens)

        val mainObj = JSONObject().apply {
            put("appName", appName)
            put("logs", logs)
            put("files", files)
            put("emulator", emulator)
        }
        
        return mainObj.toString()
    }

    private data class LogSpec(val name: String, val dept: String, val msg: String, val severity: String)

    private fun createFileObj(path: String, content: String, language: String, size: String): JSONObject {
        return JSONObject().apply {
            put("path", path)
            put("content", content)
            put("language", language)
            put("size", size)
        }
    }

    private fun createScreenObj(tabName: String, icon: String, widgets: JSONArray): JSONObject {
        return JSONObject().apply {
            put("tabName", tabName)
            put("icon", icon)
            put("widgets", widgets)
        }
    }

    private fun createWidgetObj(type: String, title: String, subtitle: String, items: JSONArray, chartData: JSONArray, action: String): JSONObject {
        return JSONObject().apply {
            put("type", type)
            put("title", title)
            put("subtitle", subtitle)
            put("items", items)
            put("chartData", chartData)
            put("action", action)
        }
    }
}
