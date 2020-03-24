package com.example.blacksquare

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.blacksquare.Adapters.TabsViewPagerAdapter
import com.example.blacksquare.Fragments.StoreFragment
import com.example.blacksquare.Fragments.LoadLoopsFrag
import com.example.blacksquare.Fragments.LoadSoundFrag
import com.example.blacksquare.Fragments.LoadProjectsFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout


class LoadDrumSoundDialogActivity : AppCompatActivity() {


    private var snackbarlayout = 0
    private var activitySnackbar: Snackbar? = null

    companion object {
        private const val KITS_TAB = 0
        private const val SOUNDS_TAB = 1
        private const val LOOPS_TAB = 2
        private const val SEARCH_TAB = 3
    }

    private var searchQuery = ""

    //sounds
    private val percussion = "percussion"
    private val kicks = "kicks"
    private val snares = "snares"
    private val claps = "claps"
    private val subs = "808s"
    private val toms = "toms"
    private val rolls = "rolls"
    private val snaps = "snaps"
    private val builds = "builds"
    private val fills = "fills"
    private val shakers = "shakers"
    private val crash = "crash"
    private val transfx = "transfx"
    private val soundfx = "soundfx"
    private val hats = "hats"

    //loops
    private val percLoops = "percussion_loops"
    private val arpeggioLoops = "arpeggio_loops"
    private val atmosphericLoops = "atmospheric_loops"
    private val gtrLoops = "gtr_loops"
    private val hatLoops = "hat_loops"
    private val keysLoops = "keys_loops"
    private val stringsLoops = "strings_loops"
    private val synthLoops = "synth_loops"
    private val padLoops = "pad_loops"


    //kits
    private val trapKits = "trap_kits"
    private val trapSoulKits = "trap_soul_kits"
    private val rnbKits = "rnb_kits"
    private val eastCoastKits = "east_coast_kits"
    private val westCoastKits = "west_coast_kits"
    private val soundFxKits = "sound_fx_kits"


    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_drum_sound)

        //set default snackbar layout
        snackbarlayout = R.layout.snackbar_search_sound_tag_layout

        //Tabs
        val sectionsPagerAdapter = TabsViewPagerAdapter(supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        sectionsPagerAdapter.addFragment(StoreFragment(), getString(R.string.store_title))
        sectionsPagerAdapter.addFragment(LoadSoundFrag(), getString(R.string.sounds_title))
        sectionsPagerAdapter.addFragment(LoadLoopsFrag(), getString(R.string.loops_title))
        sectionsPagerAdapter.addFragment(LoadProjectsFragment(), getString(R.string.projects_title))

        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)



        tabs.setupWithViewPager(viewPager)


    }
fun showSearchTagSnackbar(){

   // val activityView: View = layoutInflater.inflate(R.layout.activity_load_drum_sound, null)
    val snackbar =
        Snackbar.make(findViewById(R.id.tabs), "", Snackbar.LENGTH_INDEFINITE)
    val layout = (snackbar.view) as Snackbar.SnackbarLayout
    val snackview: View =
        layoutInflater.inflate(snackbarlayout, null)
    layout.addView(snackview)

    snackbar.show()
    activitySnackbar = snackbar

}

    //Sound set tag functions
    fun setClapsTag(view: View) {
        searchQuery = claps
        activitySnackbar?.dismiss()
    }

    fun setSubsTag(view: View) {
        searchQuery = subs
        activitySnackbar?.dismiss()
    }

    fun setKicksTag(view: View) {
        searchQuery = kicks
        activitySnackbar?.dismiss()
    }

    fun setSnaresTag(view: View) {
        searchQuery = snares
        activitySnackbar?.dismiss()
    }

    fun setHatTag(view: View) {
        searchQuery = hats
        activitySnackbar?.dismiss()
    }

    fun setPercussionTag(view: View) {
        searchQuery = percussion
        activitySnackbar?.dismiss()
    }

    fun setBuildsTag(view: View) {
        searchQuery = builds
        activitySnackbar?.dismiss()
    }

    fun setFillsTag(view: View) {
        searchQuery = fills
        activitySnackbar?.dismiss()
    }

    fun setRollsTag(view: View) {
        searchQuery = rolls
        activitySnackbar?.dismiss()
    }

    fun setShakersTag(view: View) {
        searchQuery = shakers
        activitySnackbar?.dismiss()
    }

    fun setSnapsTag(view: View) {
        searchQuery = snaps
        activitySnackbar?.dismiss()
    }

    fun setTomsTag(view: View) {
        searchQuery = toms
        activitySnackbar?.dismiss()
    }

    fun setCrashTag(view: View) {
        searchQuery = crash
        activitySnackbar?.dismiss()
    }

    fun setTransFxTag(view: View) {
        searchQuery = transfx
        activitySnackbar?.dismiss()
    }

    fun setSoundFxTag(view: View) {
        searchQuery = soundfx
        activitySnackbar?.dismiss()
    }

    //Kit set tag functions
    fun setTrapKitTag(view: View) {
        searchQuery = trapKits
        activitySnackbar?.dismiss()
    }

    fun setTrapSoulKitsTag(view: View) {
        searchQuery = trapSoulKits
        activitySnackbar?.dismiss()
    }

    fun setEastCoastKitsTag(view: View) {
        searchQuery = eastCoastKits
        activitySnackbar?.dismiss()
    }

    fun setWestCoastKitsTag(view: View) {
        searchQuery = westCoastKits
        activitySnackbar?.dismiss()
    }

    fun setRnbKitsTag(view: View) {
        searchQuery = rnbKits
        activitySnackbar?.dismiss()
    }

    fun setSoundFxKitsTag(view: View) {
        searchQuery = soundFxKits
        activitySnackbar?.dismiss()
    }

    //Loops set tag functions
    fun setPercLoopsTag(view: View) {
        searchQuery = percLoops
        activitySnackbar?.dismiss()
    }

    fun setArpeggioLoopsTag(view: View) {
        searchQuery = arpeggioLoops
        activitySnackbar?.dismiss()
    }

    fun setAtmosphericLoopsTag(view: View) {
        searchQuery = atmosphericLoops
        activitySnackbar?.dismiss()
    }

    fun setGuitarLoopsTag(view: View) {
        searchQuery = gtrLoops
        activitySnackbar?.dismiss()
    }

    fun setHatLoopsTag(view: View) {
        searchQuery = hatLoops
        activitySnackbar?.dismiss()
    }

    fun setKeysLoopsTag(view: View) {
        searchQuery = keysLoops
        activitySnackbar?.dismiss()
    }

    fun setSynthLoopsTag(view: View) {
        searchQuery = synthLoops
        activitySnackbar?.dismiss()
    }

    fun setStringsLoopsTag(view: View) {
        searchQuery = stringsLoops
        activitySnackbar?.dismiss()
    }

    fun setPadLoopsTag(view: View) {
        searchQuery = padLoops
        activitySnackbar?.dismiss()
    }


}