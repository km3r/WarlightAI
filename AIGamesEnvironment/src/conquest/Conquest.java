package conquest;

import java.io.File;

import conquest.engine.RunGame;
import conquest.engine.RunGame.Config;
import conquest.engine.RunGame.GameResult;

public class Conquest {
	
	/**
	 * Example how to start internal bots...
	 */
	public static void startInternalBots(boolean visualize) {
		Config config = new Config();
		
		config.bot1Init = "internal:conquest.bot.BotStarter";
		config.bot2Init = "internal:conquest.bot.BotStarter";
				
		config.visualize = visualize;
		
		config.replayLog = new File("./replay.log");
		
		RunGame run = new RunGame(config);
		GameResult result = run.go();
		
		System.exit(0);
	}
	
	/**
	 * Example how to start bots "from command line"...
	 */
	public static void startProcessBots(boolean visualize) {
		Config config = new Config();
		
		config.bot1Init = "process:java -cp bin conquest.bot.BotStarter";
		config.bot2Init = "dir;process:./bin;java conquest.bot.BotStarter";
				
		config.visualize = visualize;
		
		config.replayLog = new File("./replay.log");
		
		RunGame run = new RunGame(config);
		GameResult result = run.go();
		
		System.exit(0);
	}
	
	/**
	 * Example how to start bots "from command line"...
	 */
	public static void startReplay(String replayFile) {
		Config config = new Config();
		
		config.visualize = true;
		
		RunGame run = new RunGame(config);
		GameResult result = run.goReplay(new File(replayFile));
		
		System.exit(0);
	}
	
	private static void header() {
		System.out.println("---------------");
		System.out.println("CONQUEST ENGINE");
		System.out.println("---------------");
	}
	
	private static void error() {		
		System.out.println();
		System.out.println("Invalid arguments passed. Expected 1, 5 or 6 arguemnts");
		System.out.println();
		System.out.println("To start the game (5 or 6 arguments):");
		System.out.println("java ... conquest.Conquest <max rounds:int> <bot command timeout:long> <bot1:init string> <bot2:init string> <visualization:boolean> [ <replay file:string> ]");
		System.out.println("    -- possible bot init strings:");
		System.out.println("        -- internal:fqcn ~ to start bot directly on Java classpath");
		System.out.println("        -- process:command ~ to execute the bot from command line");
		System.out.println("        -- dir;process:dir;command ~ to execute the bot from command line from specfici directory");
		System.out.println("        -- human ~ human player");
		System.out.println();
		System.out.println("To replay the game (1 argument):");
		System.out.println("java ... conquest.Conquest <replay file:string>");
		System.out.println();		
		System.exit(1);
	}

	
	
}
