package conquest.game;

public enum Player {

	ME(1),
	OPPONENT(2),
	NEUTRAL(3);
	
	public static final int LAST_ID = 3;
	
	public static final int NULL_PLAYER_FLAG = Integer.MAX_VALUE << 3;
	
	/**
	 * Must be 1-based!
	 */
	public final int id;
	
	public final int playerFlag;
	
	private Player(int id) {
		this.id = id;
		this.playerFlag = 1 << (id - 1);
	}
	
	public static Player fromFlag(int playerFlag) {
		if ((playerFlag & ME.playerFlag) != 0) return ME;
		if ((playerFlag & OPPONENT.playerFlag) != 0) return OPPONENT;
		if ((playerFlag & NEUTRAL.playerFlag) != 0) return NEUTRAL;
		return null;
	}
	
	public static Player swapPlayer(Player player) {
		switch (player) {
		case ME:       return Player.OPPONENT;
		case OPPONENT: return Player.ME;
		case NEUTRAL:  return Player.NEUTRAL;
		}
		throw new RuntimeException("Unhandled player: " + player);
	}
	
	
	
}
