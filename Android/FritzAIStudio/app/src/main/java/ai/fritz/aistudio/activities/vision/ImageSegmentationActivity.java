package ai.fritz.aistudio.activities.vision;

import android.graphics.Bitmap;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import ai.fritz.aistudio.R;
import ai.fritz.aistudio.activities.BaseRecordingActivity;
import ai.fritz.core.FritzOnDeviceModel;
import ai.fritz.core.utils.FritzModelManager;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.ModelVariant;
import ai.fritz.vision.PredictorStatusListener;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictor;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictorOptions;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationResult;
import ai.fritz.vision.imagesegmentation.SegmentationManagedModel;
import ai.fritz.vision.imagesegmentation.SegmentationOnDeviceModel;


public class ImageSegmentationActivity extends BaseRecordingActivity implements OnImageAvailableListener {

    private static final String TAG = ImageSegmentationActivity.class.getSimpleName();
    private FritzVisionSegmentationPredictor predictor;
    private FritzVisionSegmentationPredictorOptions options;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new FritzVisionSegmentationPredictorOptions();
    }

    @Override
    protected int getModelOptionsTextId() {
        return R.array.img_seg_model_options;
    }

    @Override
    protected Bitmap runPrediction(FritzVisionImage visionImage, Size cameraViewSize) {
        FritzVisionSegmentationResult segmentResult = predictor.predict(visionImage);
        Bitmap bitmap = segmentResult.buildMultiClassMask();
        return visionImage.overlay(bitmap);
    }

    @Override
    protected void loadPredictor(int choice) {
        SegmentationManagedModel managedModel = getManagedModel(choice);
        FritzOnDeviceModel activeOnDeviceModel = FritzModelManager.getActiveOnDeviceModel(managedModel.getModelId());
        if (activeOnDeviceModel != null) {
            showPredictorReadyViews();
            SegmentationOnDeviceModel onDeviceModel = new SegmentationOnDeviceModel(activeOnDeviceModel, managedModel);
            predictor = FritzVision.ImageSegmentation.getPredictor(onDeviceModel, options);
        } else {
            showPredictorNotReadyViews();
            FritzVision.ImageSegmentation.loadPredictor(managedModel, new PredictorStatusListener<FritzVisionSegmentationPredictor>() {
                @Override
                public void onPredictorReady(FritzVisionSegmentationPredictor segmentPredictor) {
                    Log.d(TAG, "Segmentation predictor is ready");
                    predictor = segmentPredictor;
                    showPredictorReadyViews();
                }
            });
        }
    }

    private SegmentationManagedModel getManagedModel(int choice) {
        switch (choice) {
            case (1):
                return FritzVisionModels.getLivingRoomSegmentationManagedModel(ModelVariant.FAST);
            case (2):
                return FritzVisionModels.getOutdoorSegmentationManagedModel(ModelVariant.FAST);
            default:
                return FritzVisionModels.getPeopleSegmentationManagedModel(ModelVariant.FAST);
        }

    }
}

