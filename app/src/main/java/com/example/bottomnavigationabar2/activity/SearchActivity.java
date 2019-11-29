package com.example.bottomnavigationabar2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.bottomnavigationabar2.R;
import com.example.bottomnavigationabar2.adapter.HistorySearchAdapter;
import com.example.bottomnavigationabar2.utils.HistorySearchUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private SearchView searchView;
    private Button searchButton;
    private RecyclerView recyclerView;
    private List<String> historyList=new ArrayList<>();
    private HistorySearchAdapter adapter;
    private LinearLayout searchLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        initView();
        initHistoryRecycler();
        getHistoryList();//得到历史记录数组
        setSearchTvListener();//设置搜索按钮监听器
/*        setHistoryEmptyTvListener();//设置清空记录按钮监听器*/

    }
    private void initView(){
        searchView=findViewById(R.id.searchView);
        searchButton=findViewById(R.id.button);
        recyclerView=findViewById(R.id.recyclerView);
        searchLayout=findViewById(R.id.searchLayout);
        searchView.setQueryHint("请输入搜索内容");
        searchView.setIconifiedByDefault(false);
    }

    private void setSearchTvListener() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryWord=searchView.getQuery().toString();
                HistorySearchUtil.getInstance(SearchActivity.this)
                        .putNewSearch(queryWord);//保存记录到数据库
                getHistoryList();
                adapter.notifyDataSetChanged();
                showViews();
                Toast.makeText(SearchActivity.this, "此条记录已保存到数据库",
                        Toast.LENGTH_SHORT).show();
                searchView.setQuery("",false);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                Intent intent=new Intent(SearchActivity.this,SearchDetailsActivity.class);
                intent.putExtra("queryWord",queryWord);
                startActivityForResult(intent,1);
            }
        });
    }

/*    private void setHistoryEmptyTvListener() {
        historyEmptyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistorySearchUtil.getInstance(MainActivity.this)
                        .deleteAllHistorySearch();
                getHistoryList();
                adapter.notifyDataSetChanged();//刷新列表
                showViews();
            }
        });
    }*/
    /**
     * 设置历史记录界面可见性，即记录为空时，不显示清空历史记录按钮等view
     */
    private void showViews() {
        if (historyList.size() > 0) {
            searchLayout.setVisibility(View.VISIBLE);
        } else {
            searchLayout.setVisibility(View.GONE);
        }
    }
    private void initHistoryRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);//解决滑动冲突
        adapter=new HistorySearchAdapter(this,historyList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new HistorySearchAdapter.OnItemClickListener() {
            @Override
            public void onItemNameTvClick(View v, String name) {
            }

            @Override
            public void onItemDeleteImgClick(View v, String name) {
                HistorySearchUtil.getInstance(SearchActivity.this)
                        .deleteHistorySearch(name);
                getHistoryList();
                adapter.notifyDataSetChanged();
                showViews();
            }
    });
}
    private void getHistoryList(){
        historyList.clear();
        historyList.addAll(HistorySearchUtil.getInstance(this)
                .queryHistorySearchList());
        System.out.println("历史长度为"+historyList.size());
        adapter.notifyDataSetChanged();
        showViews();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: 我返回拉");
        setResult(1);
        finish();
    }

}
