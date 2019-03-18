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


class NoteListAdapter(var event: MutableLiveData<NoteListEvent> = MutableLiveData()) :
    ListAdapter<Note, NoteListAdapter.NoteViewHolder>(NoteDiffUtilCallback()) {

    override fun onBindViewHolder(holder: NoteListAdapter.NoteViewHolder, position: Int) {
        getItem(position).let { note ->
            with(holder) {
                holder.content.text = note.contents
                holder.date.text = note.creationDate
                holder.content.text = note.contents
                holder.itemView.setOnClickListener {
                    event.value = NoteListEvent.OnNoteItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NoteViewHolder(
            inflater.inflate(R.layout.item_note, parent, false)
        )
    }

    class NoteViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
        var square: ImageView = root.imv_list_item_icon
        var dateIcon: ImageView = root.imv_date_and_time
        var content: TextView = root.lbl_message
        var date: TextView = root.lbl_date_and_time
    }
}

