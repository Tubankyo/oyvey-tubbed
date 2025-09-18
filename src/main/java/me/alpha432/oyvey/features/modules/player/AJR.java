package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class AutoJumpOnHit extends Module {

    public AutoJumpOnHit() {
        super("AutoJumpOnHit", "Automatically jumps when you get hit", Category.PLAYER, true, false, false);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        // Check if player recently took damage
        if (mc.player.hurtTime > 0) {
            // Make the player jump
            mc.player.jump();
        }
    }

    @Override
    public String getDisplayInfo() {
        return "Jumping on hit";
    }
}
