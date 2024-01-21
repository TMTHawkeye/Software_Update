package com.project.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.internship_task_1.R;

import java.util.List;

public class CustomAdaptorPermissions extends RecyclerView.Adapter<CustomAdaptorPermissions.viewHolder> {

    List<String> permissions;

    public CustomAdaptorPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.permissions_layout,parent,false);
        viewHolder vH=new viewHolder(v);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.permission_textview.setText(permissions.get(position).toString());

    }

    @Override
    public int getItemCount() {
        return permissions.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView permission_textview;
        Toolbar t;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            permission_textview=itemView.findViewById(R.id.permissions_layout_id);
            t=itemView.findViewById(R.id.toolbar_device_info);
        }
    }
}
