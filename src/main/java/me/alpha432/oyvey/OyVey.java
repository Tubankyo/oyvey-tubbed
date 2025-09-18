package me.alpha432.oyvey;

import me.alpha432.oyvey.manager.*;
import me.alpha432.oyvey.util.TextUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OyVey implements ModInitializer, ClientModInitializer {
    public static final String NAME = "OyVey";
    public static final String VERSION = "0.0.3 - " + SharedConstants.getGameVersion().getName();

    public static float TIMER = 1f;

    public static final Logger LOGGER = LogManager.getLogger("OyVey");
    public static ServerManager serverManager;
    public static ColorManager colorManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static HoleManager holeManager;
    public static EventManager eventManager;
    public static SpeedManager speedManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;

    @Override
    public void onInitialize() {
        // Initialize core managers
        eventManager = new EventManager();
        serverManager = new ServerManager();
        rotationManager = new RotationManager();
        positionManager = new PositionManager();
        friendManager = new FriendManager();
        colorManager = new ColorManager();
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();
        speedManager = new SpeedManager();
        holeManager = new HoleManager();

        TextUtil.init();
    }

    @Override
    public void onInitializeClient() {
        // Load events & modules
        eventManager.init();
        moduleManager.init();   // <-- registers all modules, including ShieldBreaker (added inside ModuleManager)

        // Debug check: confirm modules loaded
        LOGGER.info("Modules loaded: {}", moduleManager.getModules().size());

        // Config + color manager
        configManager = new ConfigManager();
        configManager.load();
        colorManager.init();

        // Save config on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> configManager.save()));
    }
}
