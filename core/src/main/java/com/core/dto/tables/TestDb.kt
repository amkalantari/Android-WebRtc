package com.core.dto.tables

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "testDb", indices = [Index(value = ["localId"], unique = true)])
data class TestDb(
    @PrimaryKey(autoGenerate = true) @Keep var localId: Long = 0,
    @Keep var name: String,
) : Parcelable