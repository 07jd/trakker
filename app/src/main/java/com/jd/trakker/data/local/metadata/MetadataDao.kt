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
    """)
    suspend fun getAll(type: ItemType): List<Metadata>

    @Query("""
        SELECT Metadata.*
        FROM MetadataFTS
        INNER JOIN Metadata 
        ON Metadata.id = MetadataFTS.rowid
        WHERE MetadataFTS MATCH :query
        AND language = :language
        AND type = :type
    """)
    suspend fun search(query: String, language: Language, type: ItemType): List<Metadata>

    @Query("""
        SELECT *
        FROM Metadata
        WHERE id IN (:ids)
        AND type = :type
    """)
    suspend fun getBatch(ids: List<Int>, type: ItemType): List<Metadata>
}