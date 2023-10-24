package com.example.mungnyang.DiaryPage.Schedule


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mungnyang.databinding.ItemScheduleBinding


class ScheduleAdapter(private val context: Context, private var schedule: List<Schedule>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    //private val clickedPetNames: MutableList<String> = mutableListOf()  // 클릭한 이미지의 펫 이름을 저장하는 배열 변수
    fun updateData(newData: List<Schedule>){
        schedule = newData
        notifyDataSetChanged()
    }

    // 호출되는 횟수가 정해졌음
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder(
            ItemScheduleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    override fun getItemCount(): Int = schedule.size

    //내가 그려야 할 항목의 개수를 알려주는 일을 함 (return에서 한줄로 바꿈)
    //override fun getItemCount(): Int = Profile.size

    // 스크롤을 많이하면 할수록 많이 호출됨
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val scheduleItem  = schedule[position]
        holder.bind(context, scheduleItem)

    }

    // ViewHolder를 만든다는거랑 View를 만든다는거랑 똑같은 말임
    class ScheduleViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(context: Context,
                 schedule: Schedule,
        ){
            binding.scheduleTitle.text = schedule.scheduleTitle
            binding.scheduleDetail.text = schedule.scheduleDetail
            binding.scheduleTime.text = schedule.scheduleTime


        }
    }
}