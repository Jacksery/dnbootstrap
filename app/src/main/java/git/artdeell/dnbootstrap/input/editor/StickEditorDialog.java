package git.artdeell.dnbootstrap.input.editor;

import android.app.Dialog;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import git.artdeell.dnbootstrap.R;
import git.artdeell.dnbootstrap.input.ControlStick;
import git.artdeell.dnbootstrap.input.model.ControlStickData;

public class StickEditorDialog extends LayoutEditorDialog {
    private CheckBox controlsCameraCheck;
    private SeekBar sensitivitySeek;
    private TextView sensitivityValue;

    public StickEditorDialog() {
        super(R.layout.dialog_stick_setup);
    }

    @Override
    protected void inflate(Dialog dialog) {
        super.inflate(dialog);
        controlsCameraCheck = dialog.findViewById(R.id.editor_controls_camera);
        sensitivitySeek = dialog.findViewById(R.id.editor_sensitivity_seek);
        sensitivityValue = dialog.findViewById(R.id.editor_sensitivity_value);
        sensitivitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivityValue.setText(String.format(Locale.ENGLISH, "%.3f", progress / 1000f));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void loadSettings() {
        ControlStickData data = (ControlStickData) ((ControlStick) getEditTarget()).getCreator();
        controlsCameraCheck.setChecked(data.controlsCamera);
        int progress = Math.round(data.sensitivity * 1000f);
        sensitivitySeek.setProgress(progress);
        sensitivityValue.setText(String.format(Locale.ENGLISH, "%.3f", data.sensitivity));
    }

    @Override
    protected void saveSettings() {
        ControlStickData data = (ControlStickData) ((ControlStick) getEditTarget()).getCreator();
        data.controlsCamera = controlsCameraCheck.isChecked();
        data.sensitivity = sensitivitySeek.getProgress() / 1000f;
    }
}
