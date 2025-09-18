package me.alpha432.oyvey.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.lwjgl.glfw.GLFW;

public class ShieldBreaker extends Module {

    public final Setting<Integer> key = register(new Setting<>("Key", GLFW.GLFW_KEY_B));
    public final Setting<Double> range = register(new Setting<>("Range", 5.0, 1.0, 10.0));

    private PlayerEntity currentTarget = null;

    public ShieldBreaker() {
        super("ShieldBreaker", "Automatically breaks opponents' shields when key is pressed", Category.COMBAT, true, false, false);
    }

    @Subscribe
    private void onPacketSend(PacketEvent.Send event) {
        if (mc.player == null || mc.world == null) return;

        // Only activate if the key is pressed
        if (!OyVey.keyManager.isKeyDown(key.getValue())) return;

        // Find nearest shielded player
        currentTarget = findNearestShieldedPlayer();
        if (currentTarget == null) return;

        // Find an axe in hotbar
        int axeSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof AxeItem) {
                axeSlot = i;
                break;
            }
        }
        if (axeSlot == -1) return; // No axe found

        // Save current slot and switch to axe
        int oldSlot = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = axeSlot;

        // Send attack packet
        mc.player.networkHandler.sendPacket(
                new PlayerInteractEntityC2SPacket(mc.player, currentTarget, PlayerInteractEntityC2SPacket.InteractType.ATTACK)
        );

        // Switch back to original slot
        mc.player.getInventory().selectedSlot = oldSlot;
    }

    private PlayerEntity findNearestShieldedPlayer() {
        PlayerEntity nearest = null;
        double nearestDistanceSq = range.getValue() * range.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player) || player == mc.player) continue;

            // Check if player has shield in main hand
            if (player.getMainHandStack().getItem().getName().getString().toLowerCase().contains("shield")) {
                double distanceSq = mc.player.squaredDistanceTo(player);
                if (distanceSq <= nearestDistanceSq) {
                    nearest = player;
                    nearestDistanceSq = distanceSq;
                }
            }
        }
        return nearest;
    }

    @Override
    public String getDisplayInfo() {
        if (currentTarget != null) {
            return "Target: " + currentTarget.getEntityName();
        }
        return key.getValue() != 0 ? GLFW.glfwGetKeyName(key.getValue(), 0) : "None";
    }
}
