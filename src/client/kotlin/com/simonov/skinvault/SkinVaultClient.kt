package com.simonov.skinvault

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier // ✅ Changed from ResourceLocation
import org.lwjgl.glfw.GLFW

object SkinVaultClient : ClientModInitializer {

    private val CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath("skinvault", "general") // ✅ Changed here too
    )

    lateinit var customKeyBind: KeyMapping

    override fun onInitializeClient() {
        customKeyBind = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.skinvault.my_action",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V, 
                CATEGORY
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (customKeyBind.consumeClick()) {
                Minecraft.getInstance().setScreen(SkinVaultScreen(null))
            }
        }
    }
}

