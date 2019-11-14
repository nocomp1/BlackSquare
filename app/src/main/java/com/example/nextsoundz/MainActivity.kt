package com.example.nextsoundz


import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.media.midi.MidiManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.nextsoundz.Adapters.TabsViewPagerAdapter
import com.example.nextsoundz.Fragments.*
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        //Checking for Midi capabilities
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setUpMidi()

            } else {
                //customer can not use the controller feature of the app

                TODO("VERSION.SDK_INT < M")
            }


        }

        val adapter = TabsViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DrumScreenHomeFragment(), "Drums")
        adapter.addFragment(InstrumentFragment(), "Instrument")
        adapter.addFragment(SequenceFragment(), "Edit Sequence")
        adapter.addFragment(RecordingFragment(), "Recording")
        adapter.addFragment(FileFragment(), "File")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)


    }

    @TargetApi(23)
    private fun setUpMidi() {

        val m = this.getSystemService(Context.MIDI_SERVICE) as MidiManager
        //Get List of Already Plugged In Entities
        val info = m.getDevices()

        //notification when, for example, keyboards are plugged in or unplugged
        // m.registerDeviceCallback({ x -> })


    }


}
