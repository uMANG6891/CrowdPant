package com.umang.crowdpant.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.umang.crowdpant.R;
import com.umang.crowdpant.data.CrowdPantContract;
import com.umang.crowdpant.data.CrowdPantContract.BookmarkEntry;
import com.umang.crowdpant.ui.adapter.OutfitAdapter;
import com.umang.crowdpant.utility.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by umang on 10/02/16.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.main_cv_card_main)
    CardView cvMain;

    @Bind(R.id.main_vp_shirt)
    ViewPager pagerShirts;
    @Bind(R.id.main_vp_pants)
    ViewPager pagerPants;

    @Bind(R.id.main_fab_renew)
    FloatingActionButton fabRenew;
    @Bind(R.id.main_fab_bookmark)
    FloatingActionButton fabBookmark;
    @Bind(R.id.main_tv_info)
    TextView tvInfo;

    OutfitAdapter adapterShirt, adapterPant;

    private static final int LOADER_OUTFIT_SHIRTS = 0;
    private static final int LOADER_OUTFIT_PANTS = 1;

    // private int CURRENT_INDEX;
    int lastShirtPos = -1, lastPantPos = -1;
    Cursor SHIRTS, PANTS;

    Boolean shirtLoaded = false, pantLoaded = false;

    // Adding new outfits
    private final int IMAGE_MAX_SIZE = 400;
    private String OUTFIT_TYPE;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private final int REQUEST_CAMERA = 6534;
    private final int REQUEST_FILE_GALLERY = 4756;
    private Uri captureImageUri;

    private final String SHIRT_POSITION = "SHIRT_POSITION";
    private final String PANT_POSITION = "PANT_POSITION";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SHIRT_POSITION, lastShirtPos);
        outState.putInt(PANT_POSITION, lastPantPos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            lastShirtPos = savedInstanceState.getInt(SHIRT_POSITION, -1);
            lastPantPos = savedInstanceState.getInt(PANT_POSITION, -1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        disableControls();

        fabRenew.setOnClickListener(this);
        fabBookmark.setOnClickListener(this);
        fabRenew.setOnLongClickListener(this);
        fabBookmark.setOnLongClickListener(this);

        adapterShirt = new OutfitAdapter(this, null, Constants.SHIRT);
        adapterPant = new OutfitAdapter(this, null, Constants.PANT);
        pagerShirts.setAdapter(adapterShirt);
        pagerPants.setAdapter(adapterPant);

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
                adapterShirt.swapData(data);
                showNewPair(false);
                break;
            case LOADER_OUTFIT_PANTS:
                pantLoaded = true;
                PANTS = data;
                adapterPant.swapData(data);
                showNewPair(false);
                break;
            default:
                break;
        }
    }

    private void showNewPair(boolean forceUpdate) {
        if (shirtLoaded && pantLoaded && SHIRTS.getCount() > 0 && PANTS.getCount() > 0) {
            enableControls();
            if (forceUpdate || (lastShirtPos == -1 || lastPantPos == -1)) {
                Random rand = new Random();
                lastShirtPos = rand.nextInt(SHIRTS.getCount());
                lastPantPos = rand.nextInt(PANTS.getCount());
            }
            pagerShirts.setCurrentItem(lastShirtPos);
            pagerPants.setCurrentItem(lastPantPos);
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

    @OnClick({R.id.main_ib_add_shirt, R.id.main_ib_add_pant})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_ib_add_shirt:
                checkIfHasWritePermission(CrowdPantContract.PATH_SHIRT);
                break;
            case R.id.main_ib_add_pant:
                checkIfHasWritePermission(CrowdPantContract.PATH_PANT);
                break;
            case R.id.main_fab_renew:
                showNewPair(true);
                break;
            case R.id.main_fab_bookmark:
                SHIRTS.moveToPosition(pagerShirts.getCurrentItem());
                PANTS.moveToPosition(pagerPants.getCurrentItem());
                ContentValues bookmarkValues = new ContentValues();
                bookmarkValues.put(BookmarkEntry.COLUMN_SHIRT_ID, SHIRTS.getString(Constants.COL_SHIRT_ID));
                bookmarkValues.put(BookmarkEntry.COLUMN_PANT_ID, PANTS.getString(Constants.COL_PANT_ID));
                getContentResolver().insert(BookmarkEntry.CONTENT_URI, bookmarkValues);
                showToast(R.string.added_to_bookmark);
                break;
            default:
                break;
        }
    }

    private void checkIfHasWritePermission(final String type) {
        OUTFIT_TYPE = type;
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            showImageOptionDialog();
        }
    }

    private void showImageOptionDialog() {
        final String[] items = getResources().getStringArray(R.array.get_image_from_source);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_new_outfit, OUTFIT_TYPE));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();
                if (item == 0) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, getString(R.string.new_picture));
                    values.put(MediaStore.Images.Media.DESCRIPTION, getString(R.string.from_camera));
                    captureImageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (item == 1) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, getString(R.string.select_image)),
                            REQUEST_FILE_GALLERY);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkIfHasWritePermission(OUTFIT_TYPE);
                } else {
                    // permission denied
                    showSnackBar(getString(R.string.cant_proceed_without_write_permission));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), captureImageUri);
                        uploadImage(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_FILE_GALLERY:
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    Bitmap bm;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    uploadImage(bm);
                    break;
                default:
                    break;
            }
        }
    }

    private void uploadImage(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                if (imageWidth >= imageHeight) {
                    if (imageWidth > IMAGE_MAX_SIZE) {
                        imageHeight = imageHeight / (imageWidth / IMAGE_MAX_SIZE);
                        bitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_MAX_SIZE, imageHeight, true);
                    }
                } else {
                    if (imageHeight > IMAGE_MAX_SIZE) {
                        imageWidth = imageWidth / (imageHeight / IMAGE_MAX_SIZE);
                        bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, IMAGE_MAX_SIZE, true);
                    }
                }
                String folder = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name_no_space);
                String outfitLocation = folder + "/" + OUTFIT_TYPE + "_" + System.currentTimeMillis() + ".jpg";

                // creating folder CrowdFit if it doesn't exists
                File file = new File(folder);
                boolean success = true;
                if (!file.exists()) {
                    success = file.mkdirs();
                }
                if (success) {
                    bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            new FileOutputStream(outfitLocation));
                    ContentValues outfitValues = new ContentValues();
                    if (OUTFIT_TYPE.equalsIgnoreCase(CrowdPantContract.PATH_SHIRT)) {
                        outfitValues.put(CrowdPantContract.ShirtEntry.COLUMN_STORED_AT, outfitLocation);
                        getContentResolver().insert(CrowdPantContract.ShirtEntry.CONTENT_URI, outfitValues);
                        showSnackBar(getString(R.string.success_adding_outfit, OUTFIT_TYPE));
                    } else if (OUTFIT_TYPE.equalsIgnoreCase(CrowdPantContract.PATH_PANT)) {
                        outfitValues.put(CrowdPantContract.PantEntry.COLUMN_STORED_AT, outfitLocation);
                        getContentResolver().insert(CrowdPantContract.PantEntry.CONTENT_URI, outfitValues);
                        showSnackBar(getString(R.string.success_adding_outfit, OUTFIT_TYPE));
                    }
                } else {
                    // Do something else on failure
                    // error creating the folders
                    Log.e("error", "creating folder");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showSnackBar(getString(R.string.error_with_reason, e.getLocalizedMessage()));
            }
        } else {
            showSnackBar(getString(R.string.no_image_selected));
        }
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.main_fab_renew:
                showToast(R.string.refresh_combination);
                return true;
            case R.id.main_fab_bookmark:
                showToast(R.string.add_to_bookmark);
                return true;
            default:
                break;
        }
        return false;
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
}
