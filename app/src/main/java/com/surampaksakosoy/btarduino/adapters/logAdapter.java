package com.surampaksakosoy.btarduino.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.surampaksakosoy.btarduino.R;
import com.surampaksakosoy.btarduino.models.logModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class logAdapter extends RecyclerView.Adapter<logAdapter.Holder> {
    private List<logModel> models;

    public logAdapter(List<logModel> dataModelSource){
        this.models = dataModelSource;
    }

    @NonNull
    @Override
    public logAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_log,parent,false);
        return new Holder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull logAdapter.Holder holder, int position) {
        final logModel logModel = models.get(position);
        holder.textViewEvent.setText(logModel.getEvent());
        holder.textViewTime.setText(logModel.getTime());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class Holder extends ViewHolder {

        private TextView textViewEvent, textViewTime;
        Holder(@NonNull View itemView) {
            super(itemView);
            textViewEvent = itemView.findViewById(R.id.log_event);
            textViewTime = itemView.findViewById(R.id.log_time);
        }
    }
}
