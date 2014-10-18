package id.co.viva.news.app.component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

/**
 * Created by anton on 6/9/14.
 */
public class UbuntuButton extends Button {
    public UbuntuButton(Context context) {
        super(context);
    }

    public UbuntuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UbuntuButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setTypeface(Typeface tf) {
        try {
            Typeface helveticaTF = Typeface.createFromAsset(getContext().getAssets(), "fonts/Ubuntu-L.ttf");
            super.setTypeface(helveticaTF);
        } catch (Exception e) {
            super.setTypeface(tf);
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
    }
}
