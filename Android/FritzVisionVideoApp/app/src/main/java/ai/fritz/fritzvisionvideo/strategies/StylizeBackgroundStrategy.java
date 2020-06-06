package ai.fritz.fritzvisionvideo.strategies;

import android.os.Parcel;

import ai.fritz.core.FritzOnDeviceModel;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.ModelVariant;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictor;
import ai.fritz.vision.imagesegmentation.MaskClass;
import ai.fritz.vision.imagesegmentation.SegmentationOnDeviceModel;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor;
import ai.fritz.vision.video.FritzVisionImageFilter;
import ai.fritz.vision.video.filters.StylizeImageCompoundFilter;
import ai.fritz.vision.video.filters.imagesegmentation.MaskCutOutOverlayFilter;

public class StylizeBackgroundStrategy extends VideoFilterStrategy {

    private FritzOnDeviceModel styleModel = FritzVisionModels.getPaintingStyleModels().getTheScream();
    private SegmentationOnDeviceModel peopleModel = FritzVisionModels.getPeopleSegmentationOnDeviceModel(ModelVariant.FAST);

    public StylizeBackgroundStrategy() {
        super();
    }

    public StylizeBackgroundStrategy(Parcel in) {
        super(in);
    }

    public static final Creator<VideoFilterStrategy> CREATOR = new Creator<VideoFilterStrategy>() {
        @Override
        public VideoFilterStrategy createFromParcel(Parcel in) {
            return new StylizeBackgroundStrategy(in);
        }

        @Override
        public VideoFilterStrategy[] newArray(int size) {
            return new StylizeBackgroundStrategy[size];
        }
    };

    @Override
    public FritzVisionImageFilter[] getFilters() {
        // Create predictors for the filters
        FritzVisionStylePredictor stylePredictor = FritzVision.StyleTransfer.getPredictor(styleModel);
        FritzVisionSegmentationPredictor segmentationPredictor = FritzVision.ImageSegmentation.getPredictor(peopleModel);

        return new FritzVisionImageFilter[]{
                new StylizeImageCompoundFilter(stylePredictor), // Applied first
                new MaskCutOutOverlayFilter(segmentationPredictor, MaskClass.PERSON) // Applied second
        };
    }
}
