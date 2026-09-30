package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BloodStockItem
import com.example.data.model.DonationRecord
import com.example.data.model.EmergencyRequest
import com.example.data.model.UserAccount

@Database(
    entities = [
        UserAccount::class,
        BloodStockItem::class,
        EmergencyRequest::class,
        DonationRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeLinkDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bloodStockDao(): BloodStockDao
    abstract fun emergencyRequestDao(): EmergencyRequestDao
    abstract fun donationRecordDao(): DonationRecordDao

    companion object {
        @Volatile
        private var INSTANCE: LifeLinkDatabase? = null

        fun getDatabase(context: Context): LifeLinkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeLinkDatabase::class.java,
                    "lifelink_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
