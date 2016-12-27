package com.sapience.kiva.kivafinal;

public class Response {
	
	private final String fine = "I am fine ";
	private final String time = "The time is ";
	private final String yes = "Yes";
	final static String notFound = "Command Not Found. Either the command is wrong or our database needs an upgrade";
	private final String hi = "Hi ";
	private final String good = "Good ";
	private final String morning = "morning";
	private final String afternoon = "afternoon";
	private final String thank = "Thank You";
	private final String help = "How can I help you";
	private final String taskDelivery = "What is the Hall Number";
	private final String taskMsg = "Proceed with your message";
	
	public Response(){
		
	}
	
	public String getResponseForQuestion(int questionNumber){
		String response="";
		
		switch(questionNumber){
		
		case 0:
			response = this.fine + this.thank;
			break;
		case 1:
			response = this.time;
			break;
		case 2:
			response = this.yes;
			break;
		case 3:
			response = this.fine;
			break;
		default:
			response = Response.notFound;
		}
		
		return response;
	}
	
	public String getResponseForGreeting(int greetingNumber){
		String response="";
		
		switch(greetingNumber){
		
		case 0:
			response = this.hi + this.help;
			break;
		case 1:
			response = this.good + this.morning;
			break;
		case 2:
			response = this.good + this.afternoon;
			break;
		case 3:
			response = this.thank;
			break;
		case 4:
			response = this.hi + this.help;
			break;
		default:
			response = Response.notFound;
		}
		
		return response;
	}
	
	public String getResponseForTask(int taskNumber){
		String response="";
		
		switch(taskNumber){
		
		case 0:
			response = this.taskDelivery;
			break;
		case 1:
			response = this.taskDelivery;
			break;
		case 2:
			response = this.taskMsg;
			break;
		default:
			response = Response.notFound;
		}
		
		return response;
	}
	

}
