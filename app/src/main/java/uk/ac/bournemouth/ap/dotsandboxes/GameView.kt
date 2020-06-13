package uk.ac.bournemouth.ap.dotsandboxes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import org.example.student.dotsboxgame.ComputerPlayerImpl
import org.example.student.dotsboxgame.Direction
import org.example.student.dotsboxgame.StudentDotsBoxGame
import uk.ac.bournemouth.ap.dotsandboxes.common.Constants
import uk.ac.bournemouth.ap.dotsandboxes.common.PreferenceManager
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.HumanPlayer

/*
 * GameView is responsible for displaying and handling the canvas of game
 */
class GameView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    // game board be of 3 x 3 , 5 x 5 , 7 x 7 size
    // boardWidth x boardHeight
    //in this case mine is 5 x 5

    // width of board
    private val boardWidth = 5

    // height of board
    private val boardHeight = 5

    // game object
    private var mStudentGame: StudentDotsBoxGame

    // paint for player 1
    private var mPlayer1Paint: Paint

    // paint for player 2
    private var mPlayer2Paint: Paint

    // paint for board
    private var mBoardPaint: Paint

    // paint for default lines
    private var mLinesPaint: Paint

    // paint for point
    private var mDotPaint: Paint

    // maximum screen width
    private var maxWidth = 824

    // radius of the dot
    private var radius = 0F

    // from where the game board should start displaying lines,dots etc
    private var start = 0F

    // use grid helper to responsible for spacing between lines, size of box and dot etc
    // since we have 5x5 we would have 6 rows and 6 column
    private var gridHelper1 = 0F
    private var gridHelper2 = 0F
    private var gridHelper3 = 0F
    private var gridHelper4 = 0F
    private var gridHelper5 = 0F
    private var gridHelper6 = 0F

    // reset or calculate the grid helpers values
    private fun setGridHelpers() {
        radius = 14.toFloat() / maxWidth
        start = 6.toFloat() / maxWidth
        gridHelper1 = 18.toFloat() / maxWidth
        gridHelper2 = 2.toFloat() / maxWidth
        gridHelper3 = 14.toFloat() / maxWidth
        gridHelper4 = 141.toFloat() / maxWidth
        gridHelper5 = 159.toFloat() / maxWidth
        gridHelper6 = 9.toFloat() / maxWidth
    }

    // gesture detector object
    private val myGestureDetector = GestureDetector(context, GestureListener())

    // over ride the draw method
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // set background color for board
        canvas.drawColor(ContextCompat.getColor(context, R.color.board_bg))

        // calculate the minimum value between available screen width and height
        val min = width.coerceAtMost(height)
        radius *= min
        start *= min
        gridHelper1 *= min
        gridHelper2 *= min
        gridHelper4 *= min
        gridHelper5 *= min
        gridHelper6 *= min

        var horizontalPaint: Paint? = null
        var verticalPaint: Paint? = null

        // display the lines
        // here i loop goes until boardWidth + 1
        // because to make proper box in 5 x 5 board
        // we need 6 lines
        // so i loop goes for 6 iterations
        for (i in 0 until boardWidth + 1) {
            for (j in 0 until boardHeight) {

                /*
                check if the current row ( i ) and column ( j ) is selected by any player or not
                   we are also passing the direction as horizontal / vertical
                   because we have board like

                    0,0   0,1   0,2   0,3   0,4   0,5   0,6
                    1,0   1,1   1,2   1,3   1,4   1,5   1,6
                    2,0   2,1   2,2   2,3   2,4   2,5   2,6
                    3,0   3,1   3,2   3,3   3,4   3,5   3,6
                    4,0   4,1   4,2   4,3   4,4   4,5   4,6
                    5,0   5,1   5,2   5,3   5,4   5,5   5,6

                    because from point 2,3 a line can be
                    2,3 . . 2,4
                    .
                    .
                    3,3

                    so we need to know if line is vertical or horizontal from a specific point

                */

                // check for horizontal and vertical lines
                val lineHorizontal = mStudentGame.isLineSelected(i, j, Direction.HORIZONTAL)
                val lineVertical = mStudentGame.isLineSelected(j, i, Direction.VERTICAL)

                //  if line is not null
                // then it is selected by a player
                // show color of relevant player for a line
                horizontalPaint = if (lineHorizontal != null) {
                    if (lineHorizontal.lineAddedByPlayer == 0) {
                        mPlayer1Paint
                    } else {
                        mPlayer2Paint
                    }
                } else {
                    mLinesPaint
                }

                //  if line is not null
                // then it is selected by a player
                // show color of relevant player for a line
                verticalPaint = if (lineVertical != null) {
                    if (lineVertical.lineAddedByPlayer == 0) {
                        mPlayer1Paint
                    } else {
                        mPlayer2Paint
                    }
                } else {
                    mLinesPaint
                }




                // draw a line
                canvas.drawRect(
                    start + gridHelper5 * j + gridHelper1,
                    start + gridHelper5 * i + gridHelper2,
                    start + gridHelper5 * (j + 1),
                    start + gridHelper5 * i + gridHelper1 - gridHelper2,
                    horizontalPaint
                )

                // draw a line
                canvas.drawRect(
                    start + gridHelper5 * i + gridHelper2,
                    start + gridHelper5 * j + gridHelper1,
                    start + gridHelper5 * i + gridHelper1 - gridHelper2,
                    start + gridHelper5 * (j + 1),
                    verticalPaint
                )

            }
        }

        //paint boxes
        for (i in 0 until boardWidth) {
            for (j in 0 until boardHeight) {

                // check if box is already selected or not
                val box = mStudentGame.isBoxOccupied(j, i)
                if (box != null) {
                    canvas.drawRect(
                        start + gridHelper5 * i + gridHelper1 + gridHelper2,
                        start + gridHelper5 * j + gridHelper1 + gridHelper2,
                        (start + gridHelper5 * i + gridHelper1 + gridHelper4) - gridHelper2,
                        start + gridHelper5 * j + gridHelper1 + gridHelper4 - gridHelper2,
                        if (box.boxAddedPlayerIndex == 0) mPlayer1Paint else mPlayer2Paint
                    )
                }

            }
        }

        //paint points
        for (row in 0 until boardHeight + 1) {
            for (col in 0 until boardWidth + 1) {
                canvas.drawCircle(
                    start + gridHelper6 + col * gridHelper5 + 1,
                    start + gridHelper6 + row * gridHelper5 + 1,
                    radius,
                    mDotPaint
                )
            }
        }

    }

    // override the on touch event and pass the motion event object to our gesture detector
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        myGestureDetector.onTouchEvent(ev)
        return false
    }

    // gesture detector
    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            // get the x and y values from touch event
            val touchX: Float = event.x
            val touchY: Float = event.y

            var d = -1 // direction of line touch i-e horizontal or vertical
            var a = -1 // row value
            var b = -1 // column value
            var isFound = false

            // we are using the same variables which were used earlier to
            // draw the liens, to find out the touch place
            for (i in 0..boardWidth + 1) {
                if (isFound) {
                    break
                }
                for (j in 0..boardHeight) {
                    if ((start + gridHelper5 * j + gridHelper1 - gridHelper3) <= touchX
                        && touchX <= (start + gridHelper5 * (j + 1) + gridHelper3)
                        && touchY >= start + gridHelper5 * i + gridHelper2 - gridHelper3
                        && touchY <= start + gridHelper5 * i + gridHelper1 - gridHelper2 + gridHelper3
                    ) {
                        d = 0
                        a = i
                        b = j
                        // we have found the touch area, break out of loops
                        isFound = true
                        break
                    }
                    if (start + gridHelper5 * i + gridHelper2 - gridHelper3 <= touchX
                        && touchX <= start + gridHelper5 * i + gridHelper1 - gridHelper2 + gridHelper3
                        && touchY >= start + gridHelper5 * j + gridHelper1 - gridHelper3
                        && touchY <= start + gridHelper5 * (j + 1) + gridHelper3
                    ) {
                        d = 1
                        a = j
                        b = i
                        // we have found the touch area, break out of loops
                        isFound = true
                        break
                    }
                }
            }

            // if a and b are not equals to -1 then we have found the touched line
            // perform the turn
            // so d is used for direction and
            // a nad b are for x,y values
            if (a != -1 && b != -1) {
                val direction = if (d == 0) Direction.HORIZONTAL else Direction.VERTICAL
                // perform the turn
                mStudentGame.addLineToSelectedList(a, b, direction)
            }
            return true
        }

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    } // End of myGestureListener class

    // attach change listener
    fun setGameChangeListener(listener: DotsAndBoxesGame.GameChangeListener) {
        // remove any previous listener attached to game
        mStudentGame.removeOnGameChangeListener(listener)
        // add new listener
        mStudentGame.addOnGameChangeListener(listener)
        // trigger listener so view can be updated initially
        mStudentGame.fireGameChange()
    }

    // attach game finish listener
    fun setGameFinishListener(listener: DotsAndBoxesGame.GameOverListener) {
        // remove any previous listener attached to game
        mStudentGame.removeOnGameOverListener(listener)
        // add new listener
        mStudentGame.addOnGameOverListener(listener)
    }

    // method which returns the index of current player
    // or player who has a turn
    fun getCurrentPlayerIndex() = mStudentGame.getCurrentPlayerIndex()

    // initializations
    init {

        // create game object
        // check game play mode from preference
        mStudentGame = StudentDotsBoxGame(
            boardWidth,
            boardHeight,
            if (PreferenceManager.getInstance().getGamePlayMode().trim().equals(
                    Constants.GAME_PLAY_1,
                    true
                )
            ) {
                listOf(HumanPlayer(), HumanPlayer())
            } else {
                listOf(HumanPlayer(), ComputerPlayerImpl())
            }
        )
        // attach game change listener so we can update canvas properly
        mStudentGame.addOnGameChangeListener(object : DotsAndBoxesGame.GameChangeListener {
            override fun onGameChange(game: DotsAndBoxesGame) {
                setGridHelpers()
                invalidate()
            }

        })

        mPlayer1Paint = Paint().apply {
            color = Color.RED
        }
        mPlayer2Paint = Paint().apply {
            color = Color.BLUE
        }
        mBoardPaint = Paint().apply {
            color = Color.LTGRAY
        }
        mLinesPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.white)
        }
        mDotPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.pink)
        }
        setGridHelpers()

    }

    companion object {
        private const val TAG = "GameView"
    }


}