package com.wiseassblog.jetpacknotesmvvmkotlin.model.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.wiseassblog.jetpacknotesmvvmkotlin.common.*
import com.wiseassblog.jetpacknotesmvvmkotlin.model.FirebaseNote
import com.wiseassblog.jetpacknotesmvvmkotlin.model.Note
import com.wiseassblog.jetpacknotesmvvmkotlin.model.NoteDao
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val COLLECTION_NAME = "notes"

/**
 * If this wasn't a demo project, I would apply more abstraction to this repository (i.e. local and remote would be
 * separate interfaces which this class would depend on). I wanted to keep it the back end simple since this app is
 * a demo on MVVM, which is a front end architecture pattern.
 */
class FirebaseNoteRepoImpl(
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    val remote: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val local: NoteDao
) : INoteRepository {


    override suspend fun getNoteById(noteId: String): Result<Exception, Note> {
        return if (activeUser()) getRemoteNote(noteId)
        else getLocalNote(noteId)
    }

    override suspend fun deleteNote(note: Note): Result<Exception, Unit> {
        return if (activeUser()) deleteRemoteNote(note)
        else deleteLocalNote(note)
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> {
        return if (activeUser()) updateRemoteNote(note)
        else updateLocalNote(note)
    }

    override suspend fun getNotes(): Result<Exception, List<Note>> {
        return if (activeUser()) getRemoteNotes()
        else getLocalNotes()
    }

    private fun activeUser(): Boolean {
        val user = firebaseAuth.currentUser

        if (user == null) return false
        else return true
    }

    private fun resultToNoteList(result: QuerySnapshot?): Result<Exception, List<Note>> {
        val noteList = mutableListOf<Note>()

        result?.forEach { documentSnapshop ->
            noteList.add(documentSnapshop.toObject(FirebaseNote::class.java).toNote)
        }

        return Result.build {
            noteList
        }
    }


    /* Remote Datasource */

    private suspend fun getRemoteNotes(): Result<Exception, List<Note>> {
        var reference = remote.collection(COLLECTION_NAME)

        return try {
            val task = awaitTaskResult(reference.get())

            return resultToNoteList(task)
        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    private suspend fun getRemoteNote(id: String): Result<Exception, Note> {
        var reference = remote.collection(COLLECTION_NAME)
            .document(id)

        return try {
            val task = awaitTaskResult(reference.get())

            Result.build {
                task.toObject(FirebaseNote::class.java)?.toNote ?: throw Exception()
            }
        } catch (exception: Exception) {
            Result.build { throw exception }
        }

    }

    private suspend fun deleteRemoteNote(note: Note): Result<Exception, Unit> = Result.build {
        awaitTaskCompletable(
            remote.collection(COLLECTION_NAME)
                .document(note.creationDate)
                .delete()
        )
    }

    private suspend fun updateRemoteNote(note: Note): Result<Exception, Unit> {
        return try {
            awaitTaskCompletable(
                remote.collection(COLLECTION_NAME)
                    .document(note.creationDate)
                    .set(note.toFirebaseNote)
            )

            Result.build { Unit }

        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    /* Local Datasource */
    private suspend fun getLocalNotes(): Result<Exception, List<Note>> = withContext(Dispatchers.IO)
    { Result.build { local.getNotes().toNoteListFromRoomNote() } }

    private suspend fun getLocalNote(id: String): Result<Exception, Note> =
        withContext(Dispatchers.IO) { Result.build { local.getNoteById(id).toNote } }

    private suspend fun deleteLocalNote(note: Note): Result<Exception, Unit> = withContext(Dispatchers.IO) {
        local.deleteNote(note.toRoomNote)
        Result.build { Unit }
    }

    private suspend fun updateLocalNote(note: Note): Result<Exception, Unit> = withContext(Dispatchers.IO) {
        val updated = local.insertOrUpdateNote(note.toRoomNote)

        when {
            updated == 0L -> Result.build { throw Exception() }
            else -> Result.build { Unit }
        }
    }

}