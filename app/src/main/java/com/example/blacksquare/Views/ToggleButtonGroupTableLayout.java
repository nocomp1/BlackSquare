package com.example.blacksquare.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;


public class ToggleButtonGroupTableLayout extends TableLayout implements OnClickListener {

    public interface ToggleButtonListener {
        void onToggleButtonClicked(RadioButton radioButton);
    }

    public ToggleButtonListener listener;
    private static final String TAG = "ToggleButtonGroupTableLayout";
    public RadioButton activeRadioButton;

    /**
     * @param context
     */
    public ToggleButtonGroupTableLayout(Context context) {
        super(context);

        // TODO Auto-generated constructor stub
    }

    public void setUpListener(ToggleButtonListener listener) {
        this.listener = listener;
    }

    /**
     * @param context
     * @param attrs
     */
    public ToggleButtonGroupTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onClick(View v) {

        ////Remove all selected
        for (int i = 0; i < radioButtonList.size(); i++) {

            int id = radioButtonList.get(i);
            RadioButton rb = (RadioButton) findViewById(id);

            rb.setChecked(false);
        }

        final RadioButton rb = (RadioButton) v;
        if (activeRadioButton != null) {
            activeRadioButton.setChecked(false);

        }
        rb.setChecked(true);
        activeRadioButton = rb;
        if (listener != null) {
            listener.onToggleButtonClicked(activeRadioButton);
        }
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, int, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow) child);
    }


    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {


        TableRow tableRow =(TableRow) child;
        int trCount = tableRow.getChildCount();
        for (int i=0; i < trCount; i++) {
            final View v = tableRow.getChildAt(i);
            if (v instanceof RadioButton) {
                if (((RadioButton) v).isChecked())
                    activeRadioButton =  ((RadioButton) v);
            }


        }
        super.addView(child, params);
        setChildrenOnClickListener((TableRow) child);


    }

    ArrayList<Integer> radioButtonList = new ArrayList();

    private void setChildrenOnClickListener(TableRow tr) {
        final int c = tr.getChildCount();
        for (int i = 0; i < c; i++) {
            final View v = tr.getChildAt(i);
            if (v instanceof RadioButton) {

                radioButtonList.add(v.getId());
                v.setOnClickListener(this);
                if (((RadioButton) v).isChecked())
                    activeRadioButton = (RadioButton) v;
            }
        }

    }

    public int getCheckedRadioButtonId() {
        if (activeRadioButton != null) {
            return activeRadioButton.getId();
        }
        return -1;
    }

}