package triviyou.michal.com;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    Context context;
    ImageButton imgbBack4;
    ImageView imgAccount;
    TextView tvWantChangePassword, tvIwantLogOut;
    Intent inputIntent, goGames, goLogin;

    private static final String FRAGMENT_TAG = "CHANGE_PASSWORD_FRAGMENT";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_CAMERA = 1; // identifying a photo request
    private static final int REQUEST_GALLERY = 2; //identifying a gallery request
    private static final int STORAGE_PERMISSION_CODE = 101; //identifying a storage permission.
    private Uri imageUri;
    private boolean isFragmentDisplayed = false; // manages the fragment state
    Helper helper = new Helper();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId = auth.getCurrentUser().getUid();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = ProfileActivity.this;
        imgbBack4 = findViewById(R.id.imgbBack4);
        imgAccount = findViewById(R.id.imgAccount);
        tvWantChangePassword = findViewById(R.id.tvWantChangePassword);
        inputIntent = getIntent();
        tvIwantLogOut = findViewById(R.id.tvIwantLogOut);

        goGames = new Intent(context, GamesActivity.class);
        goLogin = new Intent(context, LoginActivity.class);


        // Load the saved profile image
        loadImageFromStorage();


        imgbBack4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goGames);
            }
        });

        imgAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromStorage();
            }
        });

        tvWantChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragmentDisplayed) {
                    Helper.FragmentHelper.closeFragment(getSupportFragmentManager(), FRAGMENT_TAG);
                    isFragmentDisplayed = false;
                    tvWantChangePassword.setText(getString(R.string.iWantChangePass));
                } else {
                    showFragment();
                    tvWantChangePassword.setText(getString(R.string.close));
                }
            }
        });

        tvIwantLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isInternetAvailable(context)) {
                    helper.toasting(context, getString(R.string.noInternetConnection));
                    return;
                }
                new AlertDialog.Builder(ProfileActivity.this)
                        .setMessage(R.string.sureLogOut)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(goLogin);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null) // if user clicks "No", just close the dialog
                        .show(); // show the alert dialog

            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Helper.onActivityStarted(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Helper.onActivityStopped(this);
    }



    // define a Launcher for the gallery action
    private ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgAccount.setImageURI(imageUri);
                }
            });

    // define a Launcher for the camera action
    private ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    imgAccount.setImageURI(imageUri);
                }
            });



    private void selectImageFromStorage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.chooseImageSource)
                .setItems(new String[]{getString(R.string.gallery), getString(R.string.camera)}, (dialog, which) -> {
                    if (which == 0) {
                        openGallery();
                    } else {
                        openCamera();
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            takePhoto(); // camera
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto(); // if permission granted, proceed with taking the photo
            } else {
                helper.toasting(context, getString(R.string.cameraPermissionsRequired));
            }
        }
    }

    private void takePhoto() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = createImageFile(); // creating image file
            if (photoFile != null) {
                // create a URI using FileProvider instead of Uri.fromFile
                imageUri = FileProvider.getUriForFile(this, "triviyou.michal.com.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // add the URL to the camera
                startActivityForResult(cameraIntent, REQUEST_CAMERA); // start capturing
            }
        } catch (Exception e) {
            Log.e("error camera", "", e);
        }
}

    private void loadImageFromStorage() {
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String imagePath = preferences.getString(userId, null);

        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Uri imageUri = Uri.fromFile(imageFile);
                imgAccount.setImageURI(imageUri); // load the image into the ImageView
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(null);
        try {
            return File.createTempFile("IMG_" + timeStamp, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImageToInternalStorage(Uri imageUri) {
        try {
            // get the input stream of the selected image
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            // create a file to save the image
            File outputFile = new File(getFilesDir(), userId+".jpg"); // Save it in the internal app directory
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            // copy the image from input stream to output file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            // store the file path for later use (you can store this path in SharedPreferences or a database)
            SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(userId, outputFile.getAbsolutePath());
            editor.apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY && data != null) {
                imageUri = data.getData();
                imgAccount.setImageURI(imageUri);
                saveImageToInternalStorage(imageUri); // saving the image to internal storage (if needed)
            }
            else if (requestCode == REQUEST_CAMERA) {
                imgAccount.setImageURI(imageUri); // displaying the captured image**
                saveImageToInternalStorage(imageUri); // saving the image to internal storage**
            }
        }
    }

    private void showFragment() {
        Fragment fragment = new changePasswordFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutChangePassword, fragment, FRAGMENT_TAG)
                .commit();
        isFragmentDisplayed = true;
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutChangePassword, fragment);
        fragmentTransaction.commit();

    }
}