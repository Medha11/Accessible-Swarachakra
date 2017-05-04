package game.Typing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class CallCamera extends Activity {

		final int TAKE_PICTURE = 1;
	    final int PIC_CROP = 2;
	    public Uri imageUri;
	
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_register);
	        takePhoto();
	    }
	
	    public void takePhoto() 
	    {
	    	try {
	    	    //use standard intent to capture an image
	    	    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	    //we will handle the returned data in onActivityResult
	    	    startActivityForResult(captureIntent, TAKE_PICTURE);
	    	}
	    	
	    	catch(ActivityNotFoundException anfe){
	    	    //display an error message
	    	    String errorMessage = "Whoops - your device doesn't support capturing images!";
	    	    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
	    	    toast.show();
	    	}
	    }
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) 
		{
		    
		    if (resultCode == RESULT_OK && data != null) 
		    {
		        
		    	if(requestCode == TAKE_PICTURE)
		    	{	
		    		imageUri = data.getData();
		    		File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
		    		performCrop(Uri.fromFile(file));
		    	}
		    
		    	File f = new File(imageUri.getPath());            

                if (f.exists()) f.delete();
		    	
		    else if(requestCode == PIC_CROP)
		    	{
		    		//get the returned data
		    		Bundle extras = data.getExtras();
		    		//get the cropped bitmap
		    		Bitmap thePic = extras.getParcelable("data");
		    		String root = Environment.getExternalStorageDirectory().toString();
	                File myDir = new File(root + "/saved_images");    
	                myDir.mkdirs();
	                Random generator = new Random();
	                int n = 10000;
	                n = generator.nextInt(n);
	                String fname = "Image-"+ n +".jpg";
	                File file = new File (myDir, fname);
	                if (file.exists ()) file.delete (); 
	                try 
	                {
	                   FileOutputStream out = new FileOutputStream(file);
	                   thePic.compress(Bitmap.CompressFormat.JPEG, 90, out);   
	                } 
	                catch (Exception e) 
	                {
	                   e.printStackTrace();
	                }
		    		//retrieve a reference to the ImageView
		    		//ImageView picView = (ImageView)findViewById(R.id.userPhoto);
		    		//display the returned cropped image
		    		//picView.setImageBitmap(thePic);
		    		String bitmappath = file.getPath();
		    		Toast.makeText(this, "This file: "+bitmappath,Toast.LENGTH_LONG).show();
		    		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
		    	}
		    }
		    
	//	    switch (requestCode) {
	//	    case 1:
	//	        if (resultCode == Activity.RESULT_OK) 
	//	        {
	//	            //Uri imageUri;
	//	            Uri selectedImage = imageUri;
	//	            getContentResolver().notifyChange(selectedImage, null);
	//	            ImageView imageView = (ImageView) findViewById(R.id.IMAGE);
	//	            ContentResolver cr = getContentResolver();
	//	            Bitmap bitmap;
	//	            try 
	//	            {
	//	                 bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
	//	                 imageView.setImageBitmap(bitmap);
	//	                 Toast.makeText(this, "This file: "+selectedImage.toString(),Toast.LENGTH_LONG).show();
	//	            } 
	//	            catch (Exception e) 
	//	            {
	//	                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
	//	                Log.e("Camera", e.toString());
	//	            }
	//	        }
	//	    }
		}
	
		private void performCrop(Uri picUri) 
		{
			// TODO Auto-generated method stub
			
			try {
				//call the standard crop action intent (the user device may not support it)
				Intent cropIntent = new Intent("com.android.camera.action.CROP"); 
				    //indicate image type and Uri
				cropIntent.setDataAndType(imageUri, "image/*");
				    //set crop properties
				cropIntent.putExtra("crop", "true");
				    //indicate aspect of desired crop
				cropIntent.putExtra("aspectX", 1);
				cropIntent.putExtra("aspectY", 1);
				    //indicate output X and Y
				cropIntent.putExtra("outputX", 256);
				cropIntent.putExtra("outputY", 256);
				    //retrieve data on return
				cropIntent.putExtra("return-data", true);
				cropIntent.putExtra("save-data", true);
				    //start the activity - we handle returning in onActivityResult
				startActivityForResult(cropIntent, PIC_CROP);
			}
			catch(ActivityNotFoundException anfe){
			    //display an error message
			    String errorMessage = "Whoops - your device doesn't support the crop action!";
			    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			    toast.show();
			}
			
		}
		
    }