package com.osu.suzy.mycamera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.osu.suzy.mycamera.utils.SDCardImageLoader;
import com.osu.suzy.mycamera.utils.ScreenUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class photo extends Activity {

    String mCurrentPhotoPath;
    ImageView mImageView;
    private ArrayList<String> list;
    Bitmap bitmap;
    private byte[] new_photo;
    TextView title_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mCurrentPhotoPath = getIntent().getExtras().getString("new_photo_path");

        title_tv=(TextView)findViewById(R.id.topbar_title_tv);
        title_tv.setText("Save Photo");

        Button btn_pto=(Button)findViewById(R.id.topbar_left_btn);
        btn_pto.setVisibility(View.VISIBLE);
        btn_pto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File(mCurrentPhotoPath);
                if(file.exists()) {
                    file.delete();
                }
                Intent intent=new Intent(photo.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mImageView = (ImageView) findViewById(R.id.iv_btnail);

    //    Intent intent = getIntent();
//        new_photo=intent.getByteArrayExtra("new_photo");
//        bitmap=BitmapFactory.decodeByteArray(new_photo,0,new_photo.length);
       // bitmap = (Bitmap) intent.getParcelableExtra("new_photo");
        mImageView.setImageURI(Uri.fromFile(new File(mCurrentPhotoPath)));

        Button btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_title = (EditText) findViewById(R.id.et_title);
                String title = et_title.getText().toString();

//                list = getLatestImagePaths(1);
//                mCurrentPhotoPath=list.get(0);

                    File from = new File(mCurrentPhotoPath);
                    String dir = mCurrentPhotoPath.substring(0, mCurrentPhotoPath.lastIndexOf("/"));
                    File to=new File(dir,title+".jpg");
                    from.renameTo(to);

//                String new_name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/" + title + ".jpg";


//                OutputStream stream =null;
//                File file=new File(new_name);
//                try {
//                    file.createNewFile();
//                    stream=new FileOutputStream(file);
//                    stream.write(new_photo);
//                    stream.flush();
//                    stream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

//                write(photo.this,new_photo,title);
//
                Intent intent=new Intent(photo.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public static void toast(final Activity activity, final String message) {
        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void write(final Activity activity, final byte[] photobyte, final String title) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    File dir = computeDirectory(activity);
                    long now = System.currentTimeMillis();
                    File ptofile=new File(dir,title+".jpg");
                    writeToFile(photobyte,ptofile);
                } catch (IOException e) {
                    toast(activity, "Unable to save file");
                }
            }
        }).start();
    }

    private static void writeToFile( byte[] photo, File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
        try {
            dos.write(photo);
        } finally {
            try {
                dos.close();
            } catch (IOException ignoreThis) {
            }
        }
    }

    private static File computeDirectory(Context context) throws IOException {
        File mainDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File myDir = new File(mainDir, "MyCamera");
        myDir.mkdirs();
        if (!myDir.exists())
            throw new FileNotFoundException("Cannot create directory "
                    + myDir.getCanonicalPath());
        return myDir;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo, menu);
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

    private ArrayList<String> getLatestImagePaths(int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

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
}