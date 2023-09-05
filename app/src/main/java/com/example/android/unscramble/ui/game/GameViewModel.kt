package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class GameViewModel : ViewModel() {

    /*
     * making variables of LiveData type (accessing via .value...)
     * and setting default values in brackets.
     */
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score
    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int> get() = _currentWordCount
    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String> get() = _currentScrambledWord

    // Transformations somehow doesn't work, but this is a way to read the scrambled word letter-by-letter,
    // via tts talkback
//    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
//        if (it == null) {
//            SpannableString("")
//        } else {
//            val scrambledWord = it.toString()
//            val spannable: Spannable = SpannableString(scrambledWord)
//            spannable.setSpan(
//                TtsSpan.VerbatimBuilder(scrambledWord).build(),
//                0,
//                scrambledWord.length,
//                Spannable.SPAN_INCLUSIVE_INCLUSIVE
//            )
//            spannable
//        }
//    }
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    private var isGameOver: Boolean = false

    init {
        getNextWord()
    }

    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            // setting currentScrambledWord LiveData value to a string from tempWord...
            // incrementing the wordCount...
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordsList.add(currentWord)
        }
    }

    /*
    * Re-initializes the game data to restart the game.
    * setting LiveData values to zero...
    */
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
        isGameOver = false
    }

    // increasing LiveData score value...
    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    fun nextWord(): Boolean {
        // comparing LiveData currentWordCount value to MAX number of words...
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else {
            isGameOver = true
            false
        }
    }

    fun isGameOver() = isGameOver
}