package com.example.asus.student_app.db;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.student_app.R;
import com.example.asus.student_app.entities.User;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<User> arrayList = new ArrayList<>();

    public RecyclerAdapter(ArrayList<User> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Name.setText(arrayList.get(position).getName());
        int sync_status = arrayList.get(position).getSync_status();

        if (sync_status == DBContract.sync_status_ok){
            holder.sync_status.setImageResource(R.drawable.baseline_done_black_24);
        }
        else {
            holder.sync_status.setImageResource(R.drawable.baseline_sync_24);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView sync_status;
        TextView Name;

        public MyViewHolder(View itemView) {
            super(itemView);
            sync_status = (ImageView)itemView.findViewById(R.id.syncimage);
            Name = (TextView)itemView.findViewById(R.id.textName);
        }
    }
}
