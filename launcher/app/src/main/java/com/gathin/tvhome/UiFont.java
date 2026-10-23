package com.gathin.tvhome;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;

/**
 * The typeface behind one of the font_* strings in res/values/fonts.xml, for views built in code. On
 * Android 8 and newer (values-v26) the string points at a bundled font; below that, and in locales
 * Outfit can't draw (values-vi), it names a system family (#833).
 */
final class UiFont {
    private UiFont() {}

    static Typeface get(Context context, int fontString) {
        Resources res = context.getResources();
        TypedValue value = new TypedValue();
        res.getValue(fontString, value, true);
        String name = String.valueOf(value.string);
        // Below Android 8 the strings never name a bundled font, so the "res/" case cannot be reached.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && name.startsWith("res/")) {
            return res.getFont(value.resourceId);
        }
        return Typeface.create(name, Typeface.NORMAL);
    }
}
