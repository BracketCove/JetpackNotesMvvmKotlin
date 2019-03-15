package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist.buildlogic

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations.FirebaseAuthRepositoryImpl
import com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations.FirestoreNoteRepoImpl
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.IUserRepository

object NoteListInjector {
    private fun getNoteRepository(context: Context): INoteRepository {
        FirebaseApp.initializeApp(context)
        return FirestoreNoteRepoImpl(
            FirebaseFirestore.getInstance(),
            FirebaseAuth.getInstance()
        )
    }

    fun provideNoteListViewModelFactory(context: Context): NoteListViewModelFactory =
        NoteListViewModelFactory(
            getNoteRepository(context)
        )
}