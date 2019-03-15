package com.wiseassblog.jetpacknotesmvvmkotlin.note.notedetail.buildlogic

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations.FirestoreNoteRepoImpl
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository

object NoteDetailInjector {

    private fun getNoteRepository(context: Context): INoteRepository {
        FirebaseApp.initializeApp(context)
        return FirestoreNoteRepoImpl(
            FirebaseFirestore.getInstance(),
            FirebaseAuth.getInstance()
        )
    }

    fun provideNoteViewModelFactory(context: Context): NoteViewModelFactory =
        NoteViewModelFactory(
            getNoteRepository(context)
        )

}