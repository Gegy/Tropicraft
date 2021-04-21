package net.tropicraft.core.client.scuba;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.tropicraft.Constants;
import net.tropicraft.core.client.data.TropicraftLangKeys;
import net.tropicraft.core.common.item.scuba.ScubaArmorItem;
import net.tropicraft.core.common.item.scuba.ScubaData;
import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.annotation.Nullable;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MODID, bus = Bus.FORGE)
public class ScubaHUD {
    
    @SubscribeEvent
    public static void renderHUD(RenderGameOverlayEvent event) {
        Entity renderViewEntity = Minecraft.getInstance().cameraEntity;
        if (event.getType() == ElementType.TEXT && renderViewEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) renderViewEntity;
            // TODO support other slots than chest?
            ItemStack chestStack = player.getItemBySlot(EquipmentSlotType.CHEST);
            Item chestItem = chestStack.getItem();
            if (chestItem instanceof ScubaArmorItem) {
                LazyOptional<ScubaData> data = player.getCapability(ScubaData.CAPABILITY);
                int airRemaining = ((ScubaArmorItem)chestItem).getRemainingAir(chestStack);
                TextFormatting airColor = getAirTimeColor(airRemaining, player.level);
                double depth = ScubaData.getDepth(player);
                String depthStr;
                if (depth > 0) {
                    depthStr = String.format("%.1fm", depth); 
                } else {
                    depthStr = TropicraftLangKeys.NA.getLocalizedText();
                }
                data.ifPresent(d -> drawHUDStrings(event.getMatrixStack(),
                    TropicraftLangKeys.SCUBA_AIR_TIME.format(airColor + formatTime(airRemaining)),
                    TropicraftLangKeys.SCUBA_DIVE_TIME.format(formatTime(d.getDiveTime())),
                    TropicraftLangKeys.SCUBA_DEPTH.format(depthStr),
                    TropicraftLangKeys.SCUBA_MAX_DEPTH.format(String.format("%.1fm", d.getMaxDepth()))));
            }
        }
    }
    
    public static String formatTime(long time) {
        return DurationFormatUtils.formatDuration(time * (1000 / 20), "HH:mm:ss");
    }
    
    public static TextFormatting getAirTimeColor(int airRemaining, @Nullable World world) {
        if (airRemaining < 20 * 60) { // 1 minute
            // Flash white/red
            int speed = airRemaining < 20 * 10 ? 5 : 10;
            return world != null && (world.getGameTime() / speed) % 4 == 0 ? TextFormatting.WHITE : TextFormatting.RED;
        } else if (airRemaining < 20 * 60 * 5) { // 5 minutes
            return TextFormatting.GOLD;
        } else {
            return TextFormatting.GREEN;
        }
    }
    
    private static void drawHUDStrings(MatrixStack matrixStack, ITextComponent... components) {
        FontRenderer fr = Minecraft.getInstance().font;
        MainWindow mw = Minecraft.getInstance().getWindow();

        int startY = mw.getGuiScaledHeight() - 5 - (fr.lineHeight * components.length);
        int startX = mw.getGuiScaledWidth() - 5;
        
        for (ITextComponent text : components) {
            String s = text.getString();
            fr.drawShadow(matrixStack, s, startX - fr.width(s), startY, -1);
            startY += fr.lineHeight;
        }
    }
}
