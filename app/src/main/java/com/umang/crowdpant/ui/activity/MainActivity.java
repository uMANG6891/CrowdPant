package com.umang.crowdpant.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umang.crowdpant.R;
import com.umang.crowdpant.data.CrowdPantContract;
import com.umang.crowdpant.data.CrowdPantContract.BookmarkEntry;
import com.umang.crowdpant.utility.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 10/02/16.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.main_cv_card_main)
    CardView cvMain;
    @Bind(R.id.main_iv_shirt)
    ImageView ivShirt;

    @Bind(R.id.main_iv_pant)
    ImageView ivPant;

    @Bind(R.id.main_fab_renew)
    FloatingActionButton fabRenew;
    @Bind(R.id.main_fab_bookmark)
    FloatingActionButton fabBookmark;
    @Bind(R.id.main_tv_info)
    TextView tvInfo;

    private static final int LOADER_OUTFIT_SHIRTS = 0;
    private static final int LOADER_OUTFIT_PANTS = 1;

    private ArrayList<HashMap<Integer, Integer>> combination;
    private int CURRENT_INDEX;

    Cursor SHIRTS, PANTS;
    Boolean shirtLoaded = false, pantLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        disableControls();

        fabRenew.setOnClickListener(this);
        fabBookmark.setOnClickListener(this);

        getSupportLoaderManager().initLoader(LOADER_OUTFIT_SHIRTS, null, this);
        getSupportLoaderManager().initLoader(LOADER_OUTFIT_PANTS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_OUTFIT_SHIRTS:
                return new CursorLoader(
                        this,
                        CrowdPantContract.ShirtEntry.CONTENT_URI,
                        Constants.SHIRT_PROJECTION_COLS,
                        null,
                        null,
                        null
                );
            case LOADER_OUTFIT_PANTS:
                return new CursorLoader(
                        this,
                        CrowdPantContract.PantEntry.CONTENT_URI,
                        Constants.PANT_PROJECTION_COLS,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_OUTFIT_SHIRTS:
                shirtLoaded = true;
                SHIRTS = data;
                createShirtPantPair();
                break;
            case LOADER_OUTFIT_PANTS:
                pantLoaded = true;
                PANTS = data;
                createShirtPantPair();
                break;
            default:
                break;
        }
    }

    private void createShirtPantPair() {
        if (shirtLoaded && pantLoaded) {
            if (SHIRTS != null && SHIRTS.getCount() != 0 && PANTS != null && PANTS.getCount() != 0) {
                enableControls();
                combination = new ArrayList<>();
                HashMap<Integer, Integer> inner;
                for (int i = 0; i < SHIRTS.getCount(); i++) {
                    for (int j = 0; j < PANTS.getCount(); j++) {
                        inner = new HashMap<>();
                        inner.put(i, j);
                        combination.add(inner);
                    }
                }
                showNewPair();
            } else {
                disableControls();
                startActivity(new Intent(this, AddNewOutfitActivity.class));
            }
        } else {
            disableControls();
        }
    }

    private void showNewPair() {
        if (combination.size() > 0) {
            enableControls();
            Random rand = new Random();
            CURRENT_INDEX = rand.nextInt(combination.size());
            HashMap<Integer, Integer> r = combination.get(CURRENT_INDEX);
            int shirt_id = -1, pant_id = -1;
            for (int key : r.keySet()) {
                shirt_id = key;
                pant_id = r.get(shirt_id);
                break;
            }
            SHIRTS.moveToPosition(shirt_id);
            PANTS.moveToPosition(pant_id);
            Glide.with(this)
                    .load(SHIRTS.getString(Constants.COL_SHIRT_STORED_AT))
                    .into(ivShirt);
            Glide.with(this)
                    .load(PANTS.getString(Constants.COL_PANT_STORED_AT))
                    .into(ivPant);
        } else {
            disableControls();
            tvInfo.setText(R.string.no_more_combinations);
        }
    }

    private void disableControls() {
        fabRenew.setClickable(false);
        fabBookmark.setClickable(false);
        fabRenew.setAlpha(0.5f);
        fabBookmark.setAlpha(0.5f);

        cvMain.setVisibility(View.GONE);
        tvInfo.setVisibility(View.VISIBLE);
    }

    private void enableControls() {
        fabRenew.setClickable(true);
        fabBookmark.setClickable(true);
        fabRenew.setAlpha(1f);
        fabBookmark.setAlpha(1f);

        cvMain.setVisibility(View.VISIBLE);
        tvInfo.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fab_renew:
                combination.remove(CURRENT_INDEX);
                showNewPair();
                break;
            case R.id.main_fab_bookmark:
                ContentValues bookmarkValues = new ContentValues();
                bookmarkValues.put(BookmarkEntry.COLUMN_SHIRT_ID, SHIRTS.getString(Constants.COL_SHIRT_ID));
                bookmarkValues.put(BookmarkEntry.COLUMN_PANT_ID, PANTS.getString(Constants.COL_PANT_ID));
                getContentResolver().insert(BookmarkEntry.CONTENT_URI, bookmarkValues);
                showToast(getString(R.string.added_to_bookmark));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_OUTFIT_SHIRTS, null, this);
        getSupportLoaderManager().restartLoader(LOADER_OUTFIT_PANTS, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        shirtLoaded = false;
        pantLoaded = false;
        SHIRTS = null;
        PANTS = null;

        getSupportLoaderManager().destroyLoader(LOADER_OUTFIT_SHIRTS);
        getSupportLoaderManager().destroyLoader(LOADER_OUTFIT_PANTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_add:
                startActivity(new Intent(this, AddNewOutfitActivity.class));
                return true;
            default:
                return false;
        }
    }
}
