package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist

import android.graphics.drawable.AnimationDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.wiseassblog.jetpacknotesmvvmkotlin.R
import com.wiseassblog.jetpacknotesmvvmkotlin.common.makeToast
import com.wiseassblog.jetpacknotesmvvmkotlin.note.NoteListViewModel
import com.wiseassblog.jetpacknotesmvvmkotlin.note.notedetail.NoteDetailViewDirections
import com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist.buildlogic.NoteListInjector
import kotlinx.android.synthetic.main.fragment_note_detail.*
import kotlinx.android.synthetic.main.fragment_note_list.*
import kotlin.properties.Delegates

class NoteListView : Fragment() {

    private lateinit var viewModel: NoteListViewModel
    private lateinit var adapter: NoteListAdapter

    override fun onStart() {
        super.onStart()
        viewModel = ViewModelProviders.of(
            this,
            NoteListInjector.provideNoteListViewModelFactory(requireContext())
        ).get(
            NoteListViewModel::class.java
        )

        showLoadingState()
        setUpAdapter()
        observeViewModel()

        fab_create_new_item.setOnClickListener{
            val direction = NoteListViewDirections.actionNoteListViewToNoteDetailView("")
            findNavController().navigate(direction)
        }
    }

    private fun setUpAdapter() {
        var adapterEvent: NoteListEvent? by Delegates.observable(null as NoteListEvent?) {
                property, oldValue, newValue ->
            if (newValue != null) viewModel.handleEvent(newValue)
        }

        adapter = NoteListAdapter(adapterEvent)
        rec_list_fragment.adapter = adapter

    }

    private fun observeViewModel() {
        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { errorMessage ->
                showErrorState(errorMessage)
            }
        )

        viewModel.noteList.observe(
            viewLifecycleOwner,
            Observer { noteList ->
                adapter.submitList(noteList)

                val satelliteLoop = imv_note_detail_satellite.drawable as AnimationDrawable
                satelliteLoop.stop()
            }
        )

        viewModel.editNote.observe(
            viewLifecycleOwner,
            Observer { noteId ->
                val direction = NoteListViewDirections.actionNoteListViewToNoteDetailView(noteId)
                findNavController().navigate(direction)
            }
        )
    }

    private fun showErrorState(errorMessage: String?) = makeToast(errorMessage!!)


    private fun showLoadingState() {
        imv_space_background.setImageResource(
            resources.getIdentifier("antenna_loop_fast", "drawable", activity?.packageName)
        )

        val satelliteLoop = imv_note_detail_satellite.drawable as AnimationDrawable
        satelliteLoop.start()
    }

}