package net.blay09.mods.balm.forge.client.rendering;

import com.mojang.datafixers.util.Pair;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeBalmRenderers implements BalmRenderers {

    private static class ColorRegistration<THandler, TObject> {
        private final THandler color;
        private final Supplier<TObject[]> objects;

        public ColorRegistration(THandler color, Supplier<TObject[]> objects) {
            this.color = color;
            this.objects = objects;
        }

        public THandler getColor() {
            return color;
        }

        public Supplier<TObject[]> getObjects() {
            return objects;
        }
    }

    private final Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions = new HashMap<>();
    private final List<Pair<Supplier<BlockEntityType<?>>, BlockEntityRendererProvider<BlockEntity>>> blockEntityRenderers = new ArrayList<>();
    private final List<ColorRegistration<BlockColor, Block>> blockColors = new ArrayList<>();
    private final List<ColorRegistration<ItemColor, ItemLike>> itemColors = new ArrayList<>();
    private final List<Pair<Supplier<Block>, RenderType>> blockRenderTypes = new ArrayList<>();

    @Override
    public ModelLayerLocation registerModel(ResourceLocation location, Supplier<LayerDefinition> layerDefinition) {
        ModelLayerLocation modelLayerLocation = new ModelLayerLocation(location, "main");
        layerDefinitions.put(modelLayerLocation, layerDefinition);
        return modelLayerLocation;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<? super T> provider) {
        blockEntityRenderers.add(Pair.of(type::get, (BlockEntityRendererProvider<BlockEntity>) provider));
    }

    @Override
    public void registerBlockColorHandler(BlockColor color, Supplier<Block[]> blocks) {
        blockColors.add(new ColorRegistration<>(color, blocks));
    }

    @Override
    public void registerItemColorHandler(ItemColor color, Supplier<ItemLike[]> items) {
        itemColors.add(new ColorRegistration<>(color, items));
    }

    @Override
    public void setBlockRenderType(Supplier<Block> block, RenderType renderType) {
        blockRenderTypes.add(Pair.of(block, renderType));
    }

    @SubscribeEvent
    public void setupClient(FMLClientSetupEvent event) {
        for (Pair<Supplier<Block>, RenderType> entry : blockRenderTypes) {
            ItemBlockRenderTypes.setRenderLayer(entry.getFirst().get(), entry.getSecond());
        }
    }

    @SubscribeEvent
    public void initRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (Pair<Supplier<BlockEntityType<?>>, BlockEntityRendererProvider<BlockEntity>> entry : blockEntityRenderers) {
            event.registerBlockEntityRenderer(entry.getFirst().get(), entry.getSecond());
        }
    }

    @SubscribeEvent
    public void initLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : layerDefinitions.entrySet()) {
            event.registerLayerDefinition(entry.getKey(), entry.getValue());
        }
    }

    @SubscribeEvent
    public void initBlockColors(ColorHandlerEvent.Block event) {
        for (ColorRegistration<BlockColor, Block> blockColor : blockColors) {
            event.getBlockColors().register(blockColor.getColor(), blockColor.getObjects().get());
        }
    }

    @SubscribeEvent
    public void initItemColors(ColorHandlerEvent.Item event) {
        for (ColorRegistration<ItemColor, ItemLike> itemColor : itemColors) {
            event.getItemColors().register(itemColor.getColor(), itemColor.getObjects().get());
        }
    }
}
