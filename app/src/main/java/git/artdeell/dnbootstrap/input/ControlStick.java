package git.artdeell.dnbootstrap.input;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import git.artdeell.dnbootstrap.input.editor.LayoutEditorDialog;
import git.artdeell.dnbootstrap.input.editor.LayoutEditable;
import git.artdeell.dnbootstrap.input.editor.StickEditorDialog;
import git.artdeell.dnbootstrap.input.model.ControlStickData;
import git.artdeell.dnbootstrap.input.model.InputConfiguration;
import git.artdeell.dnbootstrap.input.model.VisibilityConfiguration;
import git.artdeell.dnbootstrap.input.model.ViewCreator;
import git.artdeell.dnbootstrap.glfw.GrabListener;
import git.artdeell.dnbootstrap.glfw.KeyCodes;
import git.artdeell.dnbootstrap.glfw.GLFW;

public class ControlStick extends View implements LayoutTouchConsumer, LayoutEditable, Recreatable, GrabListener {
    private final ControlStickData controlStickData;
    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float stickX = 0f;
    private float stickY = 0f;
    private boolean active = false;
    private final android.os.Handler tickHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            if (!active) return;
            if (controlStickData.inputConfiguration.movesCursor) {
                float SENS = controlStickData.sensitivity;
                GLFW.cursorX += stickX * SENS;
                GLFW.cursorY += stickY * SENS;
                GLFW.sendMousePos();
            } else {
                updateMovementKeysFromStick();
            }
            tickHandler.postDelayed(this, 16);
        }
    };

    public ControlStick(@NonNull Context context, ControlStickData data) {
        super(context);
        this.controlStickData = data;
        setLayoutParams(this.controlStickData.layoutParams);
        init();
    }

    public ControlStick(@NonNull Context context) {
        super(context);
        this.controlStickData = new ControlStickData();
        setLayoutParams(this.controlStickData.layoutParams);
        init();
    }

    public ControlStick(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.controlStickData = new ControlStickData();
        setLayoutParams(this.controlStickData.layoutParams);
        init();
    }

    private void init() {
        bgPaint.setColor(0x44FFFFFF);
        thumbPaint.setColor(0xFFFFFFFF);
        setClickable(true);
    }

    private boolean pressingW = false, pressingA = false, pressingS = false, pressingD = false;
    private final float DEADZONE = 0.35f;

    private void sendKey(int key, boolean press) {
        int state = press ? KeyCodes.GLFW_PRESS : KeyCodes.GLFW_RELEASE;
        GLFW.sendKeyEvent(key, state, 0);
    }

    private void releaseMovementKeys() {
        if (pressingW) { sendKey(KeyCodes.GLFW_KEY_W, false); pressingW = false; }
        if (pressingA) { sendKey(KeyCodes.GLFW_KEY_A, false); pressingA = false; }
        if (pressingS) { sendKey(KeyCodes.GLFW_KEY_S, false); pressingS = false; }
        if (pressingD) { sendKey(KeyCodes.GLFW_KEY_D, false); pressingD = false; }
    }

    private void updateMovementKeysFromStick() {
        boolean up = stickY < -DEADZONE;
        boolean down = stickY > DEADZONE;
        boolean left = stickX < -DEADZONE;
        boolean right = stickX > DEADZONE;

        if (up != pressingW) { sendKey(KeyCodes.GLFW_KEY_W, up); pressingW = up; }
        if (down != pressingS) { sendKey(KeyCodes.GLFW_KEY_S, down); pressingS = down; }
        if (left != pressingA) { sendKey(KeyCodes.GLFW_KEY_A, left); pressingA = left; }
        if (right != pressingD) { sendKey(KeyCodes.GLFW_KEY_D, right); pressingD = right; }
    }

    @Override
    public void onTouchState(boolean isTouched) {
        active = isTouched;
        if (active) {
            tickHandler.removeCallbacks(tick);
            tickHandler.post(tick);
        } else {
            tickHandler.removeCallbacks(tick);
            // reset thumb back to center when released
            stickX = 0f; stickY = 0f;
            // release any held movement keys
            releaseMovementKeys();
            post(this::invalidate);
        }
    }

    @Override
    public void onTouchPosition(float x, float y) {
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(cx, cy);
        float nx = (x - cx) / radius;
        float ny = (y - cy) / radius;
        // clamp to unit circle
        float len = (float) Math.sqrt(nx * nx + ny * ny);
        if (len > 1f) {
            nx /= len;
            ny /= len;
        }
        stickX = nx;
        stickY = ny;
        // update movement keys before tick
        if (!controlStickData.inputConfiguration.movesCursor) updateMovementKeysFromStick();
        post(this::invalidate);
    }

    @Override
    public View fullClone() {
        ControlStickData copy = new ControlStickData(this.controlStickData);
        ControlStick s = new ControlStick(getContext(), copy);
        s.setLayoutParams(copy.layoutParams);
        return s;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f;
        float radius = Math.min(cx, cy) * 0.9f;
        canvas.drawCircle(cx, cy, radius, bgPaint);
        float thumbRadius = radius * 0.35f;
        float tx = cx + stickX * (radius - thumbRadius);
        float ty = cy + stickY * (radius - thumbRadius);
        canvas.drawCircle(tx, ty, thumbRadius, thumbPaint);
    }

    @Override
    public LayoutEditorDialog createEditor() {
        return new StickEditorDialog();
    }

    @Override
    public boolean isCompatibleEditor(LayoutEditorDialog editorDialog) {
        return editorDialog instanceof StickEditorDialog;
    }

    @NonNull
    @Override
    public InputConfiguration getInputConfiguration() {
        return controlStickData.inputConfiguration;
    }

    @Override
    public VisibilityConfiguration getVisibilityConfiguration() {
        return controlStickData;
    }

    @NonNull
    @Override
    public ViewCreator getCreator() {
        return controlStickData;
    }

    @Override
    public void onGrabState(boolean isGrabbing) {

    }
}
