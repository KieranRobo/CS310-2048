package eval;

import model.State;


public class FastEvaluator implements Evaluator {

	public  final float[] HEURISTIC = new float[65536];
	private  final float[] SCORE     = new float[65536];
	
	private static final long ROW_MASK = 0xFFFFL;
	private static final long COL_MASK = 0x000F000F000F000FL;
	
	public FastEvaluator() {
		initTables();
	}
	
	@Override
	public double evaluate(State state) {
		return scoreHeuristicBoard(state.toLong());
	}
	
	private  final void initTables() {
		for (int row = 0; row < 65536; ++row) {
	        int[] line = new int[]{
	                ((row >>  0) & 0xf),
	                ((row >>  4) & 0xf),
	                ((row >>  8) & 0xf),
	                ((row >> 12) & 0xf)
	        };

	        float heur_score = 0.0f;
	        float score = 0.0f;
	        int empty = 0;
	        for (int i = 0; i < 4; ++i) {
	            int rank = line[i];
	            if (rank == 0) {
	                empty++;
	            } else if (rank >= 2) {
	                // the score is the total sum of the tile and all intermediate merged tiles
	                score += (rank - 1) * (1 << rank);
	            }
	        }
	        SCORE[row] = score;

	        int maxi = 0;
	        for (int i = 1; i < 4; ++i) {
	            if (line[i] > line[maxi]) maxi = i;
	        }
	        int edge = 0;
	        //High edge bonus.  Doubled if high corner.
	        if(maxi == 0) {
	        	edge = 1;
	        } else if (maxi == 3){
	        	edge = 1;
	        }
			
			double mono = Math.abs(mono(line));

	        HEURISTIC[row] = (float) (score * 1.0
	        		+ mono * 500.0
	        		+ edge * 1000.0);
	    }
	}
	
	private static final double smoothness (int[] line) {
		double smoothness = 0;
		for (int x = 0; x < 4; x++) {
			if(line[x] > 0) {
				int value = line[x];
				int vRight = valueRight(line, x+1);
				if(vRight > 0) 
					smoothness += Math.abs(value - vRight);
			}
		}
		smoothness = 1.0 / (smoothness + 1.0e-6);
		return smoothness;
	}
	
	private static final double mono(int[] line) {
		double mono = 0;
		for (int x = 0; x < 4; x++) {
			int value = line[x];
			if (x>2) break;
			int vRight = line[x+1];
			
			while(vRight == 0) {
				x++;
				if (x>2) break;
				vRight = line[x+1];
			}
			if (x>2) break;
			
			if(vRight == value) {
				mono += 1;
			} else {
				if(vRight > value) {
					mono += 1.0 / (2.0 * (vRight - value));
				} else {
					mono -= 1.0 / (2.0 * (value - vRight));
				}
			}
		}
		return mono;
	}
	
	static final int reverse_row(int row) {
	    return (row >> 12) | ((row >> 4) & 0x00F0) | ((row << 4) & 0x0F00) | (row << 12);
	}
	
	static final int[] reverseLine(int[] line) {
	    return new int[]{line[3], line[2], line[1], line[0]};
	}
	
	private static final int valueRight(int[] line, int x) {
		if(x>3) return -1;
		if(line[x] == 0) return valueRight(line, x+1);
		return line[x];
	}
	
	private  final void init_tables() {
		
	    for (int row = 0; row < 65536; ++row) {
	        int[] line = new int[]{
	                ((row >>  0) & 0xf),
	                ((row >>  4) & 0xf),
	                ((row >>  8) & 0xf),
	                ((row >> 12) & 0xf)
	        };

	        float heur_score = 0.0f;
	        float score = 0.0f;
	        for (int i = 0; i < 4; ++i) {
	            int rank = line[i];
	            if (rank == 0) {
	                heur_score += 10000.0f; //Bonus for empty cell, but this could mess up mono
	            } else if (rank >= 2) {
	                // the score is the total sum of the tile and all intermediate merged tiles
	                score += (rank - 1) * (1 << rank);
	            }
	        }
	        SCORE[row] = score;

	        int maxi = 0;
	        for (int i = 1; i < 4; ++i) {
	            if (line[i] > line[maxi]) maxi = i;
	        }

	        //High edge bonus.  Doubled if high corner.
	        if (maxi == 0 || maxi == 3) heur_score += 20000.0f;

	        // Check if maxi's are close to each other, and of diff ranks (eg 128 256)
	        // Smoothness
	        for (int i = 1; i < 4; ++i) {
	            if ((line[i] == line[i - 1] + 1) || (line[i] == line[i - 1] - 1)) heur_score += 1000.0f;
	        }

	        // Check if the values are ordered:
	        // TODO: improve this for relative values of monotonicity?
	        if ((line[0] < line[1]) && (line[1] < line[2]) && (line[2] < line[3])) heur_score += 10000.0f;
	        if ((line[0] > line[1]) && (line[1] > line[2]) && (line[2] > line[3])) heur_score += 10000.0f;

	        HEURISTIC[row] = heur_score;

	    }
	}
	
	private  final float scoreHelper(long board, float[] table) {
	    return table[(int) ((board >>  0) & ROW_MASK)] +
	            table[(int) ((board >> 16) & ROW_MASK)] +
	            table[(int) ((board >> 32) & ROW_MASK)] +
	            table[(int) ((board >> 48) & ROW_MASK)];
	}
	
	public final float scoreHeuristicBoard(long board) {
//		return (float) evaluator.evaluate(board);
	    return scoreHelper(          board , HEURISTIC) +
	           scoreHelper(transpose(board), HEURISTIC);
	}
	
	//For fast expectimax 'borrowed' functions
		private static final long transpose(long s) {
			long a1 = s  & 0xF0F00F0FF0F00F0FL;
			long a2 = s  & 0x0000F0F00000F0F0L;
			long a3 = s  & 0x0F0F00000F0F0000L;
			long a  = a1 | (a2 << 12) | (a3 >> 12);
			long b1 = a  & 0xFF00FF0000FF00FFL;
			long b2 = a  & 0x00FF00FF00000000L;
			long b3 = a  & 0x00000000FF00FF00L;
			return b1 | (b2 >> 24) | (b3 << 24);
		}
		
		private static final int count_empty(long x) {
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

	public final float score_board(long board) {
	    return scoreHelper(board, SCORE);
	}

}
