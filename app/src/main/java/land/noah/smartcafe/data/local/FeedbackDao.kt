package land.noah.smartcafe.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface FeedbackDao {

    @Upsert
    suspend fun upsertFeedback(
        feedback: FeedbackEntity
    )

    @Query(
        """
        SELECT * FROM feedback
        WHERE customerId = :customerId
        ORDER BY createdAt DESC
        """
    )
    suspend fun getFeedbackForCustomer(
        customerId: String
    ): List<FeedbackEntity>
}