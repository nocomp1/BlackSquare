package com.example.blacksquare.Fragments

import android.widget.SeekBar
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){

    abstract fun  onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)

    abstract fun onStartTrackingTouch(seekBar: SeekBar?)

    abstract fun onStopTrackingTouch(seekBar: SeekBar?)
}