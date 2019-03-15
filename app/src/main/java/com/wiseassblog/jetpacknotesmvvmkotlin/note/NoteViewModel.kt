package com.wiseassblog.jetpacknotesmvvmkotlin.note

import androidx.lifecycle.MutableLiveData
import com.wiseassblog.jetpacknotesmvvmkotlin.common.BaseViewModel
import com.wiseassblog.jetpacknotesmvvmkotlin.common.GET_NOTE_ERROR
import com.wiseassblog.jetpacknotesmvvmkotlin.common.Result
import com.wiseassblog.jetpacknotesmvvmkotlin.model.User
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.IUserRepository
import com.wiseassblog.jetpacknotesmvvmkotlin.model.Note
import com.wiseassblog.jetpacknotesmvvmkotlin.note.notedetail.NoteDetailEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class NoteViewModel(
    val noteRepo: INoteRepository,
    uiContext: CoroutineContext
) : BaseViewModel<NoteDetailEvent>(uiContext) {

    val note = MutableLiveData<Note>()

    val deleted = MutableLiveData<Boolean>()

    val updated = MutableLiveData<Boolean>()

    override fun handleEvent(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.OnStart -> getNote(event.noteId)
            is NoteDetailEvent.OnDeleteClick -> onDelete()
            is NoteDetailEvent.OnDoneClick -> updateNote(event.contents)
        }
    }

    private fun onDelete() = launch {
        val deleteResult = noteRepo.deleteNote(note.value!!)

        when (deleteResult) {
            is Result.Value -> deleted.value = true
            is Result.Error -> deleted.value = false
        }
    }

    private fun updateNote(contents: String) = launch {
        val updateResult = noteRepo.updateNote(
            note.value!!
                .copy(contents = contents)
        )

        when (updateResult) {
            is Result.Value -> updated.value = true
            is Result.Error -> updated.value = false
        }
    }

    private fun getNote(noteId: String) = launch {
        if (noteId == "") newNote()
        else {
            val noteResult = noteRepo.getNoteById(noteId)

            when (noteResult) {
                is Result.Value -> note.value = noteResult.value
                is Result.Error -> error.value = GET_NOTE_ERROR
            }
        }
    }

    private suspend fun newNote() {
        note.value =
            Note(getCalendarTime(), "", 0, "satellite_beam", null)
    }


    private fun getCalendarTime(): String {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        val format = SimpleDateFormat("d MMM yyyy HH:mm:ss Z")
        format.timeZone = cal.timeZone
        return format.format(cal.time)
    }
}