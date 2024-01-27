import java.util.ArrayList;

public class OthelloAIChadGPT implements IOthelloAI {

    /**
     * The current player, for which a move has to be decide: PlayerToken 1 = (black), Player 2 = (White)
     */
    private int playerToken;
    /**
     * The ply-depth for which stop the search. Higher means better accuracy but lower performance.
     */
    private int maxDepth = 7;

    /**
     * Static weights for the board, to help guide the decision.
     */
    private int[][] staticWeights =  {  {4, -3, 2, 2, 2, 2, -3, 4},
                                        {-3, -3, -1, -1, -1, -1, -4, -3},
                                        {2, -1, 1, 0, 0, 1, -1, 2},
                                        {2, -1, 0, 1, 1, 0, -1, 2},
                                        {2, -1, 0, 1, 1, 0, -1, 2},
                                        {2, -1, 1, 0, 0, 1, -1, 2},
                                        {-3, -4, -1, -1, -1, -1, -4, -3},
                                        {4, -3, 2, 2, 2, 2, -3, 4}
                                        };

    /**<p>Returns the best possible move for the current player, assuming both players play the game optimally.</p>
     * @param s The gamestate for which to decide a move from.
     * @return A Position-object with the decided move. Returns the position (-1,-1) if no legal moves can be made.
     */
    @Override
    public Position decideMove(GameState s) {
        playerToken = s.getPlayerInTurn();
        long timeStart = System.currentTimeMillis();
        ArrayList<Position> moves = s.legalMoves();
        if ( !moves.isEmpty() ){
            Node move = maxValue(s, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
            long timePast = System.currentTimeMillis() - timeStart;
            System.out.println("SmarterAI- Calculation time: " +timePast + " ms, chose the move: " + move.getPosition().toString());
            return move.getPosition();}
		else
			return new Position(-1,-1);
    }


     /**<p> Returns a Node-object with a utility-value of the maximum evaluated utility (best choice from the perspective of Max) from the current gamestate and the associated Position (move). The utility-value assumes optimal play from both Min and Max.</p>
     * @param s The gamestate for which to calculate the maximum value.
     * @param alpha The current alpha-value for decideMove(). Best value found so far, from the perspective of Max.
     * @param beta The current beta-value for decideMove(). Best value found so far, from the perspective of Min.
     * @param depth The current ply-depth traversed through consecutive recursive calls to minValue() and maxValue().
     * @return A Node-object with a utility-value of the minimum evaluated utility from the current gamestate and the associated Position (move). If the maxDepth has been reached or no legal moves are available for the player in the current gamestate, a Node-objectwith a (-1,-1) position is returned. 
     */
    private Node maxValue(GameState s, int alpha, int beta, int depth) {
        ArrayList<Position> moves = s.legalMoves();
        if(moves.size() == 0 || depth > maxDepth){
            return new Node(eval(s), new Position(-1, -1));}
        int v = Integer.MIN_VALUE;
        Position move = new Position(-1, -1);
        for (Position position : moves) {
            GameState s2 = new GameState(s.getBoard(),s.getPlayerInTurn());
            s2.insertToken(position);
            Node p2 = minValue(s2, alpha, beta, depth+1);
            if(p2.getUtility() > v){
                v = p2.getUtility();
                move = position;
            } 
            if(v >= beta){return new Node(v, move);}  
            if(v > alpha){alpha = v;}
            
        }
        return new Node(v, move);
    }

    /**<p> Returns a Node-object with a utility-value of the minimum evaluated utility (best choice from the perspective of Min) from the current gamestate and the associated Position (move). The utility-value assumes optimal play from both Min and Max.</p>
     * @param s The gamestate for which to calculate the minimum value.
     * @param alpha The current alpha-value for decideMove(). Best value found so far, from the perspective of Max.
     * @param beta The current beta-value for decideMove(). Best value found so far, from the perspective of Min.
     * @param depth The current ply-depth traversed through consecutive recursive calls to maxValue() and minValue().
     * @return A Node-object with a utility-value of the minimum evaluated utility from the current gamestate and the associated Position (move). If the maxDepth has been reached or no legal moves are available for the player in the current gamestate, a Node-objectwith a (-1,-1) position is returned. 
     */
    private Node minValue(GameState s, int alpha, int beta, int depth) {
        ArrayList<Position> moves = s.legalMoves();
        if(moves.size() == 0 || depth > maxDepth){
            return new Node(eval(s), new Position(-1, -1));}
        int v = Integer.MAX_VALUE;
        Position move = new Position(-1, -1);
        for (Position position : moves) {
            GameState s2 = new GameState(s.getBoard(),s.getPlayerInTurn());
            s2.insertToken(position);
            Node p2 = maxValue(s2, alpha, beta, depth+1);
            if(p2.getUtility() < v){
                v = p2.getUtility();
                move = position;
            }
            if(v <= alpha){return new Node(v, move);}  
            if(v < beta){beta = v;}
            
        }
        return new Node(v, move);
}

/**
 * <p>This function evaluates the likelihood of OthelloAIChadGPT winning in any gamestate (finished or unfinished). Returns an integer between -100 and 100 based on the ratio Max coins to Min coins. A value of 100 is equal to total Max dominance (only max coins occupy the board). A value of -100 means total Min dominance. A value of 0 means equality between Min and Max </p>
 * @param utility
 * @return int. In range -800 to 800
 */
    private int CoinParityUtility(int[] utility){
        int max = utility[playerToken == 1 ? 0 : 1];
        int min = utility[playerToken == 1 ? 1 : 0];

        int diff = 100*(max-min);
        // to avoid zero division and negative division
        int total = (max+min)<1 ? 1 : (max+min);
        return diff/total;
    }

/**
 * <p>This function give the static weight for game strategy.
 * @param x location on the board (collum)
 * @param y location on the board (row)
 * @return int. static weight of x,y location.
 */

    private int getHeuristic(int x, int y){
        return staticWeights[y][x];
    }

/**
 * <p>This function evaluates the {@link Gamestate} to Non-Zero-Sum value
 * @param s Gamestate. Gamestate to evaluate 
 * @return int. The game evaluation for current player
 * 
 */
    private int eval(GameState s) {
        int[][] board = s.getBoard();
        int size = board[0].length;
        int palyer1Heuristic = 0;
        int palyer2Heuristic = 0;
        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++){
                if ( board[y][x] == 1 ){
                    palyer1Heuristic += getHeuristic(x,y);
                }
                else if ( board[y][x] == 2 ){
                    palyer2Heuristic += getHeuristic(x,y);
                }
            }
        }
        int[] utility = {palyer1Heuristic, palyer2Heuristic};
        return CoinParityUtility(utility);
    }

}
