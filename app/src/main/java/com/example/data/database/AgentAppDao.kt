package com.example.data.database

import androidx.room.*
import com.example.data.model.AgentApp
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentAppDao {
    @Query("SELECT * FROM agent_apps ORDER BY creationTime DESC")
    fun getAllApps(): Flow<List<AgentApp>>

    @Query("SELECT * FROM agent_apps WHERE id = :id")
    suspend fun getAppById(id: Int): AgentApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AgentApp): Long

    @Update
    suspend fun updateApp(app: AgentApp)

    @Delete
    suspend fun deleteApp(app: AgentApp)

    @Query("DELETE FROM agent_apps")
    suspend fun deleteAllApps()
}
