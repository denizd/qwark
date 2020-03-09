package com.denizd.qwark.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizd.lawrence.util.viewBinding
import com.denizd.qwark.R
import com.denizd.qwark.adapter.ParticipationAdapter
import com.denizd.qwark.databinding.GradeFragmentBinding
import com.denizd.qwark.model.Participation
import com.denizd.qwark.sheet.ClassSessionSheet
import com.denizd.qwark.sheet.ConfirmDeletionSheet
import com.denizd.qwark.viewmodel.ParticipationCourseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class ParticipationCourseFragment : QwarkFragment(R.layout.grade_fragment), ParticipationAdapter.ParticipationClickListener {

    private val binding: GradeFragmentBinding by viewBinding(GradeFragmentBinding::bind)
    private var course: String = ""
    private var courseId: Int = 0
    private var advanced: Boolean = false
    private val recyclerViewAdapter = ParticipationAdapter(ArrayList(), this)
//    private lateinit var participationFragment: ParticipationFragment
    private var averageText = ""

    private lateinit var viewModel: ParticipationCourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        course = arguments?.getString("course") ?: ""
        courseId = arguments?.getInt("courseId") ?: 0
        advanced = arguments?.getBoolean("advanced") ?: false
        viewModel = ViewModelProvider(this)[ParticipationCourseViewModel::class.java]
//        participationFragment = targetFragment as ParticipationFragment
//        participationFragment.getAllParticipationsForCourse(courseId).observe(this, Observer { participations ->
        viewModel.getAllParticipationsForCourse(courseId).observe(this, Observer { participations ->
            recyclerViewAdapter.setParticipations(participations)
            val participationArray = if (participations.isEmpty()) arrayOf("--", "--")
                else viewModel.getParticipationCount(participations)
            averageText = buildString {
                append(participationArray[0])
                append(" : ")
                append(participationArray[1])
            }
            binding.average.text = averageText
            binding.recyclerView.scheduleLayoutAnimation()
            viewModel.updateParticipation("${participationArray[0]}|${participationArray[1]}", courseId)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.hide()
        with (view.rootView) {
            findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.GONE
            findViewById<View>(R.id.view).visibility = View.GONE
        }

        superTitle = viewModel.getSchoolYearName()
        appBarTitle = String.format(
            (if (advanced) getString(R.string.advanced_abbreviated_placeholder) else "%s"),
            course
        )
        binding.average.textSize = 64f
        binding.weighting.text = getString(R.string.participation_ratio_desc)
        binding.addGradeButton.apply {
            text = getString(R.string.start_class_session)
            setOnClickListener {
                openBottomSheet(ClassSessionSheet().also { sheet ->
                    sheet.isCancelable = false
                })
            }
        }
        binding.recyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.openParticipationFragmentButton.apply {
            text = getString(R.string.open_grade_fragment)
            setOnClickListener {
                openGradeFragment()
            }
        }

        binding.average.text = averageText
    }

    override fun onParticipationClick(participation: Participation) {
        if (participation.time > System.currentTimeMillis() - 1000 * 60 * 90) {
            openBottomSheet(ClassSessionSheet().also { sheet ->
                sheet.arguments = Bundle(2).apply {
                    putInt("timesHandRaised", participation.timesHandRaised)
                    putInt("timesSpoken", participation.timesSpoken)
                    putInt("participationId", participation.participationId)
                }
                sheet.isCancelable = false
            })
        } else {
            Snackbar
                .make(snackBarContainer, getString(R.string.participation_too_old), Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onParticipationLongClick(participation: Participation) {
        openBottomSheet(ConfirmDeletionSheet(
            getString(R.string.delete_participation_confirmation)
        ) {
            viewModel.delete(participation)
        })
    }

    private fun openGradeFragment() {
        appBar.setExpanded(true)
        (context as FragmentActivity).supportFragmentManager.also { fm ->
            if (fm.getBackStackEntryAt(0).name == "GradeFragment") {
                fm.popBackStack()
            } else {
                val f = GradeFragment()
                f.arguments = Bundle().apply {
                    putString("schoolYear", viewModel.getSchoolYearName())
                    putInt("courseId", courseId)
                }
                fm.beginTransaction()
                    .replace(R.id.fragment_container, f)
                    .addToBackStack("GradeFragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
        }
    }

    fun createParticipation(timesHandRaised: Int, timesSpoken: Int, time: Long) {
        viewModel.createParticipation(timesHandRaised, timesSpoken, time, courseId)
    }

    // TODO this function has the same name as another one with a different function signature
    fun updateParticipation(timesHandRaised: Int, timesSpoken: Int, participationId: Int) {
        viewModel.updateParticipation(timesHandRaised, timesSpoken, participationId)
    }
}