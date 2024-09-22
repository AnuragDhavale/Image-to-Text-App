package com.example.handtotext;

import static com.google.mlkit.vision.text.TextRecognizerOptionsInterface.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;

import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import java.util.concurrent.Executor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Object;


public class CaptureRead extends AppCompatActivity  {
Button cap,read,copy;
ImageView imgvw;
TextView tvtext;
Bitmap imageBitmap;
Executor executor;
StringBuffer sb;


private TextRecognizer mTextRecognizer;
static final int REQUEST_IMAGE_CAPTURE = 1;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_read);
        imgvw=(ImageView) findViewById(R.id.imageread);
        cap=(Button) findViewById(R.id.btcap);
    //    copy=(Button) findViewById(R.id.btcpy);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCapture(v);
            }
        });
        read=(Button) findViewById(R.id.btread);
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputImage ii=InputImage.fromBitmap(imageBitmap,0);
                recognizeText(ii);

            }
        });
        tvtext=(TextView) findViewById(R.id.tvtext);
         tvtext.setText("hello");
         /*copy.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 save();
             }
         });*/
    }
    public void onCapture(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            imgvw.setImageBitmap(imageBitmap);
              }

    }


    private void recognizeText(InputImage image) {
         sb=new StringBuffer();
        DevanagariTextRecognizerOptions build = new DevanagariTextRecognizerOptions.Builder()
                .setExecutor(executor)
                .build();
         TextRecognizer dev = TextRecognition.getClient(build);

        Task<Text> result =
                dev.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // [START_EXCLUDE]
                                // [START get_text]
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    sb.append(block.getText()+"\n");

                                    for (Text.Line line: block.getLines()) {



                                        for (Text.Element element: line.getElements()) {
                                            // ...
                                            for (Text.Symbol symbol: element.getSymbols()) {
                                                // ...
                                            }
                                        }
                                    }

                                    processTextBlock(visionText);
                                }
                               tvtext.setText(sb);
                                // [END get_text]
                                // [END_EXCLUDE]
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
        // [END run_detector]
    }
    public void  save()  // SAVE
    {
        //sb=new StringBuffer("hello hi");
        if(sb.length()!=0) {
            myClip = ClipData.newPlainText("text", sb);
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            String txt="";
            tvtext.setText(txt);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Cant be Empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void readboard()
    {
        ClipData clipData = myClipboard.getPrimaryClip();
        // Get item count.
        int itemCount = clipData.getItemCount();
        if(itemCount > 0)
        {
            // Get source text.
            ClipData.Item item = clipData.getItemAt(0);
            String text = item.getText().toString();
            // Set the text to target textview.
            tvtext.setText(text);


        }

}
    private void processTextBlock(Text result) {
        // [START mlkit_process_text_block]
        String resultText = result.getText();
        for (Text.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                    for (Text.Symbol symbol : element.getSymbols()) {
                        String symbolText = symbol.getText();
                        Point[] symbolCornerPoints = symbol.getCornerPoints();
                        Rect symbolFrame = symbol.getBoundingBox();
                    }
                }
            }
        }
        // [END mlkit_process_text_block]
    }

    private TextRecognizer getTextRecognizer() {
        // [START mlkit_local_doc_recognizer]
        TextRecognizer detector = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // [END mlkit_local_doc_recognizer]

        return detector;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sharemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, tvtext.getText().toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;

            case R.id.copy:
                save();
                return true;
            case R.id.pst:
                readboard();
                return true;
            case R.id.hm:
                Intent home=new Intent(CaptureRead.this,MainActivity.class);
                startActivity(home);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}