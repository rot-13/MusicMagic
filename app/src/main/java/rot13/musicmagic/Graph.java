package rot13.musicmagic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by asaf on 11/15/15.
 */
public class Graph extends View {

    private static final int SAMPLES_TO_DRAW = 200;
    private long SAMPLE_RESOLUTION_MSECS = 100;
    private long BAR_INTERVAL_MSECS = 4000;
    private static final int LINE_SPACING = 80;
    private static final float NOTE_HEIGHT = 50;

    private List<Integer> mLastSamples;
    private Paint mPaint;
    private Paint mLinePaint;
    private Date mLastBarLineTime;

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
        mPaint.setColor(0xFF666666);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(0xFF808080);
        setWillNotDraw(false);
    }

    public void addSample(int stepsAboveA) {
        while (mLastSamples.size() > SAMPLES_TO_DRAW-2) {
            mLastSamples.remove(SAMPLES_TO_DRAW-2);
        }
        mLastSamples.add(0, stepsAboveA);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRGB(255, 255, 255);
        float maxY = (float)canvas.getHeight();
        float maxX = (float)canvas.getWidth();
        float colWidth = maxX / SAMPLES_TO_DRAW;
        float samplesInBar = BAR_INTERVAL_MSECS / MainActivity.SAMPLE_LENGTH_MSECS;
        float barWidth = samplesInBar * colWidth;

        float bottomLineY = maxY/2 + 2*LINE_SPACING;
        float topLineY = bottomLineY - 4*LINE_SPACING;

        // Draw staff
        for (int i = 0; i < 5; i++) {
            float y = bottomLineY - (i*LINE_SPACING);
            canvas.drawLine(0, y, canvas.getWidth(), y, mLinePaint);
        }

        // Calculate first bar line
        Date now = new Date();
        if (mLastBarLineTime == null) {
            mLastBarLineTime = now;
        }
        long mSecsSinceLastSample = now.getTime() - mLastBarLineTime.getTime();
        float lineX;
        if (mSecsSinceLastSample >= BAR_INTERVAL_MSECS) {
            lineX = 0;
            mLastBarLineTime = now;
        } else {
            float percentElapsed = (float)mSecsSinceLastSample / BAR_INTERVAL_MSECS;
            float shiftLeft = barWidth * percentElapsed;
            lineX = barWidth - shiftLeft;
        }

        // Draw bar lines
        while (lineX <= maxX) {
            canvas.drawLine(lineX, bottomLineY, lineX, topLineY, mLinePaint);
            lineX += barWidth;
        }

        // Draw notes
        for (int i = 0; i < SAMPLES_TO_DRAW; ++i) {
            if (mLastSamples.size() <= i) {
                break;
            }
            Integer current = mLastSamples.get(i);
            if (current != -1) {
                float centerNote = getNoteCenter(mLastSamples.get(i), bottomLineY;
                canvas.drawRect(i*colWidth, centerNote - NOTE_HEIGHT / 2, (i+1)*colWidth, centerNote + NOTE_HEIGHT / 2, mPaint);
            }
        }
    }

    private float getNoteCenter(int stepsAboveA, float bottomLineY) {
        double lines[] = {1.5, 1.5, 2, -1, -1, -0.5, -0.5, 0, 0.5, 0.5, 1, 1};
        boolean flats[] = {false, true, false, false, true, false, true, false, false, true, false, true};

        int index = stepsAboveA % 12;
        if (index < 0) {
            index += 12;
        }

        double line = lines[index];
        boolean flat = flats[index];
        double noteCenter = bottomLineY - line*LINE_SPACING;
        return (float)noteCenter;
    }
}
