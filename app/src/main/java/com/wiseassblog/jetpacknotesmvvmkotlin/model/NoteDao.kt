package com.wiseassblog.jetpacknotesmvvmkotlin.model

import androidx.room.*


@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getNotes(): List<RoomNote>

    @Query("SELECT * FROM notes WHERE creation_date = :creationDate")
    fun getNoteById(creationDate: String): RoomNote

    @Delete
    fun deleteNote(note: RoomNote)

    //if update successful, will return number of rows effected, which should be 1
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateNote(note: RoomNote): Long
}