package com.denizd.qwark.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizd.qwark.R
import com.denizd.qwark.adapter.FinalGradeAdapter
import com.denizd.qwark.sheet.ConfirmDeletionBottomSheet
import com.denizd.qwark.sheet.FinalsCreateBottomSheet
import com.denizd.qwark.sheet.FinalsOptionsBottomSheet
import com.denizd.qwark.databinding.FinalsFragmentBinding
import com.denizd.qwark.sheet.FinalsLinkBottomSheet
import com.denizd.qwark.util.QwarkUtil
import com.denizd.qwark.model.FinalGrade
import com.denizd.qwark.viewmodel.FinalsViewModel
import com.google.android.material.snackbar.Snackbar

internal class FinalsFragment : QwarkFragment(), FinalGradeAdapter.FinalGradeClickListener {

    private lateinit var viewModel: FinalsViewModel
    private var _binding: FinalsFragmentBinding? = null
    private val binding: FinalsFragmentBinding get() = _binding!!

    private var pointsText = ""
    private var gradeText = ""

    private val basicCoursesAdapter = FinalGradeAdapter(ArrayList(), this)
    private val advancedCoursesAdapter = FinalGradeAdapter(ArrayList(), this)
    private val examGradesAdapter = FinalGradeAdapter(ArrayList(), this)

    private var basicScrollPosition: Parcelable? = null
    private var advancedScrollPosition: Parcelable? = null
    private var examScrollPosition: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FinalsViewModel::class.java]
        viewModel.updateLinkedGrades {
            viewModel.allFinalGrades?.observe(this, Observer { finalGrades ->
                //            createOverview(finalGrades, view.rootView) // TODO export to bitmap
                if (basicScrollPosition == null) {
                    binding.basicCoursesRecyclerview.scheduleLayoutAnimation()
                    binding.advancedCoursesRecyclerview.scheduleLayoutAnimation()
                    binding.finalExamsRecyclerview.scheduleLayoutAnimation()
                }
                saveScrollPositions()

                val basicGrades = finalGrades.filter { f -> f.type == "basic" }
                val advancedGrades = finalGrades.filter { f -> f.type == "adv" }
                val examGrades = finalGrades.filter { f -> f.type == "exam" }

                basicCoursesAdapter.setFinalGrades(basicGrades)
                advancedCoursesAdapter.setFinalGrades(advancedGrades)
                examGradesAdapter.setFinalGrades(examGrades)

                val finalScore = viewModel.getFinalScore(basicGrades, advancedGrades, examGrades)
                if (finalScore == -1) {
                    binding.pointsTextview.text = "---"
                    binding.gradeTextview.text = "---"
                } else {
                    pointsText = "$finalScore"
                    gradeText = getString(R.string.approximate_placeholder, viewModel.getFinalGrade(finalScore).toString())
                    setTextForGradeTextViews()
                }

                restoreScrollPositions()
            })
        }
    }

    private fun setTextForGradeTextViews() {
        binding.pointsTextview.text = pointsText
        binding.gradeTextview.text = gradeText
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FinalsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarTitle = getString(R.string.your_final_score)
        superTitle = if (viewModel.getScoreProfileName() == "") {
            getString(R.string.no_score_profile_selected)
        } else {
            getString(R.string.score_profile_template, viewModel.getScoreProfileName())
        }
        fab.hide()

        val layoutManagerBasic = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerAdvanced = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerExams = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.basicCoursesRecyclerview.layoutManager = layoutManagerBasic
        binding.advancedCoursesRecyclerview.layoutManager = layoutManagerAdvanced
        binding.finalExamsRecyclerview.layoutManager = layoutManagerExams

        binding.basicCoursesRecyclerview.adapter = basicCoursesAdapter
        binding.advancedCoursesRecyclerview.adapter = advancedCoursesAdapter
        binding.finalExamsRecyclerview.adapter = examGradesAdapter
    }

    override fun onResume() {
        super.onResume()
        binding.paddingView.apply {
            applyPadding()
        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.textLayout?.applyPadding()
        }
        setTextForGradeTextViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onFinalGradeClick(finalGrade: FinalGrade) {
        openBottomSheet(FinalsOptionsBottomSheet().also { sheet ->
            QwarkUtil.putFinalGradeInBundle(sheet, finalGrade)
        })
    }

    internal fun createEditGradeSheet(finalGrade: FinalGrade) {
        openBottomSheet(FinalsCreateBottomSheet().also { sheet ->
            QwarkUtil.putFinalGradeInBundle(sheet, finalGrade)
        })
    }

    internal fun createLinkGradeSheet(finalGrade: FinalGrade) {
        openBottomSheet(FinalsLinkBottomSheet().also { sheet ->
            QwarkUtil.putFinalGradeInBundle(sheet, finalGrade)
        })
    }

    internal fun getAllYears() = viewModel.getAllYears()
    internal fun getAllCourses() = viewModel.getAllCourses()

    internal fun viewLinkedCourse(courseId: Int) {
        val f = GradeFragment()
        QwarkUtil.putCourseInBundle(f, viewModel.getCourse(courseId))
        val d = viewModel.getSchoolYearName(courseId)
        Log.d("TAG", d)
        f.arguments?.putString("schoolYear", d)
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, f)
            ?.addToBackStack("GradeFragment")
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ?.commit()
    }

    internal fun clearFinalGrade(finalGrade: FinalGrade) {
        val deletionSheet = ConfirmDeletionBottomSheet(getString(R.string.clear_final_grade_desc, finalGrade.name)) {
            val newFinalGrade = FinalGrade(
                name = "",
                grade = "-1",
                type = finalGrade.type,
                note = finalGrade.note,
                courseId = -1,
                finalGradeId = finalGrade.finalGradeId,
                scoreProfileId = finalGrade.scoreProfileId
            )
            viewModel.update(newFinalGrade)
        }
        openBottomSheet(deletionSheet)
    }

    internal fun clearFinalGradeError() {
        Snackbar
            .make(snackBarContainer, getString(R.string.clear_final_grade_error), Snackbar.LENGTH_LONG)
            .setBackgroundTint(context.getColor(R.color.colorWarning))
            .show()
    }

    internal fun update(finalGrade: FinalGrade) { viewModel.update(finalGrade) }

    // TODO perhaps replace with a bottom sheet
