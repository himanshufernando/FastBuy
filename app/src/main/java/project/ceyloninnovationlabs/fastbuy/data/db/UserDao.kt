package project.ceyloninnovationlabs.fastbuy.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import project.ceyloninnovationlabs.fastbuy.data.model.user.User


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg user: User)

    @Query("SELECT * FROM user_table ")
    suspend fun getUser(): User


    @Query("DELETE  FROM user_table")
    suspend fun deleteUser()


}