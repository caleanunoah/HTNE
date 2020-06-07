package ai.fritz.camera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import java.util.Objects;

import org.json.JSONArray;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.Object;

import ai.fritz.core.Fritz;
import ai.fritz.poseestimationdemo.R;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionOrientation;
import ai.fritz.vision.ImageOrientation;
import ai.fritz.vision.base.DrawingUtils;
import ai.fritz.vision.poseestimation.FritzVisionPosePredictor;
import ai.fritz.vision.poseestimation.FritzVisionPoseResult;
import ai.fritz.vision.poseestimation.HumanSkeleton;
import ai.fritz.vision.poseestimation.Keypoint;
import ai.fritz.vision.poseestimation.Pose;
import ai.fritz.vision.poseestimation.PoseOnDeviceModel;
import ai.fritz.vision.poseestimation.PoseDecoder;
import ai.fritz.vision.poseestimation.Skeleton;
import ai.fritz.camera.FakeSkeleton;


public class MainActivity extends BaseCameraActivity implements ImageReader.OnImageAvailableListener {

    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 960);

    private AtomicBoolean isComputing = new AtomicBoolean(false);
    private AtomicBoolean shouldSample = new AtomicBoolean(true);
    private ImageOrientation orientation;
    public int offset = 150;
    public int reps = 0;
    public int drawFlag = 0; // initially draw the gray
    public int drawFlag2 = 0;
    public int endFlag = 0;
    public int endFlag2 = 0;

    FritzVisionPoseResult poseResult;
    FritzVisionPosePredictor predictor;
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
        // The code below loads a custom trained pose estimation model and creates a predictor that will be used to identify poses in live video.
        // Custom pose estimation models can be trained with the Fritz AI platform. To use a pre-trained pose estimation model,
        // see the FritzAIStudio demo in this repo.
        PoseOnDeviceModel poseEstimationOnDeviceModel = PoseOnDeviceModel.buildFromModelConfigFile("pose_recording_model.json", new HumanSkeleton());
        predictor = FritzVision.PoseEstimation.getPredictor(poseEstimationOnDeviceModel);
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
            if (poseResult != null) {

                //String num_key_string = String.valueOf(0x3f2);
               // for (int i = 0; i < 10; i++ ) {
                //    Log.d("DEBUGGING", num_key_string);
                //}


                for (Pose pose : poseResult.getPoses() ) {

                    // offset is used to calculate the values to move, from the shoulder, to draw segments for the user to align their arms with
                    //int offset = 150;
                   // int drawFlag = 0; // initially draw the gray
                    //int drawFlag2 = 0;
                    //int endFlag = 0;
                    //int endFlag2 = 0;

                    // draw the skeleton, do not delete lol

                    pose.draw(canvas);

                    Paint paint = new Paint();

                    paint.setColor(Color.BLACK);
                    paint.setTextSize(100);
                    canvas.drawText("Reps : " + String.valueOf(reps), 50, 100, paint);

                    // Get height and width of screen
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels; // returns a float representing a pixel value
                    int width = displayMetrics.widthPixels;  //
                    Size Screen = new Size(width, height);

                    // the following line draws a line from the top left of screen to bottom right
                    //canvas.drawLine(0, 0, width, height, DrawingUtils.DEFAULT_PAINT );

                    // Retrieve the array of keypoints
                    Keypoint[] example_keypoints = pose.getKeypoints();

                    // Lets play around with two keypoints and try to draw from one, to the other
                    Keypoint leftShoulder = example_keypoints[5].scaled(Screen);
                    Keypoint leftElbow = example_keypoints[7].scaled(Screen); // left elbow keypoint
                    Keypoint leftWrist = example_keypoints[9].scaled(Screen); // left wrist keypoint

                    Keypoint rightShoulder = example_keypoints[6].scaled(Screen);

                    PointF leftShoulderPosition = leftShoulder.getPosition();
                    PointF rightShoulderPosition = rightShoulder.getPosition();
                    //PointF leftElbowPosition = leftElbow.getPosition(); // left elbow position
                    //PointF leftWristPosition = leftWrist.getPosition();  left wrist position

                    float posShoulder_x = leftShoulderPosition.x; //  x position in PIXELS
                    float posShoulder_y = leftShoulderPosition.y; //  y position in PIXELS

                    float posRightShoulder_x = rightShoulderPosition.x; //  x position in PIXELS
                    float posRightShoulder_y = rightShoulderPosition.y; //  y position in PIXELS

                    // Package the desired positions referenced from shoulder.
                    PointF desiredElbowPostion = new PointF(posShoulder_x-offset, posShoulder_y);
                    PointF desiredWristPosition = new PointF(posShoulder_x+offset, posShoulder_y-offset);
                    PointF desiredWristPosition2 = new PointF(posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 2.5*offset);

                    //PointF desiredWristPositionRight =  new PointF(posRightShoulder_x-offset, posRightShoulder_y+offset);
                   // PointF desiredWristPositionRight2 = new PointF(posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y + (float) 3*offset);
                    // %%%%f
                    float leftShoudlerDistance = leftElbow.calculateSquaredDistanceFromCoordinates(desiredElbowPostion);
                    float leftWristDistance = leftWrist.calculateSquaredDistanceFromCoordinates(desiredWristPosition);
                    float endLeftWristDistance = leftWrist.calculateSquaredDistanceFromCoordinates(desiredWristPosition2);




                    //String distance = String.valueOf(distanceFromDesired);
                    //Log.d("THIS IS THE DISTANCE", distance);
                    System.out.println(leftWristDistance);

                    if ((leftWristDistance <= 20000)&&(endFlag2==0)) {
                        drawFlag = 1;
                        endFlag = 1;

                        if (drawFlag == 1) {
                            canvas.drawLine(posShoulder_x, posShoulder_y, posShoulder_x + offset, posShoulder_y, DrawUtils2.DEFAULT_PAINT); // DEFAULT_PAINT
                            canvas.drawLine(posShoulder_x + offset, posShoulder_y, posShoulder_x + offset, posShoulder_y - offset, DrawUtils2.DEFAULT_PAINT);

                            canvas.drawLine(posRightShoulder_x, posRightShoulder_y, posRightShoulder_x - offset, posRightShoulder_y, DrawUtils2.DEFAULT_PAINT); // DEFAULT_PAINT
                            canvas.drawLine(posRightShoulder_x - offset, posRightShoulder_y, posRightShoulder_x - offset, posRightShoulder_y - offset, DrawUtils2.DEFAULT_PAINT);

                            // OTHER LEFT draw the other pose in gray
                            canvas.drawLine(posShoulder_x, posShoulder_y, posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 1.5*offset, DrawUtils2.GRAY_PAINT); // DEFAULT_PAINT
                            canvas.drawLine(posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 1.5*offset, posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 3*offset, DrawUtils2.GRAY_PAINT);
                            // OTHER RIGHT
                            canvas.drawLine(posRightShoulder_x, posRightShoulder_y, posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 1.5*offset, DrawUtils2.GRAY_PAINT); // DEFAULT_PAINT
                            canvas.drawLine(posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 1.5*offset, posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 3*offset, DrawUtils2.GRAY_PAINT);


                            drawFlag2 = 1; // enabled coloring other pose green
                            endFlag2=1;
                        }


                    }

                    if ((endFlag == 1) ){
                        //drawFlag2 = 1;

                        if(drawFlag2 == 1 && (endLeftWristDistance <= 20000) ) {
                            canvas.drawLine(posShoulder_x, posShoulder_y, posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 1.5*offset, DrawUtils2.DEFAULT_PAINT); // DEFAULT_PAINT
                            canvas.drawLine(posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 1.5*offset, posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 3*offset, DrawUtils2.DEFAULT_PAINT);

                            canvas.drawLine(posRightShoulder_x, posRightShoulder_y, posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 1.5*offset, DrawUtils2.DEFAULT_PAINT); // DEFAULT_PAINT
                            canvas.drawLine(posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 1.5*offset, posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 3*offset, DrawUtils2.DEFAULT_PAINT);

                            endFlag2=0;
                            drawFlag2=0;
                            drawFlag = 0; // initially draw the gray
                            endFlag = 0;
                            reps += 1;
                        }
                        else {
                            //canvas.drawLine(posShoulder_x, posShoulder_y, posShoulder_x + (float) 0.5*offset, posShoulder_y - (float) 1.5*offset, DrawUtils2.GRAY_PAINT); // DEFAULT_PAINT
                            //canvas.drawLine(posShoulder_x + (float) 0.5*offset, posShoulder_y - (float) 1.5*offset, posShoulder_x - offset, posShoulder_y - (float) 3.25*offset, DrawUtils2.GRAY_PAINT);
                            canvas.drawLine(posShoulder_x, posShoulder_y, posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 1.5*offset, DrawUtils2.GRAY_PAINT); // DEFAULT_PAINT
                            canvas.drawLine(posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 1.5*offset, posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 3*offset, DrawUtils2.GRAY_PAINT);

                            canvas.drawLine(posRightShoulder_x, posRightShoulder_y, posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 1.5*offset, DrawUtils2.GRAY_PAINT); // DEFAULT_PAINT
                            canvas.drawLine(posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 1.5*offset, posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 3*offset, DrawUtils2.GRAY_PAINT);
                        }
                    }




                    if(endFlag==1 && drawFlag2==1)
                    {
                        canvas.drawLine(posShoulder_x, posShoulder_y, posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 1.5*offset, DrawUtils2.GRAY_PAINT); // DEFAULT_PAINT
                        canvas.drawLine(posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 1.5*offset, posShoulder_x + (float) 0.75*offset, posShoulder_y - (float) 3*offset, DrawUtils2.GRAY_PAINT);
                        canvas.drawLine(posRightShoulder_x, posRightShoulder_y, posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 1.5*offset, DrawUtils2.GRAY_PAINT); // DEFAULT_PAINT
                        canvas.drawLine(posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 1.5*offset, posRightShoulder_x - (float) 0.75*offset, posRightShoulder_y - (float) 3*offset, DrawUtils2.GRAY_PAINT);
                    }

                    if (drawFlag==0) {
                        // Draw from left shoulder guide for user in green. This is where they start
                        canvas.drawLine(posShoulder_x, posShoulder_y, posShoulder_x + offset, posShoulder_y, DrawUtils2.GRAY_PAINT); // DEFAULT_PAINT
                        canvas.drawLine(posShoulder_x + offset, posShoulder_y, posShoulder_x + offset, posShoulder_y - offset, DrawUtils2.GRAY_PAINT);

                        canvas.drawLine(posRightShoulder_x, posRightShoulder_y, posRightShoulder_x - offset, posRightShoulder_y, DrawUtils2.GRAY_PAINT); // DEFAULT_PAINT
                        canvas.drawLine(posRightShoulder_x - offset, posRightShoulder_y, posRightShoulder_x - offset, posRightShoulder_y - offset, DrawUtils2.GRAY_PAINT);




                    }
                }

            }
            isComputing.set(false);




        });

        // Snapshot View
        snapshotFrame = findViewById(R.id.snapshot_frame);
        snapshotOverlay = findViewById(R.id.snapshot_view);
        snapshotOverlay.setCallback(
                canvas -> {
                    if (poseResult != null) {
                        Bitmap bitmap = visionImage.overlaySkeletons(poseResult.getPoses());
                        canvas.drawBitmap(bitmap, null, new RectF(0, 0, cameraViewSize.getWidth(), cameraViewSize.getHeight()), null);
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
                predictor.record(visionImage, poseResult, null, () -> {
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
            poseResult = predictor.predict(visionImage);
            requestRender();
        });
    }
}
