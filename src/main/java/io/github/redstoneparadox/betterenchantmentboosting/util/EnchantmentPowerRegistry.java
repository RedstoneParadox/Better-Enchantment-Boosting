package io.github.redstoneparadox.betterenchantmentboosting.util;

import io.github.redstoneparadox.betterenchantmentboosting.BetterEnchantmentBoosting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.BlockState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Deprecated
public class EnchantmentPowerRegistry {
	private static final Map<BlockState, Double> STATE_REGISTRY = new HashMap<>();
	private static final Map<BlockState, Double> TEMPORARY_STATE_REGISTRY = new HashMap<>();
	private static final List<DeferredRegistryAction> DEFERRED_REGISTRY_ACTIONS = new ArrayList<>();

	static {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(BetterEnchantmentBoosting.MODID, "enchantment_power");
			}

			@Override
			public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
				return null;
			}
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			if (success) {
				TEMPORARY_STATE_REGISTRY.clear();

				for (DeferredRegistryAction action: DEFERRED_REGISTRY_ACTIONS) action.run(EnchantmentPowerRegistry::registerTemporary);
			}
		});
	}

	public static void register(BlockState state, double power) {
		STATE_REGISTRY.put(state, power);
	}

	public static void registerDeferred(DeferredRegistryAction action) {
		DEFERRED_REGISTRY_ACTIONS.add(action);
	}

	private static void registerTemporary(BlockState state, double power) {
		TEMPORARY_STATE_REGISTRY.put(state, power);
	}

	public static double getPower(BlockState state) {
		if (STATE_REGISTRY.containsKey(state)) return STATE_REGISTRY.get(state);
		else if (TEMPORARY_STATE_REGISTRY.containsKey(state)) return TEMPORARY_STATE_REGISTRY.get(state);
		return 0;
	}

	public static boolean isRegistered(BlockState state) {
		return STATE_REGISTRY.containsKey(state) || TEMPORARY_STATE_REGISTRY.containsKey(state);
	}

	public interface DeferredRegistryAction {
		void run(RegistryFunction registryFunction);
	}

	public interface RegistryFunction {
		void register(BlockState state, double power);
	}
}
