package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

public class ShapelessOreRecipe implements RecipeType {
    private ItemStack output = null;
    private ArrayList input = new ArrayList();

    public ShapelessOreRecipe(Block result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapelessOreRecipe(Item result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapelessOreRecipe(ItemStack result, Object... recipe) {
        this.output = result.copy();

        for(Object in : recipe) {
            if (in instanceof ItemStack) {
                this.input.add(((ItemStack)in).copy());
            } else if (in instanceof Item) {
                this.input.add(new ItemStack((Item)in));
            } else if (in instanceof Block) {
                this.input.add(new ItemStack((Block)in));
            } else {
                if (!(in instanceof String)) {
                    String ret = "Invalid shapeless ore recipe: ";

                    for(Object tmp : recipe) {
                        ret = ret + tmp + ", ";
                    }

                    ret = ret + this.output;
                    throw new RuntimeException(ret);
                }

                this.input.add(OreDictionary.getOres((String)in));
            }
        }

    }

    public int getSize() {
        return this.input.size();
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public ItemStack getResult(CraftingInventory var1) {
        return this.output.copy();
    }

    public boolean matches(CraftingInventory var1, World world) {
        ArrayList required = new ArrayList(this.input);

        for(int x = 0; x < var1.getInvSize(); ++x) {
            ItemStack slot = var1.getInvStack(x);
            if (slot != null) {
                boolean inRecipe = false;
                Iterator req = required.iterator();

                while(req.hasNext()) {
                    boolean match = false;
                    Object next = req.next();
                    if (next instanceof ItemStack) {
                        match = this.checkItemEquals((ItemStack)next, slot);
                    } else if (next instanceof ArrayList) {
                        for(ItemStack item : (ArrayList<ItemStack>)next) {
                            match = match || this.checkItemEquals(item, slot);
                        }
                    }

                    if (match) {
                        inRecipe = true;
                        required.remove(next);
                        break;
                    }
                }

                if (!inRecipe) {
                    return false;
                }
            }
        }

        return required.isEmpty();
    }

    private boolean checkItemEquals(ItemStack target, ItemStack input) {
        return target.id == input.id && (target.getMeta() == -1 || target.getMeta() == input.getMeta());
    }
}
