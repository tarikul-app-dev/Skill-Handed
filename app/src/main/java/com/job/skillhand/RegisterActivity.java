package com.job.skillhand;

//import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.job.skillhand.utils.ImageCompressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    FrameLayout choosePic;
    EditText edtFullName,edtMobNumber,edtAddress,edtBirthdate,edtGuardName,
            edtGuardPhone,edtTrainSch,edtOverTrade,edtOverDur,edtOverCountry,
            edtOverCompany;
    RadioGroup rgGender,rgHavePassport,rgOverExp;
    Spinner spSkill;
    LinearLayout linBtnSubmit;
    CircleImageView imgvShowPic;
    private final int MULTIPLE_PERMISSIONS = 104;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    //String picPath = " ";
    Uri photouri;
    String photoPicPath = " ";
    String picName = " ";
    File takePicImageFile;
    ImageCompressor imageCompressor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
    }

    public void initViews(){
        choosePic = findViewById(R.id.frame_layout_choose_pic);
        edtFullName = findViewById(R.id.edt_fullname);
        edtMobNumber = findViewById(R.id.edt_mobile_no);
        edtAddress = findViewById(R.id.edt_address);
        edtBirthdate = findViewById(R.id.edt_birth_day);
        edtGuardName = findViewById(R.id.edt_guard_name);
        edtGuardPhone = findViewById(R.id.edt_guard_ph_no);
        edtTrainSch = findViewById(R.id.edt_training_school);
        edtOverTrade = findViewById(R.id.edt_trade);
        edtOverDur = findViewById(R.id.edt_duration);
        edtOverCountry = findViewById(R.id.edt_country);
        edtOverCompany = findViewById(R.id.edt_company);
        spSkill = findViewById(R.id.sp_skill);
        rgGender = findViewById(R.id.rg_gender);
        rgHavePassport = findViewById(R.id.rg_passport);
        rgOverExp = findViewById(R.id.rg_oversease);
        linBtnSubmit = findViewById(R.id.lin_submit);
        imgvShowPic = findViewById(R.id.imgv_pic);
        imageCompressor = new ImageCompressor();

        linBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Take Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = checkPermissions();
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result) {
                        takePictureIntent();
                    }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result) {
                        galleryIntent();
                    }

                }
//                else if (items[item].equals("Delete photo")) {
//                   // txvImgUpSuccess.setText("");
//                    //imgvAddReceipt.setVisibility(View.VISIBLE);
//                    picPath = null;
//                    //imgvShowImage.setBackgroundDrawable(null);
//                    ;
//                }
                else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

   private void takePictureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (pictureIntent.resolveActivity(getPackageManager()) != null) {

                File photoFile = null;
                try {
                    photoFile = createPhotoFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                photouri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photouri);
                startActivityForResult(pictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private File createPhotoFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        photoPicPath = image.getAbsolutePath();
        picName = imageFileName+".jpg";
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }

        }
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                takePicImageFile = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    takePicImageFile.createNewFile();
                    fo = new FileOutputStream(takePicImageFile);
                    fo.write(bytes.toByteArray());
                    fo.close();

                    String picture_url = takePicImageFile.getAbsolutePath();
                    String filePath = getRealPathFromURI(picture_url, RegisterActivity.this);
                    File mfile = new File(filePath);
                    long length = mfile.length() / 1024;
                    //    if (length <= 700) {
                    File afResizePicFile = takePicImageFile;
                    photoPicPath = picture_url;
                    imageCompressor.compressPicInboundTempImage(photoPicPath,photoPicPath,RegisterActivity.this);
//                    } else {
//                        afResizePicFile = imageCompressor.compressPicOneTempImage(picture_url, picture_url, ProductStockDetailsActivity.this);
//                    }

                    String resOutPicPath = imageCompressor.getInboundPicFileName();
                    //imgvShowImage.setVisibility(View.VISIBLE);
                    Drawable drawable = new BitmapDrawable(bm);
                    imgvShowPic.setVisibility(View.GONE);
                    choosePic.setBackgroundDrawable(drawable);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {

        try {

          File resizeFile  = imageCompressor.compressPicInboundTempImage(photoPicPath,photoPicPath,
                    RegisterActivity.this);

           Bitmap bitmap = BitmapFactory.decodeFile(resizeFile.getAbsolutePath());
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            imgvShowPic.setVisibility(View.GONE);
            choosePic.setBackground(drawable);
         } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.

                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }


    private String getRealPathFromURI(String contentURI, Context context) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
