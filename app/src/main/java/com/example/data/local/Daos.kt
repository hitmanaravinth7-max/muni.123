package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BloodStockItem
import com.example.data.model.DonationRecord
import com.example.data.model.EmergencyRequest
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserAccount>)

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccount?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserAccount?

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUserFlow(): Flow<UserAccount?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveUser(): UserAccount?

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAll()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setActiveUser(userId: Long)

    @Query("SELECT * FROM users WHERE role = 'DONOR'")
    fun getAllDonors(): Flow<List<UserAccount>>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'DONOR'")
    fun getDonorCount(): Flow<Int>
}

@Dao
interface BloodStockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BloodStockItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BloodStockItem): Long

    @Query("SELECT * FROM blood_stock ORDER BY units DESC")
    fun getAllStock(): Flow<List<BloodStockItem>>

    @Query("UPDATE blood_stock SET units = :units, lastUpdatedMillis = :updatedMillis WHERE id = :id")
    suspend fun updateStockUnits(id: Long, units: Int, updatedMillis: Long)

    @Query("SELECT * FROM blood_stock WHERE bloodGroup = :bloodGroup AND units > 0")
    fun getAvailableByGroup(bloodGroup: String): Flow<List<BloodStockItem>>

    @Query("SELECT SUM(units) FROM blood_stock")
    fun getTotalUnits(): Flow<Int?>
}

@Dao
interface EmergencyRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: EmergencyRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<EmergencyRequest>)

    @Query("SELECT * FROM emergency_requests ORDER BY timestampMillis DESC")
    fun getAllRequests(): Flow<List<EmergencyRequest>>

    @Query("SELECT * FROM emergency_requests WHERE status = 'Open' ORDER BY timestampMillis DESC")
    fun getOpenRequests(): Flow<List<EmergencyRequest>>

    @Query("UPDATE emergency_requests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM emergency_requests WHERE status = 'Open'")
    fun getActiveCount(): Flow<Int>
}

@Dao
interface DonationRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DonationRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<DonationRecord>)

    @Query("SELECT * FROM donation_records WHERE donorId = :donorId ORDER BY dateMillis DESC")
    fun getRecordsForDonor(donorId: Long): Flow<List<DonationRecord>>
}
