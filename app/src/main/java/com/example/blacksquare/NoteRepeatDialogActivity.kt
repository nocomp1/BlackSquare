package com.example.blacksquare

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.RadioButton
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.example.blacksquare.Singleton.ApplicationState
import com.example.blacksquare.Views.ToggleButtonGroupTableLayout
import kotlinx.android.synthetic.main.activity_note_repeat_dialog.*
import java.util.*

class NoteRepeatDialogActivity : AppCompatActivity() {

    private lateinit var patternList: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_note_repeat_dialog)


        patternList = getRadioBtnGroupIds(note_repeat_radio_group as ToggleButtonGroupTableLayout)
        for (element in patternList) {

            Log.d("noteRepeatList","${element}")
        }

        setUpNoteRepeatChoice()
    }


    private fun setUpNoteRepeatChoice() {
        val selectedChoice = ApplicationState.selectedNoteRepeatId

        if (selectedChoice == -1) {

            findViewById<RadioButton>(patternList[0]).isChecked = true

        } else {

            findViewById<RadioButton>(selectedChoice).isChecked = true
        }

    }

    private fun getRadioBtnGroupIds(view: ToggleButtonGroupTableLayout): ArrayList<Int> {

        var groupIds: ArrayList<Int> = ArrayList()

        //The table layout that holds our radio buttons
        val tl = view as (TableLayout)
        val tlRowCount = tl.childCount
        var rowCounter = 0
        var viewCounter = 0

        Log.d("noteRepeatList","tlRowCount =${tlRowCount}")

        while (rowCounter < tlRowCount) {
            viewCounter = 0
            Log.d("noteRepeatList","RowCounter =${rowCounter}")
            val tr = tl.getChildAt(rowCounter) as (TableRow)
            val c = tr.childCount
            Log.d("noteRepeatList","child count ${c}")
            while (viewCounter < c) {
                ///Get the view that in the table row
                val v = tr.getChildAt(viewCounter)
                if (v is RadioButton) {
                    //get the id
                    groupIds.add(v.id)
                    Log.d("noteRepeatList","groupID's inside =${v.id}")
                }
                viewCounter++
            }

            rowCounter++
        }


        for (element in groupIds) {

            Log.d("noteRepeatList","groupID's =${element}")
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
