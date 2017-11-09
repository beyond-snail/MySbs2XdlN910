package com.views.MenuPopWindow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adapter.AdapterBase;
import com.toollibrary.R;

import java.util.List;

/**
 * Created by zhouwei on 16/11/30.
 */

public class MyPopAdapter extends AdapterBase<String> {
    private List<String> mData;

    public MyPopAdapter(List<String> data, Context context) {
        super(data, context);
        this.mData = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.pop_list_item, null);

            holder.mTextView = (TextView) convertView.findViewById(R.id.text_content);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(mData.get(position));

        return convertView;
    }

    private static final class ViewHolder {

        public TextView mTextView;
    }

//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_list_item,null));
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//       ViewHolder viewHolder = (ViewHolder) holder;
//
//       viewHolder.mTextView.setText(mData.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return mData == null ? 0:mData.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder{
//        public TextView mTextView;
//        public ViewHolder(View itemView) {
//            super(itemView);
//            mTextView = (TextView) itemView.findViewById(R.id.text_content);
//        }
//    }
}