//    private fun createOverview(finalGrades: List<FinalGrade>, view: View) {
//        val dialogBuilder = MaterialAlertDialogBuilder(context)
//        val v = View.inflate(context, R.layout.finals_overview, null)
//        val l = v.findViewById<LinearLayout>(R.id.linear_layout)
//
//        for (finalGrade in finalGrades) {
//            val fv = View.inflate(context, R.layout.finals_overview_item, null)
//            fv.findViewById<TextView>(R.id.course).text = finalGrade.name
//            fv.findViewById<TextView>(R.id.grade).text = finalGrade.grade
//            l.addView(fv)
//        }
//        l.addView(View.inflate(context, R.layout.powered_by_qwark, null))
//        val dialog = dialogBuilder.setView(v).create()
//        dialog.show()
//        save(drawToBitmap(l, v.width, v.height))
//    }
//
//    private fun drawToBitmap(layout: View, width: Int, height: Int): Bitmap {
//        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bmp)
//        layout.measure(
//             View.MeasureSpec.makeMeasureSpec(canvas.width, View.MeasureSpec.EXACTLY),
//             View.MeasureSpec.makeMeasureSpec(canvas.height, View.MeasureSpec.EXACTLY))
//        layout.layout(0,0,layout.measuredWidth,layout.measuredHeight)
//        layout.draw(canvas)
//        return bmp
//    }
//
//    private fun save(bitmap: Bitmap) {
//        val name = "image"
//        val relativeLocation = Environment.DIRECTORY_PICTURES + File.pathSeparator + "Qwark"
//
//        val contentValues  = ContentValues().apply {
//            put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
//
//            // without this part causes "Failed to create new MediaStore record" exception to be invoked (uri is null below)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                put(MediaStore.Images.ImageColumns.RELATIVE_PATH, relativeLocation)
//            }
//        }
//
//        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        var stream: OutputStream? = null
//        var uri: Uri? = null
//        val contentResolver = context.contentResolver
//
//        try {
//            uri = contentResolver.insert(contentUri, contentValues)
//            if (uri == null) {
//                throw IOException("Failed to create new MediaStore record.")
//            }
//
//            stream = contentResolver.openOutputStream(uri)
//
//            if (stream == null) {
//                throw IOException("Failed to get output stream.")
//            }
//            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
//                throw IOException("Failed to save bitmap.")
//            }
//
////            Snackbar.make(mCoordinator, R.string.image_saved_success, Snackbar.LENGTH_INDEFINITE).setAction("Open") {
////                val intent = Intent()
////                intent.type = "image/*"
////                intent.action = Intent.ACTION_VIEW
////                intent.data = contentUri
////                startActivity(Intent.createChooser(intent, "Select Gallery App"))
////            }.show()
//
//        } catch (e: IOException) {
//            if (uri != null) {
//                contentResolver.delete(uri, null, null)
//            }
//            throw IOException(e)
//
//        } finally {
//            stream?.close()
//        }
//    }

    private fun saveScrollPositions(reset: Boolean = false) {
        if (reset) {
            basicScrollPosition = null
            advancedScrollPosition = null
            examScrollPosition = null
        } else {
            basicScrollPosition = binding.basicCoursesRecyclerview.layoutManager?.onSaveInstanceState()
            advancedScrollPosition = binding.advancedCoursesRecyclerview.layoutManager?.onSaveInstanceState()
            examScrollPosition = binding.finalExamsRecyclerview.layoutManager?.onSaveInstanceState()
        }
    }

    private fun restoreScrollPositions() {
        binding.basicCoursesRecyclerview.layoutManager?.onRestoreInstanceState(basicScrollPosition)
        binding.advancedCoursesRecyclerview.layoutManager?.onRestoreInstanceState(advancedScrollPosition)
        binding.finalExamsRecyclerview.layoutManager?.onRestoreInstanceState(examScrollPosition)
    }

    internal fun getCourseSortType() = viewModel.getCourseSortType()
    fun getGradeType() = viewModel.getGradeType()
}