package com.example.civiladvocacy;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private static final String TAG = "EmployeesAdapter";
    private final List<Official> officialList;
    private final MainActivity mainAct;

    OfficialAdapter(List<Official> oList, MainActivity ma) {
        this.officialList = oList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_entry, parent, false);

        itemView.setOnClickListener(mainAct);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official official = officialList.get(position);

        holder.title.setText(official.getTitle());
        holder.name.setText(official.getName());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }

}
