package ai.fritz.fritzvisionvideo.strategies;

import android.os.Parcel;
import android.os.Parcelable;

import ai.fritz.vision.video.FritzVisionImageFilter;

public abstract class VideoFilterStrategy implements Parcelable {

    protected VideoFilterStrategy() {
    }

    protected VideoFilterStrategy(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Retrieve created filters.
     *
     * @return A filter array.
     */
    public abstract FritzVisionImageFilter[] getFilters();
}
