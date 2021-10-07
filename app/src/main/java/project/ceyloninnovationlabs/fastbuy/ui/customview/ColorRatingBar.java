package project.ceyloninnovationlabs.fastbuy.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import project.ceyloninnovationlabs.fastbuy.R;

public class ColorRatingBar extends AppCompatRatingBar {

    public ColorRatingBar(Context context) {
        super(context);
    }

    public ColorRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ColorRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorRatingBar, defStyleAttr, 0);
        int progressColor = a.getColor(R.styleable.ColorRatingBar_progress_color, ContextCompat.getColor(context, R.color.purple_700));
        int emptyColor = a.getColor(R.styleable.ColorRatingBar_empty_color, ContextCompat.getColor(context, R.color.teal_200));
        boolean changeable = a.getBoolean(R.styleable.ColorRatingBar_changeable, true);

        LayerDrawable stars = (LayerDrawable) getProgressDrawable();
        // Filled stars
        setRatingStarColor(DrawableCompat.wrap(stars.getDrawable(2)), progressColor);
        // Half filled stars
        setRatingStarColor(DrawableCompat.wrap(stars.getDrawable(1)), progressColor);
        // Empty stars
        setRatingStarColor(DrawableCompat.wrap(stars.getDrawable(0)), emptyColor);

        setIsIndicator(!changeable);
    }

    private void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void setRatingProgressColor(int resId) {
        LayerDrawable stars = (LayerDrawable) getProgressDrawable();
        // Filled stars
        setRatingStarColor(DrawableCompat.wrap(stars.getDrawable(2)), ContextCompat.getColor(getContext(), resId));
    }

    public void setRatingHalfColor(int resId) {
        LayerDrawable stars = (LayerDrawable) getProgressDrawable();
        // Half filled stars
        setRatingStarColor(DrawableCompat.wrap(stars.getDrawable(1)), ContextCompat.getColor(getContext(), resId));
    }

    public void setRatingEmptyColor(int resId) {
        LayerDrawable stars = (LayerDrawable) getProgressDrawable();
        // Half filled stars
        setRatingStarColor(DrawableCompat.wrap(stars.getDrawable(0)), ContextCompat.getColor(getContext(), resId));
    }
}