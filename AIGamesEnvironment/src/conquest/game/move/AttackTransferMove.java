// Copyright 2014 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package conquest.game.move;
import conquest.game.RegionData;

/**
 * This Move is used in the second part of each round. It represents the attack or transfer of armies from
 * fromRegion to toRegion. If toRegion is owned by the player himself, it's a transfer. If toRegion is
 * owned by the opponent, this Move is an attack. 
 */

public class AttackTransferMove extends Move {
	
	private RegionData fromRegion;
	private RegionData toRegion;
	private int armies;
	
	//geen misbruik maken van playerName aub, alleen je eigen botnaam invullen
	public AttackTransferMove(String playerName, RegionData fromRegion, RegionData toRegion, int armies)
	{
		super.setPlayerName(playerName);
		this.fromRegion = fromRegion;
		this.toRegion = toRegion;
		this.armies = armies;
	}
	
	/**
	 * @param n Sets the number of armies of this Move
	 */
	public void setArmies(int n) {
		armies = n;
	}
	
	/**
	 * @return The Region this Move is attacking or transferring from
	 */
	public RegionData getFromRegion() {
		return fromRegion;
	}
	
	/**
	 * @return The Region this Move is attacking or transferring to
	 */
	public RegionData getToRegion() {
		return toRegion;
	}
	
	/**
	 * @return The number of armies this Move is attacking or transferring with
	 */
	public int getArmies() {
		return armies;
	}
	
	/**
	 * @return A string representation of this Move
	 */
	public String getString() {
		//@Logan: I added checks here to make sure these regions aren't null.
		//	It stops the crash, but the bot tends to get stuck repeating the same 
		//	the same illegal move.
		if(getIllegalMove().equals("") && (fromRegion != null) && (toRegion != null))
			return getPlayerName() + " attack/transfer " + fromRegion.getId() + " " + toRegion.getId() + " " + armies;
		else
			return getPlayerName() + " illegal_move " + getIllegalMove();
	}
}
