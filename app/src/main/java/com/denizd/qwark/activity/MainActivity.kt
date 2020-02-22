package com.denizd.qwark.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.denizd.qwark.R
import com.denizd.qwark.databinding.ActivityMainBinding
import com.denizd.qwark.fragment.*
import com.denizd.qwark.viewmodel.MainViewModel

class MainActivity : FragmentActivity() {

    private lateinit var viewModel: MainViewModel

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 42)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.applyTheme(window, this)
        viewModel.checkForFirstTimeYearNotice(this)

        binding.appBarLayout.also { appBarLayout ->
            appBarLayout.setOnApplyWindowInsetsListener { _, insets ->
                (appBarLayout.layoutParams as ViewGroup.MarginLayoutParams)
                    .topMargin = insets.systemWindowInsetTop
                insets
            }
        }

        // TODO remove this translucent app bar layout stuff, it's hard to maintain
        window.decorView.systemUiVisibility = if (resources.getBoolean(R.bool.isLight)) {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        window.attributes = window.attributes.apply {
            flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        window.statusBarColor = Color.TRANSPARENT

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            binding.appBarLayout.setExpanded(true)
            loadFragment(viewModel.getFragmentType(item.itemId))
        }

        // TODO figure out why this isn't working
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().apply {
//                val fragments = arrayOf(
//                    CourseFragment(),
////                    GradeFragment(),
//                    NoteFragment(),
//                    ParticipationFragment(),
////                    ParticipationCourseFragment(),
//                    FinalsFragment(),
//                    SettingsFragment()
//                )
//                for (fragment in fragments) {
//                    add(R.id.fragment_container, fragment, fragment.name)
//                    if (fragment !is CourseFragment) hide(fragment)
//                }
//            }.commit()
//        }

        // TODO reselected listener for scrolling up
    }

    override fun onResume() {
        super.onResume()
        if (supportFragmentManager.fragments.size == 0) {
            binding.bottomNav.selectedItemId = R.id.courses
            loadFragment(viewModel.getFragmentType(R.id.courses))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loadFragment(type: String): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag(type) ?: viewModel.getFragment(type)

        if (fragment.lifecycle.currentState != Lifecycle.State.INITIALIZED) {
            return false
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, type)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

//        with (supportFragmentManager) {
//            beginTransaction()
//                .hide(fragments[fragments.size - 1]) // should this be 0?
//                .show(findFragmentByTag(type) ?: TODO())
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit()
//        }
        return true
    }

    override fun onBackPressed() {
//        Log.d("TAG2", "${supportFragmentManager.}") // supportFragmentManager.fragments[0].tag} ${supportFragmentManager.fragments[0].javaClass.simpleName
        when (supportFragmentManager.fragments[0].tag) {
            "GradeFragment", "ParticipationCourseFragment" -> {
                binding.bottomNav.visibility = View.INVISIBLE
                binding.view.visibility = View.INVISIBLE
                super.onBackPressed()
            }
            "NoteFragment" -> {
                if ((supportFragmentManager.fragments[0] as NoteFragment).onBackPressed()) {
                    super.onBackPressed()
                }
            }
            else -> {
                binding.bottomNav.visibility = View.VISIBLE
                binding.view.visibility = View.VISIBLE
                super.onBackPressed()
            }
        }
    }
}
