package com.example.notesapp

import androidx.room.*
import androidx.room.Update

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM Note")
    suspend fun getAllNotes(): List<Note>
}