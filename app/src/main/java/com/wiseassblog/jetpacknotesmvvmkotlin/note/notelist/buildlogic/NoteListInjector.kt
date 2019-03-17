package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist.buildlogic

import android.content.Context
import com.google.firebase.FirebaseApp
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations.FirebaseNoteRepoImpl
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository

object NoteListInjector {
    private fun getNoteRepository(context: Context): INoteRepository {
        FirebaseApp.initializeApp(context)
        return FirebaseNoteRepoImpl(
            local = RoomNoteDatabase.getInstance(context).roomNoteDao()
        )
    }

    fun provideNoteListViewModelFactory(context: Context): NoteListViewModelFactory =
        NoteListViewModelFactory(
            getNoteRepository(context)
        )
}