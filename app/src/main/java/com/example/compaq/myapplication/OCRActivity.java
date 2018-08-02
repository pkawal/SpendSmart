package com.example.compaq.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
public class OCRActivity extends ActionBarActivity implements View.OnClickListener {
    private ImageView bill;
    String place = "";
    private EditText amount;
    private Button uploadamount,submit;
    private Bitmap bitmapamount;
    private FirebaseUser user;
    private Firebase firebase;
    private FirebaseAuth firebaseAuth;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyAmOhrB1hah-gVYrWZLLgpe2yAiz8NdBTk";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        cuisine=null;
        a=null;
        amount=(EditText) findViewById(R.id.amount);

        firebase=new Firebase("https://myapp-54743.firebaseio.com/");
        user=firebaseAuth.getInstance().getCurrentUser();
        bill=(ImageView)findViewById(R.id.bill);
        uploadamount=(Button) findViewById(R.id.uploadamount);
        submit=(Button) findViewById(R.id.submit);
        uploadamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OCRActivity.this);
                builder
                        .setMessage("Choose a picture")
                        .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();

            }
        });
        submit.setOnClickListener(this);
        getcuisine();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ocr, menu);
        return true;
    }


    String[] cuisine;
    void getcuisine()
    {

        final JSONObject[] obj = {null};

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urls.cuisine,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                     response=response.substring(2);
                        response=response.substring(0,response.length()-2);
                        cuisine=response.split("\",\"");

                        Toast.makeText(OCRActivity.this,cuisine[0],Toast.LENGTH_LONG).show();
                    }                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

        };
        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
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

    @Override
    public void onClick(View v) {

        if(v==submit)
        {
            str=str.toLowerCase();

            Toast.makeText(OCRActivity.this,"cuisine "+cuisine.length,Toast.LENGTH_LONG).show();
            Toast.makeText(OCRActivity.this,"match"+a.size(),Toast.LENGTH_LONG).show();
            for(i=0;i<a.size();i++)
                Toast.makeText(OCRActivity.this,"in bill "+a.get(i) ,Toast.LENGTH_LONG).show();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            final String datefinal = df.format(c.getTime());
            final Transaction transaction=new Transaction(Integer.parseInt(amount.getText().toString()),place,datefinal);
            firebase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User usernew = dataSnapshot.getValue(User.class);
                    HashMap<String,ArrayList<Transaction>> transactions = new HashMap<String, ArrayList<Transaction>>();
                    if (usernew.getTransactions() == null)
                        usernew.setTransactions(transactions);
                    transactions = usernew.getTransactions();
                    ArrayList<Transaction> list = new ArrayList<Transaction>();
                    ArrayList<String> p = new ArrayList<String>();
                    if (transactions.get("Food") == null)
                        transactions.put("Food", list);
                    transactions.get("Food").add(transaction);
                    usernew.spent += Integer.parseInt(amount.getText().toString());
                    if(transactions.get("Cuisines")==null)
                        transactions.put("Cuisines",list);
                    list=transactions.get("Cuisines");
                    Transaction ef = null;
                    if(list!=null)
                    {
                        for(i=0;i<list.size();i++)
                            p.add(transactions.get("Cuisines").get(i).getPlace());
                        for(i=0;i<a.size();i++) {
                            if (p.contains(a.get(i)))

                            {
                                for(int j=0;j<p.size();j++)
                                {
                                    if(transactions.get("Cuisines").get(j).getPlace()==a.get(i))
                                        transactions.get("Cuisines").get(j).amount++;
                                }
                            }
                            else
                              ef = new Transaction(0, a.get(i), datefinal);
                            transactions.get("Cuisines").add(ef);
                        }
                        
                    }
                    else
                    {
                        for(i=0;i<a.size();i++) {
                           ef = new Transaction(0, a.get(i), datefinal);
                            transactions.get("Cuisines").add(ef);
                        }
                    }
                    firebase.child(user.getUid()).setValue(usernew);
                    Toast.makeText(OCRActivity.this, "Transaction Added", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });



        }

    }
    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }

    }
    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }

    }




    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                Boolean b=PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults);
                if (b) {
                    startCamera();
                   }

                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                bill.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Something is wrong with that image. Pick a different one please.", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this," Something is wrong with that image. Pick a different one please.", Toast.LENGTH_LONG).show();
        }
    }


    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
         amount.setText("Uploading image. Please wait.");

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("TEXT_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                amount.setText(result);
            }
        }.execute();
        Toast.makeText(OCRActivity.this,"Number of dish found"+count,Toast.LENGTH_LONG).show();

    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
