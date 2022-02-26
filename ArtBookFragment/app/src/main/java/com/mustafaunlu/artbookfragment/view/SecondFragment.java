package com.mustafaunlu.artbookfragment.view;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mustafaunlu.artbookfragment.R;
import com.mustafaunlu.artbookfragment.databinding.FragmentFirstBinding;
import com.mustafaunlu.artbookfragment.databinding.FragmentSecondBinding;

import java.io.ByteArrayOutputStream;


public class SecondFragment extends Fragment {

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData;
    Bitmap selectedImage;
    SQLiteDatabase database;
    String comment;
    Bundle bundle;

    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerLauncher();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button=getView().findViewById(R.id.saveButton);
        EditText editText=getView().findViewById(R.id.artNameEditText);
        ImageView imageView=getActivity().findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(view);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });
        database=getActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null);
        bundle=getArguments();

        if(bundle.getString("info").matches("old")){
            //old
            int artId=bundle.getInt("anahtarId");
            button.setVisibility(View.INVISIBLE);

            try{
                Cursor cursor=database.rawQuery("SELECT * FROM arts where id=?",new String[]{String.valueOf(artId)});
                int commentIx=cursor.getColumnIndex("comment");
                int imageIx=cursor.getColumnIndex("image");

                while(cursor.moveToNext()){
                    editText.setText(cursor.getString(commentIx));

                    byte[] bytes=cursor.getBlob(imageIx);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageView.setImageBitmap(bitmap);

                }
                cursor.close();

            }catch (Exception e){
                e.getLocalizedMessage();
            }


        }
        else if(bundle.getString("info").matches("new")){
            //new
            editText.setText("");
            button.setVisibility(View.VISIBLE);

        }


    }
    public void save(View view){


        System.out.println("save ici");
       EditText toComment=getActivity().findViewById(R.id.artNameEditText);
        comment=toComment.getText().toString();
        //System.out.println("comment:" +comment);
        System.out.println("URI: "+imageData );

        Bitmap smallImage=makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();

        try{
            database=getActivity().openOrCreateDatabase("Arts",Context.MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,comment VARCHAR,image BLOB)");

            String SQLString="INSERT INTO arts(comment,image) VALUES(?,?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(SQLString);
            sqLiteStatement.bindString(1,comment);
            sqLiteStatement.bindBlob(2,byteArray);
            sqLiteStatement.execute();



        }catch (Exception e){
            System.out.println("database::error => "+e.getLocalizedMessage());
        }
        goToFirstFragment(view);
    }

    private Bitmap makeSmallerImage(Bitmap image, int maximumSize) {
        int width=image.getWidth();
        int height=image.getHeight();

        float bitmapRatio=(float)width/(float)height;
        if(bitmapRatio>1){
            width=maximumSize;
            height=(int)(maximumSize/bitmapRatio);
        }
        else{
            height=maximumSize;
            width=(int)(maximumSize*bitmapRatio);
        }
        return image.createScaledBitmap(image,width,height,true);
    }

    public void selectImage(View view){
        System.out.println("selectimage");
        //izinler
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //permission launcher
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            }
            else{
                //permission launcher
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else{
            //go to gallery
            Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }

    }

    private void registerLauncher() {

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentFromResult=result.getData();
                    if(intentFromResult !=null){
                        imageData=intentFromResult.getData();
                        ImageView imageView=getActivity().findViewById(R.id.imageView);
                        try{
                            if(Build.VERSION.SDK_INT>28){
                                ImageDecoder.Source source=ImageDecoder.createSource(getActivity().getContentResolver(),imageData);
                                selectedImage=ImageDecoder.decodeBitmap(source);
                                imageView.setImageBitmap(selectedImage);

                            }
                            else{
                                selectedImage=MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageData);
                                imageView.setImageBitmap(selectedImage);
                            }



                        }catch (Exception e){
                            System.out.println("Hatalar: "+e.getLocalizedMessage());
                        }
                    }
                }
            }
        });
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //go to firstFragment
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }
                else{
                    Toast.makeText(getActivity(), "Permissin needed!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void goToFirstFragment(View view){
        System.out.println("goToFirstFragment");

        NavDirections action=SecondFragmentDirections.actionSecondFragmentToFirstFragment();
        Navigation.findNavController(view).navigate(action);






    }
}