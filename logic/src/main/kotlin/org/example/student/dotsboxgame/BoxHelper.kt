package org.example.student.dotsboxgame

/*
 * This class helps us to determine the counts of lines around a box
 */
class BoxHelper(var left: Boolean, var top: Boolean, var right: Boolean, var bottom: Boolean) {

    fun occupiedLineCount(): Int {
        var count = 0
        if (left) count++
        if (right) count++
        if (top) count++
        if (bottom) count++
        return count
    }

}
