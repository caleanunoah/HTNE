package ai.fritz.aistudio.activities.vision;

import android.graphics.Canvas;
import android.util.Size;

import java.util.List;

import ai.fritz.aistudio.activities.BaseLiveVideoActivity;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.FritzVisionObject;
import ai.fritz.vision.objectdetection.FritzVisionObjectPredictor;
import ai.fritz.vision.objectdetection.FritzVisionObjectResult;
import ai.fritz.vision.objectdetection.ObjectDetectionOnDeviceModel;

public class ObjectDetectionActivity extends BaseLiveVideoActivity {

    private FritzVisionObjectPredictor objectPredictor;
    private FritzVisionObjectResult objectResult;

    @Override
    protected void onCameraSetup(final Size cameraSize) {
        ObjectDetectionOnDeviceModel onDeviceModel = FritzVisionModels.getObjectDetectionOnDeviceModel();
        objectPredictor = FritzVision.ObjectDetection.getPredictor(onDeviceModel);
    }

    @Override
    protected void handleDrawingResult(Canvas canvas, Size cameraSize) {
        if (objectResult != null) {
            List<FritzVisionObject> visionObjects = objectResult.getObjects();
            for (FritzVisionObject object : visionObjects) {
                object.draw(canvas);
            }
        }
    }

    @Override
    protected void runInference(FritzVisionImage fritzVisionImage) {
        objectResult = objectPredictor.predict(fritzVisionImage);
    }
}
