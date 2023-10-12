package com.jryg.driver.myapplication.schedule

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.jryg.driver.myapplication.R

class TimeScaleView : LinearLayout {
    companion object {
        //每十分钟一个刻度
        private const val SCALE_LINE_COUNT = 24 * 6
    }

    private val lines by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        mutableListOf<ScaleLineBean>().apply {
            for (i in 0..SCALE_LINE_COUNT) {
                val hour = i / 6
                val tenMin = i % 6 * 10
                add(
                    ScaleLineBean(
                        if (i % 6 == 0) ScaleLineKind.HOUR else ScaleLineKind.TEN_MINUTE,
                        ScaleLineState.DEFAULT,
                        "${hour / 10}${hour % 10}:${tenMin / 10}${tenMin % 10}",
                        i
                    )
                )
            }
        }.toList()
    }

    fun findLine(hour: Int, tenMin: Int): ScaleLineBean {
        val offset = hour * 6 + tenMin / 10
        return lines[offset]
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        orientation = VERTICAL
        lines.forEach {
            addView(
                if (it.kind == ScaleLineKind.HOUR) {
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_test_schedule_time_scale_hour, this, false)
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_test_schedule_time_scale_ten_minute, this, false)
                }?.apply {
                    findViewById<LinearLayout>(R.id.ll_scale_tag).apply {
                        layoutParams.height= context.resources.getDimensionPixelOffset(R.dimen.ygf_dp_10)
                    }.isSelected =
                        it.state == ScaleLineState.HIGHLIGHT
                    findViewById<TextView>(R.id.tv_tag).text = it.content
                    if (lines.last() == it) {
                        findViewById<View>(R.id.view_space).visibility = View.GONE
                    }
                    layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        context.resources.getDimensionPixelOffset(R.dimen.ygf_dp_20)
                    )
                }
            )
        }
    }
}

data class ScaleLineBean(
    val kind: ScaleLineKind,
    val state: ScaleLineState,
    val content: String,
    val offset: Int
)

enum class ScaleLineKind {
    //整时
    HOUR,

    //十分
    TEN_MINUTE
}

enum class ScaleLineState {
    DEFAULT,
    HIGHLIGHT,
}