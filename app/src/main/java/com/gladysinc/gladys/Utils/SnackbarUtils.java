package com.gladysinc.gladys.Utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.gladysinc.gladys.R;

/**
 * Gladys project app
 * http://gladysproject.com
 * Created by Mathieu Andrade on 31/03/2018.
 */

public class SnackbarUtils {
    public static void simpleSnackBar(Context context, View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        snackbar.show();
    }
}
