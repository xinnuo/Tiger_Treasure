package com.ruanmeng.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.WindowManager;

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-12-01 15:27
 */

public class BottomSheetEditDialog extends BottomSheetDialog {

    public BottomSheetEditDialog(@NonNull Context context) {
        super(context);
    }

    public BottomSheetEditDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected BottomSheetEditDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
