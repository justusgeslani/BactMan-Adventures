package fr.plnech.igem.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import fr.plnech.igem.R;
import io.fabric.sdk.android.Fabric;

public class HomeActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "HomeActivity";
    private MediaPlayer mPlayer;

    @InjectView(R.id.videoView)
    protected SurfaceView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFabric(this);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        SurfaceHolder holder = videoView.getHolder();
        holder.addCallback(this);
    }


    @OnClick(R.id.button_home_new_game)
    protected void onClickNewGame() {
        startActivity(new Intent(this, GameMenuActivity.class));
    }

    @OnClick(R.id.button_home_team)
    protected void onClickTeam() {
        startActivity(new Intent(this, UsActivity.class));
    }

    @OnClick(R.id.button_home_iGEM)
    protected void onClickIGEM() {
        startActivity(new Intent(this, iGEMActivity.class));
    }

    @OnClick(R.id.button_home_credits)
    protected void onClickCredits() {
        startActivity(new Intent(this, CreditsActivity.class));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        final Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int screenWidth;

        if (Build.VERSION.SDK_INT >= 13) {
            Point screenSize = new Point();
            defaultDisplay.getSize(screenSize);
            screenWidth = screenSize.x;
        } else {
            //noinspection deprecation
            screenWidth = defaultDisplay.getWidth();
        }

        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        lp.width = screenWidth;
        lp.height = (int) (((float) videoView.getHeight() / (float) videoView.getWidth()) * (float) screenWidth);
        videoView.setLayoutParams(lp);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animation);
        mPlayer = MediaPlayer.create(getApplicationContext(), uri, holder);
        mPlayer.setLooping(true);
        mPlayer.start();
        Log.d(TAG, "onCreate - MediaPlayer started.");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed - Destroying MediaPlayer.");
        mPlayer.release();
    }
    public static void initFabric(Context context) {
        /* Disabled on DEBUG builds */
//        final CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
//        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());
        /* Normal */
        Fabric.with(context, new Crashlytics());
        /* Debug mode */
//        final Fabric fabric = new Fabric.Builder(this)
//                .kits(new Crashlytics())
//                .debuggable(true)
//                .build();
//        Fabric.with(fabric);
    }
}