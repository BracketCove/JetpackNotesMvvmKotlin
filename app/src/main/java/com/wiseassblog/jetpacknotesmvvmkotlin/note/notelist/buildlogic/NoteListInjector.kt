package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist.buildlogic

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations.FirebaseNoteRepoImpl
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository

class NoteListInjector(application:Application): AndroidViewModel(application) {
    private fun getNoteRepository(): INoteRepository {
        FirebaseApp.initializeApp(getApplication())
        return FirebaseNoteRepoImpl(
            local = RoomNoteDatabase.getInstance(getApplication()).roomNoteDao()
        )
    }

    fun provideNoteListViewModelFactory(): NoteListViewModelFactory =
        NoteListViewModelFactory(
            getNoteRepository()
        )
}