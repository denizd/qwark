package com.denizd.qwark.sheet

import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.denizd.qwark.R
import com.denizd.qwark.databinding.ClassSessionSheetBinding
import com.denizd.qwark.fragment.ParticipationCourseFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.random.Random

class ClassSessionSheet : BottomSheetDialogFragment() {

    private var _binding: ClassSessionSheetBinding? = null
    private val binding: ClassSessionSheetBinding get() = _binding!!

    private lateinit var mContext: Context
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
    private val time: Long = System.currentTimeMillis()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ClassSessionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        participationFragment = targetFragment as ParticipationCourseFragment

        binding.handRaisedButton.apply {
            setCompoundDrawables(context.getDrawable(R.drawable.hand).also { drawable ->
                drawable?.bounds = Rect(0, 0, 256, 256)
                drawable?.setTint(mContext.getColor(R.color.colorAccent))
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
            setCompoundDrawables(context.getDrawable(R.drawable.speech).also { drawable ->
                drawable?.bounds = Rect(0, 0, 256, 256)
                drawable?.setTint(mContext.getColor(R.color.colorAccent))
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

        binding.finishButton.apply {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun makeToast(stringResource: Int) {
        Toast
            .makeText(mContext, getString(stringResource), Toast.LENGTH_LONG)
            .show()
    }

    private fun finish() {
        participationFragment.createParticipation(
            timesHandRaised, timesSpoken, time
        )
    }
}