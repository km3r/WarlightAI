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

package conquest.bot;

import java.util.ArrayList;
import java.util.LinkedList;

import conquest.game.RegionData;
import conquest.game.move.AttackTransferMove;
import conquest.game.move.PlaceArmiesMove;
import conquest.view.GUI;


public class BotStarter implements Bot 
{
	/**
	 * A method used at the start of the game to decide which player start with what Regions. 6 Regions are required to be returned.
	 * This example randomly picks 6 regions from the pickable starting Regions given by the engine.
	 * @return : a list of m (m=6) Regions starting with the most preferred Region and ending with the least preferred Region to start with 
	 */
	@Override
	public ArrayList<RegionData> getPreferredStartingRegions(BotState state, Long timeOut)
	{
		int m = 6;
		ArrayList<RegionData> preferredStartingRegions = new ArrayList<RegionData>();
		for(int i=0; i<m; i++)
		{
			double rand = Math.random();
			int r = (int) (rand*state.getPickableStartingRegions().size());
			int regionId = state.getPickableStartingRegions().get(r).getId();
			RegionData region = state.getFullMap().getRegion(regionId);

			if(!preferredStartingRegions.contains(region))
				preferredStartingRegions.add(region);
			else
				i--;
		}
		
		return preferredStartingRegions;
	}
	
	/**
	 * This method is called for at first part of each round. This example puts two armies on random regions
	 * until he has no more armies left to place.
	 * @return The list of PlaceArmiesMoves for one round
	 */
	@Override
	public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) 
	{
		
		ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
		String myName = state.getMyPlayerName();
		int armies = 2;
		int armiesLeft = state.getStartingArmies();
		LinkedList<RegionData> visibleRegions = state.getMap().getRegions();
		
		while(armiesLeft > 0)
		{
			double rand = Math.random();
			int r = (int) (rand*visibleRegions.size());
			RegionData region = visibleRegions.get(r);
			
			if(region.ownedByPlayer(myName))
			{
				placeArmiesMoves.add(new PlaceArmiesMove(myName, region, Math.min(armiesLeft, armies)));
				armiesLeft -= armies;
			}
		}
		
		return placeArmiesMoves;
	}

	/**
	 * This method is called for at the second part of each round. This example attacks if a region has
	 * more than 6 armies on it, and transfers if it has less than 6 and a neighboring owned region.
	 * @return The list of PlaceArmiesMoves for one round
	 */
	@Override
	public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) 
	{
		ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
		String myName = state.getMyPlayerName();
		int armies = 5;
		
		for(RegionData fromRegion : state.getMap().getRegions())
		{
			if(fromRegion.ownedByPlayer(myName)) //do an attack
			{
				ArrayList<RegionData> possibleToRegions = new ArrayList<RegionData>();
				possibleToRegions.addAll(fromRegion.getNeighbors());
				
				while(!possibleToRegions.isEmpty())
				{
					double rand = Math.random();
					int r = (int) (rand*possibleToRegions.size());
					RegionData toRegion = possibleToRegions.get(r);
					
					if(!toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 6) //do an attack
					{
						attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
						break;
					}
					else if(toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 1) //do a transfer
					{
						attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
						break;
					}
					else
						possibleToRegions.remove(toRegion);
				}
			}
		}
		
		return attackTransferMoves;
	}

	@Override
	public void setGUI(GUI gui) {
	}

}
