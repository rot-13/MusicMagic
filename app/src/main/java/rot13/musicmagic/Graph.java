package rot13.musicmagic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asaf on 11/15/15.
 */
public class Graph extends View {

    private static final int SAMPLES_TO_DRAW = 200;

    private List<Integer> mLastSamples;
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
        float colWidth = (float)canvas.getWidth() / SAMPLES_TO_DRAW;
        float colMaxHeight = (float)canvas.getHeight();

        int lineSpacing = 80;
        float noteHeight = 50;
        float bottomLineY = colMaxHeight/2;

        for (int i = 0; i < 5; i++) {
            float y = bottomLineY - (i*lineSpacing);
            canvas.drawLine(0, y, canvas.getWidth(), y, mLinePaint);
        }

        for (int i = 0; i < SAMPLES_TO_DRAW; ++i) {
            if (mLastSamples.size() <= i) {
                break;
            }

            float centerNote = getNoteCenter(mLastSamples.get(i), bottomLineY, noteHeight, lineSpacing);
            canvas.drawRect(i*colWidth, centerNote - noteHeight / 2, (i+1)*colWidth, centerNote + noteHeight / 2, mPaint);
        }
    }

    private float getNoteCenter(int stepsAboveA, float bottomLineY, float noteHeight, float lineHeight) {
        double lines[] = {1.5, 1.5, 2, -1, -1, -0.5, -0.5, 0, 0.5, 0.5, 1, 1};
        boolean flats[] = {false, true, false, false, true, false, true, false, false, true, false, true};

        int index = stepsAboveA % 12;
        if (index < 0) {
            index += 12;
        }

        double line = lines[index];
        boolean flat = flats[index];
        double noteCenter = bottomLineY - line*lineHeight;
        return (float)noteCenter;
    }
}
