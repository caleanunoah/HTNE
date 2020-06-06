package ai.fritz.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.core.content.ContextCompat;
import ai.fritz.imagelabelingdemo.R;

import java.util.ArrayList;
import java.util.List;

import ai.fritz.vision.FritzVisionLabel;

/**
 * A simple View providing a render callback to other classes.
 */
public class OverlayView extends View {
    private DrawCallback callback;

    private static final float TEXT_SIZE_DIP = 24;
    private List<FritzVisionLabel> labels = new ArrayList<>();
    private final float textSizePx;
    private final Paint fgPaint;
    private final Paint bgPaint;

    public OverlayView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        fgPaint = new Paint();
        fgPaint.setTextSize(textSizePx);
        fgPaint.setColor(ContextCompat.getColor(context, R.color.textColorPrimary));

        bgPaint = new Paint();
        bgPaint.setColor(Color.TRANSPARENT);
    }

    public void setResult(final List<FritzVisionLabel> labels) {
        this.labels = labels;
        postInvalidate();
    }
    /**
     * Interface defining the callback for client classes.
     */
    public interface DrawCallback {
        void drawCallback(final Canvas canvas);
    }

    public void setCallback(final DrawCallback callback) {
        this.callback = callback;
    }

    @Override
    public synchronized void draw(final Canvas canvas) {
        super.draw(canvas);
        if(callback != null) {
            callback.drawCallback(canvas);
        }

        final int x = 10;
        int y = (int) (fgPaint.getTextSize() * 1.5f);

        canvas.drawPaint(bgPaint);

        if (labels.size() > 0) {
            for (final FritzVisionLabel label : labels) {
                double confidence = Math.round(label.getConfidence() * 1000) / 10.0;
                canvas.drawText(label.getText() + ": " + confidence + "%", x, y, fgPaint);
                y += fgPaint.getTextSize() * 1.5f;
            }
        }
    }
}