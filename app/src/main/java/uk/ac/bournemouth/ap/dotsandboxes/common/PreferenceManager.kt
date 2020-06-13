package uk.ac.bournemouth.ap.dotsandboxes.common

import android.content.Context
import android.content.SharedPreferences

/*
 * PreferenceManager class handles the Shared Preference of App. SharedPreference is most fast data storing and retrieving mechanism
 * This class is based on Singleton pattern
 * It has basic setter and getter methods
 */
class PreferenceManager private constructor() {

    companion object {
        private var instance_of_pref_manager: PreferenceManager? = null
        private lateinit var pref: SharedPreferences
        private lateinit var prefEditor: SharedPreferences.Editor

        // initialize the instance of pref manager
        fun initialize(context: Context) {
            // we are also init the pref ( Shared preference )
            if (instance_of_pref_manager == null) {
                instance_of_pref_manager = PreferenceManager()
                pref = context.getSharedPreferences(
                    Constants.PREF_NAME,
                    Context.MODE_PRIVATE
                                                   )
            }
        }

        // get instance of pref manager
        fun getInstance(): PreferenceManager {
            return instance_of_pref_manager ?: throw IllegalStateException("PreferenceManager must be initialized first.")
        }

    }

    // get the game play mode
    fun getGamePlayMode() = pref.getString(Constants.GAME_MODE, Constants.DEFAULT_GAME_PLAY)!!

    // set the game play mode
    fun setGamePlayMode(mode: String) {
        prefEditor = pref.edit()
        prefEditor.putString(Constants.GAME_MODE, mode)
        prefEditor.apply()
    }
}