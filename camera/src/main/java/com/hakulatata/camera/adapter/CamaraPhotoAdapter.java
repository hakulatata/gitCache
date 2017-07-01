package com.hakulatata.camera.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hakulatata.camera.R;
import com.hakulatata.camera.view.GalleryPopupWindow;

import java.util.List;

/**
 * Created by hakulatata on 2017/6/26.
 */

public class CamaraPhotoAdapter extends BaseAdapter {
    public List<String> mImagePaths;
    private Context mContext;
    private OnImageDeletedListener mListener;

    private GridView mGridView;
    private GalleryPopupWindow mGalleryPopupWindow;

    public CamaraPhotoAdapter(Context context, List<String> mImagePaths) {
        this.mImagePaths = mImagePaths;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mImagePaths.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CamearaHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.addphoto_gridview_item, null);
            holder = new CamearaHolder(convertView);
            convertView.setTag(holder);
        }

        holder = (CamearaHolder) convertView.getTag();
        //防止position 为零的时候重复加载
        if (position == parent.getChildCount()) {
            if (position == mImagePaths.size()) {
                holder.ivItemPhoto.setImageResource(R.drawable.empty_photo);
                holder.ivDelete.setVisibility(View.GONE);
            } else {
                holder.ivDelete.setVisibility(View.VISIBLE);
                String path = mImagePaths.get(position);
                Glide.with(mContext).load(path).asBitmap().error(R.drawable.empty_photo).into(holder.ivItemPhoto);
                holder.ivDelete.setOnClickListener(v -> {
                    if (mListener != null)
                        mListener.onImageDeleted(mImagePaths.indexOf(path));
                });
            }
        }

        return convertView;
    }

    public void setData(List<String> imagePaths) {
        this.mImagePaths = imagePaths;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        setGridViewVisibility();
        updateGalleryPopupWindowData();
    }

    public void setOnImageDeletedListener(OnImageDeletedListener listener) {
        this.mListener = listener;
    }

    public void setGridView(GridView gridView) {
        this.mGridView = gridView;
    }

    private void setGridViewVisibility() {
      /*  if (mGridView != null)
            mGridView.setVisibility(mImagePaths.size() > 0 ? View.VISIBLE : View.GONE);*/
    }

    public void setGalleryPopupWindow(GalleryPopupWindow galleryPopupWindow) {
        this.mGalleryPopupWindow = galleryPopupWindow;
    }

    private void updateGalleryPopupWindowData() {
        if (mGalleryPopupWindow != null)
            mGalleryPopupWindow.setImages(mImagePaths);
    }

    public interface OnImageDeletedListener {
        void onImageDeleted(int position);
    }

    class CamearaHolder {
        private final ImageView ivDelete;
        private final ImageView ivItemPhoto;

        public CamearaHolder(View itemView) {
            ivItemPhoto = (ImageView) itemView.findViewById(R.id.iv_item_photo);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
}
