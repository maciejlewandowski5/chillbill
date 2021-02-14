package com.example.chillbill.helpers;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

public class ListenableInput {

    TextView textView;

    public ListenableInput(TextView textView) {
        this.textView = textView;
    }



    public void setOnTypingEndsListener(OnTypingEndsListener onTypingEndsListener){

        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        onTypingEndsListener.onTypingEnds(v,actionId,event);
                        return true;
                    }
                }

                return false;

            }
        });
    }



    public interface OnTypingEndsListener{
        public boolean onTypingEnds(TextView v, int actionId, KeyEvent event);
    }

}
