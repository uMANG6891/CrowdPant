package com.umang.crowdpant.ui.adapter;

import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.umang.crowdpant.R;
import com.umang.crowdpant.data.CrowdPantContract;
import com.umang.crowdpant.ui.activity.AddNewOutfitActivity;
import com.umang.crowdpant.ui.adapter.OutfitAdapter.VH;
import com.umang.crowdpant.utility.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 10/02/16.
 */
public class OutfitAdapter extends RecyclerView.Adapter<VH> {

    AddNewOutfitActivity context;
    Cursor OUTFIT_DATA;
    String OUTFIT_TYPE;

    public OutfitAdapter(AddNewOutfitActivity context, String outfitType, Cursor data) {
        this.context = context;
        OUTFIT_TYPE = outfitType;
        OUTFIT_DATA = data;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_outfit, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        OUTFIT_DATA.moveToPosition(position);
        String imagePath;
        if (OUTFIT_TYPE.equalsIgnoreCase(CrowdPantContract.PATH_SHIRT))
            imagePath = OUTFIT_DATA.getString(Constants.COL_SHIRT_STORED_AT);
        else
            imagePath = OUTFIT_DATA.getString(Constants.COL_PANT_STORED_AT);
        Glide.with(context)
                .load(imagePath)
                .into(holder.ivOutfit);
    }

    @Override
    public int getItemCount() {
        return OUTFIT_DATA == null ? 0 : OUTFIT_DATA.getCount();
    }

    public void swapData(Cursor data) {
        OUTFIT_DATA = data;
        notifyDataSetChanged();
    }

    class VH extends RecyclerView.ViewHolder {

        @Bind(R.id.item_o_cv_main)
        CardView cvMain;

        @Bind(R.id.item_o_iv_outfit)
        ImageView ivOutfit;

        public VH(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
