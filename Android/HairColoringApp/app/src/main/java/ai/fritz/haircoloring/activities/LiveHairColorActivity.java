package ai.fritz.haircoloring.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;
import android.util.Size;

import com.github.veritas1.verticalslidecolorpicker.VerticalSlideColorPicker;

import ai.fritz.haircoloring.R;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.ModelVariant;
import ai.fritz.vision.imagesegmentation.BlendMode;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictor;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictorOptions;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationResult;
import ai.fritz.vision.imagesegmentation.MaskClass;
import ai.fritz.vision.imagesegmentation.SegmentationOnDeviceModel;


public class LiveHairColorActivity extends BaseLiveGPUActivity {
    private int maskColor = Color.RED;
    private static final int HAIR_ALPHA = 180;
    private static final BlendMode BLEND_MODE = BlendMode.SOFT_LIGHT;
    private static final boolean RUN_ON_GPU = true;

    private FritzVisionSegmentationPredictor hairPredictor;
    private FritzVisionSegmentationResult hairResult;
    private FritzVisionSegmentationPredictorOptions options;

    private SegmentationOnDeviceModel onDeviceModel;

    public void onCreate(final Bundle savedInstanceState) {
        setCameraFacingDirection(CameraCharacteristics.LENS_FACING_FRONT);
        super.onCreate(savedInstanceState);

        // Create the segmentation options
        options = new FritzVisionSegmentationPredictorOptions();
        options.useGPU = RUN_ON_GPU;

        // Set the on-device model
        onDeviceModel = FritzVisionModels.getHairSegmentationOnDeviceModel(ModelVariant.FAST);

        // Load the predictor when the activity is created (iff not running on the GPU)
        if (!RUN_ON_GPU) {
            hairPredictor = FritzVision.ImageSegmentation.getPredictor(onDeviceModel, options);
        }
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final Size cameraSize, final int rotation) {
        super.onPreviewSizeChosen(size, cameraSize, rotation);

        VerticalSlideColorPicker colorPicker = findViewById(R.id.color_picker);

        // Change the mask color upon using the slider
        colorPicker.setOnColorChangeListener(new VerticalSlideColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {
                if (selectedColor != Color.TRANSPARENT) {
                    maskColor = selectedColor;
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_color_slider;
    }

    @Override
    protected void runInference(FritzVisionImage fritzVisionImage) {
        // If you're using the GPU, it MUST run on the same thread
        if (RUN_ON_GPU && hairPredictor == null) {
            hairPredictor = FritzVision.ImageSegmentation.getPredictor(onDeviceModel, options);
        }
        hairResult = hairPredictor.predict(fritzVisionImage);
        Bitmap alphaMask = hairResult.buildSingleClassMask(MaskClass.HAIR, HAIR_ALPHA, options.confidenceThreshold, options.confidenceThreshold, maskColor);
        fritzSurfaceView.drawBlendedMask(fritzVisionImage, alphaMask, BLEND_MODE, getCameraFacingDirection() == CameraCharacteristics.LENS_FACING_FRONT);
    }
}
