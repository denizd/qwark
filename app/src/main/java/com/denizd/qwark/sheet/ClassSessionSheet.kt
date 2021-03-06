package com.denizd.qwark.sheet

import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.denizd.lawrence.util.viewBinding
import com.denizd.qwark.R
import com.denizd.qwark.databinding.ClassSessionSheetBinding
import com.denizd.qwark.fragment.ParticipationCourseFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.random.Random

class ClassSessionSheet : QwarkSheet(R.layout.class_session_sheet) {

    private val binding: ClassSessionSheetBinding by viewBinding(ClassSessionSheetBinding::bind)
    private lateinit var participationFragment: ParticipationCourseFragment

    private var timesHandRaised: Int = 0
        set(value) {
            field = value
            binding.handRaisedButton.text = "$value"
        }
    private var timesSpoken: Int = 0
        set(value) {
            field = value
            binding.spokenButton.text = "$value"
        }
    private var participationId = -1
    private val time: Long = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        participationFragment = targetFragment as ParticipationCourseFragment

        arguments?.let { args ->
            timesHandRaised = args.getInt ("timesHandRaised")
            timesSpoken = args.getInt("timesSpoken")
            participationId = args.getInt("participationId")
            binding.title.text = getString(R.string.class_session_resumed)
        }

        binding.handRaisedButton.apply {
            setCompoundDrawables(context.getDrawable(R.drawable.hand)?.also { drawable ->
                drawable.bounds = Rect(0, 0, 256, 256)
                drawable.setTint(context.getColor(R.color.colorAccent))
            }, null, null, null)
            text = "$timesHandRaised"
            setOnClickListener {
                timesHandRaised += 1
            }
            setOnLongClickListener {
                if (timesHandRaised > 0)
                    timesHandRaised -= 1
                else
                    if (Random.nextInt(25) == 20) makeToast(R.string.self_esteem)
                true
            }
        }
        binding.spokenButton.apply {
            setCompoundDrawables(context.getDrawable(R.drawable.speech)?.also { drawable ->
                drawable.bounds = Rect(0, 0, 256, 256)
                drawable.setTint(context.getColor(R.color.colorAccent))
            }, null, null, null)
            text = "$timesSpoken"
            setOnClickListener {
                timesSpoken += 1
            }
            setOnLongClickListener {
                if (timesSpoken > 0)
                    timesSpoken -= 1
                else
                    if (Random.nextInt(25) == 20) makeToast(R.string.self_esteem)
                true
            }
        }
        binding.cancelButton.apply {
            setOnClickListener {
                makeToast(R.string.long_press_to_cancel)
            }
            setOnLongClickListener {
                dismiss()
                true
            }
        }

        binding.saveButton.apply {
            setOnClickListener {
                makeToast(R.string.long_press_to_finish)
            }
            setOnLongClickListener {
                finish()
                dismiss()
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    private fun makeToast(stringResource: Int) {
        Toast
            .makeText(context, getString(stringResource), Toast.LENGTH_LONG)
            .show()
    }

    private fun finish() {
        if (participationId == -1) {
            participationFragment.createParticipation(timesHandRaised, timesSpoken, time)
        } else {
            participationFragment.updateParticipation(timesHandRaised, timesSpoken, participationId)
        }
    }
}