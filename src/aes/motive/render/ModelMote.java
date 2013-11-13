package aes.motive.render;

import net.minecraft.entity.Entity;

public class ModelMote extends ModelMotiveBase {
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);

		renderMote(f5);
	}
}
