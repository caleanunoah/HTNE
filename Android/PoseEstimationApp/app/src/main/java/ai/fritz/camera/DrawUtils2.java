package ai.fritz.camera;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class DrawUtils2 {
    public static final Paint DEFAULT_PAINT = buildDefaultBoundingBoxPaint();
    public static final Paint DEFAULT_TEXT_PAINT = buildDefaultTextPaint();
    public static final Paint GRAY_PAINT = buildDefaultBoundingBoxPaintGREY();

    public DrawUtils2() {
    }

    // GREEN
    private static Paint buildDefaultBoundingBoxPaint() {
        Paint paint = new Paint();
        paint.setColor(-16711936);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(5.0F);
        return paint;
    }
    // GREEN
    private static Paint buildDefaultTextPaint() {
        Paint paint = new Paint();
        paint.setColor( -16711936);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(2.0F);
        paint.setTextSize(32.0F);
        paint.setStyle(Style.FILL);
        return paint;
    }

    private static Paint buildDefaultBoundingBoxPaintGREY() {
        Paint paint = new Paint();
        paint.setColor(-7829368);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(5.0F);
        return paint;
    }

}