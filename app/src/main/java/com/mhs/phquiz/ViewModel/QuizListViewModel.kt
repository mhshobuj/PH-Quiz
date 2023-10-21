package com.mhs.phquiz.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhs.phquiz.Repository.QuizListRepository
import com.mhs.phquiz.Response.QuizListResponse
import com.mhs.phquiz.Utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizListViewModel @Inject constructor(private val apiRepository: QuizListRepository) :
    ViewModel() {

    //getQuizList
    private val _quizList = MutableLiveData<DataStatus<QuizListResponse>>()
    val quizList: LiveData<DataStatus<QuizListResponse>> get() = _quizList

    fun getQuizList() = viewModelScope.launch {
        apiRepository.getQuizList().collect {
            _quizList.value = it
        }
    }
}