package net.fabricmc.example;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class MouseEntity extends Monster {
    private int eatCooldown = 0;

    public MouseEntity(EntityType<? extends Monster> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        // 1. Keep swimming if it falls in water
        this.goalSelector.addGoal(1, new FloatGoal(this));
        // 2. Chase down and bite the player
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        // 3. Stroll around randomly when idle
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        // 4. Stare at the player if close enough
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));

        // Target Selector: Actively lock onto players within its 8-block vision range
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.eatCooldown > 0) {
            this.eatCooldown--;
        }

        // Search for cheese on the ground every 10 ticks (half a second)
        if (!this.level().isClientSide && this.isAlive() && this.tickCount % 10 == 0 && this.eatCooldown == 0) {
            // Creates a scanning box extending 6 blocks outwards
            AABB boundingBox = this.getBoundingBox().inflate(6.0D, 3.0D, 6.0D);
            List<ItemEntity> itemsNearby = this.level().getEntitiesOfClass(ItemEntity.class, boundingBox);

            for (ItemEntity itemEntity : itemsNearby) {
                // If it finds your custom cheese item laying on the ground
                if (itemEntity.isAlive() && itemEntity.getItem().is(ExampleMod.CHEESE)) {
                    // Start running toward the item entity
                    this.getNavigation().moveTo(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 1.3D);

                    // If it gets within mouth-range (less than 1.2 blocks away)
                    if (this.distanceToSqr(itemEntity) < 1.44D) {
                        itemEntity.getItem().shrink(1); // Delete 1 cheese from the stack
                        this.heal(10.0F);               // Restore 10 health points to the mouse
                        this.eatCooldown = 60;          // Set a 3-second cooldown before eating again
                        break;
                    }
                }
            }
        }
    }
}
