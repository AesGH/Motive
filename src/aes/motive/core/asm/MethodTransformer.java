package aes.motive.core.asm;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import aes.utils.Obfuscation;

public abstract class MethodTransformer {
	public String className, methodName, methodDescriptor;

	protected MethodTransformer(String className, String methodName, String methodDescriptor) {
		this.className = className;
		this.methodName = methodName;
		this.methodDescriptor = methodDescriptor;

		Obfuscation.getClassName(className);
	}

	public abstract void transform(ClassNode classNode, MethodNode methodNode) throws Exception;
}