package aes.motive.core.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.TABLESWITCH;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.TraceClassVisitor;

import aes.motive.core.MotiveCore;
import aes.utils.Obfuscation;

public class Transformer implements IClassTransformer {
	public class TransformBlock extends MethodTransformer {
		public TransformBlock() {
			super("net.minecraft.block.Block", "collisionRayTrace",
					"(Lnet/minecraft/world/World;IIILnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) throws Exception {
			InsnList ins = prepareForRenderHookCall();
			ins.add(new VarInsnNode(ALOAD, 1));
			ins.add(new VarInsnNode(ILOAD, 2));
			ins.add(new VarInsnNode(ILOAD, 3));
			ins.add(new VarInsnNode(ILOAD, 4));
			ins.add(renderHookCall("modifyMovingBlockBounds", "(Lnet/minecraft/world/World;III)V"));

			AbstractInsnNode insertLocation = null;
			ListIterator<AbstractInsnNode> iter = methodNode.instructions.iterator();

			while (iter.hasNext()) {
				AbstractInsnNode targetNode = iter.next();
				if (targetNode.getOpcode() == INVOKEVIRTUAL) {
					while (iter.hasNext()) {
						targetNode = iter.next();
						if (targetNode.getOpcode() == ALOAD) {
							insertLocation = targetNode;
							break;
						}
					}
					break;
				}
			}

			if (insertLocation == null)
				throw new Exception("couldn't find first insert point");

			methodNode.instructions.insertBefore(insertLocation, ins);

			ins = prepareForRenderHookCall();
			ins.add(new VarInsnNode(ALOAD, 1));
			ins.add(new VarInsnNode(ILOAD, 2));
			ins.add(new VarInsnNode(ILOAD, 3));
			ins.add(new VarInsnNode(ILOAD, 4));
			ins.add(renderHookCall("resetMovingBlockBounds", "(Lnet/minecraft/world/World;III)V"));

			insertLocation = null;
			iter = methodNode.instructions.iterator();
			while (iter.hasNext()) {
				AbstractInsnNode targetNode = iter.next();
				if (targetNode.getOpcode() == ARETURN) {
					while (iter.hasPrevious()) {
						targetNode = iter.previous();
						if (targetNode.getOpcode() == ASTORE) {
							while (iter.hasNext()) {
								targetNode = iter.next();
								if (targetNode.getOpcode() == ALOAD) {
									insertLocation = targetNode;
									break;
								}
							}
							break;
						}
					}
					break;
				}
			}

			if (insertLocation == null)
				throw new Exception("couldn't find second insert point");

			methodNode.instructions.insertBefore(insertLocation, ins);
		}
	}

	public class TransformBlockRenderer extends MethodTransformer {
		public TransformBlockRenderer() {
			super("net.minecraft.client.renderer.RenderBlocks", "renderBlockByRenderType", "(Lnet/minecraft/block/Block;III)Z");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) {
			final LabelNode beforeReturnLabel = new LabelNode();

			boolean skippedFirstReturn = false;
			final ListIterator<AbstractInsnNode> iter = methodNode.instructions.iterator();
			while (iter.hasNext()) {
				final AbstractInsnNode targetNode = iter.next();
				if (targetNode.getOpcode() == IRETURN) {
					if (!skippedFirstReturn) {
						skippedFirstReturn = true;
						continue;
					}
					methodNode.instructions.insertBefore(targetNode, new JumpInsnNode(GOTO, beforeReturnLabel));
					methodNode.instructions.remove(targetNode);
				}

				if (targetNode.getOpcode() == TABLESWITCH) {
					final InsnList ins = prepareForRenderHookCall();
					ins.add(new VarInsnNode(ILOAD, 2));
					ins.add(new VarInsnNode(ILOAD, 3));
					ins.add(new VarInsnNode(ILOAD, 4));
					ins.add(renderHookCall("offsetMovingTesselator", "(III)V"));
					methodNode.instructions.insertBefore(targetNode, ins);
				}
			}

			final InsnList ins = prepareForRenderHookCall();
			ins.insert(beforeReturnLabel);
			/*
			 * // ins.add(new VarInsnNode(ALOAD, 1)); ins.add(new
			 * VarInsnNode(ILOAD, 2)); ins.add(new VarInsnNode(ILOAD, 3));
			 * ins.add(new VarInsnNode(ILOAD, 4)); //
			 * ins.add(renderHookCall("highlightIfConnected",
			 * "(Lnet/minecraft/client/renderer/RenderBlocks;III)V"));
			 * ins.add(renderHookCall("highlightIfConnected", "(III)V"));
			 * 
			 * addGetRenderHookInstance(ins);
			 */ins.add(renderHookCall("resetMovingTesselator", "()V"));
			ins.add(new InsnNode(IRETURN));
			methodNode.instructions.add(ins);
		}

	}

