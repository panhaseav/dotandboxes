package org.example.student.dotsboxgame

import uk.ac.bournemouth.ap.dotsandboxeslib.AbstractDotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.ComputerPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.Player
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.Matrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableSparseMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.SparseMatrix

/*
 * Implementation of Game
 */
class StudentDotsBoxGame(val width: Int, val height: Int, listOfPlayers: List<Player>) : AbstractDotsAndBoxesGame() {

    // list of players
    override val players: List<Player> = listOfPlayers

    // index of current player or player who has a turn
    private var currentPlayerIndex = 0

    // current player
    override val currentPlayer: Player = players[currentPlayerIndex]

    // selected lines
    private val listOfSelectedLines = ArrayList<StudentLine>()

    // selected or created boxes
    private val selectedBoxes = ArrayList<StudentBox>()

    // create a box
    private fun addSelectedBox(x: Int, y: Int) {
        val selectedBoxOB = StudentBox(x, y)
        selectedBoxOB.boxAddedPlayerIndex = currentPlayerIndex
        selectedBoxes.add(selectedBoxOB)
    }

    // getter for index
    fun getCurrentPlayerIndex() = currentPlayerIndex

    // change the turn of player
    // also check if game is finish or not
    private fun changeTurn() {
        currentPlayerIndex = if (currentPlayerIndex == 0) 1 else 0
        fireGameChange()

        // if game is finished then fire the game over event
        if (checkGameFinished()) {
            val pair1 = Pair(players[0], getPlayerBoxesCount(0))
            val pair2 = Pair(players[1], getPlayerBoxesCount(1))
            fireGameOver(listOf(pair1, pair2))
        }

    }

    // check game finished
    // width x height = 5 x 5 = 25
    // so maximum 25 boxes
    // if selected boxes are greater than or equal to 25
    // then game is over, as all boxes are filled
    fun checkGameFinished(): Boolean {
        val maxBoxes = width * height
        return selectedBoxes.size >= maxBoxes
    }

    // add line to selected line list
    // check if line is already selected then do nothing
    // else add the line
    fun addLineToSelectedList(x: Int, y: Int, direction: Direction) {
        if (isLineSelected(x, y, direction) == null) {
            val studentOb = StudentLine(x, y)
            studentOb.direction = direction
            studentOb.lineAddedByPlayer = currentPlayerIndex
            listOfSelectedLines.add(studentOb)

            // check whether user has created a box with this turn or not
            val isBoxMade = tryToOccupyBox(studentOb)

            // fire the game change listeners
            fireGameChange()

            // if user has created a box
            // then allow user to perform another turn
            // else change the turn
            if (!isBoxMade) {
                changeTurn()
            }

            // check if player is computer
            if (players[getCurrentPlayerIndex()] is ComputerPlayerImpl) {
                // play for computer
                val player = players[getCurrentPlayerIndex()] as ComputerPlayerImpl
                // make arrangements
                player.makeMove(this)
                // get the line to draw
                var line = player.getLineToDraw()
                // try to get another line, if previous line was null
                while (line == null) {
                    player.makeMove(this)
                    line = player.getLineToDraw()
                }
                // add line to view
                addLineToSelectedList(line.lineX, line.lineY, line.direction!!)
                fireGameChange()
            }

        }
    }

    // check the given x,y with direction are already selected or not
    // return selected Line or return null if not selected
    fun isLineSelected(x: Int, y: Int, direction: Direction): StudentLine? {
        listOfSelectedLines.forEach {
            if (it.lineX == x && it.lineY == y && it.direction == direction) {
                return it
            }
        }
        return null
    }

    // check the given x,y with direction are already selected or not
    // return type is boolean
    fun isLineSelected(direction: Direction, x: Int, y: Int): Boolean {
        listOfSelectedLines.forEach {
            if (it.lineX == x && it.lineY == y && it.direction == direction) {
                return true
            }
        }
        return false
    }

    // check whether given x and y has a box created or not
    fun isBoxOccupied(x: Int, y: Int): StudentBox? {
        selectedBoxes.forEach {
            if (it.boxX == x && it.boxY == y) {
                return it
            }
        }
        return null
    }

    override val boxes: Matrix<StudentBox> = MutableMatrix(width, height, ::StudentBox)

    override val lines: SparseMatrix<StudentLine> = MutableSparseMatrix(width + 1, height * 2 + 1, ::StudentLine) { x, y ->
        y % 2 == 1 || x < width
    }

    override val isFinished: Boolean = checkGameFinished()

