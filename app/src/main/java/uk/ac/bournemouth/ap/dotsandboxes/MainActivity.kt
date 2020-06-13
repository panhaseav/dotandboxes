package uk.ac.bournemouth.ap.dotsandboxes

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.example.student.dotsboxgame.StudentDotsBoxGame
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.HumanPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.Player


/*
 * Main Screen of Game
 */
class MainActivity : AppCompatActivity() {

    // create variables
    private lateinit var gameView: GameView
    private lateinit var player1View: LinearLayout
    private lateinit var player2View: LinearLayout
    private lateinit var player1Boxes: TextView
    private lateinit var player2Boxes: TextView
    private lateinit var time: TextView
    private var isPlayerNamesSet = false
    private var isWinnerShown = false

    // time interval values
    // one second is 1000 milli seconds
    private var oneSecond = 1000L

    // 3 minutes will be 180 seconds
    private var totalGameTime = 180 * oneSecond

    // current game object
    private lateinit var gameObject: StudentDotsBoxGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // variables initialization
        gameView = findViewById(R.id.gameView)
        player1View = findViewById(R.id.player1View)
        player2View = findViewById(R.id.player2View)
        player1Boxes = findViewById(R.id.player1Boxes)
        player2Boxes = findViewById(R.id.player2Boxes)
        time = findViewById(R.id.time)

        // hide the action bar
        supportActionBar!!.hide()

        // attaching the game change listener
        // so whenever state of game is change, we can also show the relevant information
        // in the user interface
        gameView.setGameChangeListener(object : DotsAndBoxesGame.GameChangeListener {
            override fun onGameChange(game: DotsAndBoxesGame) {

                gameObject = game as StudentDotsBoxGame

                // check if player name is not already set
                // then set it to view
                // since player name would not change as game goes
                // so we do not need to refresh it views everytime
                // game state is changed

                if (!isPlayerNamesSet) {

                    // check if player is Human then show human else show computer
                    if (game.players[0] is HumanPlayer) {
                        findViewById<TextView>(R.id.player1Text).text = "Human"
                    } else {
                        findViewById<TextView>(R.id.player1Text).text = "Computer"
                    }

                    // check if player is Human then show human else show computer
                    if (game.players[1] is HumanPlayer) {
                        findViewById<TextView>(R.id.player2Text).text = "Human"
                    } else {
                        findViewById<TextView>(R.id.player2Text).text = "Computer"
                    }

                    // set the boolean as true, since we have displayed the names
                    isPlayerNamesSet = true
                }

                // check which player has a turn
                // if player index is 0 then player 1 has turn
                // so show the green border for player 1
                // and show grey bolder for player 2

                if (gameView.getCurrentPlayerIndex() == 0) {
                    player1View.setBackgroundResource(R.drawable.btn_bg_green)
                    player2View.setBackgroundResource(R.drawable.btn_bg_grey)
                } else {
                    player2View.setBackgroundResource(R.drawable.btn_bg_green)
                    player1View.setBackgroundResource(R.drawable.btn_bg_grey)
                }

                // game state is change
                // check the number of boxes for each player
                // player 1 count of boxes
                val player1Count = gameObject.getPlayerBoxesCount(0)
                // player 2 count of boxes
                val player2Count = gameObject.getPlayerBoxesCount(1)

                // show in the view
                player1Boxes.text = "Boxes : $player1Count"
                player2Boxes.text = "Boxes : $player2Count"

                // check if game is finished or not
                // if game is finished then show winner
                if (gameObject.checkGameFinished()) {
                    // check if we have not already shown the winner
                    if (!isWinnerShown) {
                        // manage boolean so we do not show winner multiple times
                        isWinnerShown = true
                        // show winner of game
                        showWinner(player1Count, player2Count)
                    }
                }
            }
        })

        // listen for the finish of game
        gameView.setGameFinishListener(object : DotsAndBoxesGame.GameOverListener {
            override fun onGameOver(game: DotsAndBoxesGame, scores: List<Pair<Player, Int>>) {
                // get players
                val player1 = scores[0]
                val player2 = scores[1]
                // check if we have not already shown the winner
                if (!isWinnerShown) {
                    // manage boolean so we do not show winner multiple times
                    isWinnerShown = true
                    // show winner of game
                    showWinner(player1.second, player2.second)
                }
            }
        })

        object : CountDownTimer(totalGameTime, oneSecond) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = millisUntilFinished / oneSecond
                time.text = getFormattedDuration(remaining)
            }

            override fun onFinish() {
                // player 1 count of boxes
                val player1Count = gameObject.getPlayerBoxesCount(0)
                // player 2 count of boxes
                val player2Count = gameObject.getPlayerBoxesCount(1)

                // game is finished
                if (!isWinnerShown) {
                    // manage boolean so we do not show winner multiple times
                    isWinnerShown = true
                    // show winner of game
                    showWinner(player1Count, player2Count)
                }
            }
        }.start()


    }

    //show winner
    private fun showWinner(score1: Int, score2: Int) {

        // check for high scores
        val message = if (score1 == score2) {
            "Game Draw.\nPlayer 1 Score = $score1\nPlayer 2 Score = $score2"
        } else if (score1 > score2) {
            "Player 1  ( RED )  Has won the game.\nPlayer 1 Score = $score1\nPlayer 2 Score = $score2"
        } else {
            "Player 2  ( BLUE )  Has won the game.\nPlayer 1 Score = $score1\nPlayer 2 Score = $score2"
        }

        // dialog
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            dialogInterface.dismiss()
            finish()
        }
        builder.setCancelable(false)
        val alert = builder.create()
        alert.show()
    }

    fun getFormattedDuration(milliseconds: Long): String {
        val seconds = milliseconds % 60
        val minutes = (milliseconds % 3600) / 60
        val secondsStr = if (seconds <= 9) "0$seconds" else seconds.toString()
        val minutesStr = if (minutes <= 9) "0$minutes" else minutes.toString()
        return "$minutesStr:$secondsStr"
    }

}
