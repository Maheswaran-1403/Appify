package com.example.data.repository

import com.example.data.database.AgentAppDao
import com.example.data.model.AgentApp
import kotlinx.coroutines.flow.Flow

class AgentAppRepository(private val agentAppDao: AgentAppDao) {
    val allApps: Flow<List<AgentApp>> = agentAppDao.getAllApps()

    suspend fun getAppById(id: Int): AgentApp? {
        return agentAppDao.getAppById(id)
    }

    suspend fun insertApp(app: AgentApp): Long {
        return agentAppDao.insertApp(app)
    }

    suspend fun updateApp(app: AgentApp) {
        agentAppDao.updateApp(app)
    }

    suspend fun deleteApp(app: AgentApp) {
        agentAppDao.deleteApp(app)
    }

    suspend fun deleteAllApps() {
        agentAppDao.deleteAllApps()
    }
}