    override fun playComputerTurns() {
        var current = currentPlayer
        while (current is ComputerPlayer && !isFinished) {
            current.makeMove(this)
            current = currentPlayer
        }
    }

    // get the boxes count for a player
    fun getPlayerBoxesCount(playerIndex: Int): Int {
        var count = 0
        selectedBoxes.forEach { box ->
            if (box.boxAddedPlayerIndex == playerIndex) {
                count++
            }
        }
        return count
    }


    /**
     * This is an inner class as it needs to refer to the game to be able to look up the correct
     * lines and boxes. Alternatively you can have a game property that does the same thing without
     * it being an inner class.
     */
    inner class StudentLine(lineX: Int, lineY: Int) : AbstractLine(lineX, lineY) {

        var direction: Direction? = null
        var lineAddedByPlayer: Int = -99

        override var isDrawn: Boolean = false

        override val adjacentBoxes: Pair<StudentBox?, StudentBox?> = Pair(null, null)

        override fun drawLine() {
            lines.forEach {
                if (it.lineX == lineX && it.lineY == lineY) {
                    it.isDrawn = true
                }
            }
        }

        private fun checkIfLineIsDrawn(): Boolean {
            lines.forEach {
                if (it.lineX == lineX && it.lineY == lineY) {
                    return it.isDrawn
                }
            }
            return false
        }

    }

    inner class StudentBox(boxX: Int, boxY: Int) : AbstractBox(boxX, boxY) {

        var boxAddedPlayerIndex: Int = -99

        override val owningPlayer: Player? = if (boxAddedPlayerIndex == -99) null else players[boxAddedPlayerIndex]

        override val boundingLines: Iterable<DotsAndBoxesGame.Line> = lines
    }

    // user has performed a move
    // or user has drawn a line
    // check if the line completes a box or not
    private fun tryToOccupyBox(move: StudentLine): Boolean {
        val rightOccupied = tryToOccupyRightBox(move)
        val underOccupied = tryToOccupyUnderBox(move)
        val upperOccupied = tryToOccupyUpperBox(move)
        val leftOccupied = tryToOccupyLeftBox(move)
        return leftOccupied || rightOccupied || upperOccupied || underOccupied
    }

    // check if the line completes a box above it
    private fun tryToOccupyUpperBox(move: StudentLine): Boolean {
        // if direction is not horizontal then do not need to calculate further
        if (move.direction !== Direction.HORIZONTAL || move.lineX <= 0) return false
        return if (isLineSelected(Direction.HORIZONTAL, move.lineX - 1, move.lineY)
            && isLineSelected(Direction.VERTICAL, move.lineX - 1, move.lineY)
            && isLineSelected(Direction.VERTICAL, move.lineX - 1, move.lineY + 1)
        ) {
            addSelectedBox(move.lineX - 1, move.lineY)
            true
        } else {
            false
        }
    }

    // check if the line completes a box below it
    private fun tryToOccupyUnderBox(move: StudentLine): Boolean {
        if (move.direction !== Direction.HORIZONTAL || move.lineX >= height) return false
        return if (isLineSelected(Direction.HORIZONTAL, move.lineX + 1, move.lineY)
            && isLineSelected(Direction.VERTICAL, move.lineX, move.lineY)
            && isLineSelected(Direction.VERTICAL, move.lineX, move.lineY + 1)
        ) {
            addSelectedBox(move.lineX, move.lineY)
            true
        } else {
            false
        }
    }

    // check if the line completes a box left to it
    private fun tryToOccupyLeftBox(move: StudentLine): Boolean {
        if (move.direction !== Direction.VERTICAL || move.lineY <= 0) return false
        return if (isLineSelected(Direction.VERTICAL, move.lineX, move.lineY - 1)
            && isLineSelected(Direction.HORIZONTAL, move.lineX, move.lineY - 1)
            && isLineSelected(Direction.HORIZONTAL, move.lineX + 1, move.lineY - 1)
        ) {
            addSelectedBox(move.lineX, move.lineY - 1)
            true
        } else {
            false
        }
    }

    // check if the line completes a box right to it
    private fun tryToOccupyRightBox(move: StudentLine): Boolean {
        if (move.direction !== Direction.VERTICAL || move.lineY >= width) return false
        return if (isLineSelected(Direction.VERTICAL, move.lineX, move.lineY + 1)
            && isLineSelected(Direction.HORIZONTAL, move.lineX, move.lineY)
            && isLineSelected(Direction.HORIZONTAL, move.lineX + 1, move.lineY)
        ) {
            addSelectedBox(move.lineX, move.lineY)
            true
        } else {
            false
        }
    }

}