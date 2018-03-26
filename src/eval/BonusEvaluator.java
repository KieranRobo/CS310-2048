package eval;

import model.State;

public class BonusEvaluator implements Evaluator {


	@Override
	public double evaluate(State state) {
		return eval(state.toLong());
	}
	
	private double eval(long state) {
		byte[][] cells = split2D(state);
		double emptyCells = nEmptyCells(cells) + 0.000001;
		double mono = 1.0 / (Math.abs(mono(cells))+1);
		double smoothness = 1 / (Math.abs(smoothness(cells))+1);
		int score = approxScore(state);
		double eval = score * 1.0
				+ mono * 100000 * emptyCells
				+ smoothness * 1000.0
				+ maxCorner(state) * 1000;
		return eval/100.0;
	}
	
	private double maxCorner(long state) {
		int highestTile = 0;
		boolean isCorner = false;
		for(int i = 0 ; i < 16 ; i++) {
			int v = (int) ((state >> (i*4)) & 0xF);
			if (v > highestTile) {
				highestTile = v;
				isCorner = i == 0 || i == 3 || i == 12 || i == 15;
			}
		}
		return isCorner ? 1.0 : 0.1;
	}
	
	private int approxScore(long state) {
		int score2 = 0;
		int score4 = 0;
		for(int i = 0 ; i < 16 ; i++) {
			int n = (int) ((state >> (i*4)) & 0xF);
			if(n > 0) {
				score2 += (n-1)*(1<<n);
				if(n > 1) {
					score4 += (n-2)*(1<<n);
				}
			}
		}
		return (int)(score2 * 0.9 + score4 * 0.1);
	}
	
	private int nEmptyCells(byte[][] cells) {
		int empty = 0;
		for(int y = 0 ; y < 4 ; y++) {
			for(int x = 0 ; x < 4 ; x++) {
				if(cells[y][x] == 0) {
					empty++;
				}
			}
		}
		return empty;
	}
	
	private byte[][] split2D(long state) {
		byte[][] grid = new byte[4][4];
		for(int y = 3 ; y >= 0 ; y--) {
			for(int x = 3 ; x >= 0 ; x--) {
				grid[y][x] = (byte)(state & 0xF);
				state >>= 4;
			}
		}
		return grid;
	}
	
	private double mono(byte[][] cells) {
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
	
	private double smoothness(byte[][] cells) {
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
	
	private int valueDown(byte[][] cells, int x, int y) {
		if(y>cells.length-1) return 0;
		if(cells[y][x] == 0) return valueDown(cells, x, y+1);
		return cells[y][x];
	}

	private int valueRight(byte[][] cells, int x, int y) {
		if(x>cells[y].length-1) return 0;
		if(cells[y][x] == 0) return valueRight(cells, x+1, y);
		return cells[y][x];
	}

}
