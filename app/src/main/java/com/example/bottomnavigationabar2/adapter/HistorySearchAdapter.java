package com.example.bottomnavigationabar2.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bottomnavigationabar2.R;

import java.util.ArrayList;
import java.util.List;

public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.ViewHolder> implements View.OnClickListener {
    private View view;
    private static final String TAG = "HistorySearchAdapter";
    private Context context;
    private List<String> historys=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;//item点击监听接口

    public HistorySearchAdapter(Context context, List<String> historys) {
        this.context = context;
        this.historys = historys;
    }

    @NonNull
    @Override
    public HistorySearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       view= LayoutInflater.from(context).inflate(R.layout.history_item,viewGroup,false);
       ViewHolder viewHolder=new ViewHolder(view);
       return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistorySearchAdapter.ViewHolder viewHolder, int i) {
        viewHolder.name.setText(historys.get(i));
        Log.i(TAG, "onBindViewHolder: 开始绑定"+historys.get(i));
    }

    @Override
    public int getItemCount() {
        return historys.size();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.name://点击历史纪录名称时调用
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemNameTvClick(v, (String) v.getTag());
                }
                break;
/*            case R.id.search_history_item_img://点击删除按钮时调用
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemDeleteImgClick(v, (String) v.getTag());
                }
                break;*/
            default:
        }
    }


    /**
     * 设置item点击监听器
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
        }
    }
    /**
     * item点击接口
     */
    public interface OnItemClickListener {
        void onItemNameTvClick(View v, String name);//点击历史纪录名称时
        void onItemDeleteImgClick(View v, String name);//点击删除按钮时
    }
}