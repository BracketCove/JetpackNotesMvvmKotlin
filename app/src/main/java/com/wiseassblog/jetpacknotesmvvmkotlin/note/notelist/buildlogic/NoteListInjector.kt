package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist.buildlogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.wiseassblog.jetpacknotesmvvmkotlin.model.RoomNoteDatabase
import com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations.NoteRepoImpl
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository

class NoteListInjector(application:Application): AndroidViewModel(application) {
    private fun getNoteRepository(): INoteRepository {
        FirebaseApp.initializeApp(getApplication())
        return NoteRepoImpl(
            local = RoomNoteDatabase.getInstance(getApplication()).roomNoteDao()
        )
    }

    fun provideNoteListViewModelFactory(): NoteListViewModelFactory =
        NoteListViewModelFactory(
            getNoteRepository()
        )
}