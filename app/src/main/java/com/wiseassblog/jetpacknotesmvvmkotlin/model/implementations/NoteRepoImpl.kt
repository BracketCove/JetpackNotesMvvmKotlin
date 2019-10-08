package com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.wiseassblog.jetpacknotesmvvmkotlin.common.*
import com.wiseassblog.jetpacknotesmvvmkotlin.model.FirebaseNote
import com.wiseassblog.jetpacknotesmvvmkotlin.model.Note
import com.wiseassblog.jetpacknotesmvvmkotlin.model.NoteDao
import com.wiseassblog.jetpacknotesmvvmkotlin.model.User
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository

private const val COLLECTION_NAME = "notes"

/**
 * If this wasn't a demo project, I would apply more abstraction to this repository (i.e. local and remote would be
 * separate interfaces which this class would depend on). I wanted to keep it the back end simple since this app is
 * a demo on MVVM, which is a front end architecture pattern.
 */
class NoteRepoImpl(
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    val remote: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val local: NoteDao
) : INoteRepository {


    override suspend fun getNoteById(noteId: String): Result<Exception, Note> {
        val user = getActiveUser()
        return if (user != null) getRemoteNote(noteId, user)
        else getLocalNote(noteId)
    }

    override suspend fun deleteNote(note: Note): Result<Exception, Unit> {
        val user = getActiveUser()
        return if (user != null) deleteRemoteNote(note.copy(creator = user))
        else deleteLocalNote(note)
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> {
        val user = getActiveUser()
        return if (user != null) updateRemoteNote(note.copy(creator = user))
        else updateLocalNote(note)
    }

    override suspend fun getNotes(): Result<Exception, List<Note>> {
        val user = getActiveUser()
        return if (user != null) getRemoteNotes(user)
        else getLocalNotes()
    }

    /**
     * if currentUser != null, return true
     */
    private fun getActiveUser(): User? {
        return firebaseAuth.currentUser?.toUser
    }


    private fun resultToNoteList(result: QuerySnapshot?): Result<Exception, List<Note>> {
        val noteList = mutableListOf<Note>()

        result?.forEach { documentSnapshot ->
            noteList.add(documentSnapshot.toObject(FirebaseNote::class.java).toNote)
        }

        return Result.build {
            noteList
        }
    }


    /* Remote Datasource */

    private suspend fun getRemoteNotes(user: User): Result<Exception, List<Note>> {
        return try {
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .whereEqualTo("creator", user.uid)
                    .get()
            )

            resultToNoteList(task)
        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    private suspend fun getRemoteNote(creationDate: String, user: User): Result<Exception, Note> {
        return try {
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .document(creationDate + user.uid)
                    .get()
            )

            Result.build {
                //Task<DocumentSnapshot!>
                task.toObject(FirebaseNote::class.java)?.toNote ?: throw Exception()
            }
        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    private suspend fun deleteRemoteNote(note: Note): Result<Exception, Unit> = Result.build {
        awaitTaskCompletable(
            remote.collection(COLLECTION_NAME)
                .document(note.creationDate + note.creator!!.uid)
                .delete()
        )
    }

    /**
     * Notes are stored with the following composite document name:
     * note.creationDate + note.creator.uid
     * The reason for this, is that if I just used the creationDate, hypothetically two users
     * creating a note at the same time, would have duplicate entries in the cloud database :(
     */
    private suspend fun updateRemoteNote(note: Note): Result<Exception, Unit> {
        return try {
            awaitTaskCompletable(
                remote.collection(COLLECTION_NAME)
                    .document(note.creationDate + note.creator!!.uid)
                    .set(note.toFirebaseNote)
            )

            Result.build { Unit }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    /* Local Datasource */
    private suspend fun getLocalNotes(): Result<Exception, List<Note>> = Result.build {
        local.getNotes().toNoteListFromRoomNote()
    }

    private suspend fun getLocalNote(id: String): Result<Exception, Note> = Result.build {
        local.getNoteById(id).toNote
    }

    private suspend fun deleteLocalNote(note: Note): Result<Exception, Unit> = Result.build {
        local.deleteNote(note.toRoomNote)
        Unit
    }

    private suspend fun updateLocalNote(note: Note): Result<Exception, Unit> = Result.build {
        local.insertOrUpdateNote(note.toRoomNote)
        Unit
    }



}