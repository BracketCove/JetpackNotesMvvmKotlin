package com.wiseassblog.jetpacknotesmvvmkotlin.note.notedetail.buildlogic

import android.content.Context
import com.google.firebase.FirebaseApp
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations.FirebaseNoteRepoImpl
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository

object NoteDetailInjector {

    private fun getNoteRepository(context: Context): INoteRepository {
        FirebaseApp.initializeApp(context)
        return FirebaseNoteRepoImpl(
            local = RoomNoteDatabase.getInstance(context).roomNoteDao()
        )
    }

    fun provideNoteViewModelFactory(context: Context): NoteViewModelFactory =
        NoteViewModelFactory(
            getNoteRepository(context)
        )

}