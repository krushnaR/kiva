package com.sapience.kiva.kivafinal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class PathFinder {
	
	Context context;
	private DbHelper helper;
	
	private int distF;
	private int distR;
	private int total;
	
	public PathFinder(Context context) {
		super();
		this.context = context;
		this.total = DbFeed.TOTAL_QR;
		this.helper = new DbHelper(context);
	}
	
	public String path(String content, String state, String target){
		
		Bundle bundle = helper.getContent(content);
		Bundle targetBundle = helper.getContent(target);
		
		
		int posId = bundle.getInt(DbFeed.COLUMN_ID);
		boolean isCorner = bundle.getBoolean(DbFeed.COLUMN_IS_CORNER);
		String forwardTurn = bundle.getString(DbFeed.COLUMN_FORWARD_TURN);
		String reverseTurn = bundle.getString(DbFeed.COLUMN_REVERSE_TURN);
		
		int targetId = targetBundle.getInt(DbFeed.COLUMN_ID);
		
		
		if(content.equals(target) || isCorner){
			if(state.equals("forward"))
				return forwardTurn;
			else
				return reverseTurn;
		}
		else{
			
			if(posId > targetId){
				distF = total - posId + targetId;
				distR = posId - targetId;
			}
			else{
				distF = targetId - posId;
				distR = total - targetId + posId;
			}
			
			if(distF < distR)
				return "forward";
			else
				return "reverse";
		}
	}

}
