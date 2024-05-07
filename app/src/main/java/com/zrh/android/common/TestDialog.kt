package com.zrh.android.common

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.zrh.android.common.utils.R
import com.zrh.android.common.utils.dp2px

/**
 *
 * @author zrh
 * @date 2024/5/7
 *
 */
class TestDialog:DialogFragment() {

    init {
        arguments = Bundle()
    }

    var count:Int
        get() {
            return requireArguments().getInt("count", 0)
        }
        set(value) {
            requireArguments().putInt("count", value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(context.dp2px(300f), context.dp2px(300f))
            setGravity(Gravity.CENTER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_test, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tvCount).text = count.toString()
    }
}