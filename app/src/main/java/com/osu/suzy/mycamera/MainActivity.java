package com.osu.suzy.mycamera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.osu.suzy.mycamera.Adapter.PhotoWallAdapter;
import com.osu.suzy.mycamera.utils.ScreenUtils;
import com.osu.suzy.mycamera.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends Activity {

    private TextView title_tv;
   // private String[] item={"A","B","C","D"};
  //  private int[] image = { R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d};
    GridView gv;
  //  SimpleAdapter adapter;
    private ArrayList<String> list;
    private PhotoWallAdapter adapter;
    /**
     * 当前文件夹路径
     */
    private String currentFolder = null;
    /**
     * 当前展示的是否为最近照片
     */
    private boolean isLatest = true;

    static final int REQUEST_IMAGE_CAPTURE = 11111;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenUtils.initScreen(this);
        setContentView(R.layout.activity_main);

        title_tv=(TextView)findViewById(R.id.topbar_title_tv);
        title_tv.setText("Photos");

        Button btn_pto=(Button)findViewById(R.id.topbar_right_btn);
        btn_pto.setVisibility(View.VISIBLE);
        gv=(GridView)findViewById(R.id.gridView);

        list = getImagePaths(100);
        adapter = new PhotoWallAdapter(this, list);
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //show big picture
            }
        });

        btn_pto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Take a photo
                displayCamera();
            }
        });
    }
    //send an intent to capture a photo
    private void displayCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //create the file where the photo should go
            File photoFile=null;
            try {
                photoFile=createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photoFile!=null){
                mCurrentPhotoPath=photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
               // takePictureIntent.putExtra("data",Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

           }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent intent=new Intent(MainActivity.this, photo.class);
            intent.putExtra("new_photo_path",mCurrentPhotoPath);
            startActivity(intent);
            finish();
        }
    }

    private byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0)
            return null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException ioe) {
            return null;
        }

        byte[] bytes = bos.toByteArray();
        return bytes != null && bytes.length > 0 ? bytes : null;
    }

    private File createImageFile() throws IOException{
        //create an image file name
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="JPEG"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(imageFileName,".jpg",storageDir);

        //save a file:path for use with Action_view intents
        mCurrentPhotoPath="file:"+image.getAbsolutePath();
        return image;
    }
    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    private ArrayList<String> getLatestImagePaths(int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
       // Uri mImageUri= Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment
             //   .DIRECTORY_PICTURES));
        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContentResolver();

        // 只查询jpg和png的图片,按最新修改排序
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<String> latestImagePaths = null;
        if (cursor != null) {
            //从最新的图片开始读取.
            //当cursor中没有数据时，cursor.moveToLast()将返回false
            if (cursor.moveToLast()) {
                latestImagePaths = new ArrayList<String>();

                while (true) {
                    // 获取图片的路径
                    String path = cursor.getString(0);
                    latestImagePaths.add(path);

                    if (latestImagePaths.size() >= maxCount || !cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }

        return latestImagePaths;
    }

    private ArrayList<String> getImagePaths(int maxCount) {
        ArrayList<String>  imagePaths =  new ArrayList<String>();
        try{
            File dir =new File(Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_PICTURES).toString());
            FilenameFilter fileNameFilter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    if(name.lastIndexOf('.')>0)
                    {
                        // get last index for '.' char
                        int lastIndex = name.lastIndexOf('.');

                        // get extension
                        String str = name.substring(lastIndex);

                        // match path name extension
                        if(str.equals(".jpg"))
                        {
                            return true;
                        }
                    }
                    return false;
                }
            };
            if(dir.isDirectory()){
                File[] flist=dir.listFiles(fileNameFilter);
                for(File path:flist){
                    imagePaths.add(path.toString());
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return  imagePaths;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
