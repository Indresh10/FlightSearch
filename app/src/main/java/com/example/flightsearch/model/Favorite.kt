package com.example.flightsearch.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "departure_code")
    val deptCode: String,
    @ColumnInfo(name = "destination_code")
    val destCode: String
)

data class FavoriteItem(
    val id: Int,
    val deptCode: String,
    val deptName: String,
    val destCode: String,
    val destName: String,
    val isFavorite: Boolean
)

fun Favorite.toFavoriteItem(deptName: String, destName: String): FavoriteItem =
    FavoriteItem(id, deptCode, deptName, destCode, destName, true)

fun FavoriteItem.toFavorite(): Favorite = Favorite(id, deptCode, destCode)