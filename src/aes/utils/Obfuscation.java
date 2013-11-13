package aes.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.BiMap;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class Obfuscation {
	static HashMap<String, String> classTranslations = new HashMap<String, String>();

	static HashMap<String, String> methodTranslations = new HashMap<String, String>();

	static HashMap<String, String> fieldTranslations = new HashMap<String, String>();

	static HashMap<String, String> mcpToSrgTranslations = new HashMap<String, String>();

	public static String getClassName(String name) {
		if (name.indexOf('.') != -1)
			return getClassName(name.replace('.', '/')).replace('/', '.');

		final String result = classTranslations.get(name);
		return result == null ? name : result;
	}

	public static String getDescriptor(String descriptor) {
		String result = "";
		int index = 0;
		while (true) {
			if (index >= descriptor.length())
				return result;
			final int indexOfNextClass = descriptor.indexOf("L", index);
			if (indexOfNextClass == -1) {
				result += descriptor.substring(index);
				return result;
			}
			result += descriptor.substring(index, indexOfNextClass);
			final int indexOfEnd = descriptor.indexOf(";", indexOfNextClass);
			final String className = descriptor.substring(indexOfNextClass + 1, indexOfEnd);
			result += "L" + getClassName(className) + ";";
			index = indexOfEnd + 1;
		}
	}

	public static String getFieldName(String owner, String name, String descriptor) {
		final String result = fieldTranslations.get(getSrgName(name));
		return result == null ? name : result;
	}

	public static String getMethodName(String owner, String name, String descriptor) {
		final String result = methodTranslations.get(getSrgName(name));
		return result == null ? name : result;
	}

	public static String getSrgName(String mcpName) {
		final String srgName = mcpToSrgTranslations.get(mcpName);
		return srgName == null ? mcpName : srgName;
	}

	@SuppressWarnings("unchecked")
	public static void init(Boolean runtimeDeobfuscationEnabled) {
		if (!runtimeDeobfuscationEnabled)
			return;
		try {
			mcpToSrgTranslations.put("rayTraceBlocks_do_do", "func_72831_a");
			mcpToSrgTranslations.put("collisionRayTrace", "func_71878_a");
			mcpToSrgTranslations.put("renderBlockByRenderType", "func_78612_b");
			mcpToSrgTranslations.put("isBlockOpaqueCube", "func_72804_r");
			mcpToSrgTranslations.put("drawSelectionBox", "func_72731_b");
			mcpToSrgTranslations.put("renderTileEntityAt", "func_76894_a");
			mcpToSrgTranslations.put("renderEntities", "func_72713_a");

			mcpToSrgTranslations.put("blockX", "field_72311_b");
			mcpToSrgTranslations.put("blockY", "field_72312_c");
			mcpToSrgTranslations.put("blockZ", "field_72309_d");

			mcpToSrgTranslations.put("storageArrays", "field_76652_q");

			mcpToSrgTranslations.put("pendingTickListEntriesHashSet", "field_73064_N");
			mcpToSrgTranslations.put("pendingTickListEntriesTreeSet", "field_73065_O");

			mcpToSrgTranslations.put("worldRenderers", "field_72765_l");
			mcpToSrgTranslations.put("playersInChunk", "field_73263_b");

			final Map<String, Map<String, String>> rawFieldMaps = (Map<String, Map<String, String>>) PrivateFieldAccess.getValue(
					FMLDeobfuscatingRemapper.INSTANCE, "rawFieldMaps");
			final Map<String, Map<String, String>> rawMethodMaps = (Map<String, Map<String, String>>) PrivateFieldAccess.getValue(
					FMLDeobfuscatingRemapper.INSTANCE, "rawMethodMaps");
			final BiMap<String, String> classNameBiMap = (BiMap<String, String>) PrivateFieldAccess.getValue(FMLDeobfuscatingRemapper.INSTANCE,
					"classNameBiMap");

			for (final Entry<String, Map<String, String>> classes : rawFieldMaps.entrySet()) {
				for (final Entry<String, String> entry : classes.getValue().entrySet()) {
					final String srg = entry.getValue();
					if (srg.startsWith("field_")) {
						fieldTranslations.put(srg, entry.getKey().split(":")[0]);
					}
				}
			}

			for (final Entry<String, Map<String, String>> classes : rawMethodMaps.entrySet()) {
				for (final Entry<String, String> entry : classes.getValue().entrySet()) {
					final String srg = entry.getValue();
					if (srg.startsWith("func_")) {
						methodTranslations.put(srg, entry.getKey().split("\\(")[0]);
					}
				}
			}

			for (final Entry<String, String> entry : classNameBiMap.entrySet()) {
				classTranslations.put(entry.getValue(), entry.getKey());
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
