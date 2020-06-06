package ai.fritz.fritzvisionvideo.strategies.customfilters;

import android.graphics.Bitmap;

import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationMaskOptions;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictor;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationResult;
import ai.fritz.vision.imagesegmentation.MaskClass;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor;
import ai.fritz.vision.styletransfer.FritzVisionStyleResult;
import ai.fritz.vision.video.filters.imagesegmentation.FritzVisionSegmentationFilter;

public class StylizeHairFilter extends FritzVisionSegmentationFilter {

    private FritzVisionStylePredictor stylePredictor;

    public StylizeHairFilter(
            FritzVisionStylePredictor stylePredictor,
            FritzVisionSegmentationPredictor model,
            MaskClass segmentationMask
    ) {
        super(model, segmentationMask);
        this.stylePredictor = stylePredictor;
    }

    public StylizeHairFilter(
            FritzVisionStylePredictor stylePredictor,
            FritzVisionSegmentationPredictor predictor,
            FritzVisionSegmentationMaskOptions options,
            MaskClass segmentationMask
    ) {
        super(predictor, options, segmentationMask);
        this.stylePredictor = stylePredictor;
    }

    @Override
    public FilterCompositionMode getCompositionMode() {
        return FilterCompositionMode.OVERLAY_ON_ORIGINAL_IMAGE;
    }

    @Override
    public FritzVisionImage processImage(FritzVisionImage image) {
        FritzVisionSegmentationResult hairResult = predictor.predict(image);
        FritzVisionStyleResult styleResult = stylePredictor.predict(image);

        FritzVisionImage stylizedImage = FritzVisionImage.fromBitmap(styleResult.toBitmap());
        Bitmap mask = hairResult.buildSingleClassMask(segmentationMask, options);

        return FritzVisionImage.fromBitmap(stylizedImage.mask(mask));
    }
}
