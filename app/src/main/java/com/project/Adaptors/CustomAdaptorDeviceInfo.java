package com.project.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.internship_task_1.R;

import java.util.ArrayList;

public class CustomAdaptorDeviceInfo extends RecyclerView.Adapter<CustomAdaptorDeviceInfo.viewHolder> {

    ArrayList<String> details_of_device;
    ArrayList<String> status_of_device;

    public CustomAdaptorDeviceInfo(ArrayList<String> details, ArrayList<String> status) {
        this.details_of_device=details;
        this.status_of_device=status;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.deviceinfo_layout,parent,false);
        viewHolder vH=new viewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.feature.setText(details_of_device.get(position));
        holder.status.setText(status_of_device.get(position));

    }

    @Override
    public int getItemCount() {
        return details_of_device.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        TextView feature;
        TextView status;
        Context c;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            feature=itemView.findViewById(R.id.details_of_dev_textview);
            status=itemView.findViewById(R.id.details_of_dev_textview2);
            c=itemView.getContext();

        }
    }
}
