package model;

import static model.AbstractState.MOVE.DOWN;
import static model.AbstractState.MOVE.LEFT;
import static model.AbstractState.MOVE.RIGHT;
import static model.AbstractState.MOVE.UP;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.AbstractState.MOVE;
public class LongInfo {
	
	
	
	public static final double 
				smoothWeight = 0.2,
				monoWeight   = 5.0,
				emptyWeight  = 2.7,
				maxWeight    = 1.0,
				scoreWeight  = 0.0,
				cornerWeight = 0.0;
	
	private static Random rng = new Random();
	private static final int[][] SHAMT = new int[][]{
		{60,56,52,48},
		{44,40,36,32},
		{28,24,20,16},
		{12,8, 4, 0}};
	
	private static final int[] pW = new int[]{0,7,8,15,1,6,9,14,2,5,10,13,3,4,11,12};
	private static final int[] pM = new int[]{3,4,11,12,2,5,10,13,1,6,9,14,0,7,8,15};
	private static final int[] pE = new int[]{0,1,2,3,7,6,5,4,8,9,10,11,15,14,13,12};
	private static final int[] p3 = new int[]{3,2,1,0,4,5,6,7,11,10,9,8,12,13,14,15};
	
	private static final long[] masks = new long[] {
		0x0000000000000000L,
		0xF000000000000000L,
		0xFF00000000000000L,
		0xFFF0000000000000L,
		0xFFFF000000000000L,
		0xFFFFF00000000000L,
		0xFFFFFF0000000000L,
		0xFFFFFFF000000000L,
		0xFFFFFFFF00000000L,
		0xFFFFFFFFF0000000L,
		0xFFFFFFFFFF000000L,
		0xFFFFFFFFFFF00000L,
		0xFFFFFFFFFFFF0000L,
		0xFFFFFFFFFFFFF000L,
		0xFFFFFFFFFFFFFF00L,
		0xFFFFFFFFFFFFFFF0L,
		0xFFFFFFFFFFFFFFFFL,
	};
	private static final Point[] LR = new Point[]{
		new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3), 
		new Point(1,3), new Point(1,2), new Point(1,1), new Point(1,0), 
		new Point(2,0), new Point(2,1), new Point(2,2), new Point(2,3), 
		new Point(3,3), new Point(3,2), new Point(3,1), new Point(3,0)
	};
	
	private static final Point[] RL = new Point[]{
		new Point(0,3), new Point(0,2), new Point(0,1), new Point(0,0), 
		new Point(1,0), new Point(1,1), new Point(1,2), new Point(1,3), 
		new Point(2,3), new Point(2,2), new Point(2,1), new Point(2,0), 
		new Point(3,0), new Point(3,1), new Point(3,2), new Point(3,3)
	};
	
	private static final Point[] UD = new Point[]{
		new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0), 
		new Point(3,1), new Point(2,1), new Point(1,1), new Point(0,1), 
		new Point(0,2), new Point(1,2), new Point(2,2), new Point(3,2), 
		new Point(3,3), new Point(2,3), new Point(1,3), new Point(0,3)
	};
	
	private static final Point[] DU = new Point[]{
		new Point(3,0), new Point(2,0), new Point(1,0), new Point(0,0), 
		new Point(0,1), new Point(1,1), new Point(2,1), new Point(3,1), 
		new Point(3,2), new Point(2,2), new Point(1,2), new Point(0,2), 
		new Point(0,3), new Point(1,3), new Point(2,3), new Point(3,3)
	};
	
	public static double eval4(long state) {
		int e = 0;
		int highestTile = -1;
		int htp = 0;
		boolean isCorner = false;
//		int m1 = 0, m2 = 0, m3 = 0, m4 =0;
//		long s1 = snake1(state);
//		long s2 = snake2(state);
//		long s3 = transpose(s1);
//		long s4 = transpose(s2);
		int score = 0;
		for(int i = 0 ; i < 16 ; i++) {
//			if(i<15) {
//				m1 -= Math.abs(((s1>>(i*4)) & 0xF) - ((s1>>((i+1)*4)) & 0xF));
//				m2 -= Math.abs(((s2>>(i*4)) & 0xF) - ((s2>>((i+1)*4)) & 0xF));
//				m3 -= Math.abs(((s3>>(i*4)) & 0xF) - ((s3>>((i+1)*4)) & 0xF));
//				m4 -= Math.abs(((s4>>(i*4)) & 0xF) - ((s4>>((i+1)*4)) & 0xF));				
//			}
			int v = (int) ((state >> (i*4)) & 0xF);
			if(v == 0) {
				e++;
			} else {
				score += (v-1)*(1<<v);
				if (v > highestTile) {
					highestTile = v;
					htp = i;
				}
			}
		}
		isCorner = htp == 0 || htp == 3 || htp == 12 || htp == 15;
		byte[] tiles = split1D(state);
		//One or the other, or both?
//		int mono =  Math.max(Math.max(m1, m2), Math.max(m3, m4));
		
		int mono = 0;
		int smoothness = 0;
		
		if (htp == 15 || htp == 14 || htp == 11 || htp == 10) {
			//Top left
			byte[] mWtiles = new byte[8];
			byte[] m3tiles = new byte[8];
			for(int i = 0 ; i < 8 ; i++) {
				mWtiles[i] = tiles[pW[i]];
				m3tiles[i] = tiles[p3[i]];
			}
			
			int monoW = mono(mWtiles);
			int mono3 = mono(m3tiles);
			if(monoW > mono3) {
				mono = monoW;
				char[] rows = splitIntoRows(state);
				smoothness = smoothness(new char[]{rows[1],rows[2],rows[3]});
			} else if (mono3 > monoW) {
				long stateT = transpose(state);
				char[] rows = splitIntoRows(stateT);
				smoothness = smoothness(new char[]{rows[1],rows[2],rows[3]});
			} else {
				mono = monoW;
				char[] rows = splitIntoRows(state);
				long stateT = transpose(state);
				char[] rowsT = splitIntoRows(stateT);
				int sW = smoothness(new char[]{rows[1],rows[2],rows[3]});
				int s3 = smoothness(new char[]{rowsT[1],rowsT[2],rowsT[3]});
				smoothness = Math.max(sW, s3);
			}
		} else if(htp == 13 || htp == 12 || htp == 9 || htp == 8) {
			//Top right
			byte[] mWtiles = new byte[8];
			byte[] mEtiles = new byte[8];
			for(int i = 0 ; i < 8 ; i++) {
				mWtiles[i] = tiles[pW[i]];
				mEtiles[i] = tiles[pE[i]];
			}
			
			int monoW = mono(mWtiles);
			int monoE = mono(mEtiles);
			if(monoW > monoE) {
				mono = monoW;
				char[] rows = splitIntoRows(state);
				smoothness = smoothness(new char[]{rows[1],rows[2],rows[3]});
			} else if (monoE > monoW) {
				long stateT = transpose(state);
				char[] rows = splitIntoRows(stateT);
				smoothness = smoothness(new char[]{rows[2],rows[1],rows[0]});
			} else {
				mono = monoW;
				char[] rows = splitIntoRows(state);
				long stateT = transpose(state);
				char[] rowsT = splitIntoRows(stateT);
				int sW = smoothness(new char[]{rows[1],rows[2],rows[3]});
				int sE = smoothness(new char[]{rowsT[2],rowsT[1],rowsT[0]});
				smoothness = Math.max(sW, sE);
			}
		} else if(htp == 7  || htp == 6  || htp == 3 || htp == 2) {
			//Bottom left
			byte[] mMtiles = new byte[8];
			byte[] m3tiles = new byte[8];
			for(int i = 0 ; i < 8 ; i++) {
				mMtiles[i] = tiles[pM[i]];
				m3tiles[i] = tiles[p3[i]];
			}
			
			int monoM = mono(mMtiles);
			int mono3 = mono(m3tiles);
			if(monoM > mono3) {
				mono = monoM;
				char[] rows = splitIntoRows(state);
				smoothness = smoothness(new char[]{rows[2],rows[1],rows[0]});
			} else if (mono3 > monoM) {
				long stateT = transpose(state);
				char[] rows = splitIntoRows(stateT);
				smoothness = smoothness(new char[]{rows[1],rows[2],rows[3]});
			} else {
				mono = monoM;
				char[] rows = splitIntoRows(state);
				long stateT = transpose(state);
				char[] rowsT = splitIntoRows(stateT);
				int sM = smoothness(new char[]{rows[2],rows[1],rows[0]});
				int s3 = smoothness(new char[]{rowsT[1],rowsT[2],rowsT[3]});
				smoothness = Math.max(sM, s3);
			}
		} else {
			//Bottom right
			byte[] mMtiles = new byte[8];
			byte[] mEtiles = new byte[8];
			for(int i = 0 ; i < 8 ; i++) {
				mMtiles[i] = tiles[pM[i]];
				mEtiles[i] = tiles[pE[i]];
			}
			
			int monoM = mono(mMtiles);
			int monoE = mono(mEtiles);
			if(monoM > monoE) {
				mono = monoM;
				char[] rows = splitIntoRows(state);
				smoothness = smoothness(new char[]{rows[2],rows[1],rows[0]});
			} else if (monoE > monoM) {
				long stateT = transpose(state);
				char[] rows = splitIntoRows(stateT);
				smoothness = smoothness(new char[]{rows[2],rows[1],rows[0]});
			} else {
				mono = monoM;
				char[] rows = splitIntoRows(state);
				long stateT = transpose(state);
				char[] rowsT = splitIntoRows(stateT);
				int sM = smoothness(new char[]{rows[2],rows[1],rows[0]});
				int sE = smoothness(new char[]{rowsT[2],rowsT[1],rowsT[0]});
				smoothness = Math.max(sM, sE);
			}
		}
//		System.out.println(mono + " " + smoothness + " " + e + " " + isCorner + " " + highestTile + " " + score);
		double eval = 
				  smoothness 			* smoothWeight 
				+ mono       			* monoWeight
				+ Math.log(e) 			* emptyWeight 
				+ highestTile			* maxWeight
				+ score 				* scoreWeight
				+ (isCorner ? 10000 : 0);
		return eval; 
	}

	private static int smoothness(char[] cs) {
		byte[][] cells = new byte[][]{splitChar1D(cs[0]), split1D(cs[1]), split1D(cs[2])};
//		System.out.println("Smoothness:");
//		print(cells);
//		System.out.println();
		int smoothness = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 4; x++) {
				if(cells[y][x] > 0) {
					int value = cells[y][x];
					int vRight = valueRight(cells, x+1, y);
					int vDown = valueDown(cells, x, y+1);
					if(vRight > 0) 
						smoothness -= Math.abs(value - vRight);
					if(vDown > 0)
						smoothness -= Math.abs(value - vDown);
				}
			}
		}
		return smoothness;
	}

	private static int mono(byte[] tiles) {
		return -monoR(0, tiles[0], tiles);
	}
	
	private static int monoR(int i, byte v, byte[] tiles) {
		if(tiles.length == i) {
			return 0;
		}
		if(tiles[i] == 0) return monoR(i+1, v, tiles);
		return Math.abs(v - tiles[i]) + monoR(i+1, tiles[i], tiles);
	}

	public static double eval5(long state) {
		int highestTile = 0;
		int e = 0;
		boolean isCorner = false;
		for(int i = 0 ; i < 16 ; i++) {
			int v = (int) ((state >> (i*4)) & 0xF);
			if(v == 0) {
				e++;
			} else if (v > highestTile) {
				highestTile = v;
				isCorner = i == 0 || i == 3 || i == 12 || i == 15;
			}
		}
		return highestTile + Math.log(e) * 3 + (isCorner ? 10000 : 0);
	}

