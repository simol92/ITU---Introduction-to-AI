/**
 * An object holding a Position-object, and it's associated utility.
 */


public class Node {
    
    /**
     * Integer-value representation of the utility of a certain move.
     * @see OthelloAIChadGPT
     */
    private Integer utility;
    /**
     * A position object
     */
    private Position position;
    

    /**
     * 
     * @param utility The utility to set this node to.
     * @param position The Position-object to set this node to
     */
    public Node(Integer utility, Position position) {
        this.utility = utility;
        this.position = position;
    }

    /**
     * Returns the utility of this node.
     * @return An integer equal to the utility of this Node
     */
    public Integer getUtility() {
        return utility;
    }

    /**
     * 
     * @param utility the utility to set this node to.
     */
    public void setUtility(Integer utility) {
        this.utility = utility;
    }

    /**
     * Returns the position related to this node.
     * @return the Position-object related to this node.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * 
     * @param position the Position-object to set this node to
     */
    public void setPosition(Position position) {
        this.position = position;
    }
    
}
