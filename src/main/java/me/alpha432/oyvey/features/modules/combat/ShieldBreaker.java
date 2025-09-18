package me.alpha432.oyvey.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.lwjgl.glfw.GLFW;

public class ShieldBreaker extends Module {

    public ShieldBreaker() {
        super("ShieldBreaker", "Automatically breaks opponents' shields when key is pressed", Category.COMBAT, true, false, false);
    }

    @Subscribe
    private void onPacketSend(PacketEvent.Send event) {
        if (mc.player == null || mc.world == null) return;

        // Only activate if B is pressed
        if (!OyVey.keyManager.isKeyDown(GLFW.GLFW_KEY_B)) return;

        PlayerEntity target = null;
        double range = 5.0;

        // Find nearest shielded player
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player) || player == mc.player) continue;

            if (player.getMainHandStack().getItem().getName().getString().toLowerCase().contains("shield")) {
                double distanceSq = mc.player.squaredDistanceTo(player);
                if (distanceSq <= range * range) {
                    target = player;
                    break;
                }
            }
        }

        if (target == null) return;

        // Find axe in hotbar
        int axeSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof AxeItem) {
                axeSlot = i;
                break;
            }
        }
        if (axeSlot == -1) return;

        int oldSlot = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = axeSlot;

        // Send attack packet
        mc.player.networkHandler.sendPacket(
                new PlayerInteractEntityC2SPacket(mc.player, target, PlayerInteractEntityC2SPacket.InteractType.ATTACK)
        );

        // Switch back to original slot
        mc.player.getInventory().selectedSlot = oldSlot;
    }

    @Override
    public String getDisplayInfo() {
        return "Key B";
    }
}
