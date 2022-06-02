package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

public class ShapedOreRecipe implements RecipeType {
    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;
    private ItemStack output = null;
    private Object[] input = null;
    private int width = 0;
    private int height = 0;
    private boolean mirrored = true;

    public ShapedOreRecipe(Block result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapedOreRecipe(Item result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapedOreRecipe(ItemStack result, Object... recipe) {
        this.output = result.copy();
        String shape = "";
        int idx = 0;
        if (recipe[idx] instanceof Boolean) {
            this.mirrored = (boolean) recipe[idx];
            if (recipe[idx + 1] instanceof Object[]) {
                recipe = (Object[]) recipe[idx + 1];
            } else {
                idx = 1;
            }
        }

        if (recipe[idx] instanceof String[]) {
            String[] parts = (String[])recipe[idx++];

            for(String s : parts) {
                this.width = s.length();
                shape = shape + s;
            }

            this.height = parts.length;
        } else {
            while(recipe[idx] instanceof String) {
                String s = (String)recipe[idx++];
                shape = shape + s;
                this.width = s.length();
                ++this.height;
            }
        }

        if (this.width * this.height != shape.length()) {
            String ret = "Invalid shaped ore recipe: ";

            for(Object tmp : recipe) {
                ret = ret + tmp + ", ";
            }

            ret = ret + this.output;
            throw new RuntimeException(ret);
        } else {
            HashMap<Character, Object> itemMap;
            for(itemMap = new HashMap<>(); idx < recipe.length; idx += 2) {
                Character chr = (Character)recipe[idx];
                Object in = recipe[idx + 1];
                Object val = null;
                if (in instanceof ItemStack) {
                    itemMap.put(chr, ((ItemStack)in).copy());
                } else if (in instanceof Item) {
                    itemMap.put(chr, new ItemStack((Item)in));
                } else if (in instanceof Block) {
                    itemMap.put(chr, new ItemStack((Block)in, 1, -1));
                } else {
                    if (!(in instanceof String)) {
                        String ret = "Invalid shaped ore recipe: ";

                        for(Object tmp : recipe) {
                            ret = ret + tmp + ", ";
                        }

                        ret = ret + this.output;
                        throw new RuntimeException(ret);
                    }

                    itemMap.put(chr, OreDictionary.getOres((String)in));
                }
            }

            this.input = new Object[this.width * this.height];
            int x = 0;

            for(char chr : shape.toCharArray()) {
                this.input[x++] = itemMap.get(chr);
            }

        }
    }

    public ItemStack getResult(CraftingInventory var1) {
        return this.output.copy();
    }

    public int getSize() {
        return this.input.length;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public boolean matches(CraftingInventory inv, World world) {
        for(int x = 0; x <= 3 - this.width; ++x) {
            for(int y = 0; y <= 3 - this.height; ++y) {
                if (this.checkMatch(inv, x, y, true)) {
                    return true;
                }

                if (this.mirrored && this.checkMatch(inv, x, y, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(CraftingInventory inv, int startX, int startY, boolean mirror) {
        for(int x = 0; x < 3; ++x) {
            for(int y = 0; y < 3; ++y) {
                int subX = x - startX;
                int subY = y - startY;
                Object target = null;
                if (subX >= 0 && subY >= 0 && subX < this.width && subY < this.height) {
                    if (mirror) {
                        target = this.input[this.width - subX - 1 + subY * this.width];
                    } else {
                        target = this.input[subX + subY * this.width];
                    }
                }

                ItemStack slot = inv.method_3280(x, y);
                if (target instanceof ItemStack) {
                    if (!this.checkItemEquals((ItemStack)target, slot)) {
                        return false;
                    }
                } else if (!(target instanceof ArrayList)) {
                    if (target == null && slot != null) {
                        return false;
                    }
                } else {
                    boolean matched = false;

                    for(ItemStack item : (ArrayList<ItemStack>)target) {
                        matched = matched || this.checkItemEquals(item, slot);
                    }

                    if (!matched) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean checkItemEquals(ItemStack target, ItemStack input) {
        if ((input != null || target == null) && (input == null || target != null)) {
            return target.id == input.id && (target.getMeta() == -1 || target.getMeta() == input.getMeta());
        } else {
            return false;
        }
    }

    public ShapedOreRecipe setMirrored(boolean mirror) {
        this.mirrored = mirror;
        return this;
    }
}
