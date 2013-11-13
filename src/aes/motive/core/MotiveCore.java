package aes.motive.core;

import java.util.Map;
import java.util.logging.Logger;

import aes.motive.core.asm.Transformer;
import aes.utils.Obfuscation;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions(value = { "aes" })
@MCVersion("1.6.4")
public class MotiveCore extends DummyModContainer implements IFMLLoadingPlugin, IFMLCallHook {

	static {
		final String name = MotiveCore.class.getName();
		FMLRelaunchLog.makeLog(name);
		logger = Logger.getLogger(name);
	}

	public static Logger logger;

	public static boolean runtimeDeobfuscationEnabled;

	public static void log(String message) {
		logger.info(message);
	}

	public MotiveCore() {
		super(new ModMetadata());
		final ModMetadata metadata = getMetadata();
		metadata.modId = "motivecore";
		metadata.version = "@VERSION@.@BUILD_NUMBER@";
		metadata.name = "Motive Core";
		metadata.description = "Motive Core";
		metadata.url = "https://github.com/AesGH/Motive";
		metadata.authorList.add("Aes");
	}

	@Override
	public Void call() throws Exception {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { Transformer.class.getName() };
	}

	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return getClass().getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		Obfuscation.init((Boolean) data.get("runtimeDeobfuscationEnabled"));
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
}
