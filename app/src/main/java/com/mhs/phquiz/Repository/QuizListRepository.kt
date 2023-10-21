package com.mhs.phquiz.Repository

import com.mhs.phquiz.Api.ApiService
import com.mhs.phquiz.Utils.DataStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class QuizListRepository @Inject constructor(private val apiService: ApiService) {

    //getQuizList
    suspend fun getQuizList() = flow {
        emit(DataStatus.loading())
        val result = apiService.getCoinsList()
        when (result.code()) {
            200 -> {
                emit(DataStatus.success(result.body()))
            }

            400 -> {
                emit(DataStatus.error(result.message()))
            }

            500 -> {
                emit(DataStatus.error(result.message()))
            }
        }
    }.catch {
        emit(DataStatus.error(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}