package conquest.bot.playground;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import conquest.bot.BotParser;
import conquest.bot.map.RegionBFS;
import conquest.bot.map.RegionBFS.BFSNode;
import conquest.bot.map.RegionBFS.BFSVisitResult;
import conquest.bot.map.RegionBFS.BFSVisitResultType;
import conquest.bot.map.RegionBFS.BFSVisitor;
import conquest.bot.state.ChooseCommand;
import conquest.bot.state.GameBot;
import conquest.bot.state.GameState.RegionState;
import conquest.bot.state.MoveCommand;
import conquest.bot.state.PlaceCommand;
import conquest.engine.RunGame;
import conquest.engine.Engine.FightMode;
import conquest.engine.RunGame.Config;
import conquest.engine.RunGame.GameResult;
import conquest.game.Player;
import conquest.game.world.Continent;
import conquest.game.world.Region;
import conquest.view.GUI;

/*
 * @Author Logan and Kyle
 *  Continues to build up one army and then runs it through whatever 
 * territory is not already owned, proritizes capturing its own continents first..
 *  Very fun to watch actually. Play it against StarterBot and enjoy.
 */

public class LessBasicAttackBot extends GameBot {

    private Region frontline;
    
    public LessBasicAttackBot() {
        System.err.println("---==[ BASIC ATTACK BOT INITIALIZED ]==---");
    }

    @Override
    public List<ChooseCommand> chooseRegions(List<Region> choosable, long timeout) {
        int m = 6;
        
        // SORT PICKABLE REGIONS ACCORDING TO THE PRIORITY
        Collections.sort(choosable, new Comparator<Region>() {
            @Override
            public int compare(Region o1, Region o2) {
                int priority1 = getPrefferedContinentPriority(o1.continent);
                int priority2 = getPrefferedContinentPriority(o2.continent);                
                return priority1 - priority2;
            }
        });
        
        // REMOVE CONTINENT WE DO NOT WANT
        while (choosable.size() > m) choosable.remove(choosable.size()-1);
        
        // CREATE COMMANDS
        List<ChooseCommand> result = new ArrayList<ChooseCommand>(choosable.size());
        for (Region region : choosable) {
            result.add(new ChooseCommand(region));
        }
        
        return result;
    }
    
    public int getPrefferedContinentPriority(Continent continent) {
        switch (continent) {
        case Australia:     return 1;
        case South_America: return 2;
        case North_America: return 3;
        case Europe:        return 4;       
        case Africa:        return 5;
        case Asia:          return 6;
        default:            return 7;
        }
    }

    @Override
    // Continue building up the most powerful army
    public List<PlaceCommand> placeArmies(long timeout) {
        List<PlaceCommand> result = new ArrayList<PlaceCommand>();
            
        List<RegionState> armies = findArmy(2);

        // PLACE ALL ARMIES ONTO REGION WITH MOST ARMIES
        for(RegionState army: armies)
            result.add(new PlaceCommand(army.region, state.me.placeArmies/armies.size()));
        // check remainder
        int remain = state.me.placeArmies - armies.size() * ((int)(state.me.placeArmies / armies.size())) ;
        if (remain > 0)
            result.add(new PlaceCommand(armies.get(0).region, remain));
        return result;
    }


