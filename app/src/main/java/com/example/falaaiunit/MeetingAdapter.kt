import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.falaaiunit.R

data class Meeting(
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String
)

class MeetingAdapter(
    private val meetings: List<Meeting>
) : RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>() {

    class MeetingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMeetingTitle: TextView = view.findViewById(R.id.tvMeetingTitle)
        val tvMeetingDescription: TextView = view.findViewById(R.id.tvMeetingDescription)
        val tvMeetingTime: TextView = view.findViewById(R.id.tvMeetingTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meeting, parent, false)
        return MeetingViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        val meeting = meetings[position]

        holder.tvMeetingTitle.text = meeting.title
        holder.tvMeetingDescription.text = meeting.description

        holder.tvMeetingTime.text = "${meeting.startTime} - ${meeting.endTime}"
    }

    override fun getItemCount(): Int {
        return meetings.size
    }
}