//	public static void main(String[] args) {
//		long state = 0xcb11da31e850f761L;
//		System.out.println(approxScore(state));
//		char[] chars = splitIntoRows(state);
//		print(chars);
//		System.out.println();
//		byte[][] tiles = new byte[][]{splitChar1D(chars[0]),splitChar1D(chars[1]),splitChar1D(chars[2]),splitChar1D(chars[3])};
//		print(tiles);
//		System.out.println();
////		eval4(state);
//		print(state);
////		char[] tiles = split(state);
////		long s2 = unsplit(tiles);
////		print(s2);
////		long s3 = slide(s2, MOVE.LEFT);
////		print(s3);
////		s3 = addRandomTile(s3);
////		print(s3);
//	}
	
	private static void print(char[] chars) {
		for(int y = 0 ; y < chars.length ; y++) {
			System.out.println(Integer.toHexString(chars[y]));
		}
	}

	private static long scooch(long state) {
		for(int i = 0 ; i < 16 ; i++) {
			int v = (int) (state >> ((15-i)*4)) & 0xF;
			if(v==0) {
				state = (state & masks[i]) | ((state & ~masks[i]) << 4);
				if ((state & ~masks[i]) == 0) return state;
				i--;
			}
		}
		return state;
	}

	public static void print(long state) {
		for(int y = 3 ; y >= 0 ; y--) {
			char val = (char)((state >> (y * 16)&0xFFFF));
			System.out.println(hexString(val));
		}
		System.out.println();
	}
	
	private static void print(byte[][] grid) {
		System.out.println(grid.length + " " + grid[0].length);
		for(int y = 0 ; y < grid.length ; y++) {
			for(int x = 0 ; x < 4 ; x++) {
				System.out.print(Integer.toHexString(grid[y][x]));
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static String hexString(char value) {
		int width = 4;
		String hex = Long.toHexString(value);
		StringBuilder sb = new StringBuilder();
		while (hex.length() < width) {
			sb.append('0');
			width--;
		}
		sb.append(hex);
		return sb.toString();
	}

	public static long slide(long state, MOVE dir) {
		char[] col1, col2, col3, col4, tiles = split(state);
//		char[] next = new char[16];
		switch (dir) {
		case UP:
			col1 = slide(tiles[15], tiles[11], tiles[7], tiles[3]);
			col2 = slide(tiles[14], tiles[10], tiles[6], tiles[2]);
			col3 = slide(tiles[13], tiles[9],  tiles[5], tiles[1]);
			col4 = slide(tiles[12], tiles[8],  tiles[4], tiles[0]);
			tiles = new char[]{
					col4[3], col3[3], col2[3], col1[3],
					col4[2], col3[2], col2[2], col1[2],
					col4[1], col3[1], col2[1], col1[1],
					col4[0], col3[0], col2[0], col1[0]};
			return unsplit(tiles);
		case DOWN:
			col1 = slide(tiles[3], tiles[7], tiles[11], tiles[15]);
			col2 = slide(tiles[2], tiles[6], tiles[10], tiles[14]);
			col3 = slide(tiles[1], tiles[5],  tiles[9], tiles[13]);
			col4 = slide(tiles[0], tiles[4],  tiles[8], tiles[12]);
			tiles = new char[]{
					col4[0], col3[0], col2[0], col1[0],
					col4[1], col3[1], col2[1], col1[1],
					col4[2], col3[2], col2[2], col1[2],
					col4[3], col3[3], col2[3], col1[3]};
			return unsplit(tiles);
		case RIGHT:
			col1 = slide(tiles[3],  tiles[2],  tiles[1],  tiles[0]);
			col2 = slide(tiles[7],  tiles[6],  tiles[5],  tiles[4]);
			col3 = slide(tiles[11], tiles[10], tiles[9],  tiles[8]);
			col4 = slide(tiles[15], tiles[14], tiles[13], tiles[12]);
			tiles = new char[]{
					col1[3], col1[2], col1[1], col1[0],
					col2[3], col2[2], col2[1], col2[0],
					col3[3], col3[2], col3[1], col3[0],
					col4[3], col4[2], col4[1], col4[0]};
			return unsplit(tiles);
		case LEFT:
			col1 = slide(tiles[0],  tiles[1],  tiles[2],  tiles[3]);
			col2 = slide(tiles[4],  tiles[5],  tiles[6],  tiles[7]);
			col3 = slide(tiles[8],  tiles[9],  tiles[10], tiles[11]);
			col4 = slide(tiles[12], tiles[13], tiles[14], tiles[15]);
			tiles = new char[]{
					col1[0], col1[1], col1[2], col1[3],
					col2[0], col2[1], col2[2], col2[3],
					col3[0], col3[1], col3[2], col3[3],
					col4[0], col4[1], col4[2], col4[3]};
			return unsplit(tiles);
		}
		return -1;
	}

	public static char[] split(long state) {
		char[] tiles = new char[16];
		tiles[15] = (char)(state & 0xF);
		for(int i = 14 ; i >= 0 ; i--) {
			state >>= 4;
			tiles[i] = (char)(state & 0xF);
		}
		return tiles;
	}
	
	private static char[] splitIntoRows(long state) {
		char[] rows = new char[4];
		for (int i =0 ; i < 4 ; i++) {
//			System.out.println(i<<4);
//			System.out.println(Integer.toHexString((int) ((state >> (i<<4)) & 0xFFFF)));
			rows[3-i] = (char) ((state >> (i<<4)) & 0xFFFF);
		}
		return rows;
	}
	
	public static long unsplit(char[] tiles) {
		long state = tiles[0];
		for(int i = 1 ; i < 16 ; i++) {
			state <<= 4;
			state |= tiles[i];
		}
		return state;
	}
	
	private static char[] slide(char a, char b, char c, char d) {
		if (a + b + c + d == 0) return new char[]{a,b,c,d};
		for(int i = 0 ; d == 0 && i < 3 ; i++) {
			d = c;
			c = b;
			b = a;
			a = 0;
		}
		for(int i = 0 ; c == 0 && i < 2 ; i++) {
			c = b;
			b = a;
			a = 0;
		}
		if (b == 0) {
			b = a;
			a = 0;
		}
		if(c == d && d < 0xF) {
			d++;
			c = b;
			b = a;
			a = 0;
		}
		if(a + b + c == 0) return new char[]{a,b,c,d};
		if(c == 0) {
			c = b;
			b = a;
			a = 0;
		}
		if(b == c && c < 0xF) {
			c++;
			b = a;
			a = 0;
		}
		if(a + b == 0) return new char[]{a,b,c,d};
		if(b == 0) {
			b = a;
			a = 0;
		}
		if(a == b && b < 0xF) {
			b++;
			a = 0;
		}
		return new char[]{a,b,c,d};
	}
	
	public static int emptyCells(byte[][] cells) {
		int count = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if(cells[y][x] == 0) {
					count++;
				}
			}
		}
		return count;
	}
	
	public static int mono(byte[][] cells) {
		int[] totals = new int[] { 0, 0, 0, 0 };

		for (int y = 0; y < 4; y++) {
			int current = 0;
			int next = current + 1;
			while (next < 4) {
				while (next < 4 &&  cells[y][next] > 0) {
					next++;
				}
				if (next >= 4) {
					next--;
				}
				int currentValue = cells[y][current];
				int nextValue = cells[y][next];
				if (currentValue > nextValue) {
					totals[0] += nextValue - currentValue;
				} else if (nextValue > currentValue) {
					totals[1] += currentValue - nextValue;
				}
				current = next;
				next++;
			}
		}

		for (int x = 0; x < 4; x++) {
			int current = 0;
			int next = current + 1;
			while (next < 4) {
				while (next < 4 &&  cells[next][x] > 0) {
					next++;
				}
				if (next >= 4) {
					next--;
				}
				int currentValue = cells[current][x];
				int nextValue = cells[next][x];
				if (currentValue > nextValue) {
					totals[2] += nextValue - currentValue;
				} else if (nextValue > currentValue) {
					totals[3] += currentValue - nextValue;
				}
				current = next;
				next++;
			}
		}

		return Math.max(totals[0], totals[1]) + Math.max(totals[2], totals[3]);

	}
	
	public static int reward(long state) {
		byte[][] board = split2D(state);
		int highest = -1;
		Point hp = null;
		for(Point p : new Point[]{new Point(0,0),new Point(0,3),new Point(3,3),new Point(3,0)}) {
			if(board[p.y][p.x] > highest) {
				highest = board[p.y][p.x];
				hp = p;
			}
		}
		System.out.println(hp + " " + board[hp.x][hp.y]);
		int reward = 0;
		if(hp.x == 0) {
			reward += monoVal(board[0][0], board[0][1], board[0][2], board[0][3]);
			reward += monoVal(board[1][0], board[1][1], board[1][2], board[1][3]);
			reward += monoVal(board[2][0], board[2][1], board[2][2], board[2][3]);
			reward += monoVal(board[3][0], board[3][1], board[3][2], board[3][3]);
		} else {
			reward += monoVal(board[0][3], board[0][2], board[0][1], board[0][0]);
			reward += monoVal(board[1][3], board[1][2], board[1][1], board[1][0]);
			reward += monoVal(board[2][3], board[2][2], board[2][1], board[2][0]);
			reward += monoVal(board[3][3], board[3][2], board[3][1], board[3][0]);
		}
		if(hp.y == 0) {
			reward += monoVal(board[0][0], board[1][0], board[2][0], board[3][0]);
			reward += monoVal(board[0][1], board[1][1], board[2][1], board[3][1]);
			reward += monoVal(board[0][2], board[1][2], board[2][2], board[3][2]);
			reward += monoVal(board[0][3], board[1][3], board[2][3], board[3][3]);
		} else {
			reward += monoVal(board[3][0], board[2][0], board[1][0], board[0][0]);
			reward += monoVal(board[3][1], board[2][1], board[1][1], board[0][1]);
			reward += monoVal(board[3][2], board[2][2], board[1][2], board[0][2]);
			reward += monoVal(board[3][3], board[2][3], board[1][3], board[0][3]);
		}
		return reward;
	}
	
	public static int monotonicity(long state) {
		byte[][] board = split2D(state);
		int result = 1;
		for(int i = 0 ; i < board.length ; i++) {
			result += isMono(board[i]) ? 1 : 0;
			result += isMono(new byte[]{board[0][i],board[1][i],board[2][i],board[3][i]}) ? 1 : 0;
		}
		return result;
	}
	
	public static int monotonicity2(long state) {
		byte[][] board = split2D(state);
		int result = 1;
		for(int i = 0 ; i < board.length ; i++) {
			result += isMono(board[i]) ? 1 : 0;
			result += isMono(new byte[]{board[0][i],board[1][i],board[2][i],board[3][i]}) ? 1 : 0;
		}
		return result;
	}
	
	private static boolean isMono(byte[] tiles) {
		return allEqual(tiles) || allInc(tiles) || allDec(tiles);
	}

	private static boolean allDec(byte[] tiles) {
		if(tiles.length < 2) return true;
		if(tiles[0] == 0) return allDec(Arrays.copyOfRange(tiles, 1, tiles.length));
		int i = 1;
		while(tiles[i]==0) {
			i++;
			if(i==tiles.length) return true;
		}
		if(tiles[0]>=tiles[i]) return allDec(Arrays.copyOfRange(tiles, i, tiles.length));
		return false;
	}

	private static boolean allInc(byte[] tiles) {
		if(tiles.length < 2) return true;
		if(tiles[0] == 0) return allInc(Arrays.copyOfRange(tiles, 1, tiles.length));
		int i = 1;
		while(tiles[i]==0) {
			i++;
			if(i==tiles.length) return true;
		}
		if(tiles[0]<=tiles[i]) return allInc(Arrays.copyOfRange(tiles, i, tiles.length));
		return false;
	}

	private static boolean allEqual(byte[] tiles) {
		for(int i = 0 ; i < tiles.length-1 ; i++) {
			if(tiles[i] != tiles[i+1]) return false;
		}
		return true;
	}

	private static int monoVal(byte a, byte b, byte c, byte d) {
		if(b==0) return monoVal(a,c,d);
		if(c==0) return monoVal(a,d);
		int val = 0;
		if(a>b) val -= a-b;
		else val += (a-b) * 2;
		if(b>c) val -= b-c;
		else val += (b-c) * 2;
		if(c>d) val -= c-d;
		else val += (d==0) ? 0 : (c-d) * 2;
		return val;
	}

	private static int monoVal(byte a, byte b, byte c) {
		if(b==0) return monoVal(a,c);
		int val = 0;
		if(a>b) val -= a-b;
		else val += (a-b) * 2;
		if(b>c) val -= b-c;
		else val += (c==0) ? 0 : (b-c) * 2;
		return val;
	}

	private static int monoVal(byte a, byte b) {
		int val = 0;
		if(a>b) val -= a-b;
		else val += (b==0) ? 0 : (a-b) * 2;
		return val;
	}

	private static byte[][] split2D(long state) {
		byte[][] grid = new byte[4][4];
		for(int y = 3 ; y >= 0 ; y--) {
			for(int x = 3 ; x >= 0 ; x--) {
				grid[y][x] = (byte)(state & 0xF);
				state >>= 4;
			}
		}
		return grid;
	}
	
	public static double eval(long state) {
		byte[][] cells = split2D(state);
		int eCount = 1;
		int highest = 0;
		double corner = 0.5;
		double smoothness = 0;
		double mono = 1.0 / (Math.abs(mono(cells)) + 1);
		int total = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				total += (1<<(cells[y][x]));
				if(cells[y][x] == 0) {
					eCount++;
				} else if(cells[y][x] > highest) {
					highest = cells[y][x];
					if (x == 0) {
						if(y == 0) {
							corner = 1.0;
						} else if (y == 3) {
							corner = 1.0;
						} else {
							corner = 0.5;
						}
					} else if (x == 3) {
						if(y == 0) {
							corner = 1.0;
						} else if (y == 3) {
							corner = 1.0;
						} else {
							corner = 0.5;
						}
					} else {
						corner = 0.5;
					}
				}
				if (x < 3) {
					smoothness += Math.abs(cells[y][x] - cells[y][x + 1]);
				}
				if (y < 3) {
					smoothness += Math.abs(cells[y][x] - cells[y + 1][x]);
				}
			}
		}
		smoothness = 1.0/smoothness;
