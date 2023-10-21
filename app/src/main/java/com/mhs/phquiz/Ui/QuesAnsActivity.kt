package com.mhs.phquiz.Ui

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import com.mhs.phquiz.R
import com.mhs.phquiz.Response.QuizListResponse
import com.mhs.phquiz.Utils.Constants
import com.mhs.phquiz.Utils.DataStatus
import com.mhs.phquiz.Utils.PreferenceManager
import com.mhs.phquiz.Utils.isVisible
import com.mhs.phquiz.ViewModel.QuizListViewModel
import com.mhs.phquiz.databinding.ActivityQuesAnsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuesAnsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuesAnsBinding
    private val viewModel: QuizListViewModel by viewModels()

    private var mCurrentPosition: Int = 1
    private var mQuestionList: List<QuizListResponse.Question>? = null
    private var mCorrectAnswerScore: Int = 0
    private var isSelectedAnswer: Boolean = false
    private var mSelectedItemName: String? = null
    private var mSelectedOptionPosition: Int = 0
    private var preferenceManager: PreferenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuesAnsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        getQuizList()
        defaultOptionsView()
        selectedAnswer();
    }

    private fun getQuizList() {
        lifecycleScope.launch {
            binding.apply {
                viewModel.getQuizList()
                viewModel.quizList.observe(this@QuesAnsActivity) {
                    when (it.status) {
                        DataStatus.Status.LOADING -> {
                            pBarLoading.isVisible(true, consMainLayout)
                        }

                        DataStatus.Status.SUCCESS -> {
                            pBarLoading.isVisible(false, consMainLayout)
                            mQuestionList = it.data?.questions
                            setQuestionList()
                        }

                        DataStatus.Status.ERROR -> {
                            pBarLoading.isVisible(false, consMainLayout)
                            Toast.makeText(
                                this@QuesAnsActivity,
                                "There is something wrong!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setQuestionList() {
        defaultOptionsView()
        this.mQuestionList.let {
            binding.apply {
                val questionsList = it
                var currentPosition = mCurrentPosition
                val question: QuizListResponse.Question? = questionsList?.get(currentPosition - 1)
                txtPoints.text = question?.score.toString().plus(" pont")
                Log.e("img", "" + question?.questionImageUrl)
                if (question?.questionImageUrl.toString() == "null"){
                    imgQuestion.isVisible = false
                }else {
                    imgQuestion.isVisible = true
                    imgQuestion.load(question?.questionImageUrl) {
                        crossfade(true)
                        crossfade(500)
                        //app_logo set as placeholder & error photo
                        placeholder(R.drawable.ph_logo)
                        error(R.drawable.ph_logo)
                    }
                }
                txtQuestion.text = question?.question.toString()

                //question null check and set value and invisible the other view
                if (question?.answers?.a.toString() == "null") {
                    txtQusOne.isVisible = false
                } else {
                    txtQusOne.isVisible = true
                    txtQusOne.text = question?.answers?.a.toString()
                }

                if (question?.answers?.b.toString() == "null") {
                    txtQusTwo.isVisible = false
                } else {
                    txtQusTwo.isVisible = true
                    txtQusTwo.text = question?.answers?.b.toString()
                }

                if (question?.answers?.c.toString() == "null") {
                    txtQusThree.isVisible = false
                } else {
                    txtQusThree.isVisible = true
                    txtQusThree.text = question?.answers?.c.toString()
                }

                if (question?.answers?.d.toString() == "null") {
                    txtQusFour.isVisible = false
                } else {
                    txtQusFour.isVisible = true
                    txtQusFour.text = question?.answers?.d.toString()
                }

                //set question current value and the total question
                txtQuesCurrentValue.text = mCurrentPosition.toString().plus("/")
                txtQuesTotalValue.text = mQuestionList?.size.toString()
            }
        }
    }

    private fun defaultOptionsView() {
        val options = ArrayList<AppCompatTextView>()
        binding.txtQusOne.let {
            options.add(0, it)
        }
        binding.txtQusTwo.let {
            options.add(1, it)
        }
        binding.txtQusThree.let {
            options.add(2, it)
        }
        binding.txtQusFour.let {
            options.add(3, it)
        }
        for (option in options) {
            option.setTextColor(Color.parseColor("#000000"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.button_background)
        }
    }

    private fun selectedAnswer() {
        binding.apply {
            txtQusOne.setOnClickListener {
                if (!isSelectedAnswer) {
                    selectedOptionView(1)
                }
            }
            txtQusTwo.setOnClickListener {
                if (!isSelectedAnswer) {
                    selectedOptionView(2)
                }
            }
            txtQusThree.setOnClickListener {
                if (!isSelectedAnswer) {
                    selectedOptionView(3)
                }
            }
            txtQusFour.setOnClickListener {
                if (!isSelectedAnswer) {
                    selectedOptionView(4)
                }
            }
        }
    }

    private fun selectedOptionView(selectedOptionNum: Int) {
        defaultOptionsView()
        mSelectedOptionPosition = selectedOptionNum
        isSelectedAnswer = true

        val question = mQuestionList?.get(mCurrentPosition - 1)
        question?.let {

            //selected item number check and assign value to corresponding number
            when (selectedOptionNum) {
                1 -> {
                    mSelectedItemName = "A"
                }

                2 -> {
                    mSelectedItemName = "B"
                }

                3 -> {
                    mSelectedItemName = "C"
                }

                4 -> {
                    mSelectedItemName = "D"
                }
            }

            //answer check with the response & take action according to the answer
            if (it.correctAnswer != mSelectedItemName) {
                when (it.correctAnswer) {
                    "A" -> {
                        answerView(1, R.drawable.correct_answer_background)
                    }

                    "B" -> {
                        answerView(2, R.drawable.correct_answer_background)
                    }

                    "C" -> {
                        answerView(3, R.drawable.correct_answer_background)
                    }

                    "D" -> {
                        answerView(4, R.drawable.correct_answer_background)
                    }
                }
                answerView(mSelectedOptionPosition, R.drawable.wrong_answer_background)
            } else {
                mCorrectAnswerScore += question.score
                binding.txtScore.text = mCorrectAnswerScore.toString()
                answerView(mSelectedOptionPosition, R.drawable.correct_answer_background)

                //check the current score with the pref highScore
                val highScoreFromPreferences = preferenceManager?.getInt(Constants.HIGHSCORE) ?: 0
                if (highScoreFromPreferences <= mCorrectAnswerScore) {
                    preferenceManager?.saveInt(Constants.HIGHSCORE, mCorrectAnswerScore)
                }
            }
            mSelectedOptionPosition = 0
            mCurrentPosition++
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    when {
                        mCurrentPosition <= mQuestionList!!.size -> {
                            setQuestionList()
                        }

                        else -> {
                            binding.consMainLayout.isVisible = false
                            //show the popup
                            showPopUpConfirmationWindow()
                        }
                    }
                    isSelectedAnswer = false
                },
                2000 // value in milliseconds
            )
        }
    }

    private fun answerView(answer: Int, drawableView: Int) {
        when (answer) {
            1 -> {
                binding.txtQusOne.background = ContextCompat.getDrawable(this, drawableView)
            }

            2 -> {
                binding.txtQusTwo.background = ContextCompat.getDrawable(this, drawableView)
            }

            3 -> {
                binding.txtQusThree.background = ContextCompat.getDrawable(this, drawableView)
            }

            4 -> {
                binding.txtQusFour.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }

    private fun showPopUpConfirmationWindow() {
        // Create the popup window
        val popupView = layoutInflater.inflate(R.layout.popup_window_confirmation, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Set score value in the popup window
        val txtScore: AppCompatTextView = popupView.findViewById(R.id.txtScore)
        txtScore.text =
            "Your Score is: $mCorrectAnswerScore" // Assuming currentHighScore is your score value

        // Set button click listener inside the popup window
        val backButton: AppCompatButton = popupView.findViewById(R.id.button)
        backButton.setOnClickListener {
            // Handle button click (for example, navigate back home)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            popupWindow.dismiss() // Dismiss the popup window after handling the click
        }
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }
}
