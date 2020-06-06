package ai.fritz.fritzvisionvideo.strategies;

import android.os.Parcel;

import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.ModelVariant;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationMaskOptions;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictor;
import ai.fritz.vision.imagesegmentation.MaskClass;
import ai.fritz.vision.imagesegmentation.SegmentationOnDeviceModel;
import ai.fritz.vision.poseestimation.FritzVisionPosePredictor;
import ai.fritz.vision.poseestimation.PoseOnDeviceModel;
import ai.fritz.vision.video.FritzVisionImageFilter;
import ai.fritz.vision.video.filters.DrawSkeletonCompoundFilter;
import ai.fritz.vision.video.filters.imagesegmentation.MaskOverlayFilter;

public class PoseDoubleMaskStrategy extends VideoFilterStrategy {

    private PoseOnDeviceModel poseModel = FritzVisionModels.getHumanPoseEstimationOnDeviceModel(ModelVariant.FAST);
    private SegmentationOnDeviceModel peopleModel = FritzVisionModels.getPeopleSegmentationOnDeviceModel(ModelVariant.FAST);
    private SegmentationOnDeviceModel hairModel = FritzVisionModels.getHairSegmentationOnDeviceModel(ModelVariant.FAST);

    private FritzVisionSegmentationMaskOptions peopleOptions = new FritzVisionSegmentationMaskOptions();

    public PoseDoubleMaskStrategy() {
        super();
    }

    public PoseDoubleMaskStrategy(Parcel in) {
        super(in);
    }

    public static final Creator<VideoFilterStrategy> CREATOR = new Creator<VideoFilterStrategy>() {
        @Override
        public VideoFilterStrategy createFromParcel(Parcel in) {
            return new PoseDoubleMaskStrategy(in);
        }

        @Override
        public VideoFilterStrategy[] newArray(int size) {
            return new PoseDoubleMaskStrategy[size];
        }
    };

    @Override
    public FritzVisionImageFilter[] getFilters() {
        // Reduce the alpha of the people mask
        peopleOptions.maxAlpha = 50;

        // Create predictors for the filters
        FritzVisionPosePredictor posePredictor = FritzVision.PoseEstimation.getPredictor(poseModel);
        FritzVisionSegmentationPredictor peoplePredictor = FritzVision.ImageSegmentation.getPredictor(peopleModel);
        FritzVisionSegmentationPredictor hairPredictor = FritzVision.ImageSegmentation.getPredictor(hairModel);

        return new FritzVisionImageFilter[]{
                new DrawSkeletonCompoundFilter(posePredictor), // Applied first
                new MaskOverlayFilter(peoplePredictor, peopleOptions, MaskClass.PERSON), // Applied second
                new MaskOverlayFilter(hairPredictor, MaskClass.HAIR) // Applied last
        };
    }
}
