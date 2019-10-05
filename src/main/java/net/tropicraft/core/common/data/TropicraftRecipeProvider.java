package net.tropicraft.core.common.data;

import static net.tropicraft.core.common.block.TropicraftBlocks.*;
import static net.tropicraft.core.common.item.TropicraftItems.*;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.tropicraft.core.common.TropicraftTags;

public class TropicraftRecipeProvider extends RecipeProvider {

    public TropicraftRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ore(TropicraftTags.Items.AZURITE_ORE, AZURITE, 0.3F, consumer);
        ore(TropicraftTags.Items.EUDIALYTE_ORE, EUDIALYTE, 0.5F, consumer);
        ore(TropicraftTags.Items.ZIRCON_ORE, ZIRCON, 0.5F, consumer);
        ore(TropicraftTags.Items.MANGANESE_ORE, MANGANESE, 0.5F, consumer);
        ore(TropicraftTags.Items.SHAKA_ORE, SHAKA, 0.7F, consumer);
        
        for (DyeColor color : DyeColor.values()) {
            IItemProvider wool = SheepEntity.WOOL_BY_COLOR.get(color);
            ShapedRecipeBuilder.shapedRecipe(UMBRELLAS.get(color).get())
                .patternLine("WWW").patternLine(" B ").patternLine(" B ")
                .key('W', wool)
                .key('B', Tags.Items.RODS_WOODEN)
                .addCriterion("has_" + color.getName() + "_wool", this.hasItem(wool))
                .build(consumer);
            
            // TODO other colored items
        }
        
        // Override the vanilla recipe to output ours, it's tagged so it will behave the same
        ShapedRecipeBuilder.shapedRecipe(BAMBOO_STICK.get())
            .patternLine("X").patternLine("X")
            .key('X', Items.BAMBOO)
            .addCriterion("has_bamboo", this.hasItem(Items.BAMBOO))
            .build(consumer, new ResourceLocation("stick_from_bamboo_item"));
        
        ShapedRecipeBuilder.shapedRecipe(BAMBOO_SPEAR.get())
            .patternLine("X ").patternLine(" X")
            .key('X', BAMBOO_STICK.get())
            .addCriterion("has_bamboo_stick", this.hasItem(BAMBOO_STICK.get()))
            .build(consumer);
        
        ShapelessRecipeBuilder.shapelessRecipe(RAW_COFFEE_BEAN.get())
            .addIngredient(COFFEE_BERRY.get())
            .addCriterion("has_coffee_bean", this.hasItem(COFFEE_BERRY.get()))
            .build(consumer);
        
        food(RAW_COFFEE_BEAN, ROASTED_COFFEE_BEAN, 0.1F, consumer);
        
        ShapedRecipeBuilder.shapedRecipe(BAMBOO_MUG.get())
            .patternLine("X X").patternLine("X X").patternLine("XXX")
            .key('X', Items.BAMBOO)
            .addCriterion("has_bamboo", this.hasItem(Items.BAMBOO))
            .build(consumer);

        bundle(Blocks.BAMBOO.delegate, BAMBOO_BUNDLE, consumer);
        bundle(Blocks.SUGAR_CANE.delegate, THATCH_BUNDLE, consumer);
    }
    
    private ResourceLocation safeId(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), safeName(id));
    }
    
    private ResourceLocation safeId(IForgeRegistryEntry<?> registryEntry) {
        return safeId(registryEntry.getRegistryName());
    }
    
    private String safeName(ResourceLocation nameSource) {
        return nameSource.getPath().replace('/', '_');
    }
    
    private String safeName(IForgeRegistryEntry<?> registryEntry) {
        return safeName(registryEntry.getRegistryName());
    }
    
    private <T extends IItemProvider & IForgeRegistryEntry<T>> void ore(Tag<Item> source, Supplier<T> result, float xp, Consumer<IFinishedRecipe> consumer) {
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(source), result.get(), xp, 100)
            .addCriterion("has_" + safeName(source.getId()), this.hasItem(source))
            .build(consumer);
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromTag(source), result.get(), xp, 100)
            .addCriterion("has_" + safeName(source.getId()), this.hasItem(source))
            .build(consumer, safeId(result.get()) + "_from_blasting");
    }

    private <T extends IItemProvider & IForgeRegistryEntry<T>> void food(Supplier<? extends T> source, Supplier<? extends T> result, float xp, Consumer<IFinishedRecipe> consumer) {
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(source.get()), result.get(), xp, 100)
            .addCriterion("has_" + safeName(source.get().getRegistryName()), this.hasItem(source.get()))
            .build(consumer);
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(source.get()), result.get(), xp, 100, IRecipeSerializer.SMOKING)
            .addCriterion("has_" + safeName(source.get().getRegistryName()), this.hasItem(source.get()))
            .build(consumer, safeId(result.get()) + "_from_smoking");
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(source.get()), result.get(), xp, 100, IRecipeSerializer.CAMPFIRE_COOKING)
            .addCriterion("has_" + safeName(source.get().getRegistryName()), this.hasItem(source.get()))
            .build(consumer, safeId(result.get()) + "_from_campfire");
    }

    private <T extends IItemProvider & IForgeRegistryEntry<T>> void bundle(Supplier<T> source, Supplier<T> result, Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapelessRecipe(result.get())
            .addIngredient(source.get(), 9)
            .addCriterion("has_" + safeName(source.get()), this.hasItem(source.get()))
            .build(consumer);
    }
}
