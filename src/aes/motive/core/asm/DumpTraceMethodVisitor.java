package aes.motive.core.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class DumpTraceMethodVisitor extends MethodVisitor {
	public DumpTraceMethodVisitor(MethodVisitor mv) {
		super(Opcodes.ASM4, mv);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
	}
}
