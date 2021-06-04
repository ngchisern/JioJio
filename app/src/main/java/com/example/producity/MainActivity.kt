package com.example.producity

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.producity.databinding.ActivityMainBinding
import com.example.producity.ui.home.HomeViewModel
import com.example.producity.ui.home.HomeViewModelFactory
import com.example.producity.ui.home.ScheduleDetail
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels() {
        HomeViewModelFactory((application as MyApplication).scheduleRepository)
    }
    private lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard,
                R.id.navigation_notifications, R.id.navigation_friends
            )
        )

        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("New Activity")

        val input = EditText(this)
        input.setHint("Enter Activity")
        input.inputType = InputType.TYPE_CLASS_TEXT

        val view: View = LayoutInflater.from(this).inflate(R.layout.add_schedule, null)

        builder.setView(view)

        builder.setPositiveButton("Done", DialogInterface.OnClickListener { dialog, which ->
            val text1: EditText = view.findViewById(R.id.taskInput1)
            val text2: EditText = view.findViewById(R.id.taskInput2)
            homeViewModel.insert(
                ScheduleDetail(
                    homeViewModel.targetDate.value!!,
                    text1.text.toString(),
                    text2.text.toString()
                )
            )
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        builder.show()

    }

}