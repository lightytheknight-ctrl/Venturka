package com.venture.client.freecam;

import com.venture.client.config.VentureConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/** Detached camera: only the camera moves, your body and packets are untouched. */
public final class Freecam {
	public static boolean active = false;
	public static double x, y, z, px, py, pz;
	public static float yaw, pitch;

	private Freecam() {}

	public static void toggle(Minecraft mc) {
		if (active) { disable(); return; }
		if (!VentureConfig.INSTANCE.freecamEnabled || mc.player == null) return;
		Vec3 eye = mc.player.getEyePosition();
		x = px = eye.x; y = py = eye.y; z = pz = eye.z;
		yaw = mc.player.getYRot();
		pitch = mc.player.getXRot();
		active = true;
	}

	public static void disable() { active = false; }

	public static void look(double dYaw, double dPitch) {
		yaw += (float) dYaw * 0.15f;
		pitch = Mth.clamp(pitch + (float) dPitch * 0.15f, -90f, 90f);
	}

	public static void tick(Minecraft mc) {
		if (!active) return;
		LocalPlayer p = mc.player;
		VentureConfig c = VentureConfig.INSTANCE;
		if (p == null || !c.freecamEnabled || (c.freecamDisableOnDamage && p.hurtTime > 0)) {
			disable();
			return;
		}
		px = x; py = y; pz = z;

		double yawR = Math.toRadians(yaw), pitchR = Math.toRadians(pitch);
		double fx = -Math.sin(yawR) * Math.cos(pitchR), fy = -Math.sin(pitchR), fz = Math.cos(yawR) * Math.cos(pitchR);
		double rx = -Math.cos(yawR), rz = -Math.sin(yawR);

		double mf = (mc.options.keyUp.isDown() ? 1 : 0) - (mc.options.keyDown.isDown() ? 1 : 0);
		double mr = (mc.options.keyRight.isDown() ? 1 : 0) - (mc.options.keyLeft.isDown() ? 1 : 0);
		double mu = (mc.options.keyJump.isDown() ? 1 : 0) - (mc.options.keyShift.isDown() ? 1 : 0);

		double s = c.freecamSpeed;
		x += (fx * mf + rx * mr) * s;
		y += (fy * mf + mu) * s;
		z += (fz * mf + rz * mr) * s;

		Vec3 eye = p.getEyePosition();
		double dx = x - eye.x, dy = y - eye.y, dz = z - eye.z;
		double dist = Math.sqrt(dx * dx + dy * dy + dz * dz), max = c.freecamMaxDistance;
		if (dist > max) {
			double k = max / dist;
			x = eye.x + dx * k; y = eye.y + dy * k; z = eye.z + dz * k;
		}
	}
}
