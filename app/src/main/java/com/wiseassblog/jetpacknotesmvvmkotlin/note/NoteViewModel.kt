package com.wiseassblog.jetpacknotesmvvmkotlin.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wiseassblog.jetpacknotesmvvmkotlin.common.BaseViewModel
import com.wiseassblog.jetpacknotesmvvmkotlin.common.GET_NOTE_ERROR
import com.wiseassblog.jetpacknotesmvvmkotlin.common.Result
import com.wiseassblog.jetpacknotesmvvmkotlin.model.Note
import com.wiseassblog.jetpacknotesmvvmkotlin.model.repository.INoteRepository
import com.wiseassblog.jetpacknotesmvvmkotlin.note.notedetail.NoteDetailEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class NoteViewModel(
    val noteRepo: INoteRepository,
    uiContext: CoroutineContext
) : BaseViewModel<NoteDetailEvent>(uiContext) {

    private val noteState = MutableLiveData<Note>()
    val note: LiveData<Note> get() = noteState

    private val deletedState = MutableLiveData<Boolean>()
    val deleted: LiveData<Boolean> get() = deletedState

    private val updatedState = MutableLiveData<Boolean>()
    val updated: LiveData<Boolean> get() = updatedState

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
            is Result.Value -> deletedState.value = true
            is Result.Error -> deletedState.value = false
        }
    }


    private fun updateNote(contents: String) = launch {
        val updateResult = noteRepo.updateNote(
            note.value!!
                .copy(contents = contents)
        )

        when (updateResult) {
            is Result.Value -> updatedState.value = true
            is Result.Error -> updatedState.value = false
        }
    }

    private fun getNote(noteId: String) = launch {
        if (noteId == "") newNote()
        else {
            val noteResult = noteRepo.getNoteById(noteId)

            when (noteResult) {
                is Result.Value -> noteState.value = noteResult.value
                is Result.Error -> errorState.value = GET_NOTE_ERROR
            }
        }
    }

    private fun newNote() {
        noteState.value =
            Note(getCalendarTime(), "", 0, "rocket_loop", null)
    }


    private fun getCalendarTime(): String {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        val format = SimpleDateFormat("d MMM yyyy HH:mm:ss Z")
        format.timeZone = cal.timeZone
        return format.format(cal.time)
    }
}