//		System.out.println("Smoothness: " + smoothness + ", Mono: " + mono + ", Empty: " + eCount + ", Highest: " + (1<<highest));
		return smoothness * 0.2 + mono + Math.log(eCount) * 2;
	}

	public static boolean gameOver(long state) {
		byte[][] cells = split2D(state);
		for(int y = 0 ; y < 4 ; y++) {
			for(int x = 0 ; x < 4 ; x++) {
				if(cells[y][x] == 0) return false;
				if (x < 3) {
					if(cells[y][x] == cells[y][x+1]) return false;
				}
				if (y < 3) {
					if(cells[y][x] == cells[y+1][x]) return false;
				}
			}
		}
		return true;
	}

	public static Map<MOVE, Long> expand(long state) {
		Map<MOVE, Long> children = new HashMap<>();
		for(MOVE move : MOVE.values()) {
			long s2 = slide(state, move);
			if(s2!=state) {
				children.put(move, s2);
			}
		}
		return children;
	}
	
	public static List<Long> expand2(long state) {
		List<Long> list = new ArrayList<Long>();
		for(MOVE move : MOVE.values()) {
			long s2 = slide(state, move);
			if(s2!=state) {
				list.add(s2);
			}
		}
		return list;
	}
	
	public static List<Long> expandMIN(long state) {
		List<Long> list = new ArrayList<Long>();
		for(int i = 0 ; i < 16 ; i++) {
			int shamt = i << 2;
			long val = (state >> shamt) & 0xF;
			
//			System.out.print(shamt + " = " + Integer.toHexString((int) val) + " || ");
			if(val == 0) {
				list.add(state | (1L << shamt));
				list.add(state | (2L << shamt));
			}
		}
//		System.out.println();
		return list;
	}
	
	public static long addRandomTile(long state) {
		List<Point> empty = getEmptyCells(state);
		if(empty.isEmpty()) return state;
//		System.out.println(empty);
//		System.out.println(empty.size());
		int i = rng.nextInt(empty.size());
//		System.out.println(i);
		Point e = empty.get(i);
		long shamt = SHAMT[e.y][e.x];
//		System.out.println(shamt);
		state |= (rng.nextFloat() < 0.9 ? 1L : 2L) << shamt;
		return state;
	}

	private static List<Point> getEmptyCells(long state) {
		byte[][] cells = split2D(state);
//		print(cells);
		List<Point> empty = new ArrayList<Point>();
		for(int y = 0 ; y < 4 ; y++) {
			for(int x = 0 ; x < 4 ; x++) {
				if(cells[y][x] == 0) {
					empty.add(new Point(x,y));
				}
			}
		}
		return empty;
	}

	public static double rollout(long state) {
		while(!gameOver(state)) {
//			Map<MOVE, Long> children = expand(state);
			MOVE move = MOVE.values()[rng.nextInt(4)];
			long state2 = slide(state, move);
			if(state2 != state) {
				state = state2;
				state = addRandomTile(state);
			}
		}
		return approxScore(state);
	}
	
	public static double rollout(long state, int depth) {
		int i = 0;
		while(!gameOver(state) && i++ < depth) {
//			Map<MOVE, Long> children = expand(state);
			MOVE move = MOVE.values()[rng.nextInt(4)];
			long state2 = slide(state, move);
			if(state2 != state) {
				state = state2;
				state = addRandomTile(state);
			}
		}
		return approxScore(state);
	}
	
	public static double guidedRollout(long state) {
		while(!gameOver(state)) {
			double bestEval = Double.NEGATIVE_INFINITY;
			long bestState = 0;
			Map<MOVE, Long> children = expand(state);
			for(MOVE move : children.keySet()) {
				long s2 = children.get(move);
				double eval = eval3(s2);
				if(eval > bestEval) {
					bestEval = eval;
					bestState = s2;
				}
			}
			state = addRandomTile(bestState);
		}
		return approxScore(state);
	}
	
	public static int approxScore(long state) {
		int score = 0;
		for(int i = 0 ; i < 16 ; i++) {
			int n = (int) ((state >> (i*4)) & 0xF);
			if(n > 0) {
				score += (n-1)*(1<<n);
			}
		}
		return score;
	}
	
	public static int totalTiles(long state) {
		int total = 0;
		for(int i = 0 ; i < 16 ; i++) {
			int shamt = i << 2;
			long val = (state >> shamt) & 0xF;
			total += val;
		}
		return total;
	}
	
	public static double eval2(long state) {
		int emptyCells = getEmptyCells(state).size() + 1;

		  double smoothWeight = 0.1,
				  mono2Weight  = 1.0,
				  emptyWeight  = 2.7,
				  maxWeight    = 1.0;
		  
		  double mono = mono2(split2D(state));
		  double smoothness = smoothness2(state);
		  int max = maxValue(state);
		  double eval = 
				  smoothness * smoothWeight
				  + mono * mono2Weight
				  + Math.log(emptyCells) * emptyWeight
				  + max * maxWeight;
//		  System.out.println("Smoothness: " + smoothness + ", Mono: " + mono + ", Empty: " + emptyCells + ", Max: " + max + ", Eval: " + eval);
//		  System.out.println("Eval: " + eval);
		  return eval;
	}
	
	public static double eval3(long state) {
		int emptyCells = 0;
		double smoothness = 0.0;
		int max = 0;
		byte[][] cells = split2D(state);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (cells[y][x] > 0) {
					max = Math.max(max, cells[y][x]);
					int value = cells[y][x];
					int vRight = valueRight(cells, x + 1, y);
					int vDown = valueDown(cells, x, y + 1);
					if (vRight > 0)
						smoothness -= Math.abs(value - vRight);
					if (vDown > 0)
						smoothness -= Math.abs(value - vDown);
				} else {
					emptyCells++;
				}
			}
		}
		int[] htd = highestTile(cells);
		int hv = -htd[2];
		if (htd[0]==0 || htd[0]==3) {
			if (htd[1]==0 || htd[1]==3) {
				hv *= -1;
			}
		}
