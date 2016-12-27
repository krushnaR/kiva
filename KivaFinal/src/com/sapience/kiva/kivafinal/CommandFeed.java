package com.sapience.kiva.kivafinal;

public class CommandFeed {
	
	
	private final String[] greetings = {"hi","good morning","good afternoon","good job","hello"};
	private final String[] questions = {"how are you","what time is it","ready","what is your status"};
	private final String[] task = {"delivery for","deliver to","tell"};
	
	public CommandFeed(){
		
	}
	
	
	
	public int getQuestionNumber(String str){
		int i;
		
		for(i=0;i<questions.length;i++){
			if(questions[i].length() == str.length()){
				if(questions[i].equals(str)){
					return i;
				}
			}
		}
		return -1;
	}
	
	public int getGreetingNumber(String str){
		int i;
		
		for(i=0;i<greetings.length;i++){
			if(greetings[i].length() == str.length()){
				if(greetings[i].equals(str)){
					return i;
				}
			}
		}
		return -1;
	}
	
	public int getTaskNumber(String str){
		int i;
		
		for(i=0;i<task.length;i++){
			if(task[i].length() == str.length()){
				if(task[i].equals(str)){
					return i;
				}
			}
		}
		return -1;
	}

}
