package com.hakulatata.camera.holder;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hakulatata on 2017/6/26.
 */
public class ViewHolder {
    private SparseArray<View> mViews;
    private static int mPosition;
    private View mConvertView;
    public ViewHolder(Context mContext, ViewGroup parent, int layoutId, int position){
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(mContext).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }
    public static ViewHolder get(Context mContext, View convertView, ViewGroup parent, int layoutId, int position){
        if(convertView == null){
            return new ViewHolder(mContext,parent,layoutId,position);
        }else{
            ViewHolder holder = (ViewHolder) convertView.getTag();
            mPosition = position;
            return holder;
        }
    }

    /**
     * 通过viewId获取控件
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T)view;
    }

    public View getmConvertView() {
        return mConvertView;
    }
}
