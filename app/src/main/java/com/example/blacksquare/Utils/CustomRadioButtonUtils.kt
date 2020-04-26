package com.example.blacksquare.Utils

import android.widget.RadioButton
import android.widget.TableLayout
import android.widget.TableRow
import com.example.blacksquare.Views.ToggleButtonGroupTableLayout
import java.util.ArrayList

/**
 * This is to extract the ids from the pattern and track selection
 */
object CustomRadioButtonUtils {

    fun getRadioBtnGroupIds(view: ToggleButtonGroupTableLayout): ArrayList<Int> {

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

//    fun removeAllSelected(radioButtonList: ArrayList<Int>) {
//        radioButtonList.forEach {
//
//            val radioButton =
//        }
//
//
//    }
//    private fun setUpBarCountChoice(radioButtonId : Int, radioGroup: ToggleButtonGroupTableLayout, defaultRadioChecked : Int) {
//
//        if (radioButtonId != -1) {
//            findViewById<RadioButton>(radioButtonId).isChecked = true
//        } else {
//
//            ///Set a default for bar measure
//            val radioGroupList = getRadioBtnGroupIds(radioGroup)
//            findViewById<RadioButton>(radioGroupList[defaultRadioChecked]).isChecked = true
//        }
//
//    }
}