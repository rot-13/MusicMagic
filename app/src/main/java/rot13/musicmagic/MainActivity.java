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

    private static final int BASE_NOTE_FREQUENCY = 440; // A_4
    private static final double A = Math.pow(2, 1.0/12.0); // Base for freq --> pitch calculations

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result,AudioEvent e) {
                final float pitchInHz = result.getPitch();
                final String note = freq2Note(pitchInHz);
                final float probability = result.getProbability();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) findViewById(R.id.textView1);
                        TextView prob = (TextView) findViewById(R.id.textView2);
                        Graph graph = (Graph) findViewById(R.id.graph);
                        graph.addSample(probability > 0.7 ? pitchInHz : 0);
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

    private static String freq2Note(double frequency) {
        int steps = stepsAboveBase(frequency);
        String note = stepsToNote(steps);
        return note;
    }

    private static int stepsAboveBase(double frequency) {
        double unrounded = log(A, frequency / BASE_NOTE_FREQUENCY);
        return (int)Math.round(unrounded);
    }

    private static String stepsToNote(int steps) {
        int mod = steps % 12;
        if (mod < 0) {
            mod += 12;
        }
        String[] notes = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
        return notes[mod];
    }
}
