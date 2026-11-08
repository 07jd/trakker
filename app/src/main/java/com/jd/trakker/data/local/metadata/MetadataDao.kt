package com.jd.trakker.data.local.metadata

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jd.trakker.data.local.item.ItemType

@Dao
interface MetadataDao {
    @Upsert
    suspend fun upsert(data: Metadata)

    @Upsert
    suspend fun upsertBatch(data: List<Metadata>)

    @Query("""
        SELECT *
        FROM metadata
        WHERE type = :type
        AND id = :id
        LIMIT 1
    """)
    suspend fun get(type: ItemType, id: Int): Metadata?

    @Query("""
        SELECT *
        FROM metadata
        WHERE type = :type
    """)
    suspend fun getAll(type: ItemType): List<Metadata>

    @Query("""
        SELECT Metadata.*
        FROM MetadataFTS
        INNER JOIN Metadata 
        ON Metadata.rowid = MetadataFTS.rowid
        WHERE MetadataFTS MATCH :query
        AND type = :type
    """)
    suspend fun search(query: String, type: ItemType): List<Metadata>

    @Query("""
        SELECT *
        FROM Metadata
        WHERE id IN (:ids)
        AND type = :type
    """)
    suspend fun getBatch(ids: List<Int>, type: ItemType): List<Metadata>
}