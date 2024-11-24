package com.sprtcoding.safeako.user.fragment.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.sprtcoding.safeako.R
import com.sprtcoding.safeako.model.AnswerOption

class AnswerAdapter(private val answers: List<AnswerOption>,
                    private val callback: CheckboxCallback) : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbAnswer: MaterialCheckBox = itemView.findViewById(R.id.cb_answer)
    }

    // List to maintain the state of each checkbox (true for checked, false for unchecked)
    private val checkboxStates: MutableList<Boolean> = MutableList(answers.size) { false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.answers_ll, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val answer = answers[position]

        holder.cbAnswer.text = answer.answerText
        // Temporarily remove the listener to avoid triggering it during programmatic updates
        holder.cbAnswer.setOnCheckedChangeListener(null)
        holder.cbAnswer.isChecked = checkboxStates[position]

        holder.cbAnswer.setOnCheckedChangeListener { _, isChecked ->
            checkboxStates[position] = isChecked
            callback.onCheckboxChecked(answer.position, answer.answerText, answer.question, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return answers.size
    }

    // Interface for handling checkbox selections
    interface CheckboxCallback {
        fun onCheckboxChecked(position: Int, answerSelected: String, q: String, isCheck: Boolean)
    }

    // Method to reset all checkboxes
    @SuppressLint("NotifyDataSetChanged")
    fun resetCheckboxes() {
        // Reset the checkbox states to false (unchecked)
        checkboxStates.fill(false)

        // Notify the adapter to refresh the UI and show the updated checkbox states
        notifyDataSetChanged()
    }
}
