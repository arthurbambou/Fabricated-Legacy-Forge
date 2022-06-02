package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public interface ISpecialArmor {
    ISpecialArmor.ArmorProperties getProperties(MobEntity arg, ItemStack arg2, DamageSource arg3, double d, int i);

    int getArmorDisplay(PlayerEntity arg, ItemStack arg2, int i);

    void damageArmor(MobEntity arg, ItemStack arg2, DamageSource arg3, int i, int j);

    public static class ArmorProperties implements Comparable<ISpecialArmor.ArmorProperties> {
        public int Priority = 0;
        public int AbsorbMax = Integer.MAX_VALUE;
        public double AbsorbRatio = 0.0;
        public int Slot = 0;
        private static final boolean DEBUG = false;

        public ArmorProperties(int priority, double ratio, int max) {
            this.Priority = priority;
            this.AbsorbRatio = ratio;
            this.AbsorbMax = max;
        }

        public static int ApplyArmor(MobEntity entity, ItemStack[] inventory, DamageSource source, double damage) {
            damage *= 25.0;
            ArrayList<ISpecialArmor.ArmorProperties> dmgVals = new ArrayList();

            for(int x = 0; x < inventory.length; ++x) {
                ItemStack stack = inventory[x];
                if (stack != null) {
                    ISpecialArmor.ArmorProperties prop = null;
                    if (stack.getItem() instanceof ISpecialArmor) {
                        ISpecialArmor armor = (ISpecialArmor)stack.getItem();
                        prop = armor.getProperties(entity, stack, source, damage / 25.0, x).copy();
                    } else if (stack.getItem() instanceof ArmorItem && !source.bypassesArmor()) {
                        ArmorItem armor = (ArmorItem)stack.getItem();
                        prop = new ISpecialArmor.ArmorProperties(0, (double)armor.protection / 25.0, armor.getMaxDamage() + 1 - stack.getMeta());
                    }

                    if (prop != null) {
                        prop.Slot = x;
                        dmgVals.add(prop);
                    }
                }
            }

            if (dmgVals.size() > 0) {
                ISpecialArmor.ArmorProperties[] props = (ISpecialArmor.ArmorProperties[])dmgVals.toArray(new ISpecialArmor.ArmorProperties[0]);
                StandardizeList(props, damage);
                int level = props[0].Priority;
                double ratio = 0.0;

                for(ISpecialArmor.ArmorProperties prop : props) {
                    if (level != prop.Priority) {
                        damage *= ratio;
                        ratio = 0.0;
                        level = prop.Priority;
                    }

                    ratio += prop.AbsorbRatio;
                    double absorb = damage * prop.AbsorbRatio;
                    if (absorb > 0.0) {
                        ItemStack stack = inventory[prop.Slot];
                        int itemDamage = (int)(absorb / 25.0 < 1.0 ? 1.0 : absorb / 25.0);
                        if (stack.getItem() instanceof ISpecialArmor) {
                            ((ISpecialArmor)stack.getItem()).damageArmor(entity, stack, source, itemDamage, prop.Slot);
                        } else {
                            stack.method_3406(itemDamage, entity);
                        }

                        if (stack.count <= 0) {
                            inventory[prop.Slot] = null;
                        }
                    }
                }

                damage *= ratio;
            }

            damage += (double)entity.field_3296;
            entity.field_3296 = (int)damage % 25;
            return (int)(damage / 25.0);
        }

        private static void StandardizeList(ISpecialArmor.ArmorProperties[] armor, double damage) {
            Arrays.sort(armor);
            int start = 0;
            double total = 0.0;
            int priority = armor[0].Priority;
            int pStart = 0;
            boolean pChange = false;
            boolean pFinished = false;

            for(int x = 0; x < armor.length; ++x) {
                total += armor[x].AbsorbRatio;
                if (x == armor.length - 1 || armor[x].Priority != priority) {
                    if (armor[x].Priority != priority) {
                        total -= armor[x].AbsorbRatio;
                        --x;
                        pChange = true;
                    }

                    if (total > 1.0) {
                        for(int y = start; y <= x; ++y) {
                            double newRatio = armor[y].AbsorbRatio / total;
                            if (newRatio * damage > (double)armor[y].AbsorbMax) {
                                armor[y].AbsorbRatio = (double)armor[y].AbsorbMax / damage;
                                total = 0.0;

                                for(int z = pStart; z <= y; ++z) {
                                    total += armor[z].AbsorbRatio;
                                }

                                start = y + 1;
                                x = y;
                                break;
                            }

                            armor[y].AbsorbRatio = newRatio;
                            pFinished = true;
                        }

                        if (pChange && pFinished) {
                            damage *= total;
                            total = 0.0;
                            start = x + 1;
                            priority = armor[start].Priority;
                            pStart = start;
                            pChange = false;
                            pFinished = false;
                            if (damage <= 0.0) {
                                for(int y = x + 1; y < armor.length; ++y) {
                                    armor[y].AbsorbRatio = 0.0;
                                }
                                break;
                            }
                        }
                    } else {
                        for(int y = start; y <= x; ++y) {
                            total -= armor[y].AbsorbRatio;
                            if (damage * armor[y].AbsorbRatio > (double)armor[y].AbsorbMax) {
                                armor[y].AbsorbRatio = (double)armor[y].AbsorbMax / damage;
                            }

                            total += armor[y].AbsorbRatio;
                        }

                        damage *= total;
                        total = 0.0;
                        if (x != armor.length - 1) {
                            start = x + 1;
                            priority = armor[start].Priority;
                            pStart = start;
                            pChange = false;
                            if (damage <= 0.0) {
                                for(int y = x + 1; y < armor.length; ++y) {
                                    armor[y].AbsorbRatio = 0.0;
                                }
                                break;
                            }
                        }
                    }
                }
            }

        }

        public int compareTo(ISpecialArmor.ArmorProperties o) {
            if (o.Priority != this.Priority) {
                return o.Priority - this.Priority;
            } else {
                double left = this.AbsorbRatio == 0.0 ? 0.0 : (double)this.AbsorbMax * 100.0 / this.AbsorbRatio;
                double right = o.AbsorbRatio == 0.0 ? 0.0 : (double)o.AbsorbMax * 100.0 / o.AbsorbRatio;
                return (int)(left - right);
            }
        }

        public String toString() {
            return String.format(
                    "%d, %d, %f, %d",
                    this.Priority,
                    this.AbsorbMax,
                    this.AbsorbRatio,
                    this.AbsorbRatio == 0.0 ? 0 : (int)((double)this.AbsorbMax * 100.0 / this.AbsorbRatio)
            );
        }

        public ISpecialArmor.ArmorProperties copy() {
            return new ISpecialArmor.ArmorProperties(this.Priority, this.AbsorbRatio, this.AbsorbMax);
        }
    }
}
