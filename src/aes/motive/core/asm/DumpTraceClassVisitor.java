package aes.motive.core.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class DumpTraceClassVisitor extends ClassVisitor {
	private final String methodName;
	private final String desc;

	public DumpTraceClassVisitor(ClassVisitor cv, String methodName, String desc) {
		super(Opcodes.ASM4, cv);
		this.methodName = methodName;
		this.desc = desc;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return null;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		if (name.equals(this.methodName) && desc.equals(this.desc))
			return new DumpTraceMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));

		return null;
	}
}