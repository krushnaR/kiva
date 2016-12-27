package com.sapience.kiva.kivafinal;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TalkHistoryFragment extends Fragment {
	
	TextView chatHistory;
	View contentView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		contentView = inflater.inflate(R.layout.talk_history, container);
		chatHistory = (TextView) getActivity().findViewById(R.id.talkHistory);
		return contentView;
	}

	public void updateChat(String addToChat, boolean isRobot){
		chatHistory.append("\n");
		if(isRobot)
			chatHistory.append("Kiva: ");
		else
			chatHistory.append("Me: ");
		
		chatHistory.append(addToChat);
	}
}
