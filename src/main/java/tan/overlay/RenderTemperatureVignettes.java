package tan.overlay;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import tan.api.utils.TANPlayerStatUtils;
import tan.stats.TemperatureStat;

public class RenderTemperatureVignettes extends RenderTANOverlay
{
    public float temperature;
    
    public float prevVignetteBrightness = 1.0F;
    
    public ResourceLocation vignetteFreezingLocation = new ResourceLocation("toughasnails:textures/overlay/freezingVignette.png");
    public ResourceLocation vignetteBurningLocation = new ResourceLocation("toughasnails:textures/overlay/burningVignette.png");
    
    @Override
    void preRender(RenderGameOverlayEvent.Pre event)
    {
        TemperatureStat temperatureStat = TANPlayerStatUtils.getPlayerStat(minecraft.thePlayer, TemperatureStat.class);
        
        temperature = temperatureStat.temperatureLevel;
        
        if (!minecraft.thePlayer.capabilities.isCreativeMode)
        {
            if (temperature > 44F)
            {
                float brightness = MathHelper.clamp_float((47F - temperature) / 3F, 0F, 1F);
                
                renderVignette(vignetteBurningLocation, brightness, scaledRes.getScaledWidth(), scaledRes.getScaledHeight());
            }
            else if (temperature < 30F)
            {
                float brightness = MathHelper.clamp_float(1.0F - (-(temperature - 30F) / 3), 0F, 1F);
                
                renderVignette(vignetteFreezingLocation, brightness, scaledRes.getScaledWidth(), scaledRes.getScaledHeight());
            }
        }
    }
    
    private void renderVignette(ResourceLocation texture, float brightness, int width, int height)
    {
        GL11.glEnable(GL11.GL_BLEND);
        
        brightness = 1.0F - brightness;

        if (brightness < 0.0F)
        {
            brightness = 0.0F;
        }

        if (brightness > 1.0F)
        {
            brightness = 1.0F;
        }

        this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(brightness - this.prevVignetteBrightness) * 0.01D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);
        GL11.glColor4f(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
        bindTexture(texture);
        {
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(0.0D, (double)height, -90.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)width, (double)height, -90.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)width, 0.0D, -90.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
            tessellator.draw();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
        bindTexture(new ResourceLocation("minecraft:textures/gui/icons.png"));
    }
}
