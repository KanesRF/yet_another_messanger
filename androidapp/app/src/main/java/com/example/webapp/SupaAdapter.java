package com.example.webapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.inflate;
import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;

public class SupaAdapter extends RecyclerView.Adapter<SupaAdapter.ExampleViewHolder>{
private ArrayList<Chat.msg> mExampleList;
public Context context;
private ArrayList<Chat.avatar_item> all_avatars = null;

public static class ExampleViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextView1;
    public TextView mTextView2;
    public ImageView face;
    public View item;


    public ExampleViewHolder(View itemView) {
        super(itemView);
        item = itemView;
        mTextView1 = itemView.findViewById(R.id.textView);
        mTextView2 = itemView.findViewById(R.id.textView2);
        face = itemView.findViewById(R.id.avatar);
    }
}

    public SupaAdapter(ArrayList<Chat.msg> exampleList, Context contex, ArrayList<Chat.avatar_item> ava) {
        mExampleList = exampleList;
        context = contex;
        all_avatars = ava;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Chat.msg currentItem = mExampleList.get(position);
        holder.mTextView1.setText(currentItem.span_text);
        holder.mTextView1.setMovementMethod(LinkMovementMethod.getInstance());
        holder.mTextView2.setText(currentItem.creatorName + " at " + currentItem.date);

        for (Chat.avatar_item cur:all_avatars)
        {
            if (cur.participant_uuid.equals(currentItem.creatorUUID) && cur.avatar!=null)
            {
                holder.face.setImageBitmap(cur.avatar);
                break;
            }
        }

        if(currentItem.img != null) {
            LinearLayout Supa_layout = holder.item.findViewById(R.id.dopustim);
            Supa_layout.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (Bitmap cur:currentItem.img) {
                /*ImageView imagebyCode = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 150);
                //params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                imagebyCode.setLayoutParams(params);

                imagebyCode.setImageBitmap(cur);
                Supa_layout.addView(imagebyCode);*/

                View view = inflater.inflate(R.layout.gachi_help,Supa_layout, false);
                ImageView imagebyCode = view.findViewById(R.id.imageView);
                imagebyCode.setImageBitmap(cur);
                Supa_layout.addView(view);
            }
        }


    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
