package com.example.journalmemos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.provider.MediaStore;import android.content.Intent;
import android.provider.MediaStore;import android.content.Intent;
import android.provider.MediaStore;import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;import android.content.pm.PackageManager;import java.io.ByteArrayOutputStream;import android.net.Uri;
import android.content.Intent;
import android.provider.MediaStore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import android.widget.Toast;import android.net.Uri;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import java.io.ByteArrayOutputStream;import com.google.firebase.FirebaseApp;import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnCompleteListener;import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;import android.util.Log;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private Button button;
    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        button = findViewById(R.id.logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        imageView = findViewById(R.id.capturedImage);
        button = findViewById(R.id.captureImageButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the device has a camera
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    // Check and request camera permission
                    if (checkSelfPermission(android.Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission not granted, request it
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                                REQUEST_IMAGE_CAPTURE);
                    } else {
                        // Permission already granted, proceed with camera operation
                        startCamera();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No camera hardware available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCamera() {
        // Create an intent to open the camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if there is a camera app available
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Start the camera activity for result
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(MainActivity.this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // The image capture was successful
            // You can handle the captured image here, such as saving it or displaying it
            // The captured image can be obtained from the 'data' intent
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Now you can do something with the captured image
            // For example, display it in an ImageView
            imageView.setImageBitmap(imageBitmap);

            // Create a storage reference
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Create a unique filename for the image
            String imageName = "user_image_" + System.currentTimeMillis() + ".jpg";

            // Create a reference to 'images/user_image.jpg'
            StorageReference imageRef = storageRef.child("images/" + imageName);

            // Upload the image to Firebase Storage
            uploadImageToFirebaseStorage(imageBitmap, imageRef);

            // Optionally, check for the existence of the uploaded object
            checkObjectExistence(imageName);
        }
    }

    // Add this method for uploading the image to Firebase Storage
    private void uploadImageToFirebaseStorage(Bitmap bitmap, StorageReference imageRef) {
        // Convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the image
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // Image uploaded successfully
                    Toast.makeText(MainActivity.this, "Image uploaded to Firebase Storage", Toast.LENGTH_SHORT).show();
                } else {
                    // If image upload fails, display a message
                    Toast.makeText(MainActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();

                    // Log additional details for debugging
                    Log.e("Firebase Storage", "Image upload failed. Exception: " + task.getException().getMessage());

                    // You can also log the full exception stack trace for more details
                    task.getException().printStackTrace();
                }
            }
        });
    }

    // Add this method to check for the existence of an object after image upload
    private void checkObjectExistence(String imageName) {
        try {
            // Code to retrieve or download the object from Firebase Storage
            // For example, using Firebase Storage API
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("images/" + imageName);

            pathReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                // Successfully retrieved the object data
                // Handle the downloaded data as needed
                Log.d("Firebase Storage", "Object exists. Handle the data as needed.");
                Log.d("Firebase Storage", "Checking existence for path: " + pathReference.getPath());
                Log.d("Firebase Storage", "Checking existence for imageName: " + imageName);

            }).addOnFailureListener(exception -> {
                // Handle exceptions that occurred during the download
                if (exception instanceof StorageException) {
                    StorageException storageException = (StorageException) exception;
                    if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                        // Handle 404 error (Object not found)
                        // Display an appropriate message or take necessary actions
                        Log.e("Firebase Storage", "Object not found. Display appropriate message or take necessary actions.");
                    } else {
                        // Handle other StorageException cases
                        exception.printStackTrace();
                    }
                } else {
                    // Handle other non-StorageException exceptions
                    exception.printStackTrace();
                }
            });
        } catch (Exception e) {
            // Handle other exceptions that might occur outside the StorageException
            e.printStackTrace();
        }
    }
}