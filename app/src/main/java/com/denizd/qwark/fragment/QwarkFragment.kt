package com.denizd.qwark.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.denizd.qwark.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

open class QwarkFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private lateinit var _context: Context

    private lateinit var superTitleView: TextView
    private lateinit var appBarTitleView: TextView

    open var superTitle: String = ""
        set(value) {
            superTitleView.text = value
            field = value
        }
    open var appBarTitle = ""
        set(value) {
            appBarTitleView.text = value
            field = value
        }
    lateinit var appBar: AppBarLayout
    lateinit var snackBarContainer: CoordinatorLayout
    lateinit var fab: ExtendedFloatingActionButton

    val name: String = javaClass.simpleName

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * Initialise view resources
         */
        appBarTitleView = view.rootView.findViewById(R.id.app_bar_title)
        superTitleView = view.rootView.findViewById(R.id.super_title)
        appBar = view.rootView.findViewById(R.id.app_bar_layout)
        snackBarContainer = view.rootView.findViewById(R.id.root_view)
        fab = view.rootView.findViewById(R.id.fab)

        if (this is GradeFragment || this is ParticipationCourseFragment) {
            snackBarContainer.resetBottomPadding()
        } else {
            snackBarContainer.applyBottomPadding()
        }
    }

    /**
     * Overwritten to return non-null context after onAttach has been invoked. If called before
     * onAttach has been executed, an IllegalStateException will be thrown
     *
     * @return the context
     */
    override fun getContext(): Context = _context

    protected open fun getGridColumnCount(config: Configuration): Int {
        return when (config.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 1
            else -> 2
        } + if (resources.getBoolean(R.bool.isTablet)) 1 else 0
    }

    protected fun openBottomSheet(sheet: BottomSheetDialogFragment) {
        sheet.setTargetFragment(this, 42)
        sheet.show((context as FragmentActivity).supportFragmentManager, sheet.javaClass.simpleName)
    }

    protected fun RecyclerView.addFabScrollListener() {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when {
                    dy > 0 -> fab.hide()
                    dy < 0 -> fab.show()
                }
            }
        })
    }

    protected fun NestedScrollView.addFabScrollListener() {
        setOnScrollChangeListener { _, _, dy, _, doy ->
            when {
                dy > doy -> fab.hide()
                dy < doy -> fab.show()
            }
        }
    }

    private fun <T : View> T.resetBottomPadding() {
        setPadding(paddingLeft, paddingTop, paddingRight, 0)
    }

    private fun <T : View> T.applyBottomPadding() {
        setPadding(paddingLeft, paddingTop, paddingRight, 56.dpToPx())
    }

    /**
     * Converts integer DP values to PX for use by programmatic means of changing dimensions
     *
     * @return the dimension converted to PX
     */
    private fun Int.dpToPx(): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    ).toInt()
}