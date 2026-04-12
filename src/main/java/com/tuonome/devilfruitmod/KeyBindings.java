package com.tuonome.devilfruitmod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static final KeyMapping LOGIA_TOGGLE = new KeyMapping(
        "key.devilfruitmod.logia",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key.categories.devilfruitmod"
    );

    public static final KeyMapping ABILITY_1 = new KeyMapping(
        "key.devilfruitmod.ability1",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_Z,
        "key.categories.devilfruitmod"
    );

    public static final KeyMapping ABILITY_2 = new KeyMapping(
        "key.devilfruitmod.ability2",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        "key.categories.devilfruitmod"
    );

    public static final KeyMapping ABILITY_3 = new KeyMapping(
        "key.devilfruitmod.ability3",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_C,
        "key.categories.devilfruitmod"
    );
}
