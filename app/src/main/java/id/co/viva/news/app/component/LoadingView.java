package id.co.viva.news.app.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import id.co.viva.news.app.R;

/**
 * Created by rezarachman on 03/10/14.
 */
public class LoadingView extends ImageView {

    public LoadingView(Context context) {
        super(context);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context aCtx)	{
        if (!isInEditMode()) startAnimation(AnimationUtils.loadAnimation(aCtx, R.anim.rotate));
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE && !isInEditMode()) startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));
        else setAnimation(null);
    }

}
