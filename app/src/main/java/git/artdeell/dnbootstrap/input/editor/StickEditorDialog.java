package git.artdeell.dnbootstrap.input.editor;

import android.app.Dialog;
import android.widget.TextView;
import android.widget.SeekBar;

import java.util.Locale;

import git.artdeell.dnbootstrap.R;
import git.artdeell.dnbootstrap.input.ControlStick;

public class StickEditorDialog extends InputConfigurationEditorDialog {
    private SeekBar sensitivitySeek;
    private TextView sensitivityValue;

    public StickEditorDialog() {
        super(R.layout.dialog_stick_setup);
    }

    @Override
    protected void inflate(android.app.Dialog dialog) {
        super.inflate(dialog);
        sensitivitySeek = dialog.findViewById(R.id.editor_sensitivity_seek);
        sensitivityValue = dialog.findViewById(R.id.editor_sensitivity_value);
        sensitivitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress / 1000f;
                sensitivityValue.setText(String.format(Locale.ENGLISH, "%.3f", value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void loadSettings() {
        super.loadSettings();
        ControlStick stick = (ControlStick) getEditTarget();
        float sens = 0.014f;
        if (stick.getCreator() instanceof git.artdeell.dnbootstrap.input.model.ControlStickData) {
            sens = ((git.artdeell.dnbootstrap.input.model.ControlStickData) stick.getCreator()).sensitivity;
        }
        int progress = Math.round(sens * 1000f);
        sensitivitySeek.setProgress(progress);
        sensitivityValue.setText(String.format(Locale.ENGLISH, "%.3f", sens));
    }

    @Override
    protected void saveSettings() {
        super.saveSettings();
        ControlStick target = (ControlStick) getEditTarget();
        git.artdeell.dnbootstrap.input.model.ControlStickData d = (git.artdeell.dnbootstrap.input.model.ControlStickData) target.getCreator();
        d.sensitivity = sensitivitySeek.getProgress() / 1000f;
    }
}
