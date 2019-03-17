package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.wiseassblog.jetpacknotesmvvmkotlin.R
import com.wiseassblog.jetpacknotesmvvmkotlin.common.makeToast
import com.wiseassblog.jetpacknotesmvvmkotlin.note.NoteListViewModel
import com.wiseassblog.jetpacknotesmvvmkotlin.note.notedetail.NoteDetailEvent
import com.wiseassblog.jetpacknotesmvvmkotlin.note.notedetail.NoteDetailViewArgs
import com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist.buildlogic.NoteListInjector
import kotlinx.android.synthetic.main.fragment_note_list.*
import kotlin.properties.Delegates

class NoteListView : Fragment() {

    private lateinit var viewModel: NoteListViewModel
    private lateinit var adapter: NoteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        viewModel = ViewModelProviders.of(
            this,
            NoteListInjector.provideNoteListViewModelFactory(requireContext())
        ).get(
            NoteListViewModel::class.java
        )

        val spaceLoop = imv_space_background.drawable as AnimationDrawable
        spaceLoop.setEnterFadeDuration(1000)
        spaceLoop.setExitFadeDuration(1000)
        spaceLoop.start()

        showLoadingState()
        setUpAdapter()
        observeViewModel()

        fab_create_new_item.setOnClickListener {
            val direction = NoteListViewDirections.actionNoteListViewToNoteDetailView("")
            findNavController().navigate(direction)
        }

        imv_toolbar_auth.setOnClickListener {
            findNavController().navigate(R.id.loginView)
        }

        viewModel.handleEvent(
            NoteListEvent.OnStart
        )
    }

    private fun setUpAdapter() {
        adapter = NoteListAdapter()
        adapter.event.observe(
            viewLifecycleOwner,
            Observer{
                viewModel.handleEvent(it)
            }
        )
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
                rec_list_fragment.visibility = View.VISIBLE

                val satelliteLoop = imv_satellite_animation.drawable as AnimationDrawable
                satelliteLoop.stop()

                imv_satellite_animation.visibility = View.INVISIBLE
            }
        )

        viewModel.editNote.observe(
            viewLifecycleOwner,
            Observer { noteId ->
                startNoteDetailWithArgs(noteId)
            }
        )
    }

    private fun startNoteDetailWithArgs(noteId: String) {
        val direction = NoteListViewDirections.actionNoteListViewToNoteDetailView(noteId)
        findNavController().navigate(direction)
    }

    private fun showErrorState(errorMessage: String?) = makeToast(errorMessage!!)


    private fun showLoadingState() {
        val satelliteLoop = imv_satellite_animation.drawable as AnimationDrawable
        satelliteLoop.start()
    }

}