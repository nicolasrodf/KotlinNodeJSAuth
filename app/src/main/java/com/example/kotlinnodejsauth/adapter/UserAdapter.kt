package com.example.kotlinnodejsauth.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinnodejsauth.R
import com.example.kotlinnodejsauth.interfaces.IUserClickListener
import com.example.kotlinnodejsauth.model.User
import kotlinx.android.synthetic.main.layout_user_item.view.*

class UserAdapter(internal var context:Context, internal var userList:List<User>): RecyclerView.Adapter<UserAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),View.OnClickListener{

        internal var root_view: CardView
        internal var name:TextView
        internal var email:TextView

        internal lateinit var userClickListener:IUserClickListener

        fun setClick(userClickListener: IUserClickListener){
            this.userClickListener = userClickListener
        }

        init {
            root_view = itemView.findViewById(R.id.root_view) as CardView
            name = itemView.findViewById(R.id.name_textView) as TextView
            email = itemView.findViewById(R.id.email_textView) as TextView

            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            userClickListener.onUserClick(p0!!,adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_user_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = userList[position].name
        holder.email.text = userList[position].email

        if(position % 2 == 0)
            holder.root_view.setCardBackgroundColor(Color.parseColor("#E1E1E1"))

        holder.setClick(object:IUserClickListener{
            override fun onUserClick(view: View, position: Int) {
                Toast.makeText(context, ""+userList[position].name, Toast.LENGTH_SHORT).show();
            }

        })
    }
}