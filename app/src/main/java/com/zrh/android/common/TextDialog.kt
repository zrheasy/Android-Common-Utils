package com.zrh.android.common

import android.content.Context
import com.zrh.android.common.utils.databinding.DialogTextBinding
import com.zrh.android.common.widgets.BindingDialog

/**
 *
 * @author zrh
 * @date 2023/8/22
 *
 */
class TextDialog(context: Context) : BindingDialog<DialogTextBinding>(context) {

    init {
        setCancelable(true)
    }

}