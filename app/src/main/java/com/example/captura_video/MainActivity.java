package com.example.captura_video;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.captura_video.databinding.ActivityMainBinding;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int PETICION_VIDEO = 100;
    ActivityMainBinding binding;
    Uri uriVideoGeneral;
    private Spinner sp1;
    private VideoView vv1;
    //private String [] lista;
    //private ArrayList<String> lista=new ArrayList<>();
    //private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vv1 = findViewById(R.id.videoView);
        sp1 = findViewById(R.id.spinner3);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setLayouts();
        /*
        for(int f=0;f< fileList().length;f++)
            lista.add(fileList()[f]);
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,lista);
        sp1.setAdapter(adapter);

         */
    }
    private void init(){uriVideoGeneral = null;}
    private void setLayouts(){
        binding.btnGrabarVideo.setOnClickListener(v -> permiso());
        binding.btnSaveVideo.setOnClickListener(v -> saveVideoInStorage());

    }
    private void permiso() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PETICION_VIDEO);
        }else{
            takeVideo();
        }
    }

    public void verVideo(View v){
        int pos=sp1.getSelectedItemPosition();
        //vv1.setVideoPath(getFilesDir()+"/"+lista.get(pos));
        vv1.start();
    }
 private void takeVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            pickVideo.launch(intent);
        }
    }
    private void saveVideoInStorage(){
        if (uriVideoGeneral == null){
            showMessage("Por favor capture un video");
            return;
        }
        try {
            AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(uriVideoGeneral, "r");
            FileInputStream in = videoAsset.createInputStream();
            FileOutputStream archivo = openFileOutput(newNameMP4(), Context.MODE_PRIVATE);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0){
                archivo.write(buf, 0, len);
            }
            showMessage("Video guardado correctamente");
            clearComponents();
        }catch (IOException e){
            showMessage(e.getMessage());
        }
    }
    private void clearComponents() {
        finish();
    }
    private String newNameMP4() {
        String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String name = date+".mp4";
        return name;
    }
    private final ActivityResultLauncher<Intent> pickVideo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri videoUri = result.getData().getData();
                        binding.videoView.setVideoURI(videoUri);
                        binding.videoView.start();
                        uriVideoGeneral = videoUri;
                    }
                }
            }
    );
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PETICION_VIDEO){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takeVideo();
            }else{
                showMessage("Esta funcion necesita el acceso a la camara");
            }
        }
    }

    public void showMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }








}