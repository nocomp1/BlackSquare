package com.example.blacksquare.Views

import android.widget.RadioButton
import android.widget.TableLayout
import android.widget.TableRow
import java.util.*

/**
 * This is to extract the ids from the pattern and track selection
 */
object ToggleButtonGroupExtensions {


    fun ToggleButtonGroupTableLayout.getRadioBtnGroupIds(): ArrayList<Int> {

        val groupIds: ArrayList<Int> = arrayListOf()

        val tl = this as (TableLayout)
        val tlRowCount = tl.childCount
        var rowCounter = 0



        while (rowCounter < tlRowCount) {
            //Get the table row
            val tr = tl.getChildAt(rowCounter) as (TableRow)

            val rowRadioButtonCount = tr.childCount
            var viewCounter = 0
            while (viewCounter < rowRadioButtonCount) {
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
        // }
        return groupIds

    }

    fun ToggleButtonGroupTableLayout.setRadioBtnSelection(
        choice: String?
    ) {

        choice?.let {expectedText ->
            val selectionIDs = this.getRadioBtnGroupIds()
            selectionIDs.forEach { id ->

                val radioButton = findViewById<RadioButton>(id)
                val radioButtonText = radioButton.text.toString()
                radioButton.isChecked = false

                if (expectedText == radioButtonText) {
                    this.activeRadioButton =radioButton

                    radioButton.isChecked = true

                }
            }
        }
    }

    fun ToggleButtonGroupTableLayout.getRadioBtnText(): String {
        val selectionID = this.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(selectionID)
        return radioButton.text.toString().trim()

    }


    fun ToggleButtonGroupTableLayout.setSelectionBasedOnSelected(
        selectionGroup: ToggleButtonGroupTableLayout,
        selectedChoice: String,
        selectionChoice: String
    ) {
        val selectedIDs = this.getRadioBtnGroupIds()
        selectedIDs.forEach { selectedId ->

            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            //check if we have a selected choice
            if (selectedChoice == selectedRadioButton.text) {
                selectedRadioButton.isChecked = true

                //based off selected choice get what selection is needed


                //get the ids of the selection we want to make
                val selectionIds = selectionGroup.getRadioBtnGroupIds()
                selectionIds.forEach { selectionId ->
                    val selectionRadioBtn = findViewById<RadioButton>(selectionId)


                }

            }


        }


    }
}