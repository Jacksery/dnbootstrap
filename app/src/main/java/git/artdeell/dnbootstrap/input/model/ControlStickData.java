package git.artdeell.dnbootstrap.input.model;

import android.content.Context;
import android.view.View;

import git.artdeell.dnbootstrap.input.ControlStick;
import git.artdeell.dnbootstrap.input.LoadableButtonLayout;

public class ControlStickData extends VisibilityConfiguration implements ViewCreator {
    public static final String TYPE = "stick";
    public LoadableButtonLayout.LayoutParams layoutParams;
    public InputConfiguration inputConfiguration;
    public float sensitivity = 0.014f;

    public ControlStickData() {}

    public ControlStickData(ControlStickData src) {
        this.layoutParams = new LoadableButtonLayout.LayoutParams(src.layoutParams);
        this.inputConfiguration = new InputConfiguration(src.inputConfiguration);
        this.sensitivity = src.sensitivity;
        this.showInGame = src.showInGame;
        this.showInMenu = src.showInMenu;
    }

    public static ControlStickData createDefault() {
        ControlStickData d = new ControlStickData();
        d.layoutParams = new LoadableButtonLayout.LayoutParams(12, 12);
        d.layoutParams.offsetHorizontal = 6;
        d.layoutParams.offsetVertical = 6;
        d.inputConfiguration = new InputConfiguration();
        d.inputConfiguration.movesCursor = false;
        d.inputConfiguration.sticky = true;
        d.sensitivity = 0.014f;
        d.showInGame = true;
        d.showInMenu = false;
        return d;
    }

    @Override
    public View createView(Context context) {
        return new ControlStick(context, this);
    }

    @Override
    public String getType() {
        return ControlStickData.TYPE;
    }
}
