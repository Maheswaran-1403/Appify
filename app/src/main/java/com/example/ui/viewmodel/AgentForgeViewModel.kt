package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AgentAppRepository
import com.example.data.service.GeminiService
import com.example.data.service.OfflineGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AgentForgeViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "AgentForgeViewModel"
    private val repository: AgentAppRepository

    val allApps: StateFlow<List<AgentApp>>

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _activeAgent = MutableStateFlow<AgentType?>(null)
    val activeAgent = _activeAgent.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _terminalLogs = MutableStateFlow<List<LogEntry>>(emptyList())
    val terminalLogs = _terminalLogs.asStateFlow()

    private val _currentApp = MutableStateFlow<AgentApp?>(null)
    val currentApp = _currentApp.asStateFlow()

    private val _currentFiles = MutableStateFlow<List<AppFile>>(emptyList())
    val currentFiles = _currentFiles.asStateFlow()

    private val _selectedFile = MutableStateFlow<AppFile?>(null)
    val selectedFile = _selectedFile.asStateFlow()

    private val _emulatorUi = MutableStateFlow<JSONObject?>(null)
    val emulatorUi = _emulatorUi.asStateFlow()

    private val _emulatorActiveTab = MutableStateFlow(0)
    val emulatorActiveTab = _emulatorActiveTab.asStateFlow()

    private val _inputPrompt = MutableStateFlow("")
    val inputPrompt = _inputPrompt.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AgentAppRepository(database.agentAppDao())
        
        // Convert Flow from Room to StateFlow in ViewModelScope
        val tempApps = MutableStateFlow<List<AgentApp>>(emptyList())
        allApps = tempApps.asStateFlow()

        viewModelScope.launch {
            repository.allApps.collect {
                tempApps.value = it
            }
        }
    }

    fun updatePrompt(prompt: String) {
        _inputPrompt.value = prompt
    }

    fun selectApp(app: AgentApp?) {
        _currentApp.value = app
        _emulatorActiveTab.value = 0
        if (app != null) {
            // Load files
            val filesList = mutableListOf<AppFile>()
            try {
                val filesArr = JSONArray(app.filesJson)
                for (i in 0 until filesArr.length()) {
                    val fileObj = filesArr.getJSONObject(i)
                    filesList.add(
                        AppFile(
                            path = fileObj.getString("path"),
                            content = fileObj.getString("content"),
                            language = fileObj.getString("language"),
                            size = fileObj.optString("size", "1.0 KB")
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing files", e)
            }
            _currentFiles.value = filesList
            _selectedFile.value = filesList.firstOrNull()

            // Load logs
            val logsList = mutableListOf<LogEntry>()
            try {
                val logsArr = JSONArray(app.logsJson)
                for (i in 0 until logsArr.length()) {
                    val logObj = logsArr.getJSONObject(i)
                    logsList.add(
                        LogEntry(
                            timestamp = logObj.optLong("timestamp", System.currentTimeMillis()),
                            agentName = logObj.getString("agentName"),
                            deptDisplayName = logObj.getString("dept"),
                            msg = logObj.getString("msg"),
                            severity = logObj.getString("severity")
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing logs", e)
            }
            _terminalLogs.value = logsList

            // Load emulator layout
            try {
                _emulatorUi.value = JSONObject(app.demoUiJson)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing emulator UI", e)
                _emulatorUi.value = null
            }
        } else {
            _currentFiles.value = emptyList()
            _selectedFile.value = null
            _terminalLogs.value = emptyList()
            _emulatorUi.value = null
        }
    }

    fun setEmulatorTab(index: Int) {
        _emulatorActiveTab.value = index
    }

    fun selectFile(file: AppFile) {
        _selectedFile.value = file
    }

    fun deleteApp(app: AgentApp) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteApp(app)
            if (_currentApp.value?.id == app.id) {
                selectApp(null)
            }
        }
    }

    fun clearAllApps() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllApps()
            selectApp(null)
        }
    }

    fun buildApp(prompt: String) {
        if (prompt.trim().isEmpty() || _isGenerating.value) return

        _isGenerating.value = true
        _progress.value = 0f
        _terminalLogs.value = emptyList()
        _currentFiles.value = emptyList()
        _selectedFile.value = null
        _emulatorUi.value = null
        _activeAgent.value = AgentType.SUPERVISOR

        viewModelScope.launch(Dispatchers.Default) {
            // Step 1: Query Gemini API or Fallback
            val initialLog = LogEntry(
                timestamp = System.currentTimeMillis(),
                agentName = "Supervisor Agent",
                deptDisplayName = "Core Management",
                msg = "Supervisor initialized. Analyzing blueprint requirement: '$prompt'. Querying core model clusters...",
                severity = "INFO"
            )
            _terminalLogs.value = listOf(initialLog)

            var rawResult = GeminiService.generateApp(prompt)
            val modelUsed = if (rawResult != null) "gemini-3.5-flash" else "Local Offline Engine"
            
            if (rawResult == null) {
                // Wait for visual realism
                delay(1200)
                rawResult = OfflineGenerator.generateAppOffline(prompt)
            }

            // Step 2: Parse Response
            var appName = "Custom App"
            var parsedLogs = JSONArray()
            var parsedFiles = JSONArray()
            var parsedEmulator = JSONObject()

            try {
                val resultObj = JSONObject(rawResult)
                appName = resultObj.optString("appName", "AgentCraft App")
                parsedLogs = resultObj.optJSONArray("logs") ?: JSONArray()
                parsedFiles = resultObj.optJSONArray("files") ?: JSONArray()
                parsedEmulator = resultObj.optJSONObject("emulator") ?: JSONObject()
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing generative response", e)
                // Construct a quick fallback object if parsing failed completely
                rawResult = OfflineGenerator.generateAppOffline(prompt)
                try {
                    val resultObj = JSONObject(rawResult)
                    appName = resultObj.optString("appName", "AgentCraft App")
                    parsedLogs = resultObj.optJSONArray("logs") ?: JSONArray()
                    parsedFiles = resultObj.optJSONArray("files") ?: JSONArray()
                    parsedEmulator = resultObj.optJSONObject("emulator") ?: JSONObject()
                } catch (ex: Exception) {
                    Log.e(TAG, "Double error, impossible", ex)
                }
            }

            // Step 3: Insert initial pending app to Room so the user sees it immediately in historical logs
            val demoUrl = "https://agentforge.live/apps/${UUID.randomUUID().toString().take(7)}"
            val dummyApp = AgentApp(
                prompt = prompt,
                name = appName,
                status = "Orchestrating",
                modelUsed = modelUsed,
                filesJson = "[]",
                logsJson = "[]",
                demoUiJson = "{}",
                hostedUrl = demoUrl
            )
            val appId = repository.insertApp(dummyApp).toInt()

            // Step 4: Visual Orchestration Loop (Stream logs onto terminal dynamically with delays!)
            val logsList = mutableListOf<LogEntry>()
            logsList.add(initialLog)

            val totalLogs = parsedLogs.length()
            for (i in 0 until totalLogs) {
                try {
                    val logObj = parsedLogs.getJSONObject(i)
                    val agentName = logObj.getString("agentName")
                    val deptName = logObj.getString("dept")
                    val msg = logObj.getString("msg")
                    val severity = logObj.getString("severity")

                    // Map current agent
                    val mappedAgent = AgentType.values().firstOrNull { it.displayName.equals(agentName, true) }
                    _activeAgent.value = mappedAgent ?: AgentType.SUPERVISOR

                    // Random slight typing delays for extremely authentic aesthetic feel
                    val delayMs = when (mappedAgent?.dept) {
                        Department.CORE_MANAGEMENT -> 150L
                        Department.DATA -> 250L
                        Department.ML -> 300L
                        Department.SOFTWARE -> 400L
                        Department.QUALITY -> 350L
                        Department.DEVOPS -> 300L
                        Department.DOCUMENTATION -> 200L
                        else -> 200L
                    }
                    delay(delayMs)

                    val newLog = LogEntry(
                        timestamp = System.currentTimeMillis(),
                        agentName = agentName,
                        deptDisplayName = deptName,
                        msg = msg,
                        severity = severity
                    )
                    logsList.add(newLog)
                    _terminalLogs.value = logsList.toList()

                    // Increase progress
                    _progress.value = (i + 1).toFloat() / totalLogs.toFloat()
                } catch (e: Exception) {
                    Log.e(TAG, "Error streaming log", e)
                }
            }

            // Final completion steps
            delay(1000)
            _activeAgent.value = null

            // Compile files list for screen
            val filesList = mutableListOf<AppFile>()
            for (j in 0 until parsedFiles.length()) {
                val fObj = parsedFiles.getJSONObject(j)
                filesList.add(
                    AppFile(
                        path = fObj.getString("path"),
                        content = fObj.getString("content"),
                        language = fObj.getString("language"),
                        size = fObj.optString("size", "1.5 KB")
                    )
                )
            }
            _currentFiles.value = filesList
            _selectedFile.value = filesList.firstOrNull()
            _emulatorUi.value = parsedEmulator

            // Update app entity in Room with complete code & logs
            val completeApp = AgentApp(
                id = appId,
                prompt = prompt,
                name = appName,
                status = "Completed",
                creationTime = dummyApp.creationTime,
                modelUsed = modelUsed,
                filesJson = parsedFiles.toString(),
                logsJson = parsedLogs.toString(),
                demoUiJson = parsedEmulator.toString(),
                hostedUrl = demoUrl
            )
            repository.updateApp(completeApp)

            _currentApp.value = completeApp
            _isGenerating.value = false
            _inputPrompt.value = ""
        }
    }
}
