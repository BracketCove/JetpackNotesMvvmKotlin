package com.wiseassblog.jetpacknotesmvvmkotlin.model

import androidx.room.*


@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    suspend fun getNotes(): List<RoomNote>

    @Query("SELECT * FROM notes WHERE creation_date = :creationDate")
    suspend fun getNoteById(creationDate: String): RoomNote

    @Delete
    suspend fun deleteNote(note: RoomNote)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNote(note: RoomNote): Long
}









//@Dao
//interface NoteDao {
//    @Query("SELECT * FROM notes")
//    suspend fun getNotes(): List<RoomNote>
//
//    @Query("SELECT * FROM notes WHERE creation_date = :creationDate")
//    suspend fun getNoteById(creationDate: String): RoomNote
//
//    @Delete
//    suspend fun deleteNote(note: RoomNote)
//
//    //if update successful, will return number of rows effected, which should be 1
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrUpdateNote(note: RoomNote): Long
//}