package com.sprtcoding.safeako.admin.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.admin.fragment.viewmodel.ReportViewModel
import com.sprtcoding.safeako.databinding.FragmentReportBinding
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils

class ReportFragment : Fragment() {
    private lateinit var tvMaleCount: TextView
    private lateinit var tvFemaleCount: TextView
    private lateinit var positiveCount: TextView
    private lateinit var negativeCount: TextView
    private lateinit var chartGender: PieChart
    private lateinit var chartRelationship: PieChart
    private lateinit var chartBirthMother: PieChart
    private lateinit var chartSexual1: PieChart
    private lateinit var chartSexual2: PieChart
    private lateinit var chartSexual3: PieChart
    private lateinit var chartSexual4: PieChart
    private lateinit var chartSexual5: PieChart
    private lateinit var chartSexual6: PieChart
    private lateinit var chartSexual7: PieChart
    private lateinit var chartReason: PieChart
    private lateinit var chartMedical: PieChart
    private lateinit var reportViewModel: ReportViewModel

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using ViewBinding
        _binding = FragmentReportBinding.inflate(inflater, container, false)

        initViews()
        init()
        afterInit()

        return binding.root
    }

    private fun initViews() {
        tvMaleCount = binding.tvMaleCount
        tvFemaleCount = binding.tvFemaleCount
        chartGender = binding.chartGender
        chartRelationship = binding.chartRelationship
        chartBirthMother = binding.chartBirthMother
        chartSexual1 = binding.chartSexual1
        chartSexual2 = binding.chartSexual2
        chartSexual3 = binding.chartSexual3
        chartSexual4 = binding.chartSexual4
        chartSexual5 = binding.chartSexual5
        chartSexual6 = binding.chartSexual6
        chartSexual7 = binding.chartSexual7
        chartReason = binding.chartReason
        chartMedical = binding.chartMedical
        positiveCount = binding.tvPositiveCount
        negativeCount = binding.tvNegativeCount
    }

    private fun init() {
        reportViewModel = ViewModelProvider(this)[ReportViewModel::class.java]

        reportViewModel.getGenderCount()
        reportViewModel.getRelationshipCount()
        reportViewModel.getBirthMotherCount()
        reportViewModel.getPositiveCount()
        reportViewModel.getNegativeCount()
    }

    private fun afterInit() {
        observer()
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        reportViewModel.positiveCount.observe(viewLifecycleOwner) { positive ->
            positiveCount.text = "$positive"
        }

        reportViewModel.negativeCount.observe(viewLifecycleOwner) { negative ->
            negativeCount.text = "$negative"
        }

        reportViewModel.genderCount.observe(viewLifecycleOwner) { genderCount ->
            val maleCount = genderCount.first
            val femaleCount = genderCount.second

            tvMaleCount.text = "Male: $maleCount"
            tvFemaleCount.text = "Female: $femaleCount"
            // Update PieChart
            updateGenderChart(maleCount, femaleCount)
        }

        reportViewModel.relationshipCount.observe(viewLifecycleOwner) { relationshipCount ->
            updateRelationshipChart(relationshipCount)
        }

        reportViewModel.birthMotherCount.observe(viewLifecycleOwner) { birthMotherCount ->
            updateBirthMotherChart(birthMotherCount)
        }

        Utils.getSexualCount("Sex with a male/female with no condom") { noCount, yesCount, yes2Count ->
            updateSexualChart(listOf(noCount, yesCount, yes2Count), chartSexual1)
        }

        Utils.getSexualCount("Sex with someone whom you know has HIV") { noCount, yesCount, yes2Count ->
            updateSexualChart(listOf(noCount, yesCount, yes2Count), chartSexual2)
        }

        Utils.getSexualCount("Paying for sex") { noCount, yesCount, yes2Count ->
            updateSexualChart(listOf(noCount, yesCount, yes2Count), chartSexual3)
        }

        Utils.getSexualCount("Injected drugs without doctor's advice") { noCount, yesCount, yes2Count ->
            updateSexualChart(listOf(noCount, yesCount, yes2Count), chartSexual4)
        }

        Utils.getSexualCount("Received blood transfusion") { noCount, yesCount, yes2Count ->
            updateSexualChart(listOf(noCount, yesCount, yes2Count), chartSexual5)
        }

        Utils.getSexualCount("Occupational exposure (needlestick/sharps)") { noCount, yesCount, yes2Count ->
            updateSexualChart(listOf(noCount, yesCount, yes2Count), chartSexual6)
        }

        Utils.getSexualCount("Gotten a tattoo") { noCount, yesCount, yes2Count ->
            updateSexualChart(listOf(noCount, yesCount, yes2Count), chartSexual7)
        }

        Utils.getReasonMedicalCount("Reason for submitting pre-assessment for counseling and testing") { c1, c2, c3, c4, c5 ->
            updateReasonChart(listOf(c1, c2, c3, c4, c5), chartReason)
        }

        Utils.getReasonMedicalCount("Medical History") { c1, c2, c3, c4, c5 ->
            updateMedicalChart(listOf(c1, c2, c3, c4, c5), chartMedical)
        }

    }

    private fun updateGenderChart(maleCount: Int, femaleCount: Int) {
        val entries = listOf(
            PieEntry(maleCount.toFloat(), "Male"),
            PieEntry(femaleCount.toFloat(), "Female")
        )
        setupPieChart(chartGender, entries, mutableListOf(
            ContextCompat.getColor(requireContext(), R.color.blue),
            ContextCompat.getColor(requireContext(), R.color.pink)
        ))
    }

    private fun updateRelationshipChart(relationshipCount: List<Int>) {
        val entries = listOf(
            PieEntry(relationshipCount[0].toFloat(), "No Partner"),
            PieEntry(relationshipCount[1].toFloat(), "Multiple Partner"),
            PieEntry(relationshipCount[2].toFloat(), "Steady Partner")
        )
        setupPieChart(chartRelationship, entries, mutableListOf(
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.dark_blue),
            ContextCompat.getColor(requireContext(), R.color.violet)
        ))
    }

    private fun updateBirthMotherChart(birthMotherCount: List<Int>) {
        val entries = listOf(
            PieEntry(birthMotherCount[0].toFloat(), "Do Not Know"),
            PieEntry(birthMotherCount[1].toFloat(), "No"),
            PieEntry(birthMotherCount[2].toFloat(), "Yes")
        )
        setupPieChart(chartBirthMother, entries, mutableListOf(
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.dark_blue),
            ContextCompat.getColor(requireContext(), R.color.violet)
        ))
    }

    private fun updateSexualChart(countList: List<Int>, chart: PieChart) {
        val entries = listOf(
            PieEntry(countList[0].toFloat(), "No"),
            PieEntry(countList[1].toFloat(), "Yes; the most recent time was within the past 12 months"),
            PieEntry(countList[2].toFloat(), "Yes; the most recent time was more than 12 months ago")
        )
        setupPieChart(chart, entries, mutableListOf(
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.dark_blue),
            ContextCompat.getColor(requireContext(), R.color.violet)
        ))
    }

    private fun updateReasonChart(countList: List<Int>, chart: PieChart) {
        val entries = listOf(
            PieEntry(countList[0].toFloat(), "Possible exposure to STIs"),
            PieEntry(countList[1].toFloat(), "Recommended by physician/nurse/midwife"),
            PieEntry(countList[2].toFloat(), "Referred by peer educator"),
            PieEntry(countList[3].toFloat(), "Employment - Overseas/Abroad"),
            PieEntry(countList[4].toFloat(), "Employment Local/Philippines")
        )
        setupPieChart(chart, entries, mutableListOf(
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.dark_blue),
            ContextCompat.getColor(requireContext(), R.color.violet)
        ))
    }

    private fun updateMedicalChart(countList: List<Int>, chart: PieChart) {
        val entries = listOf(
            PieEntry(countList[0].toFloat(), "Current TB patient"),
            PieEntry(countList[1].toFloat(), "With Hepatitis B"),
            PieEntry(countList[2].toFloat(), "Diagnosed with other STIs"),
            PieEntry(countList[3].toFloat(), "With Hepatitis C"),
            PieEntry(countList[4].toFloat(), "Currently Pregnant")
        )
        setupPieChart(chart, entries, mutableListOf(
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.dark_blue),
            ContextCompat.getColor(requireContext(), R.color.violet)
        ))
    }

    private fun setupPieChart(chart: PieChart, entries: List<PieEntry>, colors: MutableList<Int>) {
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors

        val pieData = PieData(dataSet)
        chart.data = pieData

        chart.legend.textSize = 7f
        chart.setEntryLabelTextSize(7f)
        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        chart.setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.white))
        chart.description.isEnabled = false
        chart.animateY(1000)
        chart.invalidate()
    }

}