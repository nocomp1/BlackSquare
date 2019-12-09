package com.example.nextsoundz

import android.os.Bundle
import android.view.Window
import android.widget.RadioButton
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.example.nextsoundz.Singleton.ApplicationState
import com.example.nextsoundz.Views.ToggleButtonGroupTableLayout
import kotlinx.android.synthetic.main.activity_note_repeat_dialog.*
import java.util.*

class NoteRepeatDialogActivity : AppCompatActivity() {

    private lateinit var patternList: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_note_repeat_dialog)


        patternList = getRadioBtnGroupIds(note_repeat_radio_group)

        setUpNoteRepeatChoice()
    }


    private fun setUpNoteRepeatChoice() {
        var selectedChoice = ApplicationState.selectedNoteRepeatId


        if (selectedChoice == -1) {

            findViewById<RadioButton>(patternList[0]).isChecked = true

        } else {


            findViewById<RadioButton>(selectedChoice).isChecked = true
        }


    }

    private fun getRadioBtnGroupIds(view: ToggleButtonGroupTableLayout): ArrayList<Int> {

        var groupIds: ArrayList<Int> = arrayListOf()

        //The table layout that holds our radio buttons
        val tl = view as (TableLayout)
        val tlRowCount = tl.childCount
        var rowCounter = 0
        var viewCounter = 0

        while (rowCounter < tlRowCount) {
            var tr = tl.getChildAt(rowCounter) as (TableRow)
            var c = tr.childCount
            while (viewCounter < c) {
                ///Get the view that in the table row
                var v = tr.getChildAt(viewCounter)
                if (v is RadioButton) {

                    //get the id
                    groupIds.add(v.id)

                }
                viewCounter++
            }
            rowCounter++
        }

        return groupIds

    }


    override fun onPause() {
        super.onPause()


        if (ApplicationState.selectedNoteRepeatId != note_repeat_radio_group.checkedRadioButtonId) {
            ApplicationState.noteRepeatHasChanged = true
        }

        if (note_repeat_radio_group.checkedRadioButtonId != -1) {
            ApplicationState.selectedNoteRepeatId = note_repeat_radio_group.checkedRadioButtonId

        }

        var x = 0
        while (x < patternList.size) {

            if (patternList[x] == note_repeat_radio_group.checkedRadioButtonId) {
                ApplicationState.selectedNoteRepeat = x
            }
            x++
        }


    }
}
