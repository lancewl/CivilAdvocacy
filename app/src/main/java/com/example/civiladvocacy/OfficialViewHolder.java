package com.example.civiladvocacy;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    TextView name;

    OfficialViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.office_title);
        name = view.findViewById(R.id.office_name);
    }
}
