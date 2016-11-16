package com.seapps.mazeexplorer;

import com.seapps.mazeexplorer.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	private ImageView iv;
	private Button bAnalyze;
	private Uri mCropImageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bAnalyze = (Button) findViewById(R.id.bAnalyze);

		iv = (ImageView) findViewById(R.id.image);

	}

	public void clickCreate(View v) {
		char[][] data = new char[10][10];
		for (int i = 0; i < 10; ++i)
			for (int j = 0; j < 10; ++j)
				data[i][j] = '#';
		MazeActivity.maze = new Maze(10, 10, data);
		Intent i = new Intent(this, MazeActivity.class);
		startActivity(i);
	}

	public void clickPickImage(View v) {
		CropImage.startPickImageActivity(this);
	}

	public void clickAnalyze(View v) {
		ImageAnalyzer ia = new ImageAnalyzer(this, mCropImageUri);
		ia.execute();
	}

	@Override
	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// handle result of pick image chooser
		if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			Uri imageUri = CropImage.getPickImageResultUri(this, data);

			// For API >= 23 we need to check specifically that we have
			// permissions to read external storage.
			boolean requirePermissions = false;
			if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
				// request permissions and handle the result in
				// onRequestPermissionsResult()
				requirePermissions = true;
				mCropImageUri = imageUri;
				requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
			} else {
				// no permissions required or already grunted, can start crop
				// image activity
				startCropImageActivity(imageUri);
			}
		}

		// handle result of CropImageActivity
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				iv.setImageURI(result.getUri());
				bAnalyze.setEnabled(true);
				mCropImageUri = result.getUri();
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			// required permissions granted, start crop image activity
			startCropImageActivity(mCropImageUri);
		} else {
			Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Start crop image activity for the given image.
	 */
	private void startCropImageActivity(Uri imageUri) {
		CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
	}
}