package aes.motive;

import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;

public class MethodToInvokeOnTileEntity {
	public TileEntity tileEntity;
	public Method method;
	public Object[] args;

	public MethodToInvokeOnTileEntity(Method method, TileEntity tileEntity, Object[] args) {
		this.method = method;
		this.tileEntity = tileEntity;
		this.args = args;
	}
}