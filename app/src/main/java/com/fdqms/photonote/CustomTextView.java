package com.fdqms.photonote;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.style.CharacterStyle;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Paint mPaint;

    private String mThinSpace = "\u200A";

    private String mJustifiedText = "";

    private float mSentenceWidth = 0;

    private int mWhiteSpacesNeeded = 0;

    private int mWordsInThisSentence = 0;

    private ArrayList<String> mTemporalLine = new ArrayList<>();

    private StringBuilder mStringBuilderCSequence = new StringBuilder();

    private List<SpanHolder> mSpanHolderList = new ArrayList<>();

    private StringBuilder sentence = new StringBuilder();

    private int mViewWidth;

    private float mThinSpaceWidth;

    private float mWhiteSpaceWidth;

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mJustifiedText.replace(" ", "")
                .replace("", mThinSpace)
                .equals(this.getText().toString().replace(" ", "").replace("", mThinSpace))) {
            return;
        }

        ViewGroup.LayoutParams params = this.getLayoutParams();

        CharSequence charSequence = this.getText();

        mSpanHolderList.clear();

        String[] words = this.getText().toString().split(" ");


        SpannableString s = SpannableString.valueOf(charSequence);
        if ((charSequence instanceof SpannedString)) {
            for (int i = 0; i < this.getText().length() - 1; i++) {
                CharacterStyle[] spans =
                        ((SpannedString) charSequence).getSpans(i, i + 1, CharacterStyle.class);
                if (spans != null && spans.length > 0) {
                    for (CharacterStyle span : spans) {
                        int spaces =
                                charSequence.toString().substring(0, i).split(" ").length + charSequence.toString()
                                        .substring(0, i)
                                        .split(mThinSpace).length;

                        SpanHolder spanHolder =
                                SpanHolder.getNewInstance(spans, s.getSpanStart(span), s.getSpanEnd(span), spaces);
                        mStringBuilderCSequence.setLength(0);
                        for (int j = 0; j <= words.length - 1; j++) {
                            mStringBuilderCSequence.append(words[j]);
                            mStringBuilderCSequence.append(" ");
                            if (mStringBuilderCSequence.length() > i) {
                                if (words[j].trim().replace(mThinSpace, "").length() == 1) {
                                    spanHolder.setWordHolderIndex(j);
                                } else {
                                    spanHolder.setWordHolderIndex(j);
                                    spanHolder.setTextChunkPadded(true);
                                }
                                break;
                            }
                        }
                        mSpanHolderList.add(spanHolder);
                    }
                }
            }
        }
        mPaint = this.getPaint();
        mViewWidth = this.getMeasuredWidth() - (getPaddingLeft() + getPaddingRight());

        if (params.width != ViewGroup.LayoutParams.WRAP_CONTENT
                && mViewWidth > 0
                && words.length > 0
                && mJustifiedText.isEmpty()) {
            mThinSpaceWidth = mPaint.measureText(mThinSpace);
            mWhiteSpaceWidth = mPaint.measureText(" ");
            for (int i = 0; i <= words.length - 1; i++) {
                boolean containsNewLine = (words[i].contains("\n") || words[i].contains("\r"));
                if (containsNewLine) {
                    String[] splitted = words[i].split("(?<=\\n)");
                    for (String splitWord : splitted) {
                        processWord(splitWord, splitWord.contains("\n"));
                    }
                } else {
                    processWord(words[i], false);
                }
            }
            mJustifiedText += joinWords(mTemporalLine);
        }

        SpannableString spannableString = SpannableString.valueOf(mJustifiedText);

        for (SpanHolder sH : mSpanHolderList) {
            int spaceCount = 0, wordCount = 0;
            boolean isCountingWord = false;
            int j = 0;
            while (wordCount < (sH.getWordHolderIndex() + 1)) {
                if (mJustifiedText.charAt(j) == ' ' || mJustifiedText.charAt(j) == ' ') {
                    spaceCount++;
                    if (isCountingWord) {
                        wordCount++;
                    }
                    isCountingWord = false;
                } else {
                    isCountingWord = true;
                }
                j++;
            }
            sH.setStart(
                    sH.getStart() + spaceCount - sH.getCurrentSpaces() + (sH.isTextChunkPadded() ? 1 : 0));
            sH.setEnd(
                    sH.getEnd() + spaceCount - sH.getCurrentSpaces() + (sH.isTextChunkPadded() ? 1 : 0));
        }
        for (SpanHolder sH : mSpanHolderList) {
            for (CharacterStyle cS : sH.getSpans())
                spannableString.setSpan(cS, sH.getStart(), sH.getEnd(), 0);
        }

        if (!mJustifiedText.isEmpty()) this.setText(spannableString);
    }

    private void processWord(String word, boolean containsNewLine) {
        if ((mSentenceWidth + mPaint.measureText(word)) < mViewWidth) {
            mTemporalLine.add(word);
            mWordsInThisSentence++;
            mTemporalLine.add(containsNewLine ? "" : " ");
            mSentenceWidth += mPaint.measureText(word) + mWhiteSpaceWidth;
            if (containsNewLine) {
                mJustifiedText += joinWords(mTemporalLine);
                resetLineValues();
            }
        } else {
            while (mSentenceWidth < mViewWidth) {
                mSentenceWidth += mThinSpaceWidth;
                if (mSentenceWidth < mViewWidth) mWhiteSpacesNeeded++;
            }

            if (mWordsInThisSentence > 1) {
                insertWhiteSpaces(mWhiteSpacesNeeded, mWordsInThisSentence, mTemporalLine);
            }
            mJustifiedText += joinWords(mTemporalLine);
            resetLineValues();

            if (containsNewLine) {
                mJustifiedText += word;
                mWordsInThisSentence = 0;
                return;
            }
            mTemporalLine.add(word);
            mWordsInThisSentence = 1;
            mTemporalLine.add(" ");
            mSentenceWidth += mPaint.measureText(word) + mWhiteSpaceWidth;
        }
    }

    private void resetLineValues() {
        mTemporalLine.clear();
        mSentenceWidth = 0;
        mWhiteSpacesNeeded = 0;
        mWordsInThisSentence = 0;
    }

    private String joinWords(ArrayList<String> words) {
        sentence.setLength(0);
        for (String word : words) {
            sentence.append(word);
        }
        return sentence.toString();
    }

    private void insertWhiteSpaces(int whiteSpacesNeeded, int wordsInThisSentence,
                                   ArrayList<String> sentence) {

        if (whiteSpacesNeeded == 0) return;

        if (whiteSpacesNeeded == wordsInThisSentence) {
            for (int i = 1; i < sentence.size(); i += 2) {
                sentence.set(i, sentence.get(i) + mThinSpace);
            }
        } else if (whiteSpacesNeeded < wordsInThisSentence) {
            for (int i = 0; i < whiteSpacesNeeded; i++) {
                int randomPosition = getRandomEvenNumber(sentence.size() - 1);
                sentence.set(randomPosition, sentence.get(randomPosition) + mThinSpace);
            }
        } else if (whiteSpacesNeeded > wordsInThisSentence) {
            while (whiteSpacesNeeded > wordsInThisSentence) {
                for (int i = 1; i < sentence.size() - 1; i += 2) {
                    sentence.set(i, sentence.get(i) + mThinSpace);
                }
                whiteSpacesNeeded -= (wordsInThisSentence - 1);
            }
            if (whiteSpacesNeeded == 0) return;

            if (whiteSpacesNeeded == wordsInThisSentence) {
                for (int i = 1; i < sentence.size(); i += 2) {
                    sentence.set(i, sentence.get(i) + mThinSpace);
                }
            } else if (whiteSpacesNeeded < wordsInThisSentence) {
                for (int i = 0; i < whiteSpacesNeeded; i++) {
                    int randomPosition = getRandomEvenNumber(sentence.size() - 1);
                    sentence.set(randomPosition, sentence.get(randomPosition) + mThinSpace);
                }
            }
        }
    }

    private int getRandomEvenNumber(int max) {
        Random rand = new Random();

        return rand.nextInt((max)) & ~1;
    }
}