	public class TransformChunkCache extends MethodTransformer {
		public TransformChunkCache() {
			super("net.minecraft.world.ChunkCache", "isBlockOpaqueCube", "(III)Z");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) {
			final LabelNode labelContinue = new LabelNode();

			final InsnList ins = prepareForRenderHookCall();
			ins.add(new VarInsnNode(ILOAD, 1));
			ins.add(new VarInsnNode(ILOAD, 2));
			ins.add(new VarInsnNode(ILOAD, 3));
			ins.add(renderHookCall("isBlockMoving", "(III)Z"));
			ins.add(new JumpInsnNode(IFEQ, labelContinue));
			ins.add(new InsnNode(ICONST_0));
			ins.add(new InsnNode(IRETURN));
			ins.add(labelContinue);

			methodNode.instructions.insertBefore(methodNode.instructions.iterator().next(), ins);
		}
	}

	public class TransformEntityRenderer extends MethodTransformer {
		public TransformEntityRenderer() {
			super("net.minecraft.client.renderer.EntityRenderer", "updateCameraAndRender", "(F)V");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) {
			InsnList ins = prepareForRenderHookCall();
			ins.add(renderHookCall("offsetViewEntityPosition", "()V"));

			methodNode.instructions.insert(ins);

			ins = prepareForRenderHookCall();
			ins.add(renderHookCall("resetViewEntityPosition", "()V"));

			methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), ins);
		}
	}

	public class TransformRenderGlobal_drawSelectionBox extends MethodTransformer {

		public TransformRenderGlobal_drawSelectionBox() {
			super("net.minecraft.client.renderer.RenderGlobal", "drawSelectionBox",
					"(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/MovingObjectPosition;IF)V");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) {
			InsnList ins = prepareForRenderHookCall();
			ins.add(new VarInsnNode(ALOAD, 2));
			ins.add(getField("net/minecraft/util/MovingObjectPosition", "blockX", "I"));
			ins.add(new VarInsnNode(ALOAD, 2));
			ins.add(getField("net/minecraft/util/MovingObjectPosition", "blockY", "I"));
			ins.add(new VarInsnNode(ALOAD, 2));
			ins.add(getField("net/minecraft/util/MovingObjectPosition", "blockZ", "I"));
			ins.add(renderHookCall("offsetMovingTesselator", "(III)V"));
			methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), ins);

			ins = prepareForRenderHookCall();
			ins.add(renderHookCall("resetMovingTesselator", "()V"));
			methodNode.instructions.insertBefore(methodNode.instructions.get(methodNode.instructions.size() - 2), ins);
		}
	}

	public class TransformRenderGlobal_renderEntities extends MethodTransformer {

		public TransformRenderGlobal_renderEntities() {
			super("net.minecraft.client.renderer.RenderGlobal", "renderEntities",
					"(Lnet/minecraft/util/Vec3;Lnet/minecraft/client/renderer/culling/ICamera;F)V");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) {
			final InsnList ins = prepareForRenderHookCall();
			ins.add(renderHookCall("updateWorldRendererTileEntities", "()V"));
			methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), ins);
		}
	}

	public class TransformRenderGlobal_updateRenderers extends MethodTransformer {

		public TransformRenderGlobal_updateRenderers() {
			super("net.minecraft.client.renderer.RenderGlobal", "updateRenderers", "(Lnet/minecraft/entity/EntityLivingBase;Z)Z");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) throws Exception {
			AbstractInsnNode insertLocation = null;
			LabelNode jumpTarget = null;
			final ListIterator<AbstractInsnNode> iter = methodNode.instructions.iterator();
			while (iter.hasNext()) {
				final AbstractInsnNode targetNode = iter.next();
				if (targetNode.getOpcode() == Opcodes.IFNE) {
					final JumpInsnNode jumpInsnNode = (JumpInsnNode) targetNode;
					jumpTarget = jumpInsnNode.label;

					insertLocation = targetNode.getNext();
					break;
				}
			}

			if (insertLocation == null)
				throw new Exception("couldn't find first insert point");

			final InsnList ins = prepareForRenderHookCall();
			ins.add(new VarInsnNode(ALOAD, 10));
			ins.add(renderHookCall("worldRendererContainsMovingBlocks", "(Lnet/minecraft/client/renderer/WorldRenderer;)Z"));

			ins.add(new JumpInsnNode(Opcodes.IFNE, jumpTarget));

			methodNode.instructions.insertBefore(insertLocation, ins);
		}
	}

	public class TransformRenderManager extends MethodTransformer {
		public TransformRenderManager() {
			super("net.minecraft.client.renderer.entity.RenderManager", "renderEntity", "(Lnet/minecraft/entity/Entity;F)V");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) {
			InsnList ins = prepareForRenderHookCall();
			ins.add(new VarInsnNode(ALOAD, 1));
			ins.add(renderHookCall("offsetOtherEntityPosition", "(Lnet/minecraft/entity/Entity;)V"));

			methodNode.instructions.insert(ins);

			ins = prepareForRenderHookCall();
			ins.add(new VarInsnNode(ALOAD, 1));
			ins.add(renderHookCall("resetOtherEntityPosition", "(Lnet/minecraft/entity/Entity;)V"));

			methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), ins);
		}
	}

	public class TransformTileEntityRenderer extends MethodTransformer {
		public TransformTileEntityRenderer() {
			super("net.minecraft.client.renderer.tileentity.TileEntityRenderer", "renderTileEntityAt", "(Lnet/minecraft/tileentity/TileEntity;DDDF)V");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) {
			AbstractInsnNode targetNode = null;
			final ListIterator<AbstractInsnNode> iter = methodNode.instructions.iterator();
			int invokeCount = 0;
			while (iter.hasNext()) {
				targetNode = iter.next();
				if (targetNode.getOpcode() == Opcodes.INVOKEVIRTUAL && ++invokeCount == 2) {

					AbstractInsnNode targetStartNode = targetNode.getPrevious();
					while (targetStartNode.getOpcode() == FLOAD || targetStartNode.getOpcode() == ALOAD || targetStartNode.getOpcode() == DLOAD) {
						targetStartNode = targetStartNode.getPrevious();
					}
					methodNode.instructions.insert(targetStartNode, prepareForRenderHookCall());
					methodNode.instructions.insert(
							targetNode,
							renderHookCall("renderTileEntity",
									"(Lnet/minecraft/client/renderer/tileentity/TileEntitySpecialRenderer;Lnet/minecraft/tileentity/TileEntity;DDDF)V"));
					methodNode.instructions.remove(targetNode);
					return;
				}
			}
		}
	}

	public class TransformWorld extends MethodTransformer {
		public TransformWorld() {
			super("net.minecraft.world.World", "rayTraceBlocks_do_do",
					"(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;ZZ)Lnet/minecraft/util/MovingObjectPosition;");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) {
			final InsnList ins = prepareForRenderHookCall();
			ins.add(new VarInsnNode(ALOAD, 0));
			ins.add(new VarInsnNode(ALOAD, 1));
			ins.add(new VarInsnNode(ALOAD, 2));
			ins.add(new VarInsnNode(ILOAD, 3));
			ins.add(new VarInsnNode(ILOAD, 4));
			ins.add(renderHookCall("rayTraceBlocks_do_do",
					"(Lnet/minecraft/world/World;Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;ZZ)Lnet/minecraft/util/MovingObjectPosition;"));
			ins.add(new InsnNode(ARETURN));

			methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), ins);
		}
	}

	public class TransformWorldRenderer extends MethodTransformer {
		public TransformWorldRenderer() {
			super("net.minecraft.client.renderer.WorldRenderer", "updateRenderer", "()V");
		}

		@Override
		public void transform(ClassNode classNode, MethodNode methodNode) throws Exception {
			final LabelNode labelContinue = new LabelNode();

			final InsnList ins = prepareForRenderHookCall();
			ins.add(new VarInsnNode(ILOAD, 14));
			ins.add(new VarInsnNode(ALOAD, 0));
			ins.add(new FieldInsnNode(GETFIELD, Obfuscation.getClassName("net/minecraft/client/renderer/WorldRenderer"), Obfuscation.getFieldName(
					"net/minecraft/client/renderer/WorldRenderer", "glRenderList", "I"), "I"));
			ins.add(new VarInsnNode(ILOAD, 11));
			ins.add(new VarInsnNode(ILOAD, 1));
			ins.add(new VarInsnNode(ILOAD, 2));
			ins.add(new VarInsnNode(ILOAD, 3));
			ins.add(renderHookCall("highlightIfConnected", "(ZIIIII)Z"));
			ins.add(new JumpInsnNode(IFEQ, labelContinue));
			ins.add(new InsnNode(Opcodes.ICONST_1));
			ins.add(new InsnNode(Opcodes.DUP));
			ins.add(new VarInsnNode(Opcodes.ISTORE, 13));
			ins.add(new VarInsnNode(Opcodes.ISTORE, 12));
			ins.add(new VarInsnNode(Opcodes.ILOAD, 11));
			ins.add(new JumpInsnNode(Opcodes.IFLE, labelContinue));
			ins.add(new InsnNode(Opcodes.ICONST_1));
			ins.add(new VarInsnNode(Opcodes.ISTORE, 14));
			ins.add(labelContinue);

			AbstractInsnNode insertLocation = null;
			final ListIterator<AbstractInsnNode> iter = methodNode.instructions.iterator();

			while (iter.hasNext()) {
				AbstractInsnNode targetNode = iter.next();
				if (targetNode.getOpcode() == INVOKEVIRTUAL) {
					final MethodInsnNode methodInsnNode = (MethodInsnNode) targetNode;
					if (methodInsnNode.name.equals(Obfuscation.getMethodName("net.minecraft.client.renderer.RenderBlocks", "renderBlockByRenderType",
							"(Lnet/minecraft/block/Block;III)Z"))) {
						while (iter.hasNext()) {
							targetNode = iter.next();
							if (targetNode.getOpcode() == ILOAD) {
								final VarInsnNode varInsnNode = (VarInsnNode) targetNode;
								if (varInsnNode.var == 14) {
									insertLocation = targetNode;
									break;
								}
							}
						}
						break;
					}
				}
			}

			if (insertLocation == null)
				throw new Exception("couldn't find first insert point");

			methodNode.instructions.insertBefore(insertLocation, ins);
		}
	}

	LinkedList<MethodTransformer> transforms;

	protected void addGetRenderHookInstance(final InsnList ins) {
		ins.add(new FieldInsnNode(GETSTATIC, "aes/motive/core/asm/RenderHook", "INSTANCE", "Laes/motive/core/asm/RenderHook;"));
	}

	protected void dumpMethod(byte[] bytes, final MethodNode methodNode) {
		final ClassReader classReader2 = new ClassReader(bytes);
		final PrintWriter printWriter = new PrintWriter(System.out);
		final DumpTraceClassVisitor myClassVisitor = new DumpTraceClassVisitor(new TraceClassVisitor(printWriter), methodNode.name, methodNode.desc);
		classReader2.accept(myClassVisitor, ClassReader.SKIP_DEBUG);
	}

	protected AbstractInsnNode getField(String className, String fieldName, String descriptor) {
		return new FieldInsnNode(GETFIELD, Obfuscation.getClassName(className), Obfuscation.getFieldName(className, fieldName, descriptor),
				Obfuscation.getDescriptor(descriptor));
	}

	protected InsnList prepareForRenderHookCall() {
		final InsnList ins = new InsnList();
		addGetRenderHookInstance(ins);
		return ins;
	}

	protected MethodInsnNode renderHookCall(String method, String descriptor) {
		return new MethodInsnNode(INVOKEVIRTUAL, "aes/motive/core/asm/RenderHook", method, Obfuscation.getDescriptor(descriptor));
	}

	@Override
	public byte[] transform(String obfuscatedName, String name, byte[] bytes) {
		if (name.equals("cpw.mods.fml.common.Loader")) {
			this.transforms = new LinkedList<MethodTransformer>();
			this.transforms.add(new TransformTileEntityRenderer());
			this.transforms.add(new TransformBlockRenderer());
			this.transforms.add(new TransformChunkCache());
			this.transforms.add(new TransformRenderGlobal_drawSelectionBox());
			this.transforms.add(new TransformRenderGlobal_renderEntities());
			this.transforms.add(new TransformRenderGlobal_updateRenderers());
			this.transforms.add(new TransformWorld());
			this.transforms.add(new TransformBlock());

			this.transforms.add(new TransformEntityRenderer());
			this.transforms.add(new TransformWorldRenderer());
			this.transforms.add(new TransformRenderManager());

		}

		if (this.transforms == null)
			return bytes;

		for (final MethodTransformer transform : this.transforms) {
			if (name.equals(transform.className)) {
				final ClassNode classNode = new ClassNode();
				final ClassReader classReader = new ClassReader(bytes);
				classReader.accept(classNode, 0);

				final Iterator<MethodNode> methods = classNode.methods.iterator();
				while (methods.hasNext()) {
					final MethodNode methodNode = methods.next();

					final String methodName = Obfuscation.getMethodName(transform.className, transform.methodName, transform.methodDescriptor);
					final String descriptor = Obfuscation.getDescriptor(transform.methodDescriptor);

					if (methodNode.name.equals(methodName) && methodNode.desc.equals(descriptor)) {
						MotiveCore.log("Transforming class " + transform.className + " method " + transform.methodName + ". Input " + bytes.length + " bytes.");

						try {
							transform.transform(classNode, methodNode);
							final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
							classNode.accept(writer);
							final byte[] result = writer.toByteArray();

							MotiveCore.log("Transformed class " + transform.className + " method " + transform.methodName + ". Result " + result.length
									+ " bytes.");

							/*
							 * dumpMethod(bytes, methodNode);
							 * MotiveCore.log("vvvvvvvvvvvvvvvvvvv");
							 * dumpMethod(result, methodNode);
							 */
							bytes = result;
							break;
						} catch (final Exception e) {
							MotiveCore.log("Exception transforming class " + transform.className + " method " + transform.methodName + ". " + e.getMessage()
									+ " @ " + e.getStackTrace());
							e.printStackTrace();
							dumpMethod(bytes, methodNode);

							return null;
						}
					}
				}
			}
		}
		return bytes;
	}

}