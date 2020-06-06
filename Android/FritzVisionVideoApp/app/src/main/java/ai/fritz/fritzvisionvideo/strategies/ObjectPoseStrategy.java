package ai.fritz.fritzvisionvideo.strategies;

import android.os.Parcel;

import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.ModelVariant;
import ai.fritz.vision.objectdetection.FritzVisionObjectPredictor;
import ai.fritz.vision.objectdetection.ObjectDetectionOnDeviceModel;
import ai.fritz.vision.poseestimation.FritzVisionPosePredictor;
import ai.fritz.vision.poseestimation.PoseOnDeviceModel;
import ai.fritz.vision.video.FritzVisionImageFilter;
import ai.fritz.vision.video.filters.DrawBoxesCompoundFilter;
import ai.fritz.vision.video.filters.DrawSkeletonCompoundFilter;

public class ObjectPoseStrategy extends VideoFilterStrategy {

    private ObjectDetectionOnDeviceModel objectModel = FritzVisionModels.getObjectDetectionOnDeviceModel();
    private PoseOnDeviceModel poseModel = FritzVisionModels.getHumanPoseEstimationOnDeviceModel(ModelVariant.FAST);


    public ObjectPoseStrategy() {
        super();
    }

    public ObjectPoseStrategy(Parcel in) {
        super(in);
    }

    public static final Creator<VideoFilterStrategy> CREATOR = new Creator<VideoFilterStrategy>() {
        @Override
        public VideoFilterStrategy createFromParcel(Parcel in) {
            return new ObjectPoseStrategy(in);
        }

        @Override
        public VideoFilterStrategy[] newArray(int size) {
            return new ObjectPoseStrategy[size];
        }
    };

    @Override
    public FritzVisionImageFilter[] getFilters() {
        // Create predictors for the filters
        FritzVisionObjectPredictor objectPredictor = FritzVision.ObjectDetection.getPredictor(objectModel);
        FritzVisionPosePredictor posePredictor = FritzVision.PoseEstimation.getPredictor(poseModel);

        return new FritzVisionImageFilter[]{
                new DrawBoxesCompoundFilter(objectPredictor), // Applied first
                new DrawSkeletonCompoundFilter(posePredictor) // Applied second
        };
    }
}
