package com.fdqms.photonote;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.Spanned;
import android.util.AttributeSet;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    Context context;

    public CustomEditText(Context context) {
        super(context);
        this.context = context;
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                id = android.R.id.pasteAsPlainText;
            } else {
                onInterceptClipDataToPlainText();
            }
        }
        return super.onTextContextMenuItem(id);
    }


    private void onInterceptClipDataToPlainText() {
        ClipboardManager clipboard = (ClipboardManager) getContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            for (int i = 0; i < clip.getItemCount(); i++) {
                final CharSequence paste;
                // Get an item as text and remove all spans by toString().
                final CharSequence text = clip.getItemAt(i).coerceToText(getContext());
                paste = (text instanceof Spanned) ? text.toString() : text;
                if (paste != null) {
                    ClipData clipData = ClipData.newPlainText("rebase_copy", text);
                    ClipboardManager manager = (ClipboardManager) context
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setPrimaryClip(clipData);
                }
            }
        }
    }
}
