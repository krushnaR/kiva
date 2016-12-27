package com.sapience.kiva.kivafinal;


public class DbFeed {
	public static final String TABLE_NAME = "qrcodes";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_QR_CONTENT = "content";
	public static final String COLUMN_IS_CORNER = "isCorner";
	public static final String COLUMN_FORWARD_TURN = "forwardTurn";
	public static final String COLUMN_REVERSE_TURN = "reverseTurn";
	
	public static final int TOTAL_QR = 8;
	
	//row0
	public static final String ROW0_CONTENT = "401";
	public static final int ROW0_IS_CORNER = 0;
	public static final String ROW0_FORWARD_TURN = "left";
	public static final String ROW0_REVERSE_TURN = "right";
	
	//row1
	public static final String ROW1_CONTENT = "corner1";
	public static final int ROW1_IS_CORNER = 1;
	public static final String ROW1_FORWARD_TURN = "right";
	public static final String ROW1_REVERSE_TURN = "left";
	
	//row2
	public static final String ROW2_CONTENT = "402";
	public static final int ROW2_IS_CORNER = 0;
	public static final String ROW2_FORWARD_TURN = "right";
	public static final String ROW2_REVERSE_TURN = "left";
	
	//row3
	public static final String ROW3_CONTENT = "corner2";
	public static final int ROW3_IS_CORNER = 1;
	public static final String ROW3_FORWARD_TURN = "right";
	public static final String ROW3_REVERSE_TURN = "left";
	
	//row4
	public static final String ROW4_CONTENT = "403";
	public static final int ROW4_IS_CORNER = 0;
	public static final String ROW4_FORWARD_TURN = "right";
	public static final String ROW4_REVERSE_TURN = "left";
	
	//row5
	public static final String ROW5_CONTENT = "corner3";
	public static final int ROW5_IS_CORNER = 1;
	public static final String ROW5_FORWARD_TURN = "right";
	public static final String ROW5_REVERSE_TURN = "left";
	
	//row6
	public static final String ROW6_CONTENT = "404";
	public static final int ROW6_IS_CORNER = 0;
	public static final String ROW6_FORWARD_TURN = "right";
	public static final String ROW6_REVERSE_TURN = "left";
	
	//row7
	public static final String ROW7_CONTENT = "corner4";
	public static final int ROW7_IS_CORNER = 1;
	public static final String ROW7_FORWARD_TURN = "right";
	public static final String ROW7_REVERSE_TURN = "left";
	
	//row8
	public static final String ROW8_CONTENT = "destination";
	public static final int ROW8_IS_CORNER = 1;
	public static final String ROW8_FORWARD_TURN = "stop";
	public static final String ROW8_REVERSE_TURN = "stop";
}
