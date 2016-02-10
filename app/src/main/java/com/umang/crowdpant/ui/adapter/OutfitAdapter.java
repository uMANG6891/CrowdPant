package com.umang.crowdpant.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.umang.crowdpant.R;
import com.umang.crowdpant.ui.activity.MainActivity;
import com.umang.crowdpant.utility.Constants;

/**
 * Created by umang on 10/02/16.
 */
public class OutfitAdapter extends PagerAdapter {

    MainActivity con;
    LayoutInflater mLayoutInflater;
    Cursor outfitData;
    int outfitType;

    public OutfitAdapter(MainActivity context, Cursor objects, int type) {
        con = context;
        mLayoutInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        outfitData = objects;
        outfitType = type;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.container_outfit, container, false);

        outfitData.moveToPosition(position);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        String path = outfitType == Constants.SHIRT ?
                outfitData.getString(Constants.COL_SHIRT_STORED_AT) :
                outfitData.getString(Constants.COL_PANT_STORED_AT);

        Glide.with(con)
                .load(path)
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return outfitData == null ? 0 : outfitData.getCount();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    public void swapData(Cursor objects) {
        outfitData = objects;
        notifyDataSetChanged();
    }
}