//		System.out.println(hv);
		int score = approxScore(state);
		double mono = mono3(cells);
		double eval = 
				  smoothness 			* smoothWeight 
				+ mono       			* monoWeight
				+ Math.log(emptyCells) 	* emptyWeight 
				+ max 					* maxWeight
				+ score 				* scoreWeight;
//		System.out.println("Smoothness: " + smoothness + ", Mono: " + mono
//				+ ", Empty: " + emptyCells + ", Max: " + max + ", Eval: " + eval);
		return eval + 10*hv;
	}

	private static int maxValue(long state) {
		int max = 0;
		for(int i = 0 ; i < 16 ; i++) {
			max = Math.max(max, (int)(state >> (i*4))&0xF);
		}
		return max;
	}
	
	private static int[] highestTile(byte[][] cells) {
		int hv = -1;
		int hx = 0;
		int hy = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				int v = cells[y][x];
				if(v > hv) {
					hv = v;
					hx = x;
					hy = y;
				}
			}
		}
		return new int[]{hx,hy,hv};
	}

	private static double smoothness(long state) {
		byte[][] cells = split2D(state);
		double smoothness = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (x < 3) {
					smoothness += Math.abs(cells[y][x] - cells[y][x + 1]);
				}
				if (y < 3) {
					smoothness += Math.abs(cells[y][x] - cells[y + 1][x]);
				}
			}
		}
		return smoothness;
	}
	
	private static double smoothness2(long state) {
		byte[][] cells = split2D(state);
		double smoothness = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if(cells[y][x] > 0) {
					int value = cells[y][x];
					int vRight = valueRight(cells, x+1, y);
					int vDown = valueDown(cells, x, y+1);
					if(vRight > 0) 
						smoothness -= Math.abs(value - vRight);
					if(vDown > 0)
						smoothness -= Math.abs(value - vDown);
				}
			}
		}
		return smoothness;
	}
	
	private static double mono2(byte[][] cells) {
		// scores for all four directions
		  int[] totals = new int[]{0, 0, 0, 0};

		  // up/down direction
		  for (int x=0; x<4; x++) {
		    int current = 0;
		    int next = current+1;
		    while ( next<4 ) {
		      while ( next<4 && cells[x][next] == 0 ) {
		        next++;
		      }
		      if (next>=4) { next--; }
		      int currentValue = cells[x][current];
		      int nextValue = cells[x][next];
		      if (currentValue > nextValue) {
		        totals[0] += nextValue - currentValue;
		      } else if (nextValue > currentValue) {
		        totals[1] += currentValue - nextValue;
		      }
		      current = next;
		      next++;
		    }
		  }

		  // left/right direction
		  for (int y=0; y<4; y++) {
		    int current = 0;
		    int next = current+1;
		    while ( next<4 ) {
		      while ( next<4 && cells[next][y] == 0) {
		        next++;
		      }
		      if (next>=4) { next--; }
		      int currentValue = cells[current][y];
		      int nextValue = cells[next][y];
		      if (currentValue > nextValue) {
		        totals[2] += nextValue - currentValue;
		      } else if (nextValue > currentValue) {
		        totals[3] += currentValue - nextValue;
		      }
		      current = next;
		      next++;
		    }
		  }

		  return Math.max(totals[0], totals[1]) + Math.max(totals[2], totals[3]);
	}
	
	private static double mono3(byte[][] cells) {
		
		List<Integer> lr = new ArrayList<Integer>(16);
		List<Integer> rl = new ArrayList<Integer>(16);
		List<Integer> ud = new ArrayList<Integer>(16);
		List<Integer> du = new ArrayList<Integer>(16);
		
		for(int i = 0 ; i < 16 ; i++) {
			Point udp = UD[i];
			Point lrp = LR[i];
			Point rlp = RL[i];
			Point dup = DU[i];
			if(cells[rlp.x][rlp.y] > 0)
				rl.add((int) cells[rlp.x][rlp.y]);
			if(cells[lrp.x][lrp.y] > 0)
				lr.add((int) cells[lrp.x][lrp.y]);
			if(cells[udp.x][udp.y] > 0)
				ud.add((int) cells[udp.x][udp.y]);
			if(cells[dup.x][dup.y] > 0)
				du.add((int) cells[dup.x][dup.y]);
		}
		double udm = 0.0, lrm = 0.0, rlm = 0.0, dum = 0.0;
		for(int i = 0 ; i < lr.size()-1 ; i++) {
			rlm -= Math.abs(rl.get(i) - rl.get(i+1));
			lrm -= Math.abs(lr.get(i) - lr.get(i+1));
			udm -= Math.abs(ud.get(i) - ud.get(i+1));
			dum -= Math.abs(du.get(i) - du.get(i+1));
		}
		//One or the other, or both?
		return Math.max(Math.max(udm, dum), Math.max(rlm, lrm));
	}

	private static int valueDown(byte[][] cells, int x, int y) {
		if(y>cells.length-1) return 0;
		if(cells[y][x] == 0) return valueDown(cells, x, y+1);
		return cells[y][x];
	}

	private static int valueRight(byte[][] cells, int x, int y) {
		if(x>cells[y].length-1) return 0;
		if(cells[y][x] == 0) return valueRight(cells, x+1, y);
		return cells[y][x];
	}
	
	private static long snake1(long s) {
		long a = s & 0x0000FFFF0000FFFFL;
		long s2 = s>>4;
		long b = s2 & 0x0F0000000F000000L;
		long c = s2 & 0x0000F0000000F000L;
		long d = s2 & 0x00F0000000F00000L;
		long e = s2 & 0x000F0000000F0000L;
		
		b ^= (c<<12);
		c ^= (b>>12);
		b ^= (c<<12);
		
		d ^= (e<<4);
		e ^= (d>>4);
		d ^= (e<<4);
		
		return a | ((b | c | d | e) << 4);
	}
	
	private static long snake2(long s) {
		long a = s & 0xFFFF0000FFFF0000L;
		long b = s & 0x0000F0000000F000L;
		long c = s & 0x0000000F0000000FL;
		long d = s & 0x00000F0000000F00L;
		long e = s & 0x000000F0000000F0L;
		
		b ^= (c<<12);
		c ^= (b>>12);
		b ^= (c<<12);
		
		d ^= (e<<4);
		e ^= (d>>4);
		d ^= (e<<4);
		
		return a | b | c | d | e;
	}
	
	//For fast expectimax 'borrowed' functions
	public static long transpose(long s) {
		long a1 = s  & 0xF0F00F0FF0F00F0FL;
		long a2 = s  & 0x0000F0F00000F0F0L;
		long a3 = s  & 0x0F0F00000F0F0000L;
		long a  = a1 | (a2 << 12) | (a3 >> 12);
		long b1 = a  & 0xFF00FF0000FF00FFL;
		long b2 = a  & 0x00FF00FF00000000L;
		long b3 = a  & 0x00000000FF00FF00L;
		return b1 | (b2 >> 24) | (b3 << 24);
	}
	
	public static int count_empty(long x) {
		x |= (x >> 2) & 0x3333333333333333L;
		x |= (x >> 1);
		x = ~x & 0x1111111111111111L;
		// At this point each nibble is:
		//  0 if the original nibble was non-zero
		//  1 if the original nibble was zero
		// Next sum them all
		x += x >> 32;
		x += x >> 16;
		x += x >>  8;
		x += x >>  4; // this can overflow to the next nibble if there were 16 empty positions
		return (int) (x & 0xf);
	}

	private static byte[] split1D(long state) {
		byte[] bytes = new byte[16];
		for(int i = 0 ; i < 16 ; i++) {
			int shmt = i << 2;
			bytes[i] = (byte) ((state >> (60 - shmt)) & 0xF);
		}
		return bytes;
	}
	
	private static byte[] splitChar1D(char c) {
		byte[] bytes = new byte[4];
		for(int i = 0 ; i < 4 ; i++) {
			int shmt = i << 2;
			bytes[i] = (byte) ((c >> (12 - shmt)) & 0xF);
		}
		return bytes;
	}
	
	public static long[] getSymmetries(long state) {
		return new long[]{
				state,																	//original
				flipV(transpose(state)),												//rotate 90
				flipV(transpose(flipV(transpose(state)))), 								//rotate 180
				flipV(transpose(flipV(transpose(flipV(transpose(state)))))), 			//rotate 270
				transpose(state),														//transpose
				flipV(state),															//rotate 90t
				flipV(transpose(flipV(state))),											//rotate 180t
				flipV(transpose(flipV(transpose(flipV(state))))),						//rotate 270t
		};
	}
	
	private static long flipV (long state) {
		return    ((state & 0xFFFF000000000000L) >>> 48)
				| ((state & 0x0000FFFF00000000L) >> 16)
				| ((state & 0x00000000FFFF0000L) << 16) 
				| ((state & 0x000000000000FFFFL) << 48);
	}
	
	public static final MOVE[][] MOVE_FIXER = new MOVE[][] {
			new MOVE[]{LEFT, UP, RIGHT, DOWN},	//original
			new MOVE[]{DOWN, LEFT, UP, RIGHT},	//rotate 90
			new MOVE[]{RIGHT, DOWN, LEFT, UP},	//rotate 180
			new MOVE[]{UP, RIGHT, DOWN, LEFT},	//rotate 270
			new MOVE[]{UP, LEFT, DOWN, RIGHT},	//transpose
			new MOVE[]{LEFT, DOWN, RIGHT, UP},	//rotate 90 t (flip V)
			new MOVE[]{DOWN, RIGHT, UP, LEFT},	//rotate 180 t
			new MOVE[]{RIGHT, UP, LEFT, DOWN}	//rotate 270 t (flip H)
	};

}
