package ai.fritz.fritzvisionvideo.strategies;

import android.os.Parcel;

import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.ModelVariant;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictor;
import ai.fritz.vision.imagesegmentation.MaskClass;
import ai.fritz.vision.imagesegmentation.SegmentationOnDeviceModel;
import ai.fritz.vision.video.FritzVisionImageFilter;
import ai.fritz.vision.video.filters.imagesegmentation.MaskCutOutOverlayFilter;
import ai.fritz.vision.video.filters.imagesegmentation.MaskOverlayFilter;


public class MaskCutStrategy extends VideoFilterStrategy {

    private SegmentationOnDeviceModel peopleModel = FritzVisionModels.getPeopleSegmentationOnDeviceModel(ModelVariant.FAST);
    private SegmentationOnDeviceModel hairModel = FritzVisionModels.getHairSegmentationOnDeviceModel(ModelVariant.FAST);

    public MaskCutStrategy() {
        super();
    }

    public MaskCutStrategy(Parcel in) {
        super(in);
    }

    public static final Creator<VideoFilterStrategy> CREATOR = new Creator<VideoFilterStrategy>() {
        @Override
        public VideoFilterStrategy createFromParcel(Parcel in) {
            return new MaskCutStrategy(in);
        }

        @Override
        public VideoFilterStrategy[] newArray(int size) {
            return new MaskCutStrategy[size];
        }
    };

    @Override
    public FritzVisionImageFilter[] getFilters() {
        // Create predictors for the filter
        FritzVisionSegmentationPredictor personPredictor = FritzVision.ImageSegmentation.getPredictor(peopleModel);
        FritzVisionSegmentationPredictor hairPredictor = FritzVision.ImageSegmentation.getPredictor(hairModel);

        return new FritzVisionImageFilter[]{
                new MaskOverlayFilter(personPredictor, MaskClass.PERSON),
                new MaskCutOutOverlayFilter(hairPredictor, MaskClass.HAIR)
        };
    }
}
