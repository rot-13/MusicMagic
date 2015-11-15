package rot13.musicmagic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asaf on 11/15/15.
 */
public class Graph extends View {

    private static final int SAMPLES_TO_DRAW = 200;
    private static final float MAX_SAMPLE_VALUE = 2000;

    private List<Float> mNotes;
    private List<Float> mLastSamples;
    private Paint mPaint;
    private Paint mLinePaint;

    public Graph(Context context) {
        super(context);
    }

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Graph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mLastSamples = new ArrayList<>(SAMPLES_TO_DRAW);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xFF000000);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(0xFF808080);
        setWillNotDraw(false);

        mNotes = new ArrayList<>(40);
        mNotes.add(16.35f);
        mNotes.add(17.32f);
        mNotes.add(18.35f);
        mNotes.add(19.45f);
        mNotes.add(20.60f);
        mNotes.add(21.83f);
        mNotes.add(23.12f);
        mNotes.add(24.50f);
        mNotes.add(25.96f);
        mNotes.add(27.50f);
        mNotes.add(29.14f);
        mNotes.add(30.87f);
        mNotes.add(32.70f);
        mNotes.add(34.65f);
        mNotes.add(36.71f);
        mNotes.add(38.89f);
        mNotes.add(41.20f);
        mNotes.add(43.65f);
        mNotes.add(46.25f);
        mNotes.add(49.00f);
        mNotes.add(51.91f);
        mNotes.add(55.00f);
        mNotes.add(58.27f);
        mNotes.add(61.74f);
        mNotes.add(65.41f);
        mNotes.add(69.30f);
        mNotes.add(73.42f);
        mNotes.add(77.78f);
        mNotes.add(82.41f);
        mNotes.add(87.31f);
        mNotes.add(92.50f);
        mNotes.add(98.00f);
        mNotes.add(103.83f);
        mNotes.add(110.00f);
        mNotes.add(116.54f);
        mNotes.add(123.47f);
        mNotes.add(130.81f);
        mNotes.add(138.59f);
        mNotes.add(146.83f);
        mNotes.add(155.56f);
        mNotes.add(164.81f);
        mNotes.add(174.61f);
        mNotes.add(185.00f);
        mNotes.add(196.00f);
        mNotes.add(207.65f);
        mNotes.add(220.00f);
        mNotes.add(233.08f);
        mNotes.add(246.94f);
        mNotes.add(261.63f);
        mNotes.add(277.18f);
        mNotes.add(293.66f);
        mNotes.add(311.13f);
        mNotes.add(329.63f);
        mNotes.add(349.23f);
        mNotes.add(369.99f);
        mNotes.add(392.00f);
        mNotes.add(415.30f);
        mNotes.add(440.00f);
        mNotes.add(466.16f);
        mNotes.add(493.88f);
        mNotes.add(523.25f);
        mNotes.add(554.37f);
        mNotes.add(587.33f);
        mNotes.add(622.25f);
        mNotes.add(659.25f);
        mNotes.add(698.46f);
        mNotes.add(739.99f);
        mNotes.add(783.99f);
        mNotes.add(830.61f);
        mNotes.add(880.00f);
        mNotes.add(932.33f);
        mNotes.add(987.77f);
        mNotes.add(1046.50f);
        mNotes.add(1108.73f);
        mNotes.add(1174.66f);
        mNotes.add(1244.51f);
        mNotes.add(1318.51f);
        mNotes.add(1396.91f);
        mNotes.add(1479.98f);
        mNotes.add(1567.98f);
        mNotes.add(1661.22f);
        mNotes.add(1760.00f);
        mNotes.add(1864.66f);
        mNotes.add(1975.53f);
        mNotes.add(2093.00f);

    }

    public void addSample(float sample) {
        while (mLastSamples.size() > SAMPLES_TO_DRAW-2) {
            mLastSamples.remove(SAMPLES_TO_DRAW-2);
        }
        mLastSamples.add(0, sample);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRGB(255, 255, 255);
        float colWidth = (float)canvas.getWidth() / SAMPLES_TO_DRAW;
        float colMaxHeight = (float)canvas.getHeight();

        for (float note : mNotes) {
            canvas.drawLine(0, colMaxHeight - (note / MAX_SAMPLE_VALUE) * colMaxHeight, canvas.getWidth(), colMaxHeight - (note / MAX_SAMPLE_VALUE) * colMaxHeight, mLinePaint);
        }

        for (int i = 0; i < SAMPLES_TO_DRAW; ++i) {
            if (mLastSamples.size() <= i) {
                break;
            }

            canvas.drawRect(i*colWidth, colMaxHeight - (mLastSamples.get(i) / MAX_SAMPLE_VALUE) * colMaxHeight, (i+1)*colWidth, colMaxHeight, mPaint);
        }
    }
}
