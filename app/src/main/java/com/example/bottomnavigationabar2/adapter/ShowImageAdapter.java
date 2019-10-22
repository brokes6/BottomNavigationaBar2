package com.example.bottomnavigationabar2.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ShowImageAdapter extends PagerAdapter {
    private List<View> list;

    public ShowImageAdapter(List<View> list){
        this.list=list;
    }

    @Override
    public int getCount() {
        if(list!=null&&list.size()>0){
            return list.size();
        }else{
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(list.get(position));
        return list.get(position);
    }

}
