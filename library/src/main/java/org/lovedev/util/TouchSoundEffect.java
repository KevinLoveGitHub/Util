package org.lovedev.util;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.provider.Settings;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Kevin
 * @date 2018/5/17
 */
public class TouchSoundEffect {

    private TouchSoundEffect() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void disTouchEffectAndSaveState(Context context) {
        SharedPreferences setting = context.getSharedPreferences("SystemSetting", MODE_PRIVATE);
        int mSoundEffect = setting.getInt("SOUND_EFFECTS_ENABLED", -1);

        if (mSoundEffect == -1) {
            SharedPreferences.Editor editor = setting.edit();
            mSoundEffect = Settings.System.getInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);

            editor.putInt("SOUND_EFFECTS_ENABLED", mSoundEffect);
            editor.apply();
        }


        AudioManager mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.unloadSoundEffects();
            Settings.System.putInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
        }
    }

    public static void recoveryTouchEffect(Context context) {
        SharedPreferences setting = context.getSharedPreferences("SystemSetting", MODE_PRIVATE);
        int mSoundEffect = setting.getInt("SOUND_EFFECTS_ENABLED", 0);
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, mSoundEffect);
        if (mAudioManager != null) {
            mAudioManager.loadSoundEffects();
        }
    }
}
