package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "module_progress")
data class ModuleProgress(
    @PrimaryKey val moduleId: String,
    val moduleName: String,
    val completed: Boolean = false,
    val score: Int = 0,
    val maxScore: Int = 0,
    val earnedStarBadge: Boolean = false
)

@Entity(tableName = "leaderboard")
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val score: Int,
    val dateString: String
)

@Dao
interface ProgressDao {
    @Query("SELECT * FROM module_progress")
    fun getAllProgress(): Flow<List<ModuleProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ModuleProgress)

    @Query("UPDATE module_progress SET completed = :completed, score = :score, maxScore = :maxScore, earnedStarBadge = :earnedStarBadge WHERE moduleId = :moduleId")
    suspend fun updateProgress(moduleId: String, completed: Boolean, score: Int, maxScore: Int, earnedStarBadge: Boolean)

    @Query("DELETE FROM module_progress")
    suspend fun resetAllProgress()

    // Leaderboard Queries
    @Query("SELECT * FROM leaderboard ORDER BY score DESC")
    fun getAllLeaderboard(): Flow<List<LeaderboardEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboard(entry: LeaderboardEntry)

    @Delete
    suspend fun deleteLeaderboard(entry: LeaderboardEntry)

    @Query("DELETE FROM leaderboard")
    suspend fun clearLeaderboard()
}

@Database(entities = [ModuleProgress::class, LeaderboardEntry::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "science_year5_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class ProgressRepository(private val progressDao: ProgressDao) {
    val allProgress: Flow<List<ModuleProgress>> = progressDao.getAllProgress()
    val allLeaderboard: Flow<List<LeaderboardEntry>> = progressDao.getAllLeaderboard()

    suspend fun saveProgress(progress: ModuleProgress) {
        progressDao.insertProgress(progress)
    }

    suspend fun updateProgress(moduleId: String, completed: Boolean, score: Int, maxScore: Int, earnedStarBadge: Boolean) {
        progressDao.updateProgress(moduleId, completed, score, maxScore, earnedStarBadge)
    }

    suspend fun resetProgress() {
        progressDao.resetAllProgress()
    }

    suspend fun saveLeaderboardEntry(entry: LeaderboardEntry) {
        progressDao.insertLeaderboard(entry)
    }

    suspend fun deleteLeaderboardEntry(entry: LeaderboardEntry) {
        progressDao.deleteLeaderboard(entry)
    }

    suspend fun clearLeaderboard() {
        progressDao.clearLeaderboard()
    }
}
