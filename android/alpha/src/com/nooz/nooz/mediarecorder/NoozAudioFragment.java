package com.nooz.nooz.mediarecorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nooz.nooz.R;
import com.nooz.nooz.widget.AudioIntensityFeedback;

public class NoozAudioFragment extends Fragment {

	private AudioIntensityFeedback mViewAudioIntensityFeedback;

	public static NoozAudioFragment newInstance() {
		return new NoozAudioFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_audio_recorder, container, false);
		mViewAudioIntensityFeedback = (AudioIntensityFeedback) view.findViewById(R.id.audio_feedback);
		return view;
	}

	public void reportAmplitude(final int amplitude) {
		mViewAudioIntensityFeedback.post(new Runnable() {
			@Override
			public void run() {
				mViewAudioIntensityFeedback.reportAmplitude(amplitude);
			}
		});
	}
}
