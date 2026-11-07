package com.jd.trakker.data.local.metadata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jd.trakker.data.local.item.Game
import com.jd.trakker.data.local.item.GameItem
import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.data.local.item.Movie
import com.jd.trakker.data.local.item.MovieItem
import com.jd.trakker.data.local.item.Show
import com.jd.trakker.data.local.item.ShowItem

@Fts4(contentEntity = Metadata::class)
@Entity(tableName = "MetadataFTS")
data class MetadataFTS(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowID: Int,
    val name: String
)

@Entity(
    tableName = "Metadata",
    primaryKeys = ["id", "type"],
    indices = [
        Index(
            value = ["id", "type"],
            unique = true
        )
    ]
)
data class Metadata(
    val id: Int,
    val type: ItemType,

    // Data
    val name: String,
    val url: String,
    val summary: String,
    val genres: List<String>,
    val cover: String?,
    val backdrops: List<String>,
    val platforms: List<String>,
    @ColumnInfo(name = "release_date")
    val releaseDate: Long,
    val rating: Double
)

fun Metadata.toItem(): Item {
    return when(type) {
        ItemType.Game  -> GameItem(Game(
            id = id,
            url = url,
            name = name,
            summary = summary,
            genres = genres,
            cover = cover,
            backdrops = backdrops,
            platforms = platforms,
            releaseDate = releaseDate,
            rating = rating
        ))
        ItemType.Movie -> MovieItem(Movie(
            id = id,
            url = url,
            name = name,
            summary = summary,
            genres = genres,
            cover = cover,
            backdrops = backdrops,
            releaseDate = releaseDate,
            rating = rating
        ))
        ItemType.Show  -> ShowItem(Show(
            id = id,
            url = url,
            name = name,
            summary = summary,
            genres = genres,
            cover = cover,
            backdrops = backdrops,
            releaseDate = releaseDate,
            rating = rating
        ))
    }
}