/*
Tyler Vogel
11161080
tjv255
04/07/20
CMPT381 A4
 */

package com.example.linesketch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.io.IOException;

public class SketchMainActivity extends AppCompatActivity {

    SketchController controller;
    SketchModel model;
    InteractionModel iModel;

    SeekBar rotationSlider;
    SeekBar scaleSlider;
    SketchView sketchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout root = findViewById(R.id.root);
        scaleSlider = findViewById(R.id.scaleSlider);
        rotationSlider = findViewById(R.id.rotationSlider);

        // MVC
        LinearLayout mainLayout = findViewById(R.id.sketchView);
        sketchView = new SketchView(this);
        mainLayout.addView(sketchView);

        controller = new SketchController(scaleSlider);
        model = new SketchModel();
        iModel = new InteractionModel();

        // Connect MVC
        controller.setIModel(iModel);
        controller.setModel(model);
        sketchView.setController(controller);
        sketchView.setModel(model);
        sketchView.setIModel(iModel);
        iModel.addSubscriber(sketchView);
        model.addSubscriber(sketchView);


        scaleSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 50; // initial seek-handle location
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!iModel.getSelectedLines().isEmpty()) {
                    if(model.internalChange) {
                        progressValue = 50; // for bar resets
                        model.internalChange = false;
                    }
                    controller.changeScale((progress - progressValue)/200f);
                    progressValue = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //un-select?
            }

        });

        rotationSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0; // initial seek-handle location
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!iModel.getSelectedLines().isEmpty()) {
                    controller.changeRotation(progress - progressValue);
                    progressValue = progress;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cut:
                controller.cut();
                return true;
            case R.id.copy:
                controller.copy();
                return true;
            case R.id.paste:
                controller.paste();
                return true;
            case R.id.group:
                LineGroup g = model.createGroup(iModel.getSelectedLines());
                iModel.addGroup(g);
                return true;
            case R.id.ungroup:
                if(iModel.getSelectedLines().size() == 1 && iModel.getSelectedLines().get(0).hasChildren()) {
                    LineGroup lg = (LineGroup)iModel.getSelectedLines().get(0);
                    iModel.setSelectedLines(model.ungroup(lg));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData(); //The uri with the locations of the image
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            model.setChartImage(bitmap);
        }
    }

}
