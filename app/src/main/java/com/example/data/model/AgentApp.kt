package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent_apps")
data class AgentApp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    val name: String,
    val status: String, // "Pending", "Orchestrating", "Completed", "Failed"
    val creationTime: Long = System.currentTimeMillis(),
    val modelUsed: String = "gemini-3.5-flash",
    val filesJson: String, // JSON Array of files: { path: String, content: String, language: String, size: String }
    val logsJson: String,  // JSON Array of logs: { timestamp: Long, agentName: String, dept: String, msg: String, severity: String }
    val demoUiJson: String, // JSON Object for dynamic interactive emulator state
    val hostedUrl: String
)
