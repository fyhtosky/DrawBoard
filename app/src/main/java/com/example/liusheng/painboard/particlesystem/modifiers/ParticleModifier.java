package com.example.liusheng.painboard.particlesystem.modifiers;


import com.example.liusheng.painboard.particlesystem.Particle;

public interface ParticleModifier {

	/**
	 * modifies the specific value of a particle given the current miliseconds
	 * @param particle
	 * @param miliseconds
	 */
	void apply(Particle particle, long miliseconds);

}
