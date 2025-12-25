package com.fdqms.photonote;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

class CustomTypefaceSpan extends MetricAffectingSpan {

    Typeface typeface;

    public CustomTypefaceSpan(Typeface typeface){
        this.typeface = typeface;
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint textPaint) {
        textPaint.setTypeface(typeface);
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setTypeface(typeface);
    }
}