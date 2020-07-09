package com.job.skillhand.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.job.skillhand.R;

public class Util {
    public Util(Context mContext) {
        this.mContext = mContext;
    }

    public Context mContext;

    public void showFullyCustomToast(String toastMsg)
    {
        // Get the custom layout view.
        View toastView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.toast_custom_view, null);
        TextView txvToastMsg = toastView.findViewById(R.id.customToastText);
        // Initiate the Toast instance.
        Toast toast = new Toast(mContext);
        // Set custom view in toast.
        toast.setView(toastView);
        txvToastMsg.setText(toastMsg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

    }


}
