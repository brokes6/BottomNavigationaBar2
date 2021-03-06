package com.example.bottomnavigationabar2.MoBan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomnavigationabar2.HomeFragment;
import com.example.bottomnavigationabar2.Post;
import com.example.bottomnavigationabar2.R;
import com.example.bottomnavigationabar2.adapter.NineGridTest2Adapter;
import com.example.bottomnavigationabar2.model.NineGridTestModel;
import com.example.bottomnavigationabar2.utils.FileCacheUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;

public class RecommendTemplate extends Fragment implements PostTemplateInterface {
    private static final String ARG_LIST = "list";
    private int page=1;
    private RecommendTemplate moBan=null;
    private RecyclerView mRecyclerView;
    private LinearLayout loadLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private NineGridTest2Adapter mAdapter;
    private List<Post> mList = new ArrayList<>();
    private TextView loadTextView;
    private Button loadButton;
    private ProgressBar progressBar;
    private View view;
    private String token= FileCacheUtil.getUser(getContext()).getToken();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PostTemplateInterface.NOTIFY:
                    mAdapter.notifyDataSetChanged();
                    loadLayout.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    break;
                case PostTemplateInterface.SHOWTOAST:
                    Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                case RequestStatus.NO_NETWORK:
                    progressBar.setVisibility(View.GONE);
                    loadTextView.setText("加载失败，请检查网络后重新尝试!");
                    loadButton.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    private boolean flag;
    private int tagId;
    private String url;

    public static RecommendTemplate newIntance(boolean flag, int tagId, String url){
        RecommendTemplate template=new RecommendTemplate();
        template.flag=flag;
        template.tagId=tagId;
        template.url=url;
        Bundle bundle=new Bundle();
        bundle.putBoolean("flag",flag);
        bundle.putInt("tagId",tagId);
        bundle.putString("url",url);
        template.setArguments(bundle);
        return template;
    }
    public static void startActivity(Context context, List<NineGridTestModel> list) {
        Intent intent = new Intent(context, PopularPostTemplate.class);
        intent.putExtra(ARG_LIST, (Serializable) list);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        flag=args.getBoolean("flag");
        tagId=args.getInt("tagId");
        url=args.getString("url");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ------------");
        view = inflater.inflate(R.layout.mo_ban_1, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NineGridTest2Adapter(getContext());
        mAdapter.setList(mList);
        mRecyclerView.setAdapter(mAdapter);
        loadLayout=view.findViewById(R.id.loadLayout);
        loadTextView=view.findViewById(R.id.loadTextView);
        loadButton=view.findViewById(R.id.loadButton);
        progressBar=view.findViewById(R.id.loading);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loadTextView.setText("正在努力加载中");
                loadButton.setVisibility(View.GONE);
                getPostList(token);
            }
        });
    }
    public void getPostList(final String token){
        new Thread(){
            @Override
            public void run() {
                final Request request = new Request.Builder()
                        .url(handlerUrl(token))
                        .build();
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message=new Message();
                        message.what=RequestStatus.NO_NETWORK;
                        handler.sendMessage(message);
                        return;
                    }
                    @Override
                    public void onResponse(Call call, Response response) {
                        try {
                            String responseStr = response.body().string();
                            Log.i(TAG, "onResponse:---"+responseStr);
                            JSONObject jsonObject = new JSONObject(responseStr);
                            int code=jsonObject.getInt("code");
                            if(code==0){
                                Message message=new Message();
                                message.what=RequestStatus.NO_NETWORK;
                                handler.sendMessage(message);
                                return;
                            }
                            String dataStr = jsonObject.getString("data");
                            Gson gson = new Gson();
                            List<Post> posts = gson.fromJson(dataStr, new TypeToken<List<Post>>() {}.getType());
                            mAdapter.setList(posts);
                            Message message = new Message();
                            message.what = PostTemplateInterface.NOTIFY;
                            handler.sendMessage(message);
                            page++;
                            for (Post post:posts){
                                post.getUsername();
                            }
                        }catch(Exception exception){
                            exception.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    public void clearList() {
        mList.clear();
        mList=new ArrayList<>();
        mAdapter = new NineGridTest2Adapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        page=1;
    }

    @Override
    public RecyclerView getRecycler() {
        return mRecyclerView;
    }

    public void showToast(String msg){
        Message message = new Message();
        message.what= PostTemplateInterface.SHOWTOAST;
        message.obj=msg;
        handler.sendMessage(message);
    }
    @Override
    public void updateInfo(Intent intent) {
        mAdapter.updateInfo(intent);
    }

    public String handlerUrl(String token){
        String requestUrl=url+"?startPage="+page+"&token="+token;
        if(flag){
            requestUrl+="&tagId="+tagId;
        }
        return requestUrl;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        page=1;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPostList(HomeFragment.userData.getToken());
    }
}
