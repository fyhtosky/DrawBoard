package com.example.liusheng.painboard.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liusheng.painboard.Adapter.WorksAdapter;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.Tools.ItemDecoration;
import com.example.liusheng.painboard.Tools.PermissionHelper;
import com.example.liusheng.painboard.Tools.ShareUtils;
import com.example.liusheng.painboard.bean.WorkBean;
import com.example.liusheng.painboard.Tools.StorageInSDCard;
import com.example.liusheng.painboard.constant.Constant;

import java.util.ArrayList;
import java.util.List;

/***
 *
 * 我的作品
 */

public class WorksActivity extends MyActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = WorksActivity.class.getSimpleName();
    List<WorkBean> mWorkData = new ArrayList<>();
    WorksAdapter mAdapter;

    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);
        transparentStatusBar();
        checkPermission();
        initView();

        showBannerAd();
    }

    void checkPermission() {
        if (PermissionHelper.hasWriteStoragePermission(this)) {
            getSupportLoaderManager().initLoader(1, null, this);
        } else {
            PermissionHelper.requestWriteStoragePermission(this);
        }
    }

    void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new ItemDecoration());

        mAdapter = new WorksAdapter(this, mWorkData, new WorksAdapter.Callback() {
            @Override
            public void onItemImageClick(View view, int position, String imagePath) {
                // TODO: 2019/2/15 图片点击事件
                //toast(imagePath);
                Intent intent = new Intent(WorksActivity.this,ImagePreviewActivity.class);
                intent.putExtra(Constant.KEY_PREVIEW_IAMGE_PATH, imagePath);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(WorksActivity.this, view, "PHOTO");
                    startActivity(intent, optionsCompat.toBundle());
                } else {
                    startActivity(intent);
                }

            }

            @Override
            public void onItemShareClick(View view, int position, String imagePath) {
                //分享
                share(view, imagePath);
            }

            @Override
            public void onItemDeleteClick(View view, int position, String imagePath) {
                //删除
                showDeleteDialog(view, imagePath);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE){
//                    if (recyclerView.canScrollVertically(1) == false) {
//                        //滑动到底部
//                        Log.e(TAG, "滑动到底部");
//
//                    }
//                    if (recyclerView.canScrollVertically(-1) == false) {
//                        //滑动到顶部
//                        Log.e(TAG, "滑动到顶部");
//
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy < 0) {
//                    //向上滑动
//                    Log.e(TAG, "向上滑动" + dy);
//                }else {
//
//                }
//            }
//        });


    }

    /***
     * 分享
     * @param view
     * @param imagePath
     */
    private void share(View view, String imagePath) {
        if (view == null || imagePath == null) {
            toast("分享失败");
            return;
        }
        ShareUtils.share(view.getContext(), imagePath);
    }

    /***
     * 删除对话框
     * @param view
     * @param imagePath
     */
    private void showDeleteDialog(final View view, final String imagePath) {
        if (view == null) {
            return;
        }
        final Dialog cleanDialog = new Dialog(view.getContext(), R.style.Dialog);
        View contentView = LayoutInflater.from(view.getContext()).inflate(R.layout.drawing_clean_dialog_layout, null);
        cleanDialog.setContentView(contentView);
        TextView title = contentView.findViewById(R.id.tv_clean_dialog_title);
        title.setText("确定要删除此项吗 ？");
        contentView.findViewById(R.id.bt_clean_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.bt_clean_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanDialog.dismiss();
                deleteItem(view.getContext(), imagePath);
            }
        });

        cleanDialog.show();

    }


    /***
     * 删除
     * @param context
     * @param imagePath
     */
    private void deleteItem(Context context, String imagePath) {
        if (context == null || imagePath == null) {
            toast("删除失败");
        }
        String where = MediaStore.Images.Media.DATA + "= ?";
        String[] selectionArgs = new String[]{imagePath};
        int result = context.getApplicationContext().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, selectionArgs);
        if (result != -1) {
            toast("删除成功");
        } else {
            toast("删除失败");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.WRITE_STORAGE_PERMISSION_CODE) {
            if (grantResults != null && grantResults.length >= 1){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportLoaderManager().initLoader(1, null, this);
                } else {
                    Toast.makeText(this, PermissionHelper.TIPS_WRITE_STORAGE_PERMISSION, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DESCRIPTION,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.WIDTH
        };
        String selection = MediaStore.Images.Media.DATA + " LIKE ? AND " + MediaStore.Images.Media.DESCRIPTION + " = ?";
        String[] selectionArgs = {"%" + StorageInSDCard.FILE_PATH + "%", StorageInSDCard.DESCRIPTION};
        String order = MediaStore.Images.Media.DATE_ADDED + " DESC";

        CursorLoader loader = new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                order
        );
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            mWorkData.clear();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            while (cursor.moveToNext()) {
                int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                int widthIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                int heightIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);

                String data = cursor.getString(dataIndex);
                long date = cursor.getLong(dateIndex);
                int width = cursor.getInt(widthIndex);
                int height = cursor.getInt(heightIndex);

                if (width <= 0 || height <= 0) {
                    BitmapFactory.decodeFile(data, options);
                    width = options.outWidth;
                    height = options.outHeight;
                }
                WorkBean workBean = new WorkBean(date * 1000, data, width, height);
                //Log.e(TAG, "WorkBean " + workBean.toString());
                mWorkData.add(workBean);
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Log.e(TAG, "onLoaderReset " + mWorkData.size());
        mWorkData.clear();
    }

    @Override
    public ViewGroup getBannerView() {
        if (bannerViewContainer == null) {
            bannerViewContainer = findViewById(R.id.works_banner_view);
        }
        return bannerViewContainer;
    }

    @Override
    protected void onDestroy() {
        mWorkData.clear();
        super.onDestroy();
    }

    public void onWorksClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