String str;
    private String convertResponseToString(BatchAnnotateImagesResponse response) {
     str=response.getResponses().get(0).getTextAnnotations().toString();
        str=str.substring(str.indexOf("description")+14,str.indexOf("\"",str.indexOf("description")+14));
        place=str.substring(0,str.indexOf("\\n"));

        int j = 0;
        Double temp=0.0;
        Double max=0.0;
        Double due = 0.0;
        String num = null;
        for(int i=0;i<str.length();i++)
        {
            if(str.charAt(i)=='.')
            {
                if(Character.isDigit(str.charAt(i+1))&&Character.isDigit(str.charAt(i+2)))
                {
                    j=i-1;
                    num=str.substring(i,i+3);
                    while(Character.isDigit(str.charAt(j))||str.charAt(j)==',')
                    {
                        num=str.charAt(j)+num;
                        j--;
                    }
                    temp=Double.parseDouble(num);
                    if(temp>max)
                        max=temp;

                }
            }
        }

    /*   if(str.toLowerCase().contains("balance amt"))
       {
            str = str.substring(str.indexOf("balance amt"));
            j=0;
       }
            else
       if(str.toLowerCase().contains("cash due"))
       {
            str=str.substring(str.indexOf("cash due"));
            j=0;
       }
        else
        if(str.toLowerCase().contains("balance refund"))
        {

            str=str.substring(str.indexOf("balance refund"));
            j=0;
        }
        if(j==0)
        {
         //   for(int i=0;i<str.length();i++)
          //  {
              /* if(str.charAt(i)=='.')
                {
                    if(Character.isDigit(str.charAt(i+1))&&Character.isDigit(str.charAt(i+2)))
                    {
                        j=i-1;
                        num=str.substring(i,i+3);
                        while(Character.isDigit(str.charAt(j))||str.charAt(j)==',')
                        {
                            num=str.charAt(j)+num;
                            j--;
                        }
                        due=Double.parseDouble(num);
                        break;
                    }
                    
                }*/
            //}
      //  }
        max=max-due;
        int max1=max.intValue();
        countdish(str);
        return max1+"";

    }
    int count = 0,i;
    ArrayList<String> a=new ArrayList<String>();
    void countdish(String str)
    {
        str=str.toLowerCase();
        String date;
        for( i=0;i<cuisine.length;i++) {
            if (str.contains(cuisine[i]))

            {
                a.add(cuisine[i]);
         /*       Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                date = df.format(c.getTime());

                final Transaction transaction = new Transaction(0, cuisine[i], date);
                firebase = new Firebase("https://myapp-54743.firebaseio.com/");
                user = firebaseAuth.getInstance().getCurrentUser();
                firebase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User usernew = dataSnapshot.getValue(User.class);

                        HashMap<String, ArrayList<Transaction>> transactions = new HashMap<String, ArrayList<Transaction>>();
                        if (usernew.getTransactions() == null)
                            usernew.setTransactions(transactions);
                        transactions = usernew.getTransactions();
                        ArrayList<Transaction> list = new ArrayList<Transaction>();
                        if (transactions.get("Cuisines") == null)
                            transactions.put("Cuisines", list);
                        list=transactions.get("Cuisines");
                        for(int j=0;j<list.size();j++)
                        {
                            if(list.get(j).type==cuisine[i])
                            {
                               transactions.get("Cuisines").get(j).amount++;
                            }
                            else
                                transactions.get("Cuisines").add(transaction);
                        }
                        firebase.child(user.getUid()).setValue(usernew);


                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });*/
            }
        }
    }
}

