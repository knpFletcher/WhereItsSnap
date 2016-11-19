package com.karenpownall.android.aca.whereitssnap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureFragment extends Fragment {

    private static final int CAMERA_REQUEST = 123;
    private ImageView mImageView;

    //the filepath for the photo
    String mCurrentPhotoPath;

    //where the captured image is stored
    private Uri mImageUri = Uri.EMPTY;

    //reference to Database
    private DataManager mDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //inflate the layout file then get all necessary references
        View view = inflater.inflate(R.layout.fragment_capture, container, false);

        mImageView = (ImageView)view.findViewById(R.id.imageView);
        Button btnCapture = (Button)view.findViewById(R.id.btnCapture);
        Button btnSave = (Button)view.findViewById(R.id.btnSave);

        final EditText mEditTextTitle = (EditText)view.findViewById(R.id.editTextTitle);
        final EditText mEditTextTag1 = (EditText)view.findViewById(R.id.editTextTag1);
        final EditText mEditTextTag2 = (EditText)view.findViewById(R.id.editTextTag2);
        final EditText mEditTextTag3 = (EditText)view.findViewById(R.id.editTextTag3);

        //listen for clicks on the caputre button
        btnCapture.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    //error occurred while creating the file
                    Log.e("Error", "error creating file");
                }

                //continue only if the file was successfully created
                if (photoFile != null){
                    mImageUri = Uri.fromFile(photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        }); //end of ButtonCapture

        // Listen for clicks on the save button
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mImageUri != null){
                    if(!mImageUri.equals(Uri.EMPTY)) {
                        // We have a photo to save

                        Photo photo = new Photo();
                        photo.setTitle(mEditTextTitle.getText().toString());
                        photo.setStorageLocation(mImageUri);

                        // What is in the tags
                        String tag1 = mEditTextTag1.getText().toString();
                        String tag2 = mEditTextTag2.getText().toString();
                        String tag3 = mEditTextTag3.getText().toString();

                        // Assign the strings to the Photo object
                        photo.setTag1(tag1);
                        photo.setTag2(tag2);
                        photo.setTag3(tag3);

                        // Send the new object to our DataManager
                        mDataManager.addPhoto(photo);
                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();
                    }else {
                        // No image
                        Toast.makeText(getActivity(), "No image to save", Toast.LENGTH_LONG).show();
                    }
                }else {
                    // Uri not initialized
                    Log.e("Error ", "uri is null");
                }
            }
        }); //end of ButtonSave

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataManager = new DataManager(getActivity()
        .getApplicationContext());
    }

    private File createImageFile() throws IOException{
        //create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_hHHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, //file name
                ".jpg", //extension
                storageDir //folder
        );

        //save for use with Action_View intent
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            try{
                mImageView.setImageURI(Uri.parse(mImageUri.toString()));
            } catch (Exception e) {
                Log.e ("Error", "Uri not set");
            }
        } else {
            mImageUri = Uri.EMPTY;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //make sure we don't run out of memory
        BitmapDrawable bd = (BitmapDrawable) mImageView.getDrawable();
        bd.getBitmap().recycle();
        mImageView.setImageBitmap(null);
    }
}
