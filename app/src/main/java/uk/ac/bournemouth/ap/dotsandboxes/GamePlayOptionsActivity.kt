package uk.ac.bournemouth.ap.dotsandboxes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import uk.ac.bournemouth.ap.dotsandboxes.common.Constants
import uk.ac.bournemouth.ap.dotsandboxes.common.PreferenceManager

/*
 * Provides options to select game play mode
 */
class GamePlayOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_play_options)

        // hide the action bar
        supportActionBar!!.hide()

    }

    // Game play is selected, save in preference and start the game
    fun humanVsHumanClicked(view: View) {
        PreferenceManager.getInstance().setGamePlayMode(Constants.GAME_PLAY_1)
        startGame()
    }

    // Game play is selected, save in preference and start the game
    fun humanVsComputerClicked(view: View) {
        PreferenceManager.getInstance().setGamePlayMode(Constants.GAME_PLAY_2)
        startGame()
    }

    // start game
    private fun startGame() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }

}
