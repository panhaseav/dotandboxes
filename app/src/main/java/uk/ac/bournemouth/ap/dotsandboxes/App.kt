package uk.ac.bournemouth.ap.dotsandboxes

import android.app.Application
import uk.ac.bournemouth.ap.dotsandboxes.common.PreferenceManager

/*
 * used to initialize the Singleton classes
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.initialize(this)
    }

}