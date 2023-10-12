package com.jryg.driver.myapplication.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.DateSorter.DAY_COUNT
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.jryg.driver.myapplication.R
import kotlin.math.roundToInt

@Suppress("AlibabaUseLogCheck")
class TestScheduleActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "TestScheduleActivity"
        private const val DAY_COUNT = 7

        fun start(context: Context) {
            context.startActivity(Intent(context, TestScheduleActivity::class.java))
        }
    }

    private lateinit var ll_time_scale: LinearLayout
    private lateinit var rv_schedules: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_schedule)
        ll_time_scale = findViewById(R.id.ll_time_scale)
        (ll_time_scale.layoutParams as LinearLayout.LayoutParams).topMargin =
            resources.getDimensionPixelOffset(R.dimen.ygf_dp_44)
        rv_schedules = findViewById(R.id.rv_schedules)
        rv_schedules.adapter = ScheduleAdapter()
        rv_schedules.addOnScrollListener(createScrollListener()!!)
    }

    private fun createScrollListener(): RecyclerView.OnScrollListener? =
        object : RecyclerView.OnScrollListener() {
            @SuppressLint("LongLogTag")
            override fun onScrollStateChanged(
                recyclerView: RecyclerView, newState: Int
            ) {
                Log.i("$TAG-onScrollStateChanged", " onScrollStateChanged newState:$newState")
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val linearLayoutManager = rv_schedules.layoutManager as LinearLayoutManager

                    val minPosition = linearLayoutManager.findFirstVisibleItemPosition()
                    val maxPosition = linearLayoutManager.findLastVisibleItemPosition()

                    val minWholePosition = (minPosition / DAY_COUNT) * DAY_COUNT
                    val maxWholePosition = minWholePosition + DAY_COUNT - 1

                    val position =
                        if (minPosition - minWholePosition < maxWholePosition - minPosition) {
                            minWholePosition
                        } else maxWholePosition

                    Log.i(
                        "$TAG-onScrollStateChanged",
                        "minPosition:$minPosition maxPosition:$maxPosition minWholePosition: $minWholePosition maxWholePosition:$maxWholePosition"
                    )
                    scrollToPosition(
                        linearLayoutManager,
                        position
                    )
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.i(
                    TAG,
                    "onScrolled: dx: $dx dy: $dy range:${recyclerView.computeHorizontalScrollRange()} ${Thread.currentThread().name}"
                )
            }
        }

    private fun scrollToPosition(
        linearLayoutManager: LinearLayoutManager,
        position: Int
    ) {
        Log.i(TAG, "scrollToPosition position: $position")
        if (position < 0) {
            return
        }
        if (linearLayoutManager.isSmoothScrolling
        ) {
            return
        }
        linearLayoutManager.startSmoothScroll(object :
            LinearSmoothScroller(this) {
            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_START
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return 100f / displayMetrics!!.densityDpi
            }
        }.apply {
            targetPosition = position
        })
    }


}

class ScheduleAdapter : RecyclerView.Adapter<ScheduleViewHolder>() {
    private val dataSchedules = arrayOfNulls<ScheduleBean>(DAY_COUNT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder =
        ScheduleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_test_schedule, parent, false)
        )

    override fun getItemCount(): Int = Short.MAX_VALUE.toInt()

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.assign(dataSchedules[position % DAY_COUNT])
    }

}

class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val colors =
        arrayOf(
            Color.Red.toArgb(),
            Color.Yellow.toArgb(),
            Color.Blue.toArgb(),
            Color.Gray.toArgb(),
            Color.DarkGray.toArgb(),
            Color.Green.toArgb(),
            Color.Magenta.toArgb()
        )

    fun assign(bean: ScheduleBean?) {
        itemView.setBackgroundColor(
            colors[adapterPosition % DAY_COUNT])
        itemView.layoutParams.width =
            (ScreenUtil.getScreenWidth(itemView.context) / 8.0).roundToInt()
        itemView.layoutParams.height =
            itemView.context.resources.getDimensionPixelOffset(R.dimen.ygf_dp_20) * 6 * 24 + itemView.context.resources.getDimensionPixelOffset(
                R.dimen.ygf_dp_54
            )
    }
}

data class ScheduleBean(val id: String, val tasks: List<ScheduleTaskBean>)
data class ScheduleTaskBean(
    val id: String,
    val beginTime: Long,
    val endTime: Long,
    val content: String?
)