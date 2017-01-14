/**
 * Created by sarvesh on 21-09-2015.
 */
package builders.submile;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

public class RoundedImageView extends android.support.v7.widget.AppCompatImageView {

    private Path mMaskPath;
    private Paint mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mCornerRadius = 10;

    public RoundedImageView(Context context) {
        super(context);

        init();
    }

    public RoundedImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, null);
        mMaskPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
    }

    /**
     * Set the corner radius to use for the RoundedRectangle.
     *
     * @param  - The corner radius of the rounded rectangle.
     */
    public void setCornerRadius(int cornerRadius) {
        mCornerRadius = cornerRadius;
        generateMaskPath(getWidth(), getHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        if (w != oldW || h != oldH) {
            generateMaskPath(w, h);
        }
    }

    private void generateMaskPath(int w, int h) {
        mMaskPath = new Path();
        mMaskPath.addRoundRect(new RectF(0,0,w,h), mCornerRadius, mCornerRadius, Direction.CW);
        mMaskPath.setFillType(FillType.INVERSE_WINDING);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(canvas.isOpaque()) { // If canvas is opaque, make it transparent
            canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 255, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
        }

        super.onDraw(canvas);

        if(mMaskPath != null) {
            canvas.drawPath(mMaskPath, mMaskPaint);
        }
    }
}