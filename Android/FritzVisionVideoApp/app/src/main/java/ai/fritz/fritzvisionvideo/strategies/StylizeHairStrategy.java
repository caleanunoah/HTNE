package ai.fritz.fritzvisionvideo.strategies;

import android.os.Parcel;

import ai.fritz.core.FritzOnDeviceModel;
import ai.fritz.fritzvisionvideo.strategies.customfilters.StylizeHairFilter;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.ModelVariant;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictor;
import ai.fritz.vision.imagesegmentation.MaskClass;
import ai.fritz.vision.imagesegmentation.SegmentationOnDeviceModel;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor;
import ai.fritz.vision.video.FritzVisionImageFilter;

public class StylizeHairStrategy extends VideoFilterStrategy {

    private FritzOnDeviceModel styleModel = FritzVisionModels.getPaintingStyleModels().getStarryNight();
    private SegmentationOnDeviceModel hairModel = FritzVisionModels.getHairSegmentationOnDeviceModel(ModelVariant.FAST);

    public StylizeHairStrategy() {
        super();
    }

    public StylizeHairStrategy(Parcel in) {
        super(in);
    }

    public static final Creator<VideoFilterStrategy> CREATOR = new Creator<VideoFilterStrategy>() {
        @Override
        public VideoFilterStrategy createFromParcel(Parcel in) {
            return new StylizeHairStrategy(in);
        }

        @Override
        public VideoFilterStrategy[] newArray(int size) {
            return new StylizeHairStrategy[size];
        }
    };

    @Override
    public FritzVisionImageFilter[] getFilters() {
        // Create predictors for the filter
        FritzVisionSegmentationPredictor hairPredictor = FritzVision.ImageSegmentation.getPredictor(hairModel);
        FritzVisionStylePredictor stylePredictor = FritzVision.StyleTransfer.getPredictor(styleModel);

        return new FritzVisionImageFilter[]{
                new StylizeHairFilter(stylePredictor, hairPredictor, MaskClass.HAIR)
        };
    }
}
