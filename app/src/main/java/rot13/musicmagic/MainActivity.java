package rot13.musicmagic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by asaf on 11/15/15.
 */
public class MainActivity extends Activity {

    public static final int SAMPLE_RATE = 22050;
    public static final int SAMPLE_LENGTH_MSECS = 50; // TODO this must be calculated somehow
    private static final int BASE_NOTE_FREQUENCY = 440; // A_4
    private static final double A = Math.pow(2, 1.0/12.0); // Base for freq --> pitch calculations
    private static final double MIN_PROBABILITY = 0.8;
    private static final int MIN_STEPS = -12;
    private static final int MAX_STEPS = 24;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, 1024, 0);
        final TextView text = (TextView) findViewById(R.id.textView1);
        final TextView prob = (TextView) findViewById(R.id.textView2);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                final int stepsAboveA = stepsAboveBase(pitchInHz);
                final String note = stepsToNote(stepsAboveA);
                final float probability = result.getProbability();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Graph graph = (Graph) findViewById(R.id.graph);
                        boolean add = probability > MIN_PROBABILITY &&
                                stepsAboveA > MIN_STEPS &&
                                stepsAboveA < MAX_STEPS;
                        graph.addSample(add ? stepsAboveA : -1);
                        text.setText(note);
                        prob.setText("" + probability);
                    }
                });
            }
        };
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    private static double log(double base, double n) {
        return Math.log(n) / Math.log(base);
    }

    private static int stepsAboveBase(double frequency) {
        if (frequency == -1) {
            return -1;
        }
        double unrounded = log(A, frequency / BASE_NOTE_FREQUENCY);
        return (int)Math.round(unrounded);
    }

    private static String stepsToNote(int steps) {
        if (steps == -1) {
            return null;
        }
        int mod = steps % 12;
        if (mod < 0) {
            mod += 12;
        }
        int octave = (int)Math.floor((double)steps / 12.) + 4;
        String[] notes = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
        return notes[mod] + octave;
    }
}
