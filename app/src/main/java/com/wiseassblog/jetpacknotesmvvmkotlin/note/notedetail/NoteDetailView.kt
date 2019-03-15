package com.wiseassblog.jetpacknotesmvvmkotlin.note.notedetail

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
import com.wiseassblog.jetpacknotesmvvmkotlin.common.toEditable
import com.wiseassblog.jetpacknotesmvvmkotlin.login.UserViewModel
import com.wiseassblog.jetpacknotesmvvmkotlin.login.buildlogic.LoginInjector
import com.wiseassblog.jetpacknotesmvvmkotlin.note.NoteViewModel
import com.wiseassblog.jetpacknotesmvvmkotlin.note.notedetail.buildlogic.NoteDetailInjector
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_note_detail.*

class NoteDetailView : Fragment() {

    private lateinit var viewModel: NoteViewModel

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProviders.of(
            this,
            NoteDetailInjector.provideNoteViewModelFactory(requireContext())
        ).get(
            NoteViewModel::class.java
        )

        showLoadingState()

        imb_toolbar_done.setOnClickListener {
            viewModel.handleEvent(
                NoteDetailEvent.OnDoneClick(
                    edt_note_detail_text.toString()
                )
            )
        }

        imb_toolbar_delete.setOnClickListener { viewModel.handleEvent(NoteDetailEvent.OnDeleteClick) }

        observeViewModel()

        viewModel.handleEvent(
            NoteDetailEvent.OnStart(
                //note NoteDetailViewArgs is genereted via Navigation component
                NoteDetailViewArgs.fromBundle(arguments!!).noteId
            )
        )
    }

    private fun observeViewModel() {
        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { errorMessage ->
                showErrorState(errorMessage)
            }
        )

        viewModel.note.observe(
            viewLifecycleOwner,
            Observer { note ->
                edt_note_detail_text.text = note.contents.toEditable()

                val satelliteLoop = imv_note_detail_satellite.drawable as AnimationDrawable
                satelliteLoop.stop()
            }
        )
    }

    private fun showErrorState(errorMessage: String?) {
        makeToast(errorMessage!!)
        findNavController().navigate(R.id.noteListView)
    }

    private fun showLoadingState() {
        imv_antenna_animation.setImageResource(
            resources.getIdentifier("antenna_loop_fast", "drawable", activity?.packageName)
        )

        val satelliteLoop = imv_note_detail_satellite.drawable as AnimationDrawable
        satelliteLoop.start()
    }
}