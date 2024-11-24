package com.sprtcoding.safeako.admin.fragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprtcoding.safeako.firebase.firebaseUtils.Utils

class ReportViewModel: ViewModel() {
    private val _genderCount = MutableLiveData<Pair<Int, Int>>()
    val genderCount: LiveData<Pair<Int, Int>> get() = _genderCount

    private val _positiveCount = MutableLiveData<Int>()
    val positiveCount: LiveData<Int> get() = _positiveCount

    private val _negativeCount = MutableLiveData<Int>()
    val negativeCount: LiveData<Int> get() = _negativeCount

    private val _relationshipCount = MutableLiveData<List<Int>>()
    val relationshipCount: LiveData<List<Int>> get() = _relationshipCount

    private val _birthMotherCount = MutableLiveData<List<Int>>()
    val birthMotherCount: LiveData<List<Int>> get() = _birthMotherCount

    fun getPositiveCount() {
        Utils.getPositiveCount { positiveCount ->
            _positiveCount.value = positiveCount
        }
    }

    fun getNegativeCount() {
        Utils.getNegativeCount { negativeCount ->
            _negativeCount.value = negativeCount
        }
    }

    fun getGenderCount() {
        Utils.getGenderCount { genderCount ->
            _genderCount.value = genderCount
        }
    }

    fun getRelationshipCount() {
        Utils.getRelationshipCount { noSteadyCount, multipleCount, steadyCount ->
            _relationshipCount.value = listOf(noSteadyCount, multipleCount, steadyCount)
        }
    }

    fun getBirthMotherCount() {
        Utils.getBirthMotherCount { doNotKnowCount, noCount, yesCount ->
            _birthMotherCount.value = listOf(doNotKnowCount, noCount, yesCount)
        }
    }
}