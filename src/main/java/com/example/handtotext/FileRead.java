package com.example.handtotext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.Executor;

public class FileRead extends AppCompatActivity {
    int SELECT_PICTURE = 200;
    ImageView disimg;
    Button opendrive, read;
    TextView rdtext;
    Bitmap imageBitmap;
    Executor executor;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    StringBuffer sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_read);
        rdtext = (TextView) findViewById(R.id.tvtext1);
        disimg = (ImageView) findViewById(R.id.readfile);
        opendrive = (Button) findViewById(R.id.btopen);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        opendrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
       read = (Button) findViewById(R.id.btreads);
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap Bi = ((BitmapDrawable) disimg.getDrawable()).getBitmap();
                InputImage ii = InputImage.fromBitmap(Bi, 0);
                recognizeText(ii);


            }
        });

    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    disimg.setImageURI(selectedImageUri);
                }
            }
        }
    }

    private void recognizeText(InputImage image) {

        sb = new StringBuffer();


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
                                    sb.append(block.getText() + "\n");


                                    for (Text.Line line : block.getLines()) {


                                        for (Text.Element element : line.getElements()) {
                                            // ...
                                            for (Text.Symbol symbol : element.getSymbols()) {
                                                // ...
                                            }
                                        }
                                    }

                                    processTextBlock(visionText);
                                }

                                if (sb != null) {

                                    rdtext.setText(sb);
                                } else
                                    Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_LONG).show();
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
        switch (id) {
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, rdtext.getText().toString());
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
                Intent home=new Intent(FileRead.this,MainActivity.class);
                startActivity(home);
            default:
                return super.onOptionsItemSelected(item);
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


    public void  save()  // SAVE
    {

        if(sb!=null) {
            Toast.makeText(getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            myClip = ClipData.newPlainText("text", sb);
            myClipboard.setPrimaryClip(myClip);



        }
        else
        {
            Toast.makeText(getApplicationContext(), "Cannot be Empty", Toast.LENGTH_SHORT).show();
        }
        rdtext.setText("");

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
            rdtext.setText(text);


        }

    }

}