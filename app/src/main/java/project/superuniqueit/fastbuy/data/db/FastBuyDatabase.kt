package project.superuniqueit.fastbuy.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import project.superuniqueit.fastbuy.data.model.user.User

@Database(entities = [User::class], version = 2, exportSchema = false)
public abstract class FastBuyDatabase :  RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: FastBuyDatabase? = null

        fun getDatabase(context: Context): FastBuyDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FastBuyDatabase::class.java,
                    "fastbuy_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
