package com.umang.crowdpant.ui.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.umang.crowdpant.R;
import com.umang.crowdpant.data.CrowdPantContract.BookmarkEntry;
import com.umang.crowdpant.ui.activity.BookmarksActivity;
import com.umang.crowdpant.ui.adapter.BookmarkAdapter.VH;
import com.umang.crowdpant.utility.Constants;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 10/02/16.
 */
public class BookmarkAdapter extends RecyclerView.Adapter<VH> {

    BookmarksActivity context;
    Cursor BOOKMARK_DATA;

    public BookmarkAdapter(BookmarksActivity context, Cursor data) {
        this.context = context;
        BOOKMARK_DATA = data;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookmark, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        BOOKMARK_DATA.moveToPosition(position);
        Glide.with(context)
                .load(BOOKMARK_DATA.getString(Constants.COL_BOOKMARK_SHIRT_SAVED_AT))
                .into(holder.ivShirt);
        Glide.with(context)
                .load(BOOKMARK_DATA.getString(Constants.COL_BOOKMARK_PANT_SAVED_AT))
                .into(holder.ivPant);
    }

    @Override
    public int getItemCount() {
        return BOOKMARK_DATA == null ? 0 : BOOKMARK_DATA.getCount();
    }

    public void swapData(Cursor data) {
        BOOKMARK_DATA = data;
        notifyDataSetChanged();
    }

    class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.item_b_cv_main)
        CardView cvMain;

        @Bind(R.id.item_b_iv_shirt)
        ImageView ivShirt;
        @Bind(R.id.item_b_iv_pant)
        ImageView ivPant;


        public VH(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cvMain.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_b_cv_main:
                    showOptionMenu();
                    break;
                default:
                    break;
            }
        }

        private void showOptionMenu() {
            String[] items = context.getResources().getStringArray(R.array.get_bookmark_dialog_option);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    final int position = getAdapterPosition();
                    if (item == 0) {
                        BOOKMARK_DATA.moveToPosition(position);
                        shareImages(
                                BOOKMARK_DATA.getString(Constants.COL_BOOKMARK_SHIRT_SAVED_AT),
                                BOOKMARK_DATA.getString(Constants.COL_BOOKMARK_PANT_SAVED_AT));
                    } else if (item == 1) {
                        AlertDialog.Builder d = new AlertDialog.Builder(context);
                        d.setTitle(context.getString(R.string.delete_this_entry))
                                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        deleteOneBookmark(position);
                                    }
                                }).setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                }
            });
            builder.show();
        }
    }

    private void shareImages(String... s) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_bookmark));
        intent.setType("image/jpeg");

        ArrayList<Uri> files = new ArrayList<Uri>();

        for (String path : s) {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        context.startActivity(intent);
    }

    private void deleteOneBookmark(int position) {
        BOOKMARK_DATA.moveToPosition(position);
        context.getContentResolver().delete(
                BookmarkEntry.CONTENT_URI,
                BookmarkEntry.TABLE_NAME + "." + BookmarkEntry._ID + " = ?",
                new String[]{BOOKMARK_DATA.getString(Constants.COL_BOOKMARK_ID)}
        );
        context.showSnackBar(context.getString(R.string.bookmark_deleted));
    }
}
