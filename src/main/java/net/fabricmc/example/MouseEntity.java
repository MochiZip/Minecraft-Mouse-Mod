package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mousemod");

    // Fix: Using ResourceLocation.parse to comply with the 26.3 mapping requirements
    public static final EntityType<MouseEntity> MOUSE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation.parse("mousemod:mouse"),
        EntityType.Builder.of(MouseEntity::new, MobCategory.MONSTER).dimensions(0.3f, 0.3f).build("mouse")
    );

    public static final Item MOUSE_SPAWN_EGG = Registry.register(
        BuiltInRegistries.ITEM,
        ResourceLocation.parse("mousemod:mouse_spawn_egg"),
        new SpawnEggItem(MOUSE, 0x990000, 0x111111, new Item.Properties())
    );

    public static final Item CHEESE = Registry.register(
        BuiltInRegistries.ITEM,
        ResourceLocation.parse("mousemod:cheese"),
        new Item.Properties()
    );

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Cheese-Eating Boss Mouse Mod!");

        FabricDefaultAttributeRegistry.register(MOUSE, MouseEntity.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 45.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FOLLOW_RANGE, 8.0D)
            .add(Attributes.ATTACK_DAMAGE, 9.0D)
            .add(Attributes.ATTACK_SPEED, 0.95D));
    }
}
