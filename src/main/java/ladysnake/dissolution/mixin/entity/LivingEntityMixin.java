package ladysnake.dissolution.mixin.entity;

import ladysnake.dissolution.api.possession.Possessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    /**
     * Marks possessed entities as the attacker for any damage caused by their possessor
     *
     * @param source damage dealt
     * @param amount amount of damage dealt
     * @param info   callback
     */
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void proxyDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (source.getAttacker() instanceof Possessor) {
            Entity possessed = (Entity) ((Possessor) source.getAttacker()).getPossessedEntity();
            if (possessed != null) {
                DamageSource newSource = null;
                if (source instanceof ProjectileDamageSource)
                    newSource = new ProjectileDamageSource(source.getName(), source.getSource(), possessed);
                else if (source instanceof EntityDamageSource)
                    newSource = new EntityDamageSource(source.getName(), possessed);
                if (newSource != null) {
                    ((LivingEntity) (Object) this).damage(newSource, amount);
                    info.setReturnValue(true);
                }
            }
        }
    }
}