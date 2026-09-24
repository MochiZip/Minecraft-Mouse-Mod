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
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.eatCooldown > 0) {
            this.eatCooldown--;
        }

        // Run cheese scanning logic on the server side every 10 ticks (0.5 seconds)
        if (!this.level().isClientSide && this.isAlive() && this.tickCount % 10 == 0 && this.eatCooldown == 0) {
            // Box area checking 6 blocks in all directions
            AABB boundingBox = this.getBoundingBox().inflate(6.0D, 3.0D, 6.0D);
            List<ItemEntity> itemsNearby = this.level().getEntitiesOfClass(ItemEntity.class, boundingBox);

            for (ItemEntity itemEntity : itemsNearby) {
                // Check if the item on the floor is our custom Cheese item
                if (itemEntity.isAlive() && itemEntity.getItem().is(ExampleMod.CHEESE)) {
                    // Navigate to the cheese on the floor
                    this.getNavigation().moveTo(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 1.3D);

                    // If close enough to "bite" it (within 1.2 blocks)
                    if (this.distanceToSqr(itemEntity) < 1.44D) {
                        itemEntity.getItem().shrink(1); // Eat 1 cheese from the ground stack
                        this.heal(10.0F); // Eating cheese heals the mouse 10 health!
                        this.eatCooldown = 60; // 3-second cooldown before eating more
                        break;
                    }
                }
            }
        }
    }
}
