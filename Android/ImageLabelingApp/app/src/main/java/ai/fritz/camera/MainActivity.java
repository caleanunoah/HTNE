package ai.fritz.camera;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.concurrent.atomic.AtomicBoolean;

import ai.fritz.core.Fritz;
import ai.fritz.imagelabelingdemo.R;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionLabel;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.FritzVisionOrientation;
import ai.fritz.vision.ImageOrientation;
import ai.fritz.vision.ModelVariant;
import ai.fritz.vision.imagelabeling.FritzVisionLabelPredictor;
import ai.fritz.vision.imagelabeling.FritzVisionLabelPredictorOptions;
import ai.fritz.vision.imagelabeling.FritzVisionLabelResult;
import ai.fritz.vision.imagelabeling.LabelingOnDeviceModel;



public class MainActivity extends BaseCameraActivity implements ImageReader.OnImageAvailableListener {

    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 960);

    private AtomicBoolean isComputing = new AtomicBoolean(false);
    private AtomicBoolean shouldSample = new AtomicBoolean(true);
    private ImageOrientation orientation;

    FritzVisionLabelResult labelResult;
    FritzVisionLabelPredictor predictor;
    FritzVisionImage visionImage;

    // Preview Frame
    RelativeLayout previewFrame;
    Button snapshotButton;
    ProgressBar snapshotProcessingSpinner;

    // Snapshot Frame
    RelativeLayout snapshotFrame;
    OverlayView snapshotOverlay;
    Button closeButton;
    Button recordButton;
    ProgressBar recordSpinner;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fritz.configure(this);
        // The code below loads a custom trained image labeling model and creates a predictor that will be used to label frames of live video.
        // Custom image labeling models can be trained with the Fritz AI platform. To use a pre-trained image labeling model,
        // see the FritzAIStudio demo in this repo.
        LabelingOnDeviceModel imageLabelingOnDeviceModel = LabelingOnDeviceModel.buildFromModelConfigFile("label_recording_model.json");
        FritzVisionLabelPredictorOptions options = new FritzVisionLabelPredictorOptions();
        options.confidenceThreshold = 0.1f;
        predictor = FritzVision.ImageLabeling.getPredictor(imageLabelingOnDeviceModel, options);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.main_camera;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onPreviewSizeChosen(final Size previewSize, final Size cameraViewSize, final int rotation) {
        orientation = FritzVisionOrientation.getImageOrientationFromCamera(this, cameraId);

        // Preview View
        previewFrame = findViewById(R.id.preview_frame);
        snapshotProcessingSpinner = findViewById(R.id.snapshot_spinner);
        snapshotButton = findViewById(R.id.take_picture_btn);
        snapshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!shouldSample.compareAndSet(true, false)) {
                    return;
                }

                runInBackground(
                        () -> {
                            showSpinner();
                            snapshotOverlay.postInvalidate();
                            switchToSnapshotView();
                            hideSpinner();
                        });
            }
        });
        setCallback(canvas -> {
            if (labelResult != null) {
                snapshotOverlay.setResult(labelResult.getVisionLabels());
                snapshotOverlay.draw(canvas);
            }
            isComputing.set(false);
        });

        // Snapshot View
        snapshotFrame = findViewById(R.id.snapshot_frame);
        snapshotOverlay = findViewById(R.id.snapshot_view);
        snapshotOverlay.setCallback(
                canvas -> {
                    if (labelResult != null) {
                        canvas.drawBitmap(visionImage.buildSourceBitmap(), null, new RectF(0, 0, cameraViewSize.getWidth(), cameraViewSize.getHeight()), null);
                    }
                });

        recordSpinner = findViewById(R.id.record_spinner);
        recordButton = findViewById(R.id.record_prediction_btn);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordSpinner.setVisibility(View.VISIBLE);
                // To record predictions and send data back to Fritz AI via the Data Collection System, use the predictors's record method.
                // In addition to the input image, predicted model results can be collected as well as user-modified annotations.
                // This allows developers to both gather data on model performance and have users collect additional ground truth data for future model retraining.
                // Note, the Data Collection System is only available on paid plans.
                predictor.record(visionImage, labelResult, null, () -> {
                    switchPreviewView();
                    return null;
                }, () -> {
                    switchPreviewView();
                    return null;
                });
            }
        });
        closeButton = findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchPreviewView();
            }
        });

    }

    private void switchToSnapshotView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                previewFrame.setVisibility(View.GONE);
                snapshotFrame.setVisibility(View.VISIBLE);
            }
        });
    }

    private void switchPreviewView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordSpinner.setVisibility(View.GONE);
                snapshotFrame.setVisibility(View.GONE);
                previewFrame.setVisibility(View.VISIBLE);
                shouldSample.set(true);
            }
        });
    }

    private void showSpinner() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snapshotProcessingSpinner.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideSpinner() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snapshotProcessingSpinner.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onImageAvailable(final ImageReader reader) {
        Image image = reader.acquireLatestImage();

        if (image == null) {
            return;
        }

        if (!shouldSample.get()) {
            image.close();
            return;
        }

        if (!isComputing.compareAndSet(false, true)) {
            image.close();
            return;
        }

        visionImage = FritzVisionImage.fromMediaImage(image, orientation);
        image.close();

        runInBackground(() -> {
            labelResult = predictor.predict(visionImage);
            requestRender();
        });
    }
}
