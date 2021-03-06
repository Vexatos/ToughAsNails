package tan;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadableResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import tan.configuration.TANConfiguration;
import tan.core.CreativeTabTAN;
import tan.core.TANArmour;
import tan.core.TANCrafting;
import tan.core.TANItems;
import tan.core.TANPlayerStats;
import tan.core.TANPotions;
import tan.core.TANTemperature;
import tan.core.TANThirst;
import tan.eventhandler.StatUpdateEventHandler;
import tan.handler.ConnectionHandler;
import tan.handler.LocalizationHandler;
import tan.network.PacketHandler;
import tan.overlay.RenderAirOverlay;
import tan.overlay.RenderTemperatureOverlay;
import tan.overlay.RenderTemperatureVignettes;
import tan.overlay.RenderThirstOverlay;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = "ToughAsNails", name = "Tough As Nails", dependencies="required-after:Forge@[1.42.666.42.1,)")
@NetworkMod(channels = { "ToughAsNails" }, packetHandler = PacketHandler.class, clientSideRequired = true, serverSideRequired = false)
public class ToughAsNails
{
    @Instance("ToughAsNails")
    public static ToughAsNails instance;
    
    @SidedProxy(clientSide="tan.ClientProxy", serverSide="tan.CommonProxy")
    public static CommonProxy proxy;
    
    public static CreativeTabs tabToughAsNails;
    public static String configPath;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        configPath = event.getModConfigurationDirectory() + "/toughasnails/";
        TANConfiguration.init(configPath);
        
        tabToughAsNails = new CreativeTabTAN(CreativeTabs.getNextID(), "tabToughAsNails");

        TANPotions.init();
        TANItems.init();
        TANArmour.init();
        TANCrafting.init();
        TANPlayerStats.init();
        TANTemperature.init();
        TANThirst.init();
        
        MinecraftForge.EVENT_BUS.register(new StatUpdateEventHandler());
        
        if (proxy instanceof ClientProxy)
        {
            ReloadableResourceManager resourceManager = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), new String[]{"mcResourceManager"});

            resourceManager.registerReloadListener(new LocalizationHandler());

            MinecraftForge.EVENT_BUS.register(new RenderTemperatureOverlay());
            MinecraftForge.EVENT_BUS.register(new RenderTemperatureVignettes());
            MinecraftForge.EVENT_BUS.register(new RenderThirstOverlay());
            MinecraftForge.EVENT_BUS.register(new RenderAirOverlay());
        }
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
