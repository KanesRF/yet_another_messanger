package com.example.webapp;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SupaAdapter extends RecyclerView.Adapter<SupaAdapter.ExampleViewHolder>{
private ArrayList<SpannableString> mExampleList;

public static class ExampleViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextView1;

    public ExampleViewHolder(View itemView) {
        super(itemView);
        mTextView1 = itemView.findViewById(R.id.textView);
    }
}

    public SupaAdapter(ArrayList<SpannableString> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        SpannableString currentItem = mExampleList.get(position);
        holder.mTextView1.setText(currentItem);
        holder.mTextView1.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
