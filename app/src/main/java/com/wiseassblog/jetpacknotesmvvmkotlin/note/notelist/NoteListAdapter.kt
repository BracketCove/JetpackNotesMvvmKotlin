package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wiseassblog.jetpacknotesmvvmkotlin.R
import com.wiseassblog.jetpacknotesmvvmkotlin.model.Note
import kotlinx.android.synthetic.main.item_note.view.*

class NoteListAdapter(val event:MutableLiveData<NoteListEvent> = MutableLiveData()): ListAdapter<Note, NoteListAdapter.NoteViewHolder>(NoteDiffUtilCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return NoteViewHolder(
            inflater.inflate(R.layout.item_note, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        getItem(position).let { note ->
            holder.content.text = note.contents
            holder.date.text = note.creationDate

            holder.itemView.setOnClickListener {
                event.value = NoteListEvent.OnNoteItemClick(position)
            }
        }
    }


    class NoteViewHolder(root: View): RecyclerView.ViewHolder(root){
        var content: TextView = root.lbl_message
        var date: TextView = root.lbl_date_and_time
    }
}



