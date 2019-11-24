package com.example.bottomnavigationabar2.MoBan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bottomnavigationabar2.HomeFragment;
import com.example.bottomnavigationabar2.Post;
import com.example.bottomnavigationabar2.R;
import com.example.bottomnavigationabar2.adapter.NineGridTest2Adapter;
import com.example.bottomnavigationabar2.model.NineGridTestModel;
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

public class PostTemplate_4 extends Fragment implements PostTemplateInterface {
    private static final String ARG_LIST = "list";
    private int page=1;
    private PopularPostTemplate popularPostTemplate =null;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NineGridTest2Adapter mAdapter;
    private List<Post> mList = new ArrayList<>();
    private View view;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PostTemplateInterface.NOTIFY:
                    mAdapter.notifyDataSetChanged();
                    break;
                case PostTemplateInterface.SHOWTOAST:
                    Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();

            }
        }
    };
    public static void startActivity(Context context, List<NineGridTestModel> list) {
        Intent intent = new Intent(context, PopularPostTemplate.class);
        intent.putExtra(ARG_LIST, (Serializable) list);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ------------");
        view = inflater.inflate(R.layout.mo_ban_1, container, false);
//        Toast.makeText(getContext(),"gogogo",Toast.LENGTH_SHORT).show();
        initView();
        getPostList(HomeFragment.userData.getToken());
        return view;
    }
    private void initView() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NineGridTest2Adapter(getContext());
        Log.i(TAG, "initView: "+mList.size());
        mAdapter.setList(mList);
        mRecyclerView.setAdapter(mAdapter);
    }
    public void getPostList(String token){
        final Request request = new Request.Builder()
                .url("http://106.54.134.17/app/getPopularPost?startPage="+page+"&token="+token)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure:失败呃");
            }
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String responseStr = response.body().string();
                    Log.i(TAG, "onResponse:---"+responseStr);
                    JSONObject jsonObject = new JSONObject(responseStr);
                    int code=jsonObject.getInt("code");
                    if(code==0){
                        showToast("别搞拉，去看看其他的地方把");
                        return;
                    }
                    String dataStr = jsonObject.getString("data");
                    Gson gson = new Gson();
                    List<Post> posts = gson.fromJson(dataStr, new TypeToken<List<Post>>() {
                    }.getType());
                    mAdapter.setList(posts);
                    Message message = new Message();
                    message.what = PostTemplateInterface.NOTIFY;
                    handler.sendMessage(message);
                    page++;
                    //存放文章内容
/*
                        setCache(,getContext(),"Text",MODE_PRIVATE);
                        //存放用户名称
                        setCache(User_name,getContext(),"username",MODE_PRIVATE);
                        //存放图片
                        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ErGaoJi/images/";
                        String state = Environment.getExternalStorageState();
                        //如果状态不是mounted，无法读写
                        if (!state.equals(Environment.MEDIA_MOUNTED)) {
                            return;
                        }
                        //通过时间来命名
                        Calendar now = new GregorianCalendar();
                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                        String fileName = simpleDate.format(now.getTime());
                        try {
                            File file = new File(dir + fileName + ".jpg");
                            FileOutputStream out = new FileOutputStream(file);
                            //mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
*/
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public void clearList() {
        mList.clear();
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
}