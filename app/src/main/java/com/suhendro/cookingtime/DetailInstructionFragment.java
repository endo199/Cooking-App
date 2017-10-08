package com.suhendro.cookingtime;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.suhendro.cookingtime.model.CookingStep;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Suhendro on 9/4/2017.
 */

public class DetailInstructionFragment extends Fragment {
    public static final String STEP_POSITION = "STEP_POSITION";
    public static final String PLAY_POSITION = "PLAY_POSITION";
    private static final String WINDOW_INDEX = "WINDOW_INDEX";

    @BindView(R.id.vdo_instruction)
    SimpleExoPlayerView mVideo;
    @BindView(R.id.tv_cooking_description)
    TextView mCookingDescription;
    @BindView(R.id.btn_next_instruction)
    Button mNextInstruction;
    @BindView(R.id.btn_prev_instruction)
    Button mPrevInstruction;

    private Unbinder unbinder;
    private CookingStep[] mListInstruction;
    private int mCurrentStep;
    private SimpleExoPlayer mPlayer;

    private DataSource.Factory datasourceFactory;
    private ExtractorsFactory extractor;
    private long position;
    private DefaultTrackSelector trackSelector;
    private int window;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("XXX onCreateView");
        View view = inflater.inflate(R.layout.detail_instruction_fragment, container, false);

        unbinder = ButterKnife.bind(this, view);

        initPlayer();
        window = C.INDEX_UNSET;
        position = C.TIME_UNSET;

        return view;
    }

    private void initPlayer() {
        Timber.d("XXX initPlayer");
        // preparing exo player
        trackSelector = new DefaultTrackSelector();
        mPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        mVideo.setPlayer(mPlayer);

        String userAgent = Util.getUserAgent(getContext(), "cook");
        TransferListener<? super DataSource> bandwidthMeter = new DefaultBandwidthMeter();
        datasourceFactory = new DefaultDataSourceFactory(getContext(), userAgent, bandwidthMeter);
        extractor = new DefaultExtractorsFactory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Timber.d("XXX onActivityCreated");
        super.onActivityCreated(savedInstanceState);

//        initPlayer();

        Timber.d("XXX there is saved instance state? %s", (savedInstanceState != null));

        if(savedInstanceState != null) {
            position = savedInstanceState.getLong(PLAY_POSITION, C.TIME_UNSET);
            window = savedInstanceState.getInt(WINDOW_INDEX, C.INDEX_UNSET);
            Timber.d("XXX last saved position %d", position);

            mCurrentStep = savedInstanceState.getInt(STEP_POSITION, 0);
            mListInstruction = (CookingStep[]) savedInstanceState.getParcelableArray("list");
            this.setStep(mCurrentStep);
        }
    }

    @Override
    public void onPause() {
        Timber.d("XXX onPause");
        super.onPause();
        if(mPlayer != null) {
            position = mPlayer.getCurrentPosition();
            window = mPlayer.getCurrentWindowIndex();

            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onResume() {
        Timber.d("XXX onResume");
        if(mPlayer == null) {
            initPlayer();
        }
        Timber.d("XXX selected step %d", this.mCurrentStep);
        setStep(this.mCurrentStep);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Timber.d("XXX onSaveInstanceState");
        super.onSaveInstanceState(outState);

//        long playPosition = mPlayer.getCurrentPosition();
        Timber.d("XXX player current position: %d", position);

        outState.putLong(PLAY_POSITION, position);
        outState.putLong(WINDOW_INDEX, window);
        outState.putInt(STEP_POSITION, mCurrentStep);

        outState.putParcelableArray("list", mListInstruction);
    }

    @OnClick(R.id.btn_next_instruction)
    public void nextInstruction(View view) {
        position = C.TIME_UNSET;
        displayInstruction(mCurrentStep + 1);
    }

    @OnClick(R.id.btn_prev_instruction)
    public void prevInstruction(View view) {
        position = C.TIME_UNSET;
        displayInstruction(mCurrentStep - 1);
    }

    public void setInstructions(CookingStep[] instructions, int step) {
        if(instructions == null) {
            Timber.e("XXX No Cooking instruction provided");
            return;
        }

        mListInstruction = instructions;
        displayInstruction(step);
    }

    public void setInstructions(CookingStep[] instructions) {
        if(instructions == null) {
            Timber.e("XXX No Cooking instruction provided");
            return;
        }

        mListInstruction = instructions;
    }

    public void setStep(int step) {
        displayInstruction(step);
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    protected void displayVideo(Uri uri) {
        Timber.d("XXX display video %s", uri.toString());
        if(mPlayer == null) {
            Timber.d("XXX player is null, don't execute request");
            return;
        }

        MediaSource mediaSource = new ExtractorMediaSource(uri, datasourceFactory, extractor, null, null);

        boolean isResume = window != C.INDEX_UNSET;

        mPlayer.prepare(mediaSource, !isResume, false);
        Timber.d("XXX seek to position %d", position);
        mPlayer.seekTo(position);
        mPlayer.setPlayWhenReady(true);
    }

    protected void displayInstruction(int step) {
        Timber.d("XXX displayInstruction %d", step);
        if(step < 0)
            step = 0;

        CookingStep inst = mListInstruction[step];
        mCookingDescription.setText(inst.getDescription());
        if(inst.getVideoUrl() != null && inst.getVideoUrl().length() > 0) {
            mVideo.setVisibility(View.VISIBLE);
            displayVideo(Uri.parse(inst.getVideoUrl()));
        } else {
            mVideo.setVisibility(View.GONE);
        }

        mCurrentStep = step;
        mPrevInstruction.setEnabled(mCurrentStep > 0);
        mNextInstruction.setEnabled(mCurrentStep < mListInstruction.length - 1);
    }
}
