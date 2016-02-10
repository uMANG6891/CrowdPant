package com.umang.crowdpant.ui.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.umang.crowdpant.R;
import com.umang.crowdpant.data.CrowdPantContract.PantEntry;
import com.umang.crowdpant.data.CrowdPantContract.ShirtEntry;
import com.umang.crowdpant.data.CrowdPantContract.BookmarkEntry;
import com.umang.crowdpant.data.CrowdPantContract;
import com.umang.crowdpant.ui.adapter.BookmarkAdapter;
import com.umang.crowdpant.utility.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 10/02/16.
 */
public class BookmarksActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.bm_rv_main)
    RecyclerView rvMain;

    @Bind(R.id.bm_tv_info)
    TextView tvInfo;

    BookmarkAdapter adapter;
    private static final int LOADER_BOOKMARKS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        ButterKnife.bind(this);

        adapter = new BookmarkAdapter(this, null);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        rvMain.setAdapter(adapter);

        setTitle(R.string.title_bookmarks);
        showInfoText();

        getSupportLoaderManager().initLoader(LOADER_BOOKMARKS, null, this);
    }


    private void showInfoText() {
        tvInfo.setVisibility(View.VISIBLE);
        rvMain.setVisibility(View.GONE);
    }

    private void hideInfoText() {
        tvInfo.setVisibility(View.GONE);
        rvMain.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_BOOKMARKS:
                return new CursorLoader(
                        this,
                        BookmarkEntry.CONTENT_URI,
                        Constants.BOOKMARK_PROJECTION_COLS,
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
            case LOADER_BOOKMARKS:
                adapter.swapData(data);
                if (data.getCount() > 0) {
                    hideInfoText();
                } else {
                    showInfoText();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
