package net.tropicraft.core.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.vector.Vector3f;
import net.tropicraft.core.client.entity.model.BeachFloatModel;
import net.tropicraft.core.common.entity.placeable.BeachFloatEntity;

public class BeachFloatRenderer extends FurnitureRenderer<BeachFloatEntity> {

    public BeachFloatRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, "beach_float", new BeachFloatModel());
        shadowRadius = .5F;
    }
    
    @Override
    protected double getYOffset() {
        return super.getYOffset() + 1.2;
    }
    
    @Override
    protected void setupTransforms(MatrixStack stack) {
        stack.mulPose(Vector3f.YP.rotationDegrees(-180));
    }
    
    @Override
    protected boolean rockOnZAxis() {
        return true;
    }
}
