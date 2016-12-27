package com.sapience.kiva.kivafinal;

public class Temp {
	
	private int posId;
	private int distF;
	private int distR;
	private int total;
	private int targetId;
	
	public Temp(int posId, int total, int targetId) {
		super();
		this.posId = posId;
		this.total = total;
		this.targetId = targetId;
	}

	
	
	public String shortestPath(int posId, int targetId, int total){
		
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
