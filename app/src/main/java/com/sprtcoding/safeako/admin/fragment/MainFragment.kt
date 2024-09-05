package com.sprtcoding.safeako.admin.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.google.android.material.card.MaterialCardView
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.authentication.login.LoginActivity
import com.sprtcoding.safeako.firebaseUtils.Utils

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    private lateinit var view: View
    private lateinit var barChart: BarChart
    private lateinit var entries: ArrayList<BarEntry>
    private lateinit var btnProfile: MaterialCardView
    private lateinit var loading: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false)

        initViews()
        init()
        afterInit()

        return view
    }

    private fun initViews() {
        barChart = view.findViewById(R.id.bar_chart)
        btnProfile = view.findViewById(R.id.btn_profile)
    }

    private fun init() {
        loading = ProgressDialog(context)
        loading.setMessage("Please wait...")

        entries = ArrayList()
        entries.add(BarEntry(1f, 2f))
        entries.add(BarEntry(2f, 4f))
        entries.add(BarEntry(3f, 6f))
        entries.add(BarEntry(4f, 8f))
    }

    private fun afterInit() {
        // Create a dataset and give it a type
        val dataSet = BarDataSet(entries, "Label") // add entries to dataset
        dataSet.color = Color.RED
        dataSet.valueTextColor = Color.BLACK

        // Create a data object with the dataset
        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.setFitBars(true)
        // Refresh the chart
        barChart.invalidate()
        barChart.animate()

        btnProfile.setOnClickListener {
            loading.show()

            Handler(Looper.getMainLooper()).postDelayed({
                context?.let { it1 -> Utils.logout({ success, message ->
                    if (success) {
                        loading.dismiss()
                        Toast.makeText(it1, message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(it1, LoginActivity::class.java))
                        activity?.finish()
                    } else {
                        loading.dismiss()
                        Toast.makeText(it1, message, Toast.LENGTH_SHORT).show()
                    }
                }, it1) }
            }, 2000)
        }
    }

}