    @Override
    public List<MoveCommand> moveArmies(long timeout) {
        List<MoveCommand> result = new ArrayList<MoveCommand>();
        List<RegionState> armyRegions = findArmy(2);    
        for (RegionState armyRegion: armyRegions) {
        RegionBFS<BFSNode> bfs = new RegionBFS<BFSNode>();
        boolean attacked = false;
        
        // ATTACK UNOWNED NEIGHBOR REGION IN SAME TERRITORY IF POSSIBLE
        for (RegionState neighbor : armyRegion.neighbours) {
            if (neighbor.owned(Player.ME) || neighbor.region.continent.id != armyRegion.region.continent.id) continue;
            
            result.add(new MoveCommand(armyRegion.region, neighbor.region, armyRegion.armies - 1 + state.me.placeArmies));
            attacked = true;
            break;
        }

        // ATTACK UNOWNED NEIGHBOR REGION IF POSSIBLE
        if (!attacked) {
            for (RegionState neighbor : armyRegion.neighbours) {
                if (neighbor.owned(Player.ME))
                    continue;

                result.add(new MoveCommand(armyRegion.region, neighbor.region, armyRegion.armies - 1 + state.me.placeArmies));
                attacked = true;
                break;
            }
        }
        
        // IF I COULDN'T ATTACK, MOVE ARMY TOWARD NEAREST FRONTLINE
        if (!attacked) {
            // FIND FRONT LINE
            bfs.run(armyRegion.region, new BFSVisitor<BFSNode>() {
        
                @Override
                public BFSVisitResult<BFSNode> visit(Region region, int level,
                        BFSNode parent, BFSNode thisNode) {
                    if (!hasOnlyMyNeighbours(state.region(region))) {
                        frontline = region;
                        return new BFSVisitResult<BFSNode>(BFSVisitResultType.TERMINATE, thisNode == null ? new BFSNode() : thisNode);
                    }
                    
                    return new BFSVisitResult<BFSNode>(thisNode == null ? new BFSNode() : thisNode);
                }
            });
            
            // MOVE THROUGH PATH
            if (frontline != null) {
                List<Region> path = bfs.getAllPaths(frontline).get(0);
                Region moveTo = path.get(1);
                
                result.add(transfer(armyRegion, state.region(moveTo)));
            }
        }
        }   
        return result;
    }

    @Override
    public void setGUI(GUI gui) {
    }
    
    /** Transfer all possible armies out of <code>from</code> into <code>to</code> */
    private MoveCommand transfer(RegionState from, RegionState to) {
        MoveCommand result = new MoveCommand(from.region, to.region, from.armies-1 + state.me.placeArmies);
        return result;
    }
    
    /** Returns true if all of <code>from</code>'s neighbors are owned by <code>Player.ME</code> */
    private boolean hasOnlyMyNeighbours(RegionState from) {
        for (RegionState region : from.neighbours) {            
            if (!region.owned(Player.ME)) return false;
        }
        return true;
    }
    
    /** Looks through my regions to find the one with the most armies */
    private RegionState findArmy() {
        return findArmy(1).get(0);
    }
    private List<RegionState> findArmy(int size){
        // CLONE REGIONS OWNED BY ME
        List<RegionState> mine = new ArrayList<RegionState>(state.me.regions.values());
        Collections.sort(mine, new Comparator<RegionState>() {
            @Override
            public int compare(RegionState a, RegionState b) {
                return b.armies - a.armies;
            }
        });
        
        // FIND REGION WITH MOST ARMIES
        return mine.subList(0,size);
    }
    
    public static void runInternal() {
        Config config = new Config();
        
        config.bot1Init = "internal:conquest.bot.playground.LessBasicAttackBot";
        //config.bot1Init = "dir;process:../Conquest-Bots;java -cp ./bin;../Conquest/bin conquest.bot.external.JavaBot conquest.bot.playground.ConquestBot ./ConquestBot.log";
        
        config.bot2Init = "internal:conquest.bot.BotStarter";
        //config.bot2Init = "human";
        
        config.engine.botCommandTimeoutMillis = 24*60*60*1000;
        //config.engine.botCommandTimeoutMillis = 20 * 1000;
        
        config.engine.maxGameRounds = 200;
        
        config.engine.fight = FightMode.CONTINUAL_1_1_A60_D70;
        
        config.visualize = true;
        config.forceHumanVisualization = true; // prepare for hijacking bot controls
        
        config.replayLog = new File("./replay.log");
        
        RunGame run = new RunGame(config);
        GameResult result = run.go();
        
        System.exit(0);
    }
    
    public static void main(String[] args)
    {   
    	//BotParser bot = new BotParser( new LessBasicAttackBot());
    	//bot.run();    
        runInternal();
    }
}
