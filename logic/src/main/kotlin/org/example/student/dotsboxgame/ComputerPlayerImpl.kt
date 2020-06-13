package org.example.student.dotsboxgame

import org.example.student.dotsboxgame.StudentDotsBoxGame.StudentLine
import uk.ac.bournemouth.ap.dotsandboxeslib.ComputerPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame

/*
 * Implementation of Abstract Computer Player
 */
class ComputerPlayerImpl : ComputerPlayer() {

    // list of lines that are safe to play or draw by computer
    var safeLines: ArrayList<StudentDotsBoxGame.StudentLine> = ArrayList()

    // list of lines that are good to play or draw by computer, these lines can lead to further building a box
    var goodLines: ArrayList<StudentDotsBoxGame.StudentLine> = ArrayList()

    // list of lines that are bad to play or draw by computer, these lines can lead other or human player to create a box
    var badLines: ArrayList<StudentDotsBoxGame.StudentLine> = ArrayList()

    // game object
    private lateinit var studentGame: StudentDotsBoxGame

    // prepare list of lines
    override fun makeMove(game: DotsAndBoxesGame) {
        studentGame = game as StudentDotsBoxGame
        safeLines.clear()
        safeLines.clear()
        safeLines.clear()
        initializeLines()
    }

    // get a line to draw or play turn
    fun getLineToDraw(): StudentLine? {
        // try to get good lines first
        if (goodLines.size != 0) return getBestGoodLine()
        // else try safe lines
        // else get a random bad line
        return if (safeLines.size != 0) getRandomSafeLine() else getRandomBadLine()
    }

    // prepare lines
    private fun initializeLines() {
        // clear previous lines
        goodLines.clear()
        badLines.clear()
        safeLines.clear()

        // loop for rows and columns
        for (i in 0..studentGame.width) {
            for (j in 0 until studentGame.height) {

                // check if current x and y or i and y are occupied
                // or if x,y has a line drawn over it by any player
                // if line is already drawn then leave it
                // else try to figure whether line is good, bad or safe
                if (!isHorizontalLineOccupied(i, j)) {
                    // check if we are reading the first row
                    // since first row does not have any row above it
                    // so we are handling it separately
                    if (i == 0) {
                        val model = studentGame.StudentLine(i, j)
                        model.direction = Direction.HORIZONTAL

                        // get the box for x,y ( the box may not exit before, but we need to capture the outside boundaries )
                        // then try to find the lines which are currently drawn in this non-existing box
                        // and add line accordingly to list
                        when (getBox(i, j).occupiedLineCount()) {
                            3    -> goodLines.add(model)
                            2    -> badLines.add(model)
                            1, 0 -> safeLines.add(model)
                        }
                    }
                    // check if we are reading the last row
                    // since last row does not have any row after it
                    // so we are handling it separately
                    else if (i == 5) {
                        val model = studentGame.StudentLine(i, j)
                        model.direction = Direction.HORIZONTAL
                        // get the box for x,y ( the box may not exit before, but we need to capture the outside boundaries )
                        // then try to find the lines which are currently drawn in this non-existing box
                        // and add line accordingly to list
                        when (getBox(i - 1, j).occupiedLineCount()) {
                            3    -> goodLines.add(model)
                            2    -> badLines.add(model)
                            1, 0 -> safeLines.add(model)
                        }
                    }
                    // handles the middle rows
                    else {
                        val model = studentGame.StudentLine(i, j)
                        model.direction = Direction.HORIZONTAL
                        // get the box for x,y ( the box may not exist before, but we need to capture the outside boundaries )
                        // then try to find the lines which are currently drawn in this non-existing box
                        // and add line accordingly to list
                        if (getBox(i, j).occupiedLineCount() == 3 || getBox(i - 1, j).occupiedLineCount() == 3) goodLines.add(model)
                        if (getBox(i, j).occupiedLineCount() == 2 || getBox(i - 1, j).occupiedLineCount() == 2) badLines.add(model)
                        if (getBox(i, j).occupiedLineCount() < 2 && getBox(i - 1, j).occupiedLineCount() < 2) safeLines.add(model)
                    }
                }
                // check if the vertical line is already drawn or not
                if (!isVerticalLineOccupied(j, i)) {
                    val model = studentGame.StudentLine(j, i)
                    model.direction = Direction.VERTICAL

                    if (i == 0) {
                        if (getBox(j, i).occupiedLineCount() == 3) goodLines.add(model)
                    } else if (i == 5) {
                        if (getBox(j, i - 1).occupiedLineCount() == 3) goodLines.add(model)
                    } else {
                        if (getBox(j, i).occupiedLineCount() == 3 || getBox(j, i - 1).occupiedLineCount() == 3) goodLines.add(model)
                        if (getBox(j, i).occupiedLineCount() == 2 || getBox(j, i - 1).occupiedLineCount() == 2) badLines.add(model)
                        if (getBox(j, i).occupiedLineCount() < 2 && getBox(j, i - 1).occupiedLineCount() < 2) safeLines.add(model)
                    }
                }
            }
        }
    }

    /*
        x,y = 2,3
        x,y+1 = 2,4
        x+1,y = 3,3

             2,3  2,4

             3,3

    */
    private fun getBox(row: Int, column: Int): BoxHelper {
        return BoxHelper(isVerticalLineOccupied(row, column), isHorizontalLineOccupied(row, column), isVerticalLineOccupied(row, column + 1), isHorizontalLineOccupied(row + 1, column))
    }

    // check if line is drawn horizontaly
    private fun isHorizontalLineOccupied(row: Int, column: Int): Boolean {
        return studentGame.isLineSelected(Direction.HORIZONTAL, row, column)
    }

    // check if line is drawn vertically
    private fun isVerticalLineOccupied(row: Int, column: Int): Boolean {
        return studentGame.isLineSelected(Direction.VERTICAL, row, column)
    }

    // get best line
    private fun getBestGoodLine(): StudentLine? {
        return goodLines[0]
    }

    // shuffle lines
    // then return
    private fun getRandomSafeLine(): StudentLine? {
        safeLines.shuffle()
        return getRandomLine(safeLines)
    }

    // shuffle lines
    // then return
    private fun getRandomBadLine(): StudentLine? {
        badLines.shuffle()
        return getRandomLine(badLines)
    }

    // try to generate index and then get value from list
    private fun getRandomLine(list: List<StudentLine>): StudentLine? {
        val index = (list.size * Math.random()).toInt()
        if (list.isNotEmpty()) {
            return list[index]
        }
        return null
    }

}