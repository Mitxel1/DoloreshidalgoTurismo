package mx.edu.utng.arg.doloreshidalgoturismo.data.dao

import androidx.room.*
import mx.edu.utng.arg.doloreshidalgoturismo.data.model.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Query("SELECT * FROM places ORDER BY createdAt DESC")
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE category = :category ORDER BY createdAt DESC")
    fun getPlacesByCategory(category: String): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getPlaceById(placeId: Int): PlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    @Update
    suspend fun updatePlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Query("UPDATE places SET isFavorite = :isFavorite WHERE id = :placeId")
    suspend fun updateFavoriteStatus(placeId: Int, isFavorite: Boolean)
}