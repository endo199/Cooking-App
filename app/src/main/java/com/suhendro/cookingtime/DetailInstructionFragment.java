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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_instruction_fragment, container, false);

        unbinder = ButterKnife.bind(this, view);

        // preparing exo player
        TrackSelector trackSelector = new DefaultTrackSelector();
        mPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        mVideo.setPlayer(mPlayer);

        String userAgent = Util.getUserAgent(getContext(), "cook");
        TransferListener<? super DataSource> bandwidthMeter = new DefaultBandwidthMeter();
        datasourceFactory = new DefaultDataSourceFactory(getContext(), userAgent, bandwidthMeter);
        extractor = new DefaultExtractorsFactory();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            mCurrentStep = savedInstanceState.getInt(STEP_POSITION, 0);
            mListInstruction = (CookingStep[]) savedInstanceState.getParcelableArray("list");
            this.setStep(mCurrentStep);

            long playPosition = savedInstanceState.getLong(PLAY_POSITION, 0);
            mPlayer.seekTo(playPosition);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayer.release();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        long playPosition = mPlayer.getCurrentPosition();
        outState.putLong(PLAY_POSITION, playPosition);
        outState.putInt(STEP_POSITION, mCurrentStep);

        outState.putParcelableArray("list", mListInstruction);
    }

    @OnClick(R.id.btn_next_instruction)
    public void nextInstruction(View view) {
        displayInstruction(mCurrentStep + 1);
    }

    @OnClick(R.id.btn_prev_instruction)
    public void prevInstruction(View view) {
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
        MediaSource mediaSource = new ExtractorMediaSource(uri, datasourceFactory, extractor, null, null);
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);

    }

    protected void displayInstruction(int step) {
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
