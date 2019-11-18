package com.example.bottomnavigationabar2.adapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.example.bottomnavigationabar2.MyImageView;
import com.example.bottomnavigationabar2.Post;
import com.example.bottomnavigationabar2.PostDetails;
import com.example.bottomnavigationabar2.R;
import com.example.bottomnavigationabar2.UserInformation;
import com.example.bottomnavigationabar2.model.NineGridTestModel;
import com.example.bottomnavigationabar2.utils.FileCacheUtil;
import com.example.bottomnavigationabar2.utils.HandlerUtil;
import com.example.bottomnavigationabar2.utils.NetWorkUtil;
import com.example.bottomnavigationabar2.view.NineGridTestLayout;
import com.example.util.DateTimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HMY on 2016/8/6
 */
public class NineGridTest2Adapter extends RecyclerView.Adapter<NineGridTest2Adapter.ViewHolder> {
    private static final String TAG = "NineGridTest2Adapter";
    private MyImageView tieze_user_img;
    private Context mContext;
    private View convertView;
    private List<Post> mList=new ArrayList<>();
    protected LayoutInflater inflater;
    private NetWorkUtil netWorkUtil;
    private String token;
    public NineGridTest2Adapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        netWorkUtil=new NetWorkUtil(context);
        token=FileCacheUtil.getUser(context).getToken();
    }

    public void setList(List<Post> list) {
        mList.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        convertView = inflater.inflate(R.layout.item_bbs_nine_grid, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);
        Log.i(TAG, "onCreateViewHolder:111");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.i(TAG, "onBindViewHolder: 开始创建"+position);
        String content=Html.fromHtml(mList.get(position).getContent()).toString();
        holder.content.setText(content);
        holder.datetime.setText(DateTimeUtil.handlerDateTime(mList.get(position).getPcreateTime()));
        holder.uimg.setImageURL(mList.get(position).getUimg());
        holder.username.setText(mList.get(position).getUsername());
        holder.postId=mList.get(position).getPid();
        holder.loveStatus=mList.get(position).getStatus();
        holder.talkNumStr.setText(String.valueOf(mList.get(position).getCommentCount()));
        holder.layout.setIsShowAll(mList.get(position).isShowAll());
        holder.loveNumStr.setText(String.valueOf(mList.get(position).getLoveCount()));
        holder.layout.setUrlList(Arrays.asList(mList.get(position).getImgUrl().split(",")));
        holder.layout.setContent(content);
        holder.loveLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (holder.loveStatus==1){
                    holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumbs_up_white));
                    holder.loveStatus=0;
                    //加加
                    holder.loveNumStr.setText(String.valueOf(Integer.valueOf(holder.loveNumStr.getText().toString())-1));

                }else{
                    holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumbs_up_complete));
                    holder.loveStatus=1;
                    //减减
                    holder.loveNumStr.setText(String.valueOf(Integer.valueOf(holder.loveNumStr.getText().toString())+1));
                }
               netWorkUtil.updatePostLove(holder.postId,token);
            }
        });
        if(holder.loveStatus==1){
            holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumbs_up_complete));
        }else{
            holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumbs_up_white));
        }
        //这里还没搞收藏的点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 随便响应"+mList.get(position).getPid());
                Intent intent = new Intent(mContext, PostDetails.class);
                intent.putExtra("postId",mList.get(position).getPid());
                android.support.v4.util.Pair<View, String> uimg = new android.support.v4.util.Pair(holder.uimg, "userphoto");
                android.support.v4.util.Pair<View, String> username = new android.support.v4.util.Pair(holder.username, "username");
                android.support.v4.util.Pair<View, String> image = new android.support.v4.util.Pair(holder.layout, "image");
                android.support.v4.util.Pair<View, String> text = new android.support.v4.util.Pair(holder.datetime, "time");
                android.support.v4.util.Pair<View, String> longtext = new android.support.v4.util.Pair(holder.content, "longtext");
                ActivityOptionsCompat optionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, image,text,longtext,uimg,username);
                mContext.startActivity(intent,optionsCompat.toBundle());
            }
        });
        tieze_user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInformation.class);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListSize(mList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        NineGridTestLayout layout;
        LinearLayout loveLayout,collectionLayout;
        MyImageView uimg;
        TextView datetime,content,username,loveNumStr,collectionStr,talkNumStr;
        ImageView loveNum,collection;
        int postId,loveStatus,collectionStatus;
        public ViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            layout = (NineGridTestLayout) itemView.findViewById(R.id.layout_nine_grid);
            username=itemView.findViewById(R.id.tiezi_username);
            tieze_user_img = itemView.findViewById(R.id.tieze_user_img);
            uimg=itemView.findViewById(R.id.tieze_user_img);
            datetime=itemView.findViewById(R.id.tiezi_time);
            content=itemView.findViewById(R.id.tieze_Text);
            loveNum=itemView.findViewById(R.id.loveNum);
            collection=itemView.findViewById(R.id.collection);
            loveNumStr=itemView.findViewById(R.id.loveNumStr);
            collectionStr=itemView.findViewById(R.id.collectionStr);
            talkNumStr=itemView.findViewById(R.id.talkNumStr);
            loveLayout=itemView.findViewById(R.id.loveLayout);
            collectionLayout=itemView.findViewById(R.id.collectionLayout);
        }

    }

    private int getListSize(List<Post> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }
    public void clear(){
        mList=null;
        mList=new ArrayList<>();
    }
}
