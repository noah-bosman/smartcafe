package land.noah.smartcafe.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        OrderEntity::class,
        OrderItemEntity::class,
        FeedbackEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CafeDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao

    abstract fun feedbackDao(): FeedbackDao

    companion object {
        @Volatile
        private var INSTANCE: CafeDatabase? = null

        fun getInstance(
            context: Context
        ): CafeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CafeDatabase::class.java,
                    "cafe.db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}