package io.agora.openduo.Adapters



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.agora.openduo.DataLayer.Models.Staff
import io.agora.openduo.R
import kotlinx.android.synthetic.main.admin_level2_items.view.*


class UserCellAdapter (
        private val context: Context,
        private var users: List<Staff>,
        private val clickListener: (Staff) -> Unit
) : RecyclerView.Adapter<UserCellAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
                LayoutInflater.from(parent.context).inflate(R.layout.admin_level2_items, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context,users[position], clickListener)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return users.size
    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context,user: Staff, clickListener: (Staff) -> Unit) {
            itemView.name_tv.text = user.user_id


            itemView.call_btn.setOnClickListener{
               clickListener(user)
            }

        }
    }

}