package project.inf431.polytechnique.fr.cardgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DrawableCard extends View{

    /** tag */
    private static final String TAG = "DrawableCard";

    /**
     * Padding between shapes of a same card, as well as between
     * shapes and borders; expressed as a fraction of the bounding
     * dimension (width or height).
     */
    private static final float SHAPE_PADDING = 0.125F;

    /** Basic spacing step between concentric shapes. */
    private static final float CONCENTRIC_STEP = 15.0F;

    /** Border stroke setting. */
    private static final float BORDER_STROKE = 15.0F;

    /** The card to draw. */
    private Card card;

    private final RectF r = new RectF();

    /** Custom view width and height. */
    private int w;
    private int h;

    /**
     * Whether the card is selected, or in the middle of a transition
     * (valid or invalid).
     */
    private int selected;

    /**
     * The paint used internally to draw all parts of the card. It may
     * be mutated by the various drawing methods.
     */
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * A path object used internally to draw diamond shapes.
     */
    private final Path p = new Path();

    private Paint mBorder = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * The constructor to build a new drawable card
     * @param context: context environment where we draw the view
     * @param attrs: view initialised attributes
     */
    public DrawableCard(Context context, AttributeSet attrs) {
        super(context, attrs);

        //initially the card is not selected
        this.selected = -1;
        init();
    }

    /**
     * The initialisation before drawing a card
     */
    private void init() {

        this.paint.setStyle(Paint.Style.FILL);
        this.mBorder.setStyle(Paint.Style.STROKE);
        this.mBorder.setColor(Color.GRAY);
        this.mBorder.setStrokeWidth(BORDER_STROKE);

    }

    @Override
    /**
     * The function to control the size of the view object
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    /**
     * @return the card drawn by this drawable
     */
    Card getCard() {
        return this.card;
    }

    /**
     * Sets the card to draw. If any view is displaying the drawable,
     * it should be invalidated.
     * @param card the card to draw
     */
    void setCard(Card card) {
        this.card = card;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect b = getBounds();

        if (this.card.isSelected()) {
            canvas.drawRect(getBounds(), mBorder);
        }

        int alpha = paint.getAlpha();

        /* Draw border or background. */
        paint.setColor(Color.DKGRAY);
        paint.setAlpha(alpha);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(b, paint);

        /* Pick a color. */
        if (this.card == null) {
            return;
        }

        switch (this.card.getColor()) {
            case 1:
                paint.setColor(0xFF882222);
                break;
            case 2:
                paint.setColor(0xFF228822);
                break;
            case 3:
                paint.setColor(0xFF222288);
                break;
            default:
                throw new IllegalStateException("Illegal color characteristic.");
        }
        paint.setAlpha(alpha);

        /* Draw shapes. */
        int n = this.card.getNumber();
        float hPadding = b.width() * SHAPE_PADDING;
        float vPadding = b.height() * SHAPE_PADDING;
        float h = (b.height() - (n + 1) * vPadding) / 3.0F;
        float t = b.top + (b.height() - n * h - (n - 1) * vPadding) / 2.0F;
        for (int i = 0; i < n; ++i) {
            /*
             * Reset the Rect r as it may have been overriden in
             * drawShape(). We also keep the variable t local for the
             * same reason.
             */
            r.left = b.left + hPadding;
            r.right = b.right - hPadding;
            r.top = t;
            r.bottom = t + h;
            t = r.bottom + vPadding;
            drawShapeWithFilling(canvas,
                    this.card.getFilling(), this.card.getShape());
        }
    }

    /**
     * Draws a single shape with the specified filling.
     * @param canvas the canvas on which to draw
     * @param filling the filling characteristic to draw
     * @param shape the shape characteristic to draw
     */
    private void drawShapeWithFilling(Canvas canvas, int filling, int shape) {
        switch (filling) {
            case 1:
                paint.setStyle(Paint.Style.STROKE);
                drawShape(canvas, shape);
                break;
            case 2:
            /*
             * For intermediate filling, we draw concentric copies of
             * the same shape.
             */
                paint.setStyle(Paint.Style.STROKE);
                float w = r.width() / 2.0F;
                float u = CONCENTRIC_STEP * (r.height() / r.width());
                for (float i = 0; i < w; i += CONCENTRIC_STEP) {
                    drawShape(canvas, shape);
                    r.left += CONCENTRIC_STEP;
                    r.top += u;
                    r.right -= CONCENTRIC_STEP;
                    r.bottom -= u;
                }
                break;
            case 3:
                paint.setStyle(Paint.Style.FILL);
                drawShape(canvas, shape);
                break;
            default:
                throw new IllegalArgumentException(
                        "Illegal filling characteristic.");
        }
    }

    /**
     * Draws a single shape.
     * @param canvas the canvas on which to draw
     * @param shape the shape characteristic to draw
     */
    private void drawShape(Canvas canvas, int shape) {
        switch (shape) {
            case 1:
                canvas.drawOval(r, paint);
                break;
            case 2:
                canvas.drawRect(r, paint);
                break;
            case 3:
                drawDiamond(canvas);
                break;
            default:
                throw new IllegalArgumentException(
                        "Illegal shape characteristic.");
        }
    }

    /**
     * Draws a diamond shape within the specified rectangle.
     * @param canvas the canvas on which to draw
     */
    private void drawDiamond(Canvas canvas) {
        p.moveTo(r.left, r.centerY());
        p.lineTo(r.centerX(), r.top);
        p.lineTo(r.right, r.centerY());
        p.lineTo(r.centerX(), r.bottom);
        p.lineTo(r.left, r.centerY());
        canvas.drawPath(p, paint);
    }

    /** Draws opaque background
     * @param canvas : the canvas on which to draw
     */
    private void drawBackground(Canvas canvas) {

    }

    private Rect getBounds() {
        Rect rectA = new Rect(getLeft(), getTop(), getLeft()+getWidth(), getTop()+getHeight());
        return rectA;
    }

}
