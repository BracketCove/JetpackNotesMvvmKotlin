package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist


import androidx.recyclerview.widget.DiffUtil
import com.wiseassblog.jetpacknotesmvvmkotlin.model.Note

class NoteDiffUtilCallback : DiffUtil.ItemCallback<Note>(){
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.creationDate == newItem.creationDate
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.creationDate == newItem.creationDate
